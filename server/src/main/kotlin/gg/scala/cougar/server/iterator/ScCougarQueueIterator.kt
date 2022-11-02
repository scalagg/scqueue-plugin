package gg.scala.cougar.server.iterator

import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.server.AbstractServer
import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.agnostic.sync.server.state.ServerState
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.server.settings.impl.CougarIterationSettings
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service("Queue Iterator")
object ScCougarQueueIterator : Runnable
{
    @Inject
    lateinit var settings: CougarQueueSettings

    @Inject
    lateinit var iteration: CougarIterationSettings

    @Configure
    fun configure()
    {
        val executor = Executors
            .newSingleThreadScheduledExecutor()

        executor.scheduleAtFixedRate(
            this, 0L,
            iteration.iterationDelay,
            TimeUnit.MILLISECONDS
        )
    }

    override fun run()
    {
        kotlin.runCatching {
            tick()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun tick()
    {
        for (queue in settings.queues)
        {
            if (queue.getHandle().isEmpty())
            {
                continue
            }

            val instance = queue.getGameServer()
                ?: continue


            if (
                (instance as AbstractServer).state != ServerState.Loaded ||
                instance.getMaxPlayers() == instance.getPlayersCount()
            )
            {
                continue
            }

            val priorityQueue = PriorityQueue(queue.getHandle())

            if (instance.getWhitelisted() == true)
            {
                priorityQueue.removeIf {
                    !instance.getWhitelistedPlayers()?.contains(it)!!
                }
            }

            if (priorityQueue.isNotEmpty())
            {
                val uniqueId = priorityQueue.poll()

                queue.getHandle().remove(uniqueId)
                queue.pushDataUpdates().join()

                ScCougarServerMessageProducer.produce(
                    "send-player",
                    "uniqueId" to uniqueId,
                    "serverId" to queue.targetServerId
                ).publish(
                    channel = "cougar:bukkit",
                    context = AwareThreadContext.SYNC
                )
            }
        }
    }
}
