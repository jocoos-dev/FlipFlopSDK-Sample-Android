package com.jocoos.flipflop.sample.main

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.api.model.VideoGoods
import com.jocoos.flipflop.api.model.VideoState
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.goods.GoodsInfo
import com.jocoos.flipflop.sample.util.formatDuration
import com.jocoos.flipflop.sample.util.getDimensionSize
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.text.SimpleDateFormat
import java.util.*

class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    private var items: List<VideoGoods<GoodsInfo>> = emptyList()
    private var listener: ((VideoGoods<GoodsInfo>) -> Unit)? = null

    fun setItems(videos: List<VideoGoods<GoodsInfo>>) {
        items = videos
    }

    fun setClickListener(listener: (VideoGoods<GoodsInfo>) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_video_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])

        holder.itemView.setOnClickListener {
            listener?.invoke(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail = itemView.findViewById<ImageView>(R.id.thumbnail)
        private val live = itemView.findViewById<TextView>(R.id.text_live)
        private val profileUrl = itemView.findViewById<ImageView>(R.id.profile_url)
        private val username = itemView.findViewById<TextView>(R.id.user_name)
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val content = itemView.findViewById<TextView>(R.id.content)
        private val viewCount = itemView.findViewById<TextView>(R.id.text_view)
        private val createdAt = itemView.findViewById<TextView>(R.id.created_at)
        private val goodsList = itemView.findViewById<RecyclerView>(R.id.goods_list)
        private val duration = itemView.findViewById<TextView>(R.id.duration)

        private val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.US)

        private val goodsImageListAdapter = GoodsImageListAdapter()

        init {
            goodsList.layoutManager = LinearLayoutManager(itemView.context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
            goodsList.adapter = goodsImageListAdapter
        }

        fun bind(videoGoods: VideoGoods<GoodsInfo>) {
            val video = videoGoods.video
            val goodsInfo = videoGoods.goodsInfo

            if (!TextUtils.isEmpty(video.thumbnailUrl)) {
                val width = itemView.context.getDimensionSize(120)
                val height = itemView.context.getDimensionSize(160)
                val round = itemView.context.getDimensionSize(4)

                Picasso.with(itemView.context)
                    .load(video.thumbnailUrl)
                    .placeholder(R.drawable.ic_camera)
                    .transform(RoundedCornersTransformation(round, 0))
                    .resize(width, height)
                    .centerCrop()
                    .into(thumbnail)
            } else {
                thumbnail.setImageResource(R.drawable.ic_camera)
            }

            if (video.userAvatarUrl != null && !TextUtils.isEmpty(video.userAvatarUrl)) {
                val radius = itemView.context.getDimensionSize(40)
                Picasso.with(itemView.context)
                    .load(video.userAvatarUrl)
                    .placeholder(R.drawable.img_profile_default)
                    .transform(CropCircleTransformation())
                    .resize(radius, radius)
                    .centerCrop()
                    .into(profileUrl)
            } else {
                profileUrl.setImageResource(R.drawable.img_profile_default)
            }

            if (video.state == VideoState.LIVE) {
                live.isVisible = true
                duration.isVisible = false
                viewCount.text = video.watchCount.toString()
            } else {
                live.isVisible = false
                duration.isVisible = true
                viewCount.text = video.viewCount.toString()
                duration.text = formatDuration(video.duration)
            }

            username.text = video.userName

            title.text = video.title
            content.text = video.content

            createdAt.text = dateFormat.format(video.createdAt)

            if (goodsInfo == null) {
                goodsImageListAdapter.goodsList = emptyList()
            } else {
                goodsImageListAdapter.goodsList = goodsInfo.goodsList
            }
            goodsImageListAdapter.notifyDataSetChanged()
        }
    }
}