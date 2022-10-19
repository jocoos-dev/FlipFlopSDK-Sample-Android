package com.jocoos.flipflop.sample.vod

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.jocoos.flipflop.sample.util.FFPlayerLifecycleWrapper
import com.jocoos.flipflop.sample.util.MainCoroutineScope
import com.jocoos.flipflop.sample.util.getDimensionSize
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.player_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * view vod
 */
class PlayerActivity : AppCompatActivity(), FFPlayerListener {
    private val scope: CoroutineScope = MainCoroutineScope(Job())
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

    private lateinit var video: Video
    private var goodsInfo: GoodsInfo? = null
    private val goodsImageListAdapter = GoodsImageListAdapter(GoodsShape.CIRCLE, GoodsSize.SMALL)

    private var player: FFPlayer? = null
    private var playerLifecycleWrapper: FFPlayerLifecycleWrapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)

        video = intent.getParcelableExtra(KEY_VIDEO)!!
        goodsInfo = intent.getParcelableExtra(KEY_GOODS_INFO)

        initView()
        initPlayer(video)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerLifecycleWrapper = null
        adapter?.unregisterAdapterDataObserver(adapterDataObserver)
        chatMessage.adapter = null
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        textDelete.setOnClickListener {
            showConfirmDialog()
        }

        goodsInfo?.let {
            if (it.goodsList.isNotEmpty()) {
                goodsList.layoutManager = LinearLayoutManager(this).apply {
                    orientation = RecyclerView.VERTICAL
                }
                goodsList.adapter = goodsImageListAdapter
                goodsImageListAdapter.goodsList = it.goodsList
            }
        }
    }

    private fun initPlayer(video: Video) {
        when (val result = FlipFlopSampleApp.flipFlopInstance?.getPlayer(this, video)) {
            is FFResult.Success -> {
                player = result.value.apply {
                    listener = this@PlayerActivity
                    prepare(this@PlayerActivity, playerView)
                }
            }
            is FFResult.Failure -> {
                Toast.makeText(this, "${result.error.code} ${result.error.message}", Toast.LENGTH_LONG).show()
                finish()
                return
            }
        }

        player?.let {
            playerLifecycleWrapper = FFPlayerLifecycleWrapper(lifecycle, it)
        }
    }

    private fun showConfirmDialog() {
        val dialog = AlertDialog.Builder(this).apply {
            setMessage("비디오를 삭제하시겠습니까?")
            setPositiveButton(android.R.string.yes) { _, _ ->
                scope.launch {
                    FlipFlopSampleApp.flipFlopInstance?.deleteVideo(video.videoKey)
                    finish()
                }
            }
            setNegativeButton(android.R.string.no) { _, _ -> }
        }
        dialog.show()
    }

    // interface FFPlayerListener
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
        Toast.makeText(this@PlayerActivity,
            "Finished broadcast",
            Toast.LENGTH_LONG).show()
    }

    override fun onSeek(player: FFPlayer, position: Long) {
        Log.i(FlipFlopSampleApp.TAG, "player onSeek")
        adapter?.removeAll()
    }

    override fun onChatMessageReceived(player: FFPlayer, message: FFMessage) {
        Log.i(FlipFlopSampleApp.TAG, "player onChatMessageReceived")
        adapter?.addItem(message)
    }

    override fun onChatStatReceived(player: FFPlayer, stat: FFStat) {
        Log.i(FlipFlopSampleApp.TAG, "player onChatStatReceived")
    }

    override fun onError(player: FFPlayer, error: FlipFlopException) {
        Log.i(FlipFlopSampleApp.TAG, "player onError")
        when (error.code) {
            FFErrorCode.ERROR_LIVE_NETWORK -> {
                Toast.makeText(this@PlayerActivity,
                    "네트워크가 원활하지 않습니다. 잠시 후 다시 시도해 주세요.",
                    Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this@PlayerActivity,
                    "${error.code} : ${error.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}