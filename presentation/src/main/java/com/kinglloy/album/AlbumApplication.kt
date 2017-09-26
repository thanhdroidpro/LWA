package com.kinglloy.album

import android.app.Application
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.extensions.DelegatesExt
import com.kinglloy.album.injection.component.ApplicationComponent
import com.kinglloy.album.injection.component.DaggerApplicationComponent
import com.kinglloy.album.injection.modules.ApplicationModule

/**
 * @author jinyalin
 * @since 2017/9/26.
 */
class AlbumApplication : Application() {

    companion object {
        private val TAG = "StyleApplication"

        var instance: AlbumApplication by DelegatesExt.notNullSingleValue()
    }

    val applicationComponent: ApplicationComponent by lazy { initializeInjector() }

    override fun onCreate() {
        super.onCreate()
        instance = this

        applicationComponent.inject(this)

        resetExceptionHandler()

        Analytics.init(this)

//        if (BuildConfig.DEMO_MODE) {
//            Stetho.initialize(
//                    Stetho.newInitializerBuilder(this)
//                            .enableDumpapp(
//                                    Stetho.defaultDumperPluginsProvider(this))
//                            .enableWebKitInspector(
//                                    Stetho.defaultInspectorModulesProvider(this))
//                            .build())
//        }
    }

    private fun initializeInjector() = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()


    private fun resetExceptionHandler() {
        val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            LogUtil.F(TAG, "exception", e)
            exceptionHandler.uncaughtException(t, e)
        }
    }
}