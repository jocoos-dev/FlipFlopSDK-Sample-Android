package com.jocoos.flipflop.sample.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jocoos.flipflop.FFResult
import com.jocoos.flipflop.FFVideoLoader
import com.jocoos.flipflop.api.model.VideoGoods
import com.jocoos.flipflop.api.model.VideoState
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.goods.GoodsInfo
import com.jocoos.flipflop.sample.live.LiveWatchingActivity
import com.jocoos.flipflop.sample.util.MainCoroutineScope
import com.jocoos.flipflop.sample.vod.PlayerActivity
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()

        const val KEY_VIDEO = "video"
        const val KEY_GOODS_INFO = "goods_info"
    }

    private val scope = MainCoroutineScope()

    private var videoListAdapter: VideoListAdapter? = null
    private var videoLoader: FFVideoLoader? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
        initVideoLoader()

        loadVideoList()
    }

    private fun initView() {
        swipeRefresh.setOnRefreshListener {
            loadVideoList()
        }

        val layoutManager = LinearLayoutManager(requireContext())
        videoListAdapter = VideoListAdapter().apply {
            setClickListener {
                when (it.video.state) {
                    VideoState.LIVE -> watchLive(it)
                    VideoState.VOD -> viewVod(it)
                    else -> {
                        Toast.makeText(context, "Not available at the moment. Please wait for a second.",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        videoList.layoutManager = layoutManager
        videoList.adapter = videoListAdapter
    }

    private fun initVideoLoader() {
        val config = FFVideoLoader.Config.Builder().count(20).build()
        videoLoader = FlipFlopSampleApp.flipFlopInstance?.getVideoLoader(config)
    }

    private fun loadVideoList() {
        if (videoLoader == null) {
            initVideoLoader()
        } else {
            videoLoader!!.reset()
        }

        scope.launch {
            // fetch video list
            when (val result = videoLoader!!.loadNextWithGoods(GoodsInfo::class.java)) {
                is FFResult.Success -> {
                    withContext(Dispatchers.Main) {
                        videoListAdapter?.run {
                            setItems(result.value)
                            notifyDataSetChanged()
                        }
                        swipeRefresh.isRefreshing = false
                    }
                }
                is FFResult.Failure -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun viewVod(videoGoods: VideoGoods<GoodsInfo>) {
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra(KEY_VIDEO, videoGoods.video)
        intent.putExtra(KEY_GOODS_INFO, videoGoods.goodsInfo)
        startActivity(intent)
    }

    private fun watchLive(videoGoods: VideoGoods<GoodsInfo>) {
        val intent = Intent(requireContext(), LiveWatchingActivity::class.java)
        intent.putExtra(KEY_VIDEO, videoGoods.video)
        intent.putExtra(KEY_GOODS_INFO, videoGoods.goodsInfo)
        startActivity(intent)
    }
}