package com.owlsoft.shared.di

import com.owlsoft.shared.UUIDRepository
import com.owlsoft.shared.managers.EncounterManager
import com.owlsoft.shared.managers.EncounterManagerImpl
import com.owlsoft.shared.remote.EncounterAPI
import com.owlsoft.shared.remote.EncounterTurnAPI
import com.owlsoft.shared.usecases.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(koinAppDeclaration: KoinAppDeclaration) = startKoin {
    koinAppDeclaration()
    modules(PlatformModuleProvider.provide(), coreModule)
}

@ExperimentalCoroutinesApi
private val coreModule = module {
    single { createHttpClient() }

    single { EncounterAPI(get()) }
    single { EncounterTurnAPI() }

    single<EncounterManager> { EncounterManagerImpl() }
    single { UUIDRepository(get(), get()) }

    useCases()
}


private fun createHttpClient() = HttpClient {

    install(JsonFeature) {
        val json = kotlinx.serialization.json.Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
        serializer = KotlinxSerializer(json)
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}

private fun Module.useCases() {
    single { GetCurrentEncounterParticipantsUseCase(get()) }
    single { ObserveTimerTickUseCase(get()) }
    single { SkipTurnUseCase(get()) }
    single { ResetTimerUseCase(get()) }
    single { SetupEncounterUseCase(get(), get()) }
    single { CreateEncounterUseCase(get(), get()) }
    single { JoinEncounterUseCase(get(), get()) }
    single { ObserveRoundUseCase(get()) }
}

