package gg.scala.cougar.server.iterator

import gg.scala.commons.agnostic.sync.server.AbstractServer
import gg.scala.commons.agnostic.sync.server.state.ServerState
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.server.settings.impl.CougarIterationSettings
import gg.scala.cougar.server.settings.impl.CougarLanguageSettings
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.serializers.Serializers
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service("Queue Update Stream")
object ScCougarQueueUpdateStreamIterator : Runnable
{
    @Inject
    lateinit var language: CougarLanguageSettings

    @Inject
    lateinit var queues: CougarQueueSettings

    @Inject
    lateinit var iteration: CougarIterationSettings

    @Configure
    fun configure()
    {
        val executor = Executors.newSingleThreadScheduledExecutor()

        executor.scheduleAtFixedRate(
            this, 500L,
            iteration.updateStreamIterationDelay,
            TimeUnit.MILLISECONDS
        )
    }

    private fun tick()
    {
        for (queue in queues.queues)
        {
            val instance = queue.getGameServer()
            val messageMap = mutableMapOf<UUID, String>()

            val offline = instance == null || (instance as AbstractServer).state != ServerState.Loaded
            val whitelisted = instance != null && instance.getWhitelisted() == true

            for (player in queue.getHandle())
            {
                var baseMessage = language.updateMessage
                    .replace("{pos}", queue.getPosition(player).toString())
                    .replace("{max}", queue.sizeOfQueue().toString())
                    .replace("{queue}", queue.fancyDisplayName)

                if (offline)
                {
                    baseMessage += "\n${language.offlineAddition}"
                } else if (whitelisted)
                {
                    baseMessage += "\n${language.whitelistedAddition}"
                }

                messageMap[player] = "$baseMessage\n "
            }

            ScCougarServerMessageProducer.produce(
                "queue-update-stream",
                "queueId" to queue.targetServerId,
                "messages" to Serializers.gson.toJson(messageMap)
            ).publish(
                channel = "cougar:bukkit"
            )
        }
    }

    override fun run()
    {
        kotlin.runCatching {
            tick()
        }.onFailure {
            it.printStackTrace()
        }
    }
}
