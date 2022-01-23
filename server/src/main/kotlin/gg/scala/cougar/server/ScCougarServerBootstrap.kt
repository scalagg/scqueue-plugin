package gg.scala.cougar.server

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.server.command.CommandService
import gg.scala.cougar.server.consumer.ScCougarServerMessageConsumer
import gg.scala.cougar.server.iterator.ScCougarQueueIterator
import gg.scala.cougar.server.iterator.ScCougarQueueUpdateStreamIterator
import gg.scala.cougar.server.settings.CougarSettingsProcessor
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import gg.scala.cougar.server.store.ScalaDataStoreStandaloneImpl
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 1/11/2022
 */
object ScCougarServerBootstrap : ScCougarPlatform()
{
    @JvmStatic
    val LOGGER: Logger = Logger.getLogger("ScCougarServerBootstrap")

    @JvmStatic
    fun main(args: Array<String>)
    {
        val startup = System.currentTimeMillis()
        LOGGER.info("Starting cougar server...")

        flavor = Flavor
            .create<ScCougarServerBootstrap>(
                FlavorOptions(LOGGER)
            )

        CougarSettingsProcessor
            .configure()

        ScalaDataStoreShared.INSTANCE =
            ScalaDataStoreStandaloneImpl

        setupLayers()
        configure()

        injectServices()

        enableQueueUpdater()
        enableShutdownHook()

        LOGGER.info(
            "Finished startup in ${System.currentTimeMillis() - startup}ms (${
                (System.currentTimeMillis() - startup) / 1000
            }s)."
        )
    }

    private fun injectServices()
    {
        flavor.inject(CommandService)
        flavor.inject(ScCougarServerMessageConsumer)

        flavor.inject(ScCougarQueueIterator)
        flavor.inject(ScCougarQueueUpdateStreamIterator)
    }

    private fun enableQueueUpdater()
    {
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
                val queueSettings = CougarSettingsProcessor
                    .locate<CougarQueueSettings>()!!

                for (queue in queueSettings.queues)
                {
                    queue.pushDataUpdates()
                }
            }, 1L, 1L, TimeUnit.SECONDS)
        }
    }

    private fun enableShutdownHook()
    {
        Runtime.getRuntime()
            .addShutdownHook(
                object : Thread()
                {
                    override fun run()
                    {
                        close()
                    }
                }
            )
    }

    override fun updateLocalInstance() = Unit

    override fun getLocalInstance() =
        throw RuntimeException("Server does not support local instances")

    override fun shouldTrackInstance() = false
}
