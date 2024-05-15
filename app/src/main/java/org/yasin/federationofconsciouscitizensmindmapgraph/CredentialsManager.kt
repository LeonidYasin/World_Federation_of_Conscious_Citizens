package org.yasin.federationofconsciouscitizensmindmapgraph


import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Secure
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.security.MessageDigest
import java.util.UUID


class CredentialsManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "MyApp",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )


    //точка старта
    suspend fun checkCredentials() {
        useApiKey()

        val login = sharedPreferences.getString("login", null)
        val password = sharedPreferences.getString("password", null)
        //showToast("checkCredentials login: $login")
        MyApp.log("checkCredentials login: $login")

        if (login == null ) {
            requestCredentials()
        } else {
            if (password != null) {
                sendCredentials(login, password)
            }
            else {
                //showToast("password = null")
                MyApp.log("password = null")
                requestPassword(login)
            }
        }
    }

    suspend fun requestListCitizens(login: String) {
        // Здесь должен быть ваш код для запроса учетных данных
        MyApp.log("requestListCitizens для запроса списка граждан")

        try {
            val sendMessageId = sendMessageToChat("$login :пришлите список граждан")

            MyApp.log("пробую getReplyMessageFromChat($sendMessageId)")
            getReplyMessageFromChat(sendMessageId)
        } catch (e: Exception) {
            MyApp.log("Exception: ${e.message}")
        }
    }

    fun saveCredentials(login: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("login", login)
        editor.putString("password", password)
        editor.apply()
    }

    suspend fun saveLogin(login: String)
    {
        val editor = sharedPreferences.edit()
        editor.putString("login", login)
        editor.apply()
        //showToast("saveLogin: $login")
        MyApp.log("saveLogin: $login")

        val password = sharedPreferences.getString("password", null)
        if (password == null) {
            requestPassword(login)
        }

    }


    fun getLogin(): String? {
        MyApp.log("getLogin()")
        return sharedPreferences.getString("login", null)

    }

    private fun saveGUID(guid: String)
    {
        val editor = sharedPreferences.edit()
        editor.putString("guid", guid)
        editor.apply()
        //showToast("записан новый guid")
        MyApp.log("записан новый guid")
    }
    fun getGUID(): String {
        MyApp.log("getGUID()")
        return sharedPreferences.getString("guid", null).toString()
    }



    fun createGuidFromAndroidId(context: Context, password: String): String {
        val androidId: String?
        androidId = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
        val saltedId = androidId + password
        val bytes = saltedId.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return UUID.nameUUIDFromBytes(digest).toString()
    }



    private suspend fun requestCredentials() {
        // Здесь должен быть ваш код для запроса учетных данных
        MyApp.log("Запущен код для запроса учетных данных")
        var guid: String
        guid = getGUID()
        if (guid=="null")
        {
            //guid = UUID.randomUUID().toString()
            guid = createGuidFromAndroidId(context, "solfedjo")
            saveGUID(guid)
        }
        val text = "GUID: $guid :жду id"
        val sendMessageId= sendMessageToChat(text);
        MyApp.log("Пытаемся запустить getReplyMessageFromChat() из requestCredentials() ")
        getReplyMessageFromChat(sendMessageId)
    }

    private suspend fun requestPassword(login: String) {
        // Здесь должен быть ваш код для запроса учетных данных
        MyApp.log("requestPassword ваш код для запроса пароля")


        //var sendMid = sendMessageToChat("$login :жду пароль")


        try {
            val sendMessageId = sendMessageToChat("$login :жду пароль")

            MyApp.log("пробую getReplyMessageFromChat($sendMessageId)")
            getReplyMessageFromChat(sendMessageId)
        } catch (e: Exception) {
            MyApp.log("Exception: ${e.message}")
        }
    }



    private suspend fun sendCredentials(login: String, password: String) {
        // Здесь должен быть ваш код для отправки учетных данных
        MyApp.log("код для отправки учетных данных")



        try {
            val sendMessageId = sendMessageToChat("Я - $login ,мой пароль: $password ")

            MyApp.log("пробую getReplyMessageFromChat($sendMessageId)")
            getReplyMessageFromChat(sendMessageId)
        } catch (e: Exception) {
            MyApp.log("Exception: ${e.message}")
        }
    }



    fun useApiKey() {
        val apiKey = BuildConfig.API_KEY
        println("API Key: $apiKey")
        MyApp.log("\"API Key: $apiKey\"")
    }


    var sendMessageId = 0  //id отправленного нами сообщения
    private fun sendMessageToChat() {
        var guid: String
        guid = getGUID()
        if (guid=="null")
        {
            //guid = UUID.randomUUID().toString()
            guid = createGuidFromAndroidId(context, "solfedjo")
            saveGUID(guid)
        }
        val botToken = BuildConfig.API_KEY
        val chatId = BuildConfig.API_KEY1
        val text = "GUID: $guid :жду id"
        val url = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$text"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                } else {
                    val responseBody = response.body?.string()
                    val jsonObject = responseBody?.let { JSONObject(it) }
                    val result = jsonObject?.getJSONObject("result")
                    sendMessageId = result?.getInt("message_id")!!

                    //showToast("GUID sent with message ID: $sendMessageId")
                    MyApp.log("GUID: $guid :жду id")
                    MyApp.log("GUID sent with message ID: $sendMessageId")
                }
            }
        })
    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val botToken = BuildConfig.API_KEY
    val chatId = BuildConfig.API_KEY1
    private  fun getMessageFromChat1(){


        val client = OkHttpClient()
        var lastUpdateId: String
        lastUpdateId = "0"





        Thread {
            var targetReplyMessageIdInt = 0
                    while (lastUpdateId!="null") {
                        try {
                            val targetMessageId = sendMessageId.toString() // Замените на ID сообщения, на которое вы хотите получать ответы

                            Thread.sleep(5000)
                            val url = "https://api.telegram.org/bot$botToken/getUpdates?offset=$lastUpdateId"
                            val request = Request.Builder()
                                .url(url)
                                .build()
                            val response = client.newCall(request).execute()
                            val responseBody = response.body?.string()

                            // Parse the response
                            val jsonElement = JsonParser.parseString(responseBody)
                            val jsonObject = jsonElement.asJsonObject
                            val resultArray = jsonObject.getAsJsonArray("result")


                            for (resultElement in resultArray) {
                                val resultObject = resultElement.asJsonObject
                                val message = resultObject.getAsJsonObject("message")
                                val replyToMessage = message.getAsJsonObject("reply_to_message")

                                // Проверяем, является ли это сообщение ответом на целевое сообщение
                                // Проверяем, является ли это сообщение ответом на целевое сообщение и имеет ли оно целевой ID ответного сообщения
                                val messageId = message.get("message_id").asString.toInt()

                                if (replyToMessage != null && replyToMessage.get("message_id").asString == targetMessageId
                                    && messageId >= targetReplyMessageIdInt) {

                                    val updateId = resultObject.get("update_id").asString
                                    val text = message.get("text").asString

                                    // Update 'lastUpdateId'
                                    lastUpdateId = updateId+1

                                    // Display the message in your app
                                    val displayText = text

                                    if (displayText.isNotEmpty()){
                                        //showToast("Received message: $displayText")
                                        MyApp.log("Received message: $displayText")
                                            // checkMessageAndDo(displayText)
                                        //lastUpdateId ="null"
                                        MyApp.log("message_id: $messageId")


                                            targetReplyMessageIdInt = messageId + 1
                                        MyApp.log("targetReplyMessageIdInt: $targetReplyMessageIdInt")


                                    }
                                }
                            }



                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }.start()

    }



    private suspend fun getMessageFromChat() = coroutineScope {
        //MyApp.log("GUID: $guid :жду id")
        MyApp.log("getMessageFromChat() запущен")
        val client = OkHttpClient()
        var lastUpdateId: String = "0"
        var targetReplyMessageIdInt = 0

        // Замените на ID сообщения, на которое вы хотите получать ответы

        val targetMessageId = sendMessageId.toString()


        launch(Dispatchers.IO) { // Запуск в фоновом потоке
            MyApp.log("launch(Dispatchers.IO) запущен")
            while (lastUpdateId != "null") {

                delay(5000)
                MyApp.log("getMessageFromChat() Пытаемся получить сообщение из чата")
                val url = "https://api.telegram.org/bot$botToken/getUpdates?offset=$lastUpdateId"
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Parse the response
                MyApp.log("Parse the response")
                val jsonElement = JsonParser.parseString(responseBody)
                val jsonObject = jsonElement.asJsonObject
                val resultArray = jsonObject.getAsJsonArray("result")

                for (resultElement in resultArray) {
                    val resultObject = resultElement.asJsonObject
                    val message = resultObject.getAsJsonObject("message")
                    val replyToMessage = message.getAsJsonObject("reply_to_message")

                    MyApp.log("Получено сообщение: $message \nпроверяем является ли оно ответом на наше")

                    // Проверяем, является ли это сообщение ответом на целевое сообщение
                    val messageId = message.get("message_id").asString.toInt()

                    if (replyToMessage != null && replyToMessage.get("message_id").asString == targetMessageId
                        && messageId >= targetReplyMessageIdInt) {

                        val updateId = resultObject.get("update_id").asString
                        val text = message.get("text").asString

                        // Update 'lastUpdateId'
                        lastUpdateId = (updateId.toInt() + 1).toString()

                        // Display the message in your app
                        val displayText = text

                        if (displayText.isNotEmpty()){
                            //showToast("Received message: $displayText")
                            MyApp.log("Received message: $displayText")
                            checkMessageAndDo(displayText)
                            MyApp.log("message_id: $messageId")

                            targetReplyMessageIdInt = messageId + 1
                            MyApp.log("targetReplyMessageIdInt: $targetReplyMessageIdInt")
                        }
                    }
                }
            }
        }
    }


    private suspend fun checkMessageAndDo(text: String?) {
        if (text != null && text.contains("Ваш новый логин:")) {
            // Извлечение нового логина
            val newLogin = text.substringAfter("Ваш новый логин:")
            //showToast("login: $newLogin")
            MyApp.log("login: $newLogin")
            saveLogin(newLogin)
        } else
            if (text != null && text.contains("Ваш новый пароль:")) {
                // Извлечение нового пароля
                val newPasword = text.substringAfter("Ваш новый пароль:")
                //showToast("login: $newPassword")
                MyApp.log("новый пароль: $newPasword")
                savePassword(newPasword)
            }
            else
                if (text != null && text.contains("Список граждан:")) {
                    // Извлечение списка граждан в виде строки json
                    val citizensJson = text.substringAfter("Список граждан:")

                    val citizenPreferences = CitizenPreferences(context)
                    citizenPreferences.saveCitizens(citizensJson)


                    //showToast("login: $newPassword")
                    MyApp.log("Получен список граждан: $citizensJson")
                    //savePassword(newPasword)
                }
    }

    private fun savePassword(newPassword: String) {
        MyApp.log("savePassword: $newPassword")

        val editor = sharedPreferences.edit()

        editor.putString("password", newPassword)
        editor.apply()

        val password = sharedPreferences.getString("password", null)

    }


    //var sendMessageId = 0  //id отправленного нами сообщения
    private  fun sendMessageToChat1(text: String) {
        var sendMessageId = 0
        val botToken = BuildConfig.API_KEY
        val chatId = BuildConfig.API_KEY1
        // text = "GUID: $guid :жду id"
        val url = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$text"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                } else {
                    val responseBody = response.body?.string()
                    val jsonObject = responseBody?.let { JSONObject(it) }
                    val result = jsonObject?.getJSONObject("result")
                    sendMessageId = result?.getInt("message_id")!!

                    //showToast("GUID sent with message ID: $sendMessageId")
                    //MyApp.log("login: $login :жду id")
                    MyApp.log("GUID sent with message ID: $sendMessageId")

                    //return $sendMessageId
                }
            }
        })
    }

    private suspend fun sendMessageToChat(text: String): Int = coroutineScope {
        MyApp.log("sendMessageToChat: $text")


        val url = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$text"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()


        MyApp.log("try client.newCall")
       val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
        if (!response.isSuccessful) {
            MyApp.log("!response.isSuccessful")
            throw IOException("Unexpected code $response")
        } else {
            MyApp.log("response.isSuccessful")
            val responseBody = response.body?.string()
            val jsonObject = responseBody?.let { JSONObject(it) }
            val result = jsonObject?.getJSONObject("result")
            val sendMessageId = result?.getInt("message_id")!!

            //showToast("Сообщение отправлено with message ID: $sendMessageId")
            //MyApp.log("GUID: $guid :жду id")
            MyApp.log("sent with message ID: $sendMessageId")

            return@coroutineScope sendMessageId
        }
    }





    private suspend fun getReplyMessageFromChat1(sendMessageId: Int) {






        //val url1 = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$text"



        val client = OkHttpClient()
        var lastUpdateId: String
        lastUpdateId = "0"





        Thread {
            var targetReplyMessageIdInt = 0
            while (lastUpdateId!="null") {
                try {
                    val targetMessageId = sendMessageId.toString() // Замените на ID сообщения, на которое вы хотите получать ответы

                    Thread.sleep(5000)
                    val url = "https://api.telegram.org/bot$botToken/getUpdates?offset=$lastUpdateId"
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    // Parse the response
                    val jsonElement = JsonParser.parseString(responseBody)
                    val jsonObject = jsonElement.asJsonObject
                    val resultArray = jsonObject.getAsJsonArray("result")


                    for (resultElement in resultArray) {
                        val resultObject = resultElement.asJsonObject
                        val message = resultObject.getAsJsonObject("message")
                        val replyToMessage = message.getAsJsonObject("reply_to_message")

                        // Проверяем, является ли это сообщение ответом на целевое сообщение
                        // Проверяем, является ли это сообщение ответом на целевое сообщение и имеет ли оно целевой ID ответного сообщения
                        val messageId = message.get("message_id").asString.toInt()

                        if (replyToMessage != null && replyToMessage.get("message_id").asString == targetMessageId
                            && messageId >= targetReplyMessageIdInt) {

                            val updateId = resultObject.get("update_id").asString
                            val text = message.get("text").asString

                            // Update 'lastUpdateId'
                            lastUpdateId = updateId+1

                            // Display the message in your app
                            val displayText = text

                            if (displayText.isNotEmpty()){
                                //showToast("Received message: $displayText")
                                MyApp.log("Received message: $displayText")
                                //checkMessageAndDo(displayText)
                                //lastUpdateId ="null"
                                MyApp.log("message_id: $messageId")


                                targetReplyMessageIdInt = messageId + 1
                                MyApp.log("targetReplyMessageIdInt: $targetReplyMessageIdInt")


                            }
                        }
                    }



                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()

    }

    private suspend fun getReplyMessageFromChat(sendMessageId: Int) = coroutineScope {
        val client = OkHttpClient()
        var lastUpdateId: String = "0"
        var targetReplyMessageIdInt = 0

        // Замените на ID сообщения, на которое вы хотите получать ответы

        val targetMessageId = sendMessageId.toString()


        launch(Dispatchers.IO) { // Запуск в фоновом потоке
            while (lastUpdateId != "null") {
                delay(5000)
                MyApp.log("try getReplyMessageFromChat()")
                val url = "https://api.telegram.org/bot$botToken/getUpdates?offset=$lastUpdateId"
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = withContext(Dispatchers.IO) { client.newCall(request).execute()}
                val responseBody = response.body?.string()

                // Parse the response
                val jsonElement = JsonParser.parseString(responseBody)
                val jsonObject = jsonElement.asJsonObject
                val resultArray = jsonObject.getAsJsonArray("result")

                for (resultElement in resultArray) {
                    val resultObject = resultElement.asJsonObject
                    val message = resultObject.getAsJsonObject("message")
                    val replyToMessage = message.getAsJsonObject("reply_to_message")

                    // Проверяем, является ли это сообщение ответом на целевое сообщение
                    val messageId = message.get("message_id").asString.toInt()

                    if (replyToMessage != null && replyToMessage.get("message_id").asString == targetMessageId
                        && messageId >= targetReplyMessageIdInt) {

                        val updateId = resultObject.get("update_id").asString
                        val text = message.get("text").asString

                        // Update 'lastUpdateId'
                        lastUpdateId = (updateId.toInt() + 1).toString()

                        // Display the message in your app
                        val displayText = text

                        if (displayText.isNotEmpty()){
                            //showToast("Received message: $displayText")
                            MyApp.log("Received message: $displayText")
                            checkMessageAndDo(displayText)
                            MyApp.log("message_id: $messageId")

                            targetReplyMessageIdInt = messageId + 1
                            MyApp.log("targetReplyMessageIdInt: $targetReplyMessageIdInt")
                        }
                    }
                }
            }
        }
    }


}



