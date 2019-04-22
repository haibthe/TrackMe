package com.hb.tm.data.repository

import com.hb.tm.data.entity.DataWrapper
import io.reactivex.Observable

/**
 * Created by buihai on 9/9/17.
 */

interface SystemRepository {

    fun getDataTest(): Observable<List<DataWrapper<*>>>

}
