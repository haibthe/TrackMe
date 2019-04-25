package com.hb.tm.ui.main

import com.hb.lib.mvp.impl.lce.HBMvpLcePresenter
import com.hb.tm.data.repository.SystemRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainPresenter
@Inject constructor(
    var systemRepository: SystemRepository
) : HBMvpLcePresenter<MainActivity>(), MainContract.Presenter {


    override fun loadData(pullToRefresh: Boolean) {
        if (isViewAttached()) {
            getView().showLoading(pullToRefresh)
        }

        val dis = systemRepository.getAllTrackings()
            .map {
                it.forEach { ti ->
                    ti.generateImagePath(getView())
                }
                it
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (isViewAttached()) {
                    getView().apply {
                        setData(it)
                        showContent()
                    }
                }
            }, {
                if (isViewAttached()) {
                    getView().showError(it, pullToRefresh)
                }
            })

        disposable.clear()
        disposable.addAll(dis)
    }
}

