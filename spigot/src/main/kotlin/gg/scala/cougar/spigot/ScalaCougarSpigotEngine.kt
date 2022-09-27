package gg.scala.cougar.spigot

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
@IgnoreAutoScan
object ScalaCougarSpigotEngine : ScCougarPlatform()
{
    @Inject
    lateinit var plugin: ScalaCougarSpigotPlugin

    @Configure
    override fun configure()
    {
        super.setupLayers()
        super.configure()
    }

    override fun configureFlavor(): Boolean
    {
        return false
    }
}
