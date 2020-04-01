package studio.bz_soft.freightforwarder.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import studio.bz_soft.freightforwarder.data.http.ApiClient
import studio.bz_soft.freightforwarder.data.http.ApiClientInterface
import studio.bz_soft.freightforwarder.data.repository.LocalStorage
import studio.bz_soft.freightforwarder.data.repository.LocalStorageInterface
import studio.bz_soft.freightforwarder.data.repository.Repository
import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface
import studio.bz_soft.freightforwarder.root.App
import studio.bz_soft.freightforwarder.root.Constants.API_MAIN_URL
import studio.bz_soft.freightforwarder.ui.auth.AuthPresenter
import studio.bz_soft.freightforwarder.ui.profile.ProfilePresenter
import studio.bz_soft.freightforwarder.ui.root.RootController
import studio.bz_soft.freightforwarder.ui.route.RoutePresenter
import studio.bz_soft.freightforwarder.ui.route.store.AddStorePresenter

val applicationModule = module {
    single { androidApplication() as App }
}

val networkModule = module {
    single { ApiClient(API_MAIN_URL, androidContext()) as ApiClientInterface }
}

val storageModule = module {
    factory<SharedPreferences> { androidContext().getSharedPreferences("local_storage", Context.MODE_PRIVATE) }
    single<LocalStorageInterface> { LocalStorage(get()) }
    single<RepositoryInterface> { Repository(get(), get()) }
}

val presenterModule = module {
    single { AuthPresenter(get()) }
    single { RoutePresenter(get()) }
    single { AddStorePresenter(get()) }
    single { ProfilePresenter(get()) }
}

val controllerModule = module {
    single { RootController(get()) }
}

val navigationModule = module {  }