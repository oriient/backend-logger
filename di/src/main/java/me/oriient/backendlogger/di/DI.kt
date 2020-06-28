package me.oriient.backendlogger.di

import me.oriient.backendlogger.services.database.DatabaseService
import me.oriient.backendlogger.services.messages.MessagesRepository
import me.oriient.backendlogger.services.os.builddata.OSBuildData
import me.oriient.backendlogger.services.os.database.DatabaseDriverProvider
import me.oriient.backendlogger.services.os.instancecreator.InstanceCreator
import me.oriient.backendlogger.services.os.network.NetworkManager
import me.oriient.backendlogger.services.os.rest.RestProvider
import me.oriient.backendlogger.services.os.scheduler.Scheduler
import me.oriient.backendlogger.services.rest.RestDataSerializer
import me.oriient.backendlogger.services.rest.RestService
import me.oriient.backendlogger.utils.DIProvidable
import me.oriient.backendlogger.utils.builddata.BuildData
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module
import kotlin.reflect.KClass

interface DI {
    fun <T: DIProvidable> get(clazz: KClass<T>): T
    fun <T: DIProvidable> inject(clazz: KClass<T>): Lazy<T>
}

@Suppress("unused")
internal class DIImpl: DI {

    private val koin: Koin

    init {
        val instanceCreator = Class.forName("me.oriient.backendlogger.services.android.instancecreator.InstanceCreatorImpl").newInstance() as InstanceCreator

        val osBuildData = instanceCreator.createInstance("me.oriient.backendlogger.services.android.builddata.OSBuildDataImpl") as OSBuildData
        val buildData = instanceCreator.createInstance("me.oriient.backendlogger.utils.builddata.BuildDataImpl", osBuildData.isDebug) as BuildData
        val restProvider = instanceCreator.createInstance("me.oriient.backendlogger.services.android.rest.RestProviderImpl") as RestProvider

        val jsonSerializer: RestDataSerializer
        try {
            jsonSerializer = instanceCreator.createInstance(
                "me.oriient.backendlogger.services.serializer.json.JsonRestDataSerializer",
                restProvider
            ) as RestDataSerializer
        } catch (e: Exception) {

            throw MissingSerializerException()
        }

        val restService = instanceCreator.createInstance("me.oriient.backendlogger.services.rest.RestServiceImpl", restProvider) as RestService
        val databaseDriverProvider = instanceCreator.createInstance("me.oriient.backendlogger.services.android.database.DatabaseDriverProviderImpl") as DatabaseDriverProvider
        val databaseService = instanceCreator.createInstance("me.oriient.backendlogger.services.database.DatabaseServiceImpl", databaseDriverProvider) as DatabaseService
        val messagesRepository = instanceCreator.createInstance("me.oriient.backendlogger.services.messages.MessageRepositoryImpl", databaseService) as MessagesRepository
        val scheduler = instanceCreator.createInstance("me.oriient.backendlogger.services.android.scheduler.SchedulerImpl") as Scheduler
        val networkManager = instanceCreator.createInstance("me.oriient.backendlogger.services.android.network.NetworkManagerImpl") as NetworkManager

        val modules = module {
            single { buildData }
            single { messagesRepository }
            single { scheduler }
            single { restService }
            single { jsonSerializer }
            single { networkManager }
        }

        koin = KoinApplication
            .init()
            .modules(modules)
            .koin
    }

    override fun <T : DIProvidable> get(clazz: KClass<T>): T {
        return koin.get(clazz, null, null)
    }

    override fun <T : DIProvidable> inject(clazz: KClass<T>): Lazy<T> {
        @Suppress("RemoveExplicitTypeArguments")
        return lazy<T> { koin.get(clazz, null, null) }
    }
}

