package studio.bz_soft.freightforwarder.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import studio.bz_soft.freightforwarder.data.db.DbClient
import studio.bz_soft.freightforwarder.data.db.DbClientInterface
import studio.bz_soft.freightforwarder.data.http.ApiClient
import studio.bz_soft.freightforwarder.data.http.ApiClientInterface
import studio.bz_soft.freightforwarder.data.repository.*
import studio.bz_soft.freightforwarder.root.App
import studio.bz_soft.freightforwarder.root.Constants.API_MAIN_URL
import studio.bz_soft.freightforwarder.root.service.LocationController
import studio.bz_soft.freightforwarder.ui.auth.AuthPresenter
import studio.bz_soft.freightforwarder.ui.profile.ProfilePresenter
import studio.bz_soft.freightforwarder.ui.root.RootController
import studio.bz_soft.freightforwarder.ui.shift.WorkShiftPresenter
import studio.bz_soft.freightforwarder.ui.stores.StoresPresenter
import studio.bz_soft.freightforwarder.ui.stores.edit.EditStorePresenter
import studio.bz_soft.freightforwarder.ui.stores.image.ImagePresenter
import studio.bz_soft.freightforwarder.ui.stores.store.AddStorePresenter

val applicationModule = module {
    single { androidApplication() as App }
}

val networkModule = module {
    single { ApiClient(API_MAIN_URL, androidContext()) as ApiClientInterface }
}

val storageModule = module {
    factory<SharedPreferences> { androidContext().getSharedPreferences("local_storage", Context.MODE_PRIVATE) }
    single { DbClient(androidApplication()) as DbClientInterface }
    single<DatabaseRepositoryInterface> { DatabaseRepository(get()) }
    single<LocalStorageInterface> { LocalStorage(get()) }
    single<RepositoryInterface> { Repository(get(), get(), get()) }
}

val presenterModule = module {
    single { AuthPresenter(get()) }
    single { WorkShiftPresenter(get()) }
    single { StoresPresenter(get()) }
    single { AddStorePresenter(get()) }
    single { EditStorePresenter(get()) }
    single { ImagePresenter(get()) }
    single { ProfilePresenter(get()) }
}

val controllerModule = module {
    single { RootController(get()) }
    single { LocationController(get()) }
}

val navigationModule = module {  }