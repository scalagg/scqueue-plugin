package gg.scala.cougar.server.settings.impl

import gg.scala.cougar.server.settings.CougarSettingsModel

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class CougarLanguageSettings(
    val updateMessage: String = "\n§eYou're position §b{pos}§e/§b{max}§e in the §b{queue}§e queue.\n§7Ranks get a higher priority in queues, purchase them at: §f§ostore.verio.cc",
    val whitelistedAddition: String = "§cThe server you're queued for is currently whitelisted.",
    val offlineAddition: String = "§cThe server you're queued for is currently offline.",
) : CougarSettingsModel()
{
    override fun getAbstractType() = this::class.java
}
