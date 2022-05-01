package gg.scala.cougar.server.redis

import gg.scala.aware.AwareHub
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.cougar.server.settings.impl.CougarRedisSettings
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 1/23/2022
 */
@Service
@IgnoreAutoScan
object ScCougarRedisEngine
{
    @Inject
    lateinit var redisConf: CougarRedisSettings

    @Configure
    fun configure()
    {
        AwareHub.configure(
            WrappedAwareUri(
                address = redisConf.address,
                port = redisConf.port,
                password = redisConf.password
            )
        ) {
            Serializers.gson
        }
    }
}
