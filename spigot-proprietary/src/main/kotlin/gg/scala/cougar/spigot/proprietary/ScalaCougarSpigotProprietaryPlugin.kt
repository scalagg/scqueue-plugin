package gg.scala.cougar.spigot.proprietary

import gg.scala.cougar.spigot.proprietary.backbone.BackboneService
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import me.lucko.helper.plugin.ExtendedJavaPlugin

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class ScalaCougarSpigotProprietaryPlugin : ExtendedJavaPlugin()
{
    override fun enable()
    {
        val flavor = Flavor.create<ScalaCougarSpigotProprietaryPlugin>(
            FlavorOptions(logger)
        )

        ScalaCougarSpigotProprietaryEngine.configure()

        flavor.bind<ScalaCougarSpigotProprietaryPlugin>() to this
        flavor.startup()

        BackboneService.configure()
    }

    override fun disable()
    {
        ScalaCougarSpigotProprietaryEngine.close()
    }
}
