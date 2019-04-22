package com.hb.tm.ui.splash

import android.annotation.SuppressLint
import com.hb.lib.mvp.impl.HBMvpPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by buihai on 7/13/17.
 */
@SuppressLint("CheckResult")
class SplashPresenter
@Inject constructor(
) : HBMvpPresenter<SplashActivity>(), SplashContract.Presenter {

    override fun loadData() {
        Observable.just(true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (isViewAttached()) {
                    getView().openMainActivity()
                }
            }, {
                if (isViewAttached()) {
                    getView().showErrorDialog("${it.message}")
                }
            })
    }



}
