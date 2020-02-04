package com.araya.arto.datas

import android.content.Context
import android.preference.PreferenceManager
import com.araya.arto.datas.databases.AppDatabases
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by el araya on 02/12/2019
 * adyranggahidayat@gmail.com
 * Copyright (c) 2019
 **/
class SharedPrefManager(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    var booleanField by PreferenceFieldDelegate.Boolean("boolean_field")
    var intField by PreferenceFieldDelegate.Int("int_field")
    var stringField by PreferenceFieldDelegate.String("string_field")

    //    for set limit outcome
    var limitOutcome by PreferenceFieldDelegate.Int("limit_outcome")

    private sealed class PreferenceFieldDelegate<T>(protected val key: kotlin.String) : ReadWriteProperty<SharedPrefManager, T> {

        class Boolean(key: kotlin.String) : PreferenceFieldDelegate<kotlin.Boolean>(key) {

            override fun getValue(thisRef: SharedPrefManager, property: KProperty<*>) = thisRef.preferences.getBoolean(key, false)

            override fun setValue(thisRef: SharedPrefManager, property: KProperty<*>, value: kotlin.Boolean) = thisRef.preferences.edit().putBoolean(key, value).apply()
        }

        class Int(key: kotlin.String) : PreferenceFieldDelegate<kotlin.Int>(key) {

            override fun getValue(thisRef: SharedPrefManager, property: KProperty<*>) = thisRef.preferences.getInt(key, 0)

            override fun setValue(thisRef: SharedPrefManager, property: KProperty<*>, value: kotlin.Int) = thisRef.preferences.edit().putInt(key, value).apply()
        }

        class String(key: kotlin.String) : PreferenceFieldDelegate<kotlin.String>(key) {

            override fun getValue(thisRef: SharedPrefManager, property: KProperty<*>) = thisRef.preferences.getString(key, "")

            override fun setValue(thisRef: SharedPrefManager, property: KProperty<*>, value: kotlin.String) = thisRef.preferences.edit().putString(key, value).apply()
        }
    }
}