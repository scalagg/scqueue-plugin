package gg.scala.cougar.spigot.backbone

import com.google.common.io.ByteStreams
import com.google.gson.reflect.TypeToken
import gg.scala.banana.Banana
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.serializers.Serializers
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.reflect.Type
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
@IgnoreAutoScan
object BackboneService
{
    private val typeToken: Type = object : TypeToken<Map<UUID, String>>() {}.type

    val banana = Banana(
        BananaCredentials(),
        BananaOptions(
            channel = "cougar:backbone",
            executor = ScCougarServerMessageProducer.executor
        )
    )

    @Inject
    lateinit var plugin: ScalaCougarSpigotPlugin

    @Configure
    fun configure()
    {
        val bukkitBackbone = Banana(
            BananaCredentials(),
            BananaOptions(
                channel = "cougar:bukkit",
                executor = ScCougarServerMessageProducer.executor
            )
        )

        bukkitBackbone.registerClass(this)
        bukkitBackbone.subscribe()

        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
    }

    @Subscribe("send-player")
    fun onSendPlayer(message: Message)
    {
        val uniqueId = UUID.fromString(
            message["uniqueId"]
        ) ?: return

        val serverId = message["serverId"]!!
        val engine = ScalaCougarSpigotEngine

        val queue = engine.queueLayer.localCache.values
            .firstOrNull {
                it.serverId.equals(serverId, true)
            } ?: return

        Bukkit.getPlayer(uniqueId)?.apply {
            plugin.server.scheduler.runTaskLater(plugin, {
                sendMessage("${ChatColor.GREEN}You're now being sent to ${ChatColor.YELLOW}${queue.fancyDisplayName}${ChatColor.GREEN}.")
                sendToServer(this, serverId)
            }, 1L)
        }
    }

    @Subscribe("queue-update-stream")
    fun onQueueUpdates(message: Message)
    {
        val messages = Serializers.gson
            .fromJson<Map<UUID, String>>(
                message["messages"], typeToken
            )

        for (onlinePlayer in Bukkit.getOnlinePlayers())
        {
            val messageForPlayer = messages[onlinePlayer.uniqueId]
                ?: continue

            onlinePlayer.sendMessage(messageForPlayer)
        }
    }

    private fun sendToServer(player: Player, server: String)
    {
        try
        {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("Connect")
            out.writeUTF(server)

            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
