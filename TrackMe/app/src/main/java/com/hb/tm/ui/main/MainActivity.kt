package com.hb.tm.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hb.lib.mvp.impl.lce.sr.HBMvpLceSRActivity
import com.hb.tm.R
import com.hb.tm.app.App
import com.hb.tm.data.entity.TrackingInfo
import com.hb.tm.navigation.Navigator
import com.hb.tm.ui.base.TrackingInfoViewHolder
import com.hb.uiwidget.recyclerview.BaseAdapter
import com.hb.uiwidget.recyclerview.OnItemClickListener

class MainActivity : HBMvpLceSRActivity<List<TrackingInfo>, MainPresenter>(), MainContract.View {

    override fun getResLayoutId(): Int {
        return R.layout.activity_main
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.bottom_navigation_view)
    lateinit var mBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        mBottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_tracking -> onTracking()
            }

            true
        }
    }


    @OnClick(R.id.fab_tracking)
    fun onTracking() {
        Navigator.startTracking(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MainContract.REQUEST_TRACKING -> {
                mPresenter.loadData(true)
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }

    }

    override fun setupRecylcerView(addItemDecoration: Boolean) {
        super.setupRecylcerView(false)
    }

    override fun createAdapter(context: Context, recyclerView: RecyclerView): RecyclerView.Adapter<*> {
        val adapter = MyAdapter(context, recyclerView)
        adapter.setOnItemClickListener(OnItemClickListener { anchor, obj, position ->

        })
        return adapter
    }

    override fun setData(data: List<TrackingInfo>) {
        val adapter = getAdapter<MyAdapter>()
        adapter.data = data
    }

    class MyViewHolder(itemView: View) : TrackingInfoViewHolder(itemView) {

        @BindView(R.id.iv_map_history)
        lateinit var mapHistory: ImageView

        @BindView(R.id.tv_speed_title)
        lateinit var speedTitle: TextView

        override fun bindData(data: TrackingInfo) {

            speedTitle.text = "Avg.Speed"

            updateSpeed(data.avgSpeed)
            updateDistance(data.distance)
            updateDuration(data.duration)

            App.imageHelper.loadImage(mapHistory, data.imageFile)
        }
    }

    class MyAdapter(context: Context, rv: RecyclerView) : BaseAdapter<List<TrackingInfo>, MyViewHolder>(context, rv) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = mInflater.inflate(R.layout.itemview_tracking_info, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val data = getItem<TrackingInfo>(position)!!
            holder.bindData(data)
        }

        override fun getItemCount(): Int {
            if (mData == null)
                return 0
            return mData!!.size
        }
    }
}
