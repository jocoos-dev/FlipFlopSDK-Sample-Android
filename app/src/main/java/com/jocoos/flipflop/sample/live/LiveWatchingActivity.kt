package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.*
import com.jocoos.flipflop.api.model.Video
import com.jocoos.flipflop.sample.FlipFlopSampleApp
import com.jocoos.flipflop.sample.FlipFlopSampleApp.Companion.KEY_VIDEO
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.chatting.ChatAdapter
import com.jocoos.flipflop.sample.goods.GoodsInfo
import com.jocoos.flipflop.sample.main.GoodsImageListAdapter
import com.jocoos.flipflop.sample.main.GoodsShape
import com.jocoos.flipflop.sample.main.GoodsSize
import com.jocoos.flipflop.sample.main.MainFragment.Companion.KEY_GOODS_INFO
import com.jocoos.flipflop.sample.util.getDimensionSize
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.live_watching_activity.*
import java.util.*

class LiveWatchingActivity : AppCompatActivity(), FFPlayerListener {
    private var player: FFPlayer? = null
    private lateinit var video: Video
    private var goodsInfo: GoodsInfo? = null
    private val goodsImageListAdapter = GoodsImageListAdapter(GoodsShape.CIRCLE, GoodsSize.SMALL)

    private var message: String = ""
    private var adapter: ChatAdapter? = null

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        @Override
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)

            if (chatMessage.adapter != null) {
                chatMessage.smoothScrollToPosition(positionStart)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_watching_activity)

        video = intent.getParcelableExtra(KEY_VIDEO)!!
        goodsInfo = intent.getParcelableExtra(KEY_GOODS_INFO)

        initView()
        initPlayer(video)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        adapter?.unregisterAdapterDataObserver(adapterDataObserver)
        chatMessage.adapter = null
    }

    private fun initView() {
        adapter = ChatAdapter().apply {
            registerAdapterDataObserver(adapterDataObserver)
        }
        chatMessage.layoutManager = LinearLayoutManager(this)
        chatMessage.adapter = adapter

        if (video.userAvatarUrl != null && !TextUtils.isEmpty(video.userAvatarUrl)) {
            val radius = getDimensionSize(40)
            Picasso.with(this)
                .load(video.userAvatarUrl)
                .placeholder(R.drawable.img_profile_default)
                .transform(CropCircleTransformation())
                .resize(radius, radius)
                .centerCrop()
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.img_profile_default)
        }
        textTitle.text = video.title
        textOwner.text = video.userName

        goodsInfo?.let {
            if (it.goodsList.isNotEmpty()) {
                goodsList.layoutManager = LinearLayoutManager(this).apply {
                    orientation = RecyclerView.VERTICAL
                }
                goodsList.adapter = goodsImageListAdapter
                goodsImageListAdapter.goodsList = it.goodsList
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                message = s.toString().trim()
                if (message.isEmpty()) {
                    sendButton.isEnabled = false
                    sendButton.setImageResource(R.drawable.ic_send_gray)
                } else {
                    sendButton.isEnabled = true
                    sendButton.setImageResource(R.drawable.ic_send_bluegreen)
                }
            }

        })

        sendButton.isEnabled = false
        sendButton.setOnClickListener {
            val item = FFMessage(
                "0",
                FFMessageType.MESSAGE,
                FlipFlopSampleApp.userManager.user.userId,
                FlipFlopSampleApp.userManager.user.username,
                FlipFlopSampleApp.userManager.user.profileUrl,
                message, "", "", null, Date()
            )
            player?.sendMessage(item)
            editText.setText("")
        }
    }

    private fun initPlayer(video: Video) {
        when (val result = FlipFlopSampleApp.flipFlopInstance?.createPlayer(this, video, lifecycle)) {
            is FFResult.Success -> {
                player = result.value.apply {
                    listener = this@LiveWatchingActivity
                    prepare(this@LiveWatchingActivity, playerPreview)
                }
            }
            is FFResult.Failure -> {
                Toast.makeText(this, "${result.error.code} ${result.error.message}", Toast.LENGTH_LONG).show()
                finish()
                return
            }
        }
    }

    override fun onPrepared(player: FFPlayer) {
        Log.i(FlipFlopSampleApp.TAG, "player onPrepared")
    }

    override fun onStarted(player: FFPlayer) {
        Log.i(FlipFlopSampleApp.TAG, "player onStarted")
    }

    override fun onStopped(player: FFPlayer) {
        Log.i(FlipFlopSampleApp.TAG, "player onStopped")
    }

    override fun onCompleted(player: FFPlayer) {
        Log.i(FlipFlopSampleApp.TAG, "player onCompleted")

        finish.isVisible = true
        editLayout.isVisible = false
        textViewer.isVisible = false
    }

    override fun onSeek(player: FFPlayer, position: Long) {
        // do nothing
    }

    override fun onChatMessageReceived(player: FFPlayer, message: FFMessage) {
        Log.i(FlipFlopSampleApp.TAG, "player onChatMessageReceived")
        adapter?.addItem(message)
    }

    override fun onChatStatReceived(player: FFPlayer, stat: FFStat) {
        textViewerCount.text = stat.participantCount.toString()
    }

    override fun onError(player: FFPlayer, error: FlipFlopException) {
        Log.i(FlipFlopSampleApp.TAG, "player onError")
    }
}