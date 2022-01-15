package gg.scala.cougar.spigot.caching

import net.evilblock.cubed.store.uuidcache.impl.RedisUUIDCache

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
interface UuidCacheProvider
{
    fun getUuidCache(): RedisUUIDCache
}
