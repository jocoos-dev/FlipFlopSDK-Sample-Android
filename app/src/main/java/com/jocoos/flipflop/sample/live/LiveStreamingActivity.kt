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
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.chatting.ChatAdapter
import com.jocoos.flipflop.sample.goods.GoodsInfo
import com.jocoos.flipflop.sample.main.GoodsImageListAdapter
import com.jocoos.flipflop.sample.main.GoodsShape
import com.jocoos.flipflop.sample.main.GoodsSize
import com.jocoos.flipflop.sample.main.MainFragment.Companion.KEY_GOODS_INFO
import kotlinx.android.synthetic.main.live_streaming_activity.*

/**
 * start live streaming
 */
class LiveStreamingActivity : AppCompatActivity(), FFStreamerListener {
    private var streamer: FFStreamer? = null
    private var isStarted = false

    private var title: String = ""
    private var content: String = ""
    private var goodsInfo: GoodsInfo? = null
    private val goodsImageListAdapter = GoodsImageListAdapter(GoodsShape.CIRCLE, GoodsSize.SMALL)

    private var adapter: ChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.live_streaming_activity)

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
        streamer = FlipFlopSampleApp.flipFlopInstance?.createStreamer(lifecycle)?.apply {
            listener = this@LiveStreamingActivity
            prepare(this@LiveStreamingActivity, liveView, StreamerConfig())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

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

        isStarted = false
        finish()
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
    }

    override fun onChatStatReceived(stat: FFStat) {
        val participantCountExceptMyself = stat.participantCount - 1
        textViewerCount.text = participantCountExceptMyself.toString()
    }

    override fun onError(error: FlipFlopException) {
        when (error.code) {
            FFErrorCode.ERROR_LIVE_USER_EXCEED -> {
                Toast.makeText(this@LiveStreamingActivity,
                    "라이브 제한에 걸려서 라이브를 진행할 수 없습니다. 관리자에게 문의 바랍니다: ${error.message}",
                    Toast.LENGTH_LONG).show()
            }
            FFErrorCode.ERROR_CHAT_DISCONNECT -> {
                // chatting error
                retry.isVisible = true
                Toast.makeText(this@LiveStreamingActivity,
                    "채팅이 끊겼습니다. 다시 시도해 보세요: ${error.message}",
                    Toast.LENGTH_LONG).show()
            }
            FFErrorCode.ERROR_CHAT_CONNECT -> {
                Toast.makeText(this@LiveStreamingActivity,
                    "채팅이 연결되지 않았습니다.나중에 다시 시도해 보세요: ${error.message}",
                    Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this@LiveStreamingActivity,
                    error.message,
                    Toast.LENGTH_LONG).show()
            }
        }

        Snackbar.make(chatLayout, "${error.code} - ${error.message}", Snackbar.LENGTH_LONG)
            .show()
    }
}