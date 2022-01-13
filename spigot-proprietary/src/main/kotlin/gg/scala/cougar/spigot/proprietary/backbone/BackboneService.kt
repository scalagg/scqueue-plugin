package gg.scala.cougar.spigot.proprietary.backbone

import com.google.gson.reflect.TypeToken
import gg.scala.banana.Banana
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.serializers.Serializers
import org.bukkit.Bukkit
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
object BackboneService
{
    val typeToken = object : TypeToken<Map<UUID, String>>() {}.type

    val banana = Banana(
        BananaCredentials(),
        BananaOptions(
            channel = "cougar:backbone",
            gson = Serializers.gson,
            executor = ScCougarServerMessageProducer.executor
        )
    )

    @Configure
    fun configure()
    {
        banana.registerClass(this)
        banana.subscribe()
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
