package gg.scala.cougar.server.settings.impl

import gg.scala.cougar.server.settings.CougarSettingsModel

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class CougarRedisSettings(
    val address: String = "127.0.0.1",
    val port: Int = 6379,
    val password: String? = null
) : CougarSettingsModel()
{
    override fun getAbstractType() = this::class.java
}
