package com.minhhop.easygolf

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.minhhop.easygolf.framework.appModule
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.services.PreferenceService
import com.testfairy.TestFairy
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.twitter.TwitterEmojiProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EasyGolfApplication : Application() {

    companion object {
        private const val NAME_DATABASE = "EASY_GOLF_DATABASE.realm"
        private const val VERSION_DATABASE: Long = 9
    }

    override fun onCreate() {
        super.onCreate()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        PreferenceService.getInstance().mPreferences = getSharedPreferences(PreferenceService.PREFS_NAME, Context.MODE_PRIVATE)
        //TODO check this for dark mode
        setTheme(R.style.ThemeAppDark)
        FirebaseApp.initializeApp(this)
        EmojiManager.install(TwitterEmojiProvider())

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        TestFairy.begin(this, getString(R.string.key_test_fairy))

        Realm.init(applicationContext)
        val realmConfig = RealmConfiguration.Builder()
                .name(NAME_DATABASE)
                .schemaVersion(VERSION_DATABASE)
                .deleteRealmIfMigrationNeeded()
//                .encryptionKey(buildKeyRealm())
                .build()
        Realm.setDefaultConfiguration(realmConfig)

        startKoin {
            androidContext(this@EasyGolfApplication)
            modules(appModule)
        }
    }

    private fun buildKeyRealm(): ByteArray {
        val key = Contains.encryptionKeyRealm()
        val byteKey = ByteArray(64)
        val result = key.toByteArray()
        val endIndex = 64.coerceAtMost(result.size)
        for (index in 0 until endIndex) {
            byteKey[index] = result[index]
        }
        return byteKey
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}