package gg.scala.cougar.server.settings.impl

import gg.scala.cougar.server.settings.CougarSettingsModel

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class CougarLanguageSettings(
    val queueJoinTimeout: String = "§cPlease wait a moment before joining another queue.",
) : CougarSettingsModel()
{
    override fun getAbstractType() = this::class.java
}
