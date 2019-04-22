package com.hb.tm.data

import android.content.Context
import com.hb.tm.data.cache.ICache
import com.hb.tm.data.pref.PreferenceHelper


class AppDataManager
constructor(
    private val context: Context,
    private val pref: PreferenceHelper,
    private val cache: ICache
) : DataManager {

    companion object {
    }

    private var mData: String = ""


    override fun setData(data: String) {
        mData = data
    }

    override fun getData(): String {
        return mData
    }

}