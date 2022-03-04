package gg.scala.cougar.server.settings

import com.google.common.io.Files
import gg.scala.cougar.server.ScCougarServerBootstrap
import gg.scala.cougar.server.settings.impl.*
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import java.io.File
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 12/15/2021
 */
@IgnoreAutoScan
@Service("Settings Processor")
object CougarSettingsProcessor
{
    val settings = mutableMapOf<KClass<*>, CougarSettingsModel>()

    @Configure
    fun configure()
    {
        Serializers.useGsonBuilderThenRebuild {
            it.setPrettyPrinting()
            it.registerTypeAdapter(
                CougarSettingsModel::class.java,
                AbstractTypeSerializer<CougarSettingsModel>()
            )
        }

        loadSettings<CougarLanguageSettings>("language")
        loadSettings<CougarQueueSettings>("queues")
        loadSettings<CougarIterationSettings>("iteration")
        loadSettings<CougarRedisSettings>("redis")
        loadSettings<CougarPrioritySettings>("priority")

        ScCougarServerBootstrap.flavor.bind<CougarLanguageSettings>().instance = locate<CougarLanguageSettings>()!!
        ScCougarServerBootstrap.flavor.bind<CougarQueueSettings>().instance = locate<CougarQueueSettings>()!!
        ScCougarServerBootstrap.flavor.bind<CougarIterationSettings>().instance = locate<CougarIterationSettings>()!!
        ScCougarServerBootstrap.flavor.bind<CougarRedisSettings>().instance = locate<CougarRedisSettings>()!!
        ScCougarServerBootstrap.flavor.bind<CougarPrioritySettings>().instance = locate<CougarPrioritySettings>()!!
    }

    inline fun <reified T> locate(): T?
    {
        return settings[T::class] as T
    }

    inline fun <reified T> loadSettings(id: String)
    {
        saveSettingsToFile(T::class, id)

        val reader = File("$id.json").reader()

        settings[T::class] = Serializers.gson
            .fromJson(reader, T::class.java) as CougarSettingsModel
    }

    fun saveSettingsToFile(
        kClass: KClass<*>, id: String
    )
    {
        val file = File("$id.json")

        if (!file.exists())
        {
            file.createNewFile()

            settings.putIfAbsent(
                kClass, kClass.java.newInstance() as CougarSettingsModel
            )
        }

        if (settings[kClass] != null)
        {
            Files.write(
                Serializers.gson.toJson(
                    settings[kClass], kClass.java
                ),
                file, Charsets.UTF_8
            )
        }
    }
}
