package gg.scala.cougar.server.settings.impl

import gg.scala.cougar.server.settings.CougarSettingsModel
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class CougarPrioritySettings(
    val priorityToDuration: Map<Int, Long> =
        mutableMapOf(10 to TimeUnit.MINUTES.toMillis(10L))
) : CougarSettingsModel()
{
    override fun getAbstractType() = this::class.java
}
