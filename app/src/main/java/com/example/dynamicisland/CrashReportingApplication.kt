package com.example.dynamicisland

import android.app.Application
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class CrashReportingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching {
                val reportFile = File(filesDir, "last_crash.txt")
                val writer = StringWriter()
                throwable.printStackTrace(PrintWriter(writer))
                reportFile.writeText(
                    "Thread: ${thread.name}\n" +
                    "Exception: ${throwable.javaClass.name}\n\n" +
                    writer.toString()
                )
            }
            previousHandler?.uncaughtException(thread, throwable)
        }
    }
}
