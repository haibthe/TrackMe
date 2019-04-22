package com.hb.tm.ui.detail

import com.hb.tm.data.entity.DataWrapper


interface DetailContract {
    interface View {
        fun updateData(data: DataWrapper<*>)

        fun showError(error: String)
    }

    interface Presenter {
        fun loadData()
    }
}