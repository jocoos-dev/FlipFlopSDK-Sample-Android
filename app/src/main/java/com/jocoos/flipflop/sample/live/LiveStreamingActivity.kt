package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jocoos.flipflop.*
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.FlipFlopSampleApp.Companion.STREAMING_RTMP
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.chatting.ChatAdapter
import com.jocoos.flipflop.sample.goods.GoodsInfo
import com.jocoos.flipflop.sample.main.GoodsImageListAdapter
import com.jocoos.flipflop.sample.main.GoodsShape
import com.jocoos.flipflop.sample.main.GoodsSize
import com.jocoos.flipflop.sample.main.MainFragment.Companion.KEY_GOODS_INFO
import com.jocoos.flipflop.sample.util.MainCoroutineScope
import kotlinx.android.synthetic.main.live_streaming_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * start live streaming
 */
class LiveStreamingActivity : AppCompatActivity(), FFStreamerListener {
    private var streamer: FFStreamer? = null
    private var isStarted = false
    private val scope: CoroutineScope = MainCoroutineScope(Job())

    private var isRTMP = false
    private var title: String = ""
    private var content: String = ""
    private var goodsInfo: GoodsInfo? = null
    private val goodsImageListAdapter = GoodsImageListAdapter(GoodsShape.CIRCLE, GoodsSize.SMALL)

    private var adapter: ChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.live_streaming_activity)

        isRTMP = intent.getBooleanExtra(STREAMING_RTMP, true)
        title = intent.getStringExtra(FlipFlopSampleApp.TITLE) ?: FlipFlopSampleApp.TITLE
        content = intent.getStringExtra(FlipFlopSampleApp.CONTENT) ?: FlipFlopSampleApp.CONTENT
        goodsInfo = intent.getParcelableExtra(KEY_GOODS_INFO)

        adapter = ChatAdapter()
        chatMessage.layoutManager = LinearLayoutManager(this)
        chatMessage.adapter = adapter
        adapter?.registerAdapterDataObserver(adapterDataObserver)

        goodsInfo?.let {
            if (it.goodsList.isNotEmpty()) {
                goodsList.layoutManager = LinearLayoutManager(this).apply {
                    orientation = RecyclerView.VERTICAL
                }
                goodsList.adapter = goodsImageListAdapter
                goodsImageListAdapter.goodsList = it.goodsList
            }
        }

        imageToggle.setOnClickListener {
            streamer?.switchCamera()
        }
        retry.setOnClickListener {
            retry.isVisible = false
            streamer?.retry()
        }
        startStreaming.setOnClickListener {
            toggleBroadcasting()
        }
        textTitle.text = title

        // get instance for live streaming
        if (isRTMP) {
            streamer = FlipFlopSampleApp.flipFlopInstance?.getStreamer()?.apply {
                listener = this@LiveStreamingActivity
                prepare(this@LiveStreamingActivity, liveView, FFStreamerConfig())
            }
        } else {
            streamer = FlipFlopSampleApp.flipFlopInstance?.createStreamer()?.apply {
                listener = this@LiveStreamingActivity
                prepare(this@LiveStreamingActivity, liveView, FFStreamerConfig())
            }
        }
    }

    override fun onStop() {
        super.onStop()
        streamer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        streamer?.reset()

        adapter?.unregisterAdapterDataObserver(adapterDataObserver)
        chatMessage.adapter = null
    }

    private fun toggleBroadcasting() {
        if (!isStarted) {
            if (streamer != null) {
                if (!streamer!!.isConnected()) {
                    streamer!!.start(title, content, goodsInfo)
                } else {
                    Snackbar.make(chatLayout, R.string.streaming_not_finished, Snackbar.LENGTH_LONG)
                        .show()
                }
            } else {
                Snackbar.make(chatLayout, R.string.oopps_shouldnt_happen, Snackbar.LENGTH_LONG)
                    .show()
            }
        } else {
            triggerStopRecording()
        }

    }
    private fun triggerStopRecording() {
        if (isStarted) {
            streamer?.stop()
        }

        scope.launch {
            streamer?.getVideoKey()?.let { videoKey ->
                when (val result = FlipFlopSampleApp.flipFlopInstance?.stopVideo(videoKey)) {
                    is FFResult.Success -> {
                        isStarted = false
                        finish()
                    }
                    is FFResult.Failure -> {
                        Snackbar.make(chatLayout, "failed to stop streaming", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)

            if (chatMessage.adapter != null) {
                chatMessage.smoothScrollToPosition(positionStart)
            }
        }
    }

    // FFStreamer2Listener
    override fun onPrepared() {
        Snackbar.make(chatLayout, "onPrepared", Snackbar.LENGTH_LONG).show()
    }

    override fun onStarted() {
        isStarted = true
        startStreaming.setText(R.string.stop_broadcasting)

        Snackbar.make(chatLayout, "Started", Snackbar.LENGTH_LONG).show()
    }

    override fun onStopped() {
        isStarted = false

        startStreaming.setText(R.string.start_broadcasting)
    }

    override fun onChatMessageReceived(item: FFMessage) {
        adapter?.addItem(item)
        if (item.messageType == FFMessageType.JOIN ||
            item.messageType == FFMessageType.LEAVE) {
            textViewerCount.text = item.participantCount.toString()
        }
    }

    override fun onError(error: FlipFlopException) {
        when (error.code) {
            FFErrorCode.ERROR_LIVE_CONNECT -> {
                Toast.makeText(this@LiveStreamingActivity,
                    "라이브 연결이 끊어졌습니다.",
                    Toast.LENGTH_LONG).show()
            }
            FFErrorCode.ERROR_CHAT_CONNECT -> {
                Toast.makeText(this@LiveStreamingActivity,
                    "채팅이 연결되지 않았습니다.",
                    Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this@LiveStreamingActivity,
                    error.message,
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}