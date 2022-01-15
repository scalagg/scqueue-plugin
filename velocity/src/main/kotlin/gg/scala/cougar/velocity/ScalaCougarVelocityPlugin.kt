package gg.scala.cougar.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.instance.ScCougarInstance
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
class ScalaCougarVelocityPlugin
@Inject
constructor(
    val server: ProxyServer,
    val logger: Logger,
    @DataDirectory
    private val directory: Path
) : ScCougarPlatform()
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<ScalaCougarVelocityPlugin>()
    }

    val directoryFile: File = directory.toFile().also {
        if (!it.exists())
        {
            it.mkdirs()
        }
    }

    init
    {
        INSTANCE = this
    }

    @Subscribe(order = PostOrder.LAST)
    fun onProxyInit(event: ProxyInitializeEvent)
    {
        configure()

        server.eventManager.register(
            this, ScalaCougarVelocityListener
        )
    }

    override fun updateLocalInstance()
    {
        TODO("Not yet implemented")
    }

    override fun getLocalInstance(): ScCougarInstance
    {
        TODO("Not yet implemented")
    }

    override fun shouldTrackInstance() = false
}
