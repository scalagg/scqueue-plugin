package gg.scala.cougar.spigot.backbone

import com.google.gson.reflect.TypeToken
import gg.scala.aware.AwareBuilder
import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.redirection.impl.VelocityRedirectSystem
import net.evilblock.cubed.serializers.Serializers
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.lang.reflect.Type
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
object BackboneService
{
    private val typeToken: Type =
        object : TypeToken<Map<UUID, String>>()
        {}.type

    @Inject
    lateinit var plugin: ScalaCougarSpigotPlugin

    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("cougar:bukkit")
            .codec(AwareMessageCodec)
            .build()
    }

    @Configure
    fun configure()
    {
        aware.listen(this)

        aware.connect()
            .toCompletableFuture()
            .join()

        plugin.server.messenger
            .registerOutgoingPluginChannel(
                plugin, "BungeeCord"
            )
    }

    @Close
    fun close()
    {
        aware.shutdown()
    }

    @Subscribe("send-player")
    fun onSendPlayer(message: AwareMessage)
    {
        val uniqueId = message
            .retrieve<UUID>("uniqueId")

        val serverId = message
            .retrieve<String>("serverId")

        val engine = ScalaCougarSpigotEngine

        val queue = engine.queueLayer.localCache().values
            .firstOrNull {
                it.serverId.equals(serverId, true)
            } ?: return

        Bukkit.getPlayer(uniqueId)?.apply {
            sendMessage(
                "${ChatColor.GREEN}You're now being sent to ${ChatColor.YELLOW}${queue.fancyDisplayName}${ChatColor.GREEN}."
            )

            VelocityRedirectSystem
                .redirect(this, serverId)
        }
    }

    val type: Type = object : TypeToken<MutableMap<UUID, String>>(){}.type

    @Subscribe("queue-update-stream")
    fun onQueueUpdates(message: AwareMessage)
    {
        val messages = Serializers.gson.fromJson<MutableMap<UUID, String>>(
            message.retrieve<String>("messages"), type
        )

        for (onlinePlayer in Bukkit.getOnlinePlayers())
        {
            val messageForPlayer = messages[onlinePlayer.uniqueId]
                ?: continue

            onlinePlayer.sendMessage(messageForPlayer)
        }
    }
}
