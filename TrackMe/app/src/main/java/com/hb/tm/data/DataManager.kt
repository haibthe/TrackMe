package com.hb.tm.data

import com.hb.lib.data.IDataManager
import com.hb.tm.data.pref.PreferenceHelper

interface DataManager : IDataManager, PreferenceHelper {


    fun setData(data: String)

    fun getData(): String
}