package gg.scala.cougar.spigot.proprietary

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

        flavor.bind<ScalaCougarSpigotProprietaryPlugin>() to this
        flavor.startup()

        ScalaCougarSpigotProprietaryEngine.configure()
    }

    override fun disable()
    {
        ScalaCougarSpigotProprietaryEngine.close()
    }
}
