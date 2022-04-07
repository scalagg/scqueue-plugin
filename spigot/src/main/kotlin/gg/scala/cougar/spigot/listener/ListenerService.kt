package gg.scala.cougar.spigot.listener

import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.ForkJoinPool

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
object ListenerService
{
    @Inject
    lateinit var plugin: ScalaCougarSpigotPlugin

    @Configure
    fun configure()
    {
        val engine = ScalaCougarSpigotEngine

        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                engine.loadPlayerResources(it.player.uniqueId).thenAccept { player ->
                    player.priority = plugin.priorityProvider.getPriority(it.player)

                    player.save().thenRun {
                        ScCougarServerMessageProducer.produce(
                            "player-connect",
                            "uniqueId" to it.player.uniqueId
                        ).publish()
                    }
                }
            }
            .bindWith(plugin)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                ForkJoinPool.commonPool().execute {
                    engine.removeResources(it.player.uniqueId)
                }

                ScCougarServerMessageProducer.produce(
                    "player-disconnect",
                    "uniqueId" to it.player.uniqueId
                ).publish()
            }
            .bindWith(plugin)
    }
}
