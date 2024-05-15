package org.yasin.federationofconsciouscitizensmindmapgraph

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Citizen(val name: String, val age: Int)

class CitizenPreferences(context: Context) {

    private val PREFS_NAME = "citizen_prefs"
    private val CITIZENS_KEY = "citizens"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCitizens(citizens: List<Citizen>) {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(citizens)
        editor.putString(CITIZENS_KEY, json)
        editor.apply()
    }

    fun getCitizens(): List<Citizen> {
        val gson = Gson()
        val json = prefs.getString(CITIZENS_KEY, null)
        val type = object : TypeToken<List<Citizen>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveCitizens(jsonString: String) {
        val gson = Gson()
        val type = object : TypeToken<List<Citizen>>() {}.type
        val citizens: List<Citizen> = gson.fromJson(jsonString, type)

        val editor = prefs.edit()
        val json = gson.toJson(citizens)
        editor.putString(CITIZENS_KEY, json)
        editor.apply()
    }
}
