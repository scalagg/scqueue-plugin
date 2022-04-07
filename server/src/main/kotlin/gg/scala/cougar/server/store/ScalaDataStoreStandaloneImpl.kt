package gg.scala.cougar.server.store

import gg.scala.cougar.server.redis.ScCougarRedisEngine
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.AbstractDataStoreConnection
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.NoAuthDataStoreRedisConnection

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object ScalaDataStoreStandaloneImpl : ScalaDataStoreShared()
{
    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        return NoAuthDataStoreRedisConnection(
            ScCougarRedisEngine.dsWrapper
        )
    }

    override fun getNewMongoConnection(): AbstractDataStoreMongoConnection
    {
        return UriDataStoreMongoConnection(
            DataStoreMongoConnectionDetails()
        )
    }

    override fun debug(from: String, message: String)
    {
        AbstractDataStoreConnection.LOGGER.info("$from: $message")
    }
}
