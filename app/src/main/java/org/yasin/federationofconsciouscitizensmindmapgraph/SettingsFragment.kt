package org.yasin.federationofconsciouscitizensmindmapgraph

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var credentialsManager: CredentialsManager
    private lateinit var sharedPreferences: SharedPreferences

    // Метод вызывается при создании экрана настроек
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Загружаем настройки из XML-ресурса
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Проверяем учетные данные пользователя
        val credentialsManager = CredentialsManager(requireContext())
        //credentialsManager.checkCredentials()

        lifecycleScope.launch {
            credentialsManager.checkCredentials()
        }


        // Устанавливаем  "login"
        val loginPreference: EditTextPreference? = findPreference("login")
        // Находим PreferenceCategory по ключу и устанавливаем новый заголовок
        val category: PreferenceCategory? = findPreference("messages_category")


        val login = credentialsManager.sharedPreferences.getString("login", null)
        if (login != null) {
            loginPreference?.text = login

            category?.title = "Логин получен"
        }

        //отображаем пароль
        val passwordPreference: EditTextPreference? = findPreference("signature")
        val password = credentialsManager.sharedPreferences.getString("password", null)
        if (password != null) {
            passwordPreference?.text = password

        }

        // Инициализируем CredentialsManager и SharedPreferences
        //credentialsManager = CredentialsManager(requireContext())
        sharedPreferences = credentialsManager.sharedPreferences

        // Регистрируем слушателя изменений
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)


    }

    // Метод вызывается при возвращении фрагмента на передний план
    override fun onResume() {
        super.onResume()
        // Регистрируем слушателя изменений настроек
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        Log.d("SettingsFragment", "onResume: слушатель изменений настроек зарегистрирован")
        MyApp.log("SettingsFragment:onResume: слушатель изменений настроек зарегистрирован")
    }

    // Метод вызывается при уходе фрагмента с переднего плана
    override fun onPause() {
        MyApp.log("onPause()")
        super.onPause()
        // Отменяем регистрацию слушателя изменений настроек
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        Log.d("SettingsFragment", "onPause: слушатель изменений настроек отменен")
        MyApp.log("SettingsFragment:onPause: слушатель изменений настроек отменен")
    }

    // Метод вызывается при изменении любой настройки
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        MyApp.log("SettingsFragment:onSharedPreferenceChanged")
        //showToast("SettingsFragment:onSharedPreferenceChanged")
        if (key == "login" || key == "password") {
            //val loginPreference: EditTextPreference? = findPreference("login")
            if (sharedPreferences != null) {
                //loginPreference?.summary = sharedPreferences.getString(key, "")

                // Устанавливаем  "login"

                // Проверяем учетные данные пользователя
                val credentialsManager = CredentialsManager(requireContext())


                // Устанавливаем  "login"
                val loginPreference: EditTextPreference? = findPreference("login")
                // Находим PreferenceCategory по ключу и устанавливаем новый заголовок
                val category: PreferenceCategory? = findPreference("messages_category")


                val login = credentialsManager.sharedPreferences.getString("login", null)
                if (login != null) {
                    activity?.runOnUiThread {
                        loginPreference?.text = login
                        category?.title = "Логин получен"

                        showToast("Логин обновлён")

                        MyApp.log(
                            "login обновлён " +
                                    "на ${login}"
                        )

                    }
                }

                //отображаем пароль
                val passwordPreference: EditTextPreference? = findPreference("signature")
                val password = credentialsManager.sharedPreferences.getString("password", null)
                if (password != null) {
                    activity?.runOnUiThread {
                        passwordPreference?.text = password
                        showToast("Пароль обновлён")

                        MyApp.log(
                            "Пароль обновлён " +
                                    "на ${password}"
                        )
                    }
                }


            }
        }
    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        MyApp.log("onDestroy()")
        super.onDestroy()

        // Отменяем регистрацию слушателя при уничтожении фрагмента
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}

