package com.hb.tm.ui.detail

import com.hb.lib.mvp.impl.HBMvpPresenter
import com.hb.tm.data.DataManager
import com.hb.tm.data.entity.DataWrapper
import com.hb.tm.data.repository.SystemRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class DetailPresenter
@Inject constructor(
    var systemRepository: SystemRepository
) : HBMvpPresenter<DetailActivity>(), DetailContract.Presenter {


    override fun loadData() {

        val dis = Observable.just(
            dataManager<DataManager>().getData()
        )
            .map {
                object : DataWrapper<String>(it) {
                    override fun getTitle(): String {
                        return getData()
                    }

                    override fun getSubtitle(): String {
                        return getData()
                    }

                    override fun getDescription(): String {
                        return getData()
                    }

                    override fun getIcon(): String {
                        return getData()
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (isViewAttached()) {
                    getView().updateData(it)
                }
            }, {
                if (isViewAttached()) {
                    getView().showError(it.message!!)
                }
            })

        disposable.addAll(dis)
    }
}