package gg.scala.cougar.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
class ScalaCougarVelocityPlugin
@Inject
constructor(
    private val server: ProxyServer
)
{
    @Subscribe(order = PostOrder.LAST)
    fun onProxyInit(event: ProxyInitializeEvent)
    {
        server.eventManager.register(
            this, ScalaCougarVelocityListener
        )
    }
}
