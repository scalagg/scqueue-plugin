package gg.scala.cougar.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
@Plugin(
    name = "cougar",
    authors = ["Scala"],
    id = "cougar-vlc",
    version = "1.0.2-exp",
    url = "https://scala.gg",
    dependencies = [
        Dependency(id = "store-velocity")
    ]
)
class ScalaCougarVelocityPlugin
@Inject
constructor(
    private val server: ProxyServer
)
{
    @Subscribe(order = PostOrder.LAST)
    fun onProxyInit(event: ProxyInitializeEvent)
    {
        this.server.eventManager.register(
            this, ScalaCougarVelocityListener
        )
    }
}
