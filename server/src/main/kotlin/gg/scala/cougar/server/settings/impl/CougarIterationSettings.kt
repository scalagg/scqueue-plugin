package gg.scala.cougar.server.settings.impl

import gg.scala.cougar.server.settings.CougarSettingsModel

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class CougarIterationSettings(
    val iterationDelay: Long = 250L,
    val updateStreamIterationDelay: Long = 5000L,
) : CougarSettingsModel()
{
    override fun getAbstractType() = this::class.java
}
