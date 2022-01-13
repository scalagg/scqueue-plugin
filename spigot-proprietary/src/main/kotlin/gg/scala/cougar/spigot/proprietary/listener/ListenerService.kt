package gg.scala.cougar.spigot.proprietary.listener

import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryEngine
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryPlugin
import gg.scala.cougar.spigot.proprietary.backbone.BackboneService
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.PlayerHandler
import me.lucko.helper.Events
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.ForkJoinPool

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service("Listeners")
object ListenerService
{
    @Inject
    lateinit var plugin: ScalaCougarSpigotProprietaryPlugin

    @Configure
    fun configure()
    {
        val engine = ScalaCougarSpigotProprietaryEngine

        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                engine.loadPlayerResources(it.player.uniqueId).thenAccept { player ->
                    player.priority = PlayerHandler.findPlayer(it.player.uniqueId)
                        .orElse(null).activeGrant!!.getRank().weight

                    player.save().thenRun {
                        ScCougarServerMessageProducer.produce(
                            "player-connect",
                            "uniqueId" to it.player.uniqueId
                        ).dispatch(BackboneService.banana)
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
                ).dispatch(BackboneService.banana)
            }
            .bindWith(plugin)
    }
}
