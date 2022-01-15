package gg.scala.cougar.spigot.proprietary.caching.impl

import gg.scala.banana.Banana
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.options.BananaOptions
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryEngine
import gg.scala.cougar.spigot.proprietary.caching.UuidCacheProvider
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.store.uuidcache.impl.RedisUUIDCache

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
object DefaultUuidCacheProvider : UuidCacheProvider
{
    private val stdUuidCache by lazy {
        val banana = Banana(
            BananaCredentials(),
            BananaOptions("cubed:uuid_cache")
        )

        return@lazy RedisUUIDCache(banana)
            .also { it.load() }
    }

    override fun getUuidCache() = stdUuidCache
}
