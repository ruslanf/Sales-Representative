package studio.bz_soft.freightforwarder.root

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import studio.bz_soft.freightforwarder.di.*

class App : Application() {

    private lateinit var instance: App

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        instance = this
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(
                applicationModule,
                networkModule,
                storageModule,
                presenterModule,
                controllerModule,
                navigationModule
            ))
        }
    }
}