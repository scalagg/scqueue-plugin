package gg.scala.cougar.spigot.caching.impl

import gg.scala.cougar.spigot.caching.UuidCacheProvider
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.store.uuidcache.impl.RedisUUIDCache

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
class CubedUuidCacheProvider : UuidCacheProvider
{
    override fun getUuidCache() = Cubed.instance.uuidCache as RedisUUIDCache
}
