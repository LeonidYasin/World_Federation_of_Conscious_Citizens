package org.yasin.federationofconsciouscitizensmindmapgraph

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyApp : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: MyApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        // Функция для логирования
        fun log(context: Context, message: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                val dateTimeString = currentDateTime.format(formatter)

                val message_new = dateTimeString + " " + message
                println("Текущая дата и время: $dateTimeString")
                Log.d("Example", message_new) // Вывод в Logcat
                println(message_new) // Вывод в стандартную консоль

                //showToast(context,message)

                // Запись в файл лога
                try {
                    val fos = context.openFileOutput("log.txt", Context.MODE_APPEND)
                    fos.write("$message_new\n".toByteArray())
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }

        private fun showToast(context: Context, s: String) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
            }
        }

        // Функция для логирования
        fun log(message: String) {
            log(applicationContext(), message)
        }
    }


    private fun showToast(context: Context,message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
