package gg.scala.cougar.spigot.proprietary.backbone

import com.google.gson.reflect.TypeToken
import gg.scala.banana.Banana
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryEngine
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bungee.BungeeUtil
import org.bukkit.Bukkit
import java.lang.reflect.Type
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
object BackboneService
{
    private val typeToken: Type = object : TypeToken<Map<UUID, String>>() {}.type

    val banana = Banana(
        BananaCredentials(),
        BananaOptions(
            channel = "cougar:backbone",
            gson = Serializers.gson,
            executor = ScCougarServerMessageProducer.executor
        )
    )

    private val bukkitBackbone = Banana(
        BananaCredentials(),
        BananaOptions(
            channel = "cougar:backbone:bukkit",
            gson = Serializers.gson,
            executor = ScCougarServerMessageProducer.executor
        )
    )

    @Configure
    fun configure()
    {
        bukkitBackbone.registerClass(this)
        bukkitBackbone.subscribe()
    }

    @Subscribe("send-player")
    fun onSendPlayer(message: Message)
    {
        val uniqueId = UUID.fromString(
            message["uniqueId"]
        ) ?: return

        val serverId = message["serverId"]!!
        val engine = ScalaCougarSpigotProprietaryEngine

        val queue = engine.queueLayer.localCache.values
            .firstOrNull {
                it.serverId.equals(serverId, true)
            } ?: return

        Bukkit.getPlayer(uniqueId)?.apply {
            Tasks.delayed(1L)
            {
                sendMessage("${CC.GREEN}You're now being sent to ${CC.YELLOW}${queue.fancyDisplayName}${CC.GREEN}.")

                BungeeUtil.sendToServer(this, serverId)
            }
        }
    }

    @Subscribe("queue-update-stream")
    fun onQueueUpdates(message: Message)
    {
        val queueId = UUID.fromString(message["queueId"])

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
}
