package gg.scala.cougar.server.redis

import gg.scala.banana.credentials.BananaCredentials
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.server.settings.impl.CougarRedisSettings
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.store.connection.redis.impl.AuthDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails

/**
 * @author GrowlyX
 * @since 1/23/2022
 */
@Service
object ScCougarRedisEngine
{
    @Inject
    lateinit var redisConf: CougarRedisSettings

    lateinit var bananaWrapper: BananaCredentials
    lateinit var dsWrapper: DataStoreRedisConnectionDetails

    @Configure
    fun configure()
    {
        bananaWrapper = BananaCredentials(
            address = redisConf.address,
            port = redisConf.port,
            password = redisConf.password ?: ""
        )

        dsWrapper = DataStoreRedisConnectionDetails(
            redisConf.address, redisConf.port, redisConf.password
        )

        ScCougarServerMessageProducer.bananaConf = bananaWrapper
    }
}
