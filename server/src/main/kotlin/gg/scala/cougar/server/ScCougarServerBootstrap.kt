package gg.scala.cougar.server

import gg.scala.banana.Banana
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.options.BananaOptions
import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.server.iterator.ScCougarQueueIterator
import gg.scala.cougar.server.consumer.ScCougarServerMessageConsumer
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.server.iterator.ScCougarQueueUpdateStreamIterator
import gg.scala.cougar.server.settings.CougarSettingsProcessor
import gg.scala.cougar.server.settings.impl.CougarIterationSettings
import gg.scala.cougar.server.settings.impl.CougarLanguageSettings
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import gg.scala.cougar.server.store.ScalaDataStoreStandaloneImpl
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.store.uuidcache.impl.RedisUUIDCache
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.concurrent.thread

/**
 * @author GrowlyX
 * @since 1/11/2022
 */
object ScCougarServerBootstrap : ScCougarPlatform()
{
    @JvmStatic
    val LOGGER: Logger = Logger.getAnonymousLogger()

    private lateinit var stdUuidCache: RedisUUIDCache

    @JvmStatic
    fun main(args: Array<String>)
    {
        val startup = System.currentTimeMillis()
        LOGGER.info("Starting cougar server...")

        ScalaDataStoreShared.INSTANCE = ScalaDataStoreStandaloneImpl

        CougarSettingsProcessor.configure()
        CougarSettingsProcessor.loadSettings<CougarLanguageSettings>("language")
        CougarSettingsProcessor.loadSettings<CougarQueueSettings>("queues")
        CougarSettingsProcessor.loadSettings<CougarIterationSettings>("iteration")

        val banana = Banana(
            BananaCredentials(),
            BananaOptions(
                channel = "cougar:uid_cache",
                gson = Serializers.gson,
                executor = executor
            )
        )

        stdUuidCache = RedisUUIDCache(banana)
        stdUuidCache.load()

        flavor.bind<CougarLanguageSettings>().instance =
            CougarSettingsProcessor.locate<CougarLanguageSettings>()!!

        flavor.bind<CougarQueueSettings>().instance =
            CougarSettingsProcessor.locate<CougarQueueSettings>()!!

        flavor.bind<CougarIterationSettings>().instance =
            CougarSettingsProcessor.locate<CougarIterationSettings>()!!

        configure()

        flavor.inject(ScCougarServerMessageConsumer)
        flavor.inject(ScCougarQueueIterator)
        flavor.inject(ScCougarQueueUpdateStreamIterator)

        CougarSettingsProcessor.locate<CougarQueueSettings>()!!.apply {
            for (queue in queues)
            {
                queueDatabaseIds[queue.targetServerId] = queueLayer
                    .useLayerWithReturn<RedisDataStoreStorageLayer<CougarQueueWrapper>, UUID>(
                        DataStoreStorageType.REDIS
                    ) {
                        this.loadAllWithFilterSync {
                            it.serverId == queue.targetServerId
                        }.values.firstOrNull()?.identifier
                            ?: UUID.randomUUID()
                    }

                queue.setHandle(PriorityQueue())
            }

            executor.scheduleAtFixedRate({
                for (queue in queues)
                {
                    queue.pushDataUpdates()
                }
            }, 0L, 1L, TimeUnit.SECONDS)
        }

        Runtime.getRuntime()
            .addShutdownHook(
                object : Thread() {
                    override fun run()
                    {
                        close()
                    }
                }
            )

        LOGGER.info("Finished startup in ${System.currentTimeMillis() - startup}ms (${
            (System.currentTimeMillis() - startup) / 1000
        }s).")
    }

    override fun getUuidCache() = stdUuidCache
    override fun updateLocalInstance() = Unit

    override fun getLocalInstance() =
        throw RuntimeException("Server does not support local instances")

    override fun shouldTrackInstance() = false
}
