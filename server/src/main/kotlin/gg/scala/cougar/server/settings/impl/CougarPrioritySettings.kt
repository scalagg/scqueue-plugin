package gg.scala.cougar.server.settings.impl

import gg.scala.cougar.server.settings.CougarSettingsModel

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class CougarPrioritySettings(
    val priorityToDuration: Map<Int, Long> =
        mutableMapOf(10 to 10L)
) : CougarSettingsModel()
{
    override fun getAbstractType() = this::class.java
}
