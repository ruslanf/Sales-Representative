package studio.bz_soft.freightforwarder.di

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.Channel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import studio.bz_soft.freightforwarder.data.http.ApiClient
import studio.bz_soft.freightforwarder.data.http.ApiClientInterface
import studio.bz_soft.freightforwarder.data.repository.LocalStorage
import studio.bz_soft.freightforwarder.data.repository.LocalStorageInterface
import studio.bz_soft.freightforwarder.data.repository.Repository
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface
import studio.bz_soft.freightforwarder.root.App
import studio.bz_soft.freightforwarder.root.Constants.API_MAIN_URL
import studio.bz_soft.freightforwarder.ui.auth.AuthPresenter

val applicationModule = module {
    single { androidApplication() as App }
}

val networkModule = module {
    single {
        ApiClient(API_MAIN_URL, androidContext()) as ApiClientInterface
    }
}

val storageModule = module {
    factory<SharedPreferences> { androidContext().getSharedPreferences("local_storage", Context.MODE_PRIVATE) }
    single<LocalStorageInterface> { LocalStorage(get()) }
    single<RepositoryInterface> { Repository(get(), get()) }
}

val presenterModule = module {
    single { AuthPresenter(get()) }
}

val controllerModule = module {  }

val navigationModule = module {
    single { Cicerone.create() }
    single { get<Cicerone<Router>>().router as Router }
    single { get<Cicerone<Router>>().navigatorHolder as NavigatorHolder }
//    single { MainRouter(get(), Channel(Channel.UNLIMITED)).apply { start() } }
}