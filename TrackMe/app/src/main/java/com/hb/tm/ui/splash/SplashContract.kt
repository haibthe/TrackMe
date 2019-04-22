package com.hb.tm.ui.splash

/**
 * Created by buihai on 7/13/17.
 */

interface SplashContract {

    interface View {
        fun loadData()

        fun openMainActivity()

        fun openTestActivity()

        fun showUpdateDialog(isForce: Boolean)

    }

    interface Presenter {

        fun loadData()



    }


}
