package ng.com.zorochi.fpsgametracker.ui

import android.app.Application
import android.util.Log
import kotlin.system.exitProcess

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "Uncaught exception in thread ${thread.name}", throwable)
            // Perform any necessary cleanup here

            // Close the app gracefully
            exitProcess(2)
        }
    }
}