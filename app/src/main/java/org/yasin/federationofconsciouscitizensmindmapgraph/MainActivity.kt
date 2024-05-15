package org.yasin.federationofconsciouscitizensmindmapgraph

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.yasin.federationofconsciouscitizensmindmapgraph.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // стираем предыдущий лог
        //val fos = this.openFileOutput("log.txt", Context.MODE_PRIVATE)

        log(this, "Запущена MainActivity onCreate()")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Переходим на следующий экран", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            val currentFragmentId = navController.currentDestination?.id

            when (currentFragmentId) {
                R.id.MainFragment -> navController.navigate(R.id.action_MainFragment_to_SettingsFragment)
                R.id.SettingsFragment -> navController.navigate(R.id.action_SettingsFragment_to_FirstFragment)
                R.id.FirstFragment -> navController.navigate(R.id.action_FirstFragment_to_MainFragment)
                R.id.SecondFragment -> navController.navigate(R.id.action_SecondFragment_to_MainFragment)
                R.id.cardViewScrollingFragment -> navController.navigate(R.id.action_cardViewScrollingFragment_to_FirstFragment)
                else -> navController.navigate(R.id.MainFragment)
            }


        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val myCustomView = binding.root.findViewById<MyCustomView>(R.id.my_custom_view)
        if (myCustomView != null) {
            myCustomView.invalidate()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_log -> {
                // Предположим, что у вас есть TextView для отображения логов
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                val currentFragmentId = navController.currentDestination?.id

//                if (currentFragmentId == R.id.FirstFragment) {
//                    navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
//                }


                when (currentFragmentId) {
                    R.id.MainFragment -> {
                        //navController.navigate(R.id.action_MainFragment_to_SettingsFragment)
                        //navController.navigate(R.id.action_SettingsFragment_to_FirstFragment)
                        navController.navigate(R.id.action_MainFragment_to_SecondFragment)
                    }

              /*      R.id.SettingsFragment -> {

                        navController.navigate(R.id.action_SettingsFragment_to_FirstFragment)
                        navController.navigate(R.id.action_global_cardViewScrollingFragment)
                        navController.navigate(R.id.action_MainFragment_to_SecondFragment)
                    }

                    R.id.FirstFragment -> {
                        navController.navigate(R.id.action_global_cardViewScrollingFragment)
                        navController.navigate(R.id.action_MainFragment_to_SecondFragment)
                    }*/

                    else -> navController.navigate(R.id.SecondFragment)
                }

               /* GlobalScope.launch(Dispatchers.Main) {
                    var textView: TextView? = null
                    while (textView == null) {
                        delay(1000) // Проверяем каждую секунду
                        textView = findViewById<TextView>(R.id.textview_second)
                    }
                    // Вызываем showLogs при выборе пункта меню "Настройки"
                    showLogs(this@MainActivity, textView)
                }*/

                @OptIn(DelicateCoroutinesApi::class)
                lifecycleScope.launch(Dispatchers.Main) {
                    var textView: TextView? = null
                    while (textView == null) {
                        delay(1000) // Проверяем каждую секунду
                        textView = findViewById<TextView>(R.id.textview_second)
                    }
                    // Вызываем showLogs при выборе пункта меню "Настройки"
                    showLogs(this@MainActivity, textView)
                }



                true
            }

            R.id.action_exit -> {
                //log(this, "exit")
                exitProcess(0)
                true
            }

            R.id.action_mindmap -> {
                // Предположим, что у вас есть TextView для отображения логов
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                val currentFragmentId = navController.currentDestination?.id

//                if (currentFragmentId == R.id.FirstFragment) {
//                    navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
//                }


                when (currentFragmentId) {
                    R.id.MainFragment -> {
                        navController.navigate(R.id.action_MainFragment_to_SettingsFragment)
                        navController.navigate(R.id.action_SettingsFragment_to_FirstFragment)
                       // navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }

                    R.id.SettingsFragment -> {

                        navController.navigate(R.id.action_SettingsFragment_to_FirstFragment)
                        //navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }

                    //R.id.FirstFragment -> navController.navigate(R.id.action_FirstFragment_to_SecondFragment)

                    else -> navController.navigate(R.id.FirstFragment)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    // Функция для чтения логов из файла и отображения их в TextView
    private fun showLogs(context: Context, textView: TextView) {
        try {
            val fis = context.openFileInput("log.txt")
            val isr = InputStreamReader(fis)
            val bufferedReader = BufferedReader(isr)
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                //stringBuilder.append(line).append("\n")
                stringBuilder.insert(0, line + "\n")
                line = bufferedReader.readLine()
            }
            textView.text = stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Функция для логирования
    private fun log(context: Context, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDateTime =
                LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            val dateTimeString = currentDateTime.format(formatter)

            val message_new = dateTimeString + " " + message
            println("Текущая дата и время: $dateTimeString")
            Log.d("Example", message_new) // Вывод в Logcat
            println(message_new) // Вывод в стандартную консоль

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
}