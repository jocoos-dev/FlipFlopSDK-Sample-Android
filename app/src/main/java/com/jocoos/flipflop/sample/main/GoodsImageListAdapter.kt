package com.jocoos.flipflop.sample.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.goods.GoodsItem
import com.jocoos.flipflop.sample.util.getDimensionSize
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

enum class GoodsShape { CIRCLE, ROUND }
enum class GoodsSize { MEDIUM, SMALL }

class GoodsImageListAdapter(
    private val goodsShape: GoodsShape = GoodsShape.ROUND,
    private val goodsSize: GoodsSize = GoodsSize.MEDIUM
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var goodsList: List<GoodsItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = when (goodsSize) {
            GoodsSize.MEDIUM -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_goods_list_medium, parent, false)
            }
            GoodsSize.SMALL -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_goods_list_small, parent, false)
            }
        }
        return ViewHolder(v, goodsShape, goodsSize)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(goodsList[position])
    }

    override fun getItemCount(): Int {
        return goodsList.size
    }

    class ViewHolder(
        itemView: View,
        private val goodsShape: GoodsShape = GoodsShape.ROUND,
        private val goodsSize: GoodsSize = GoodsSize.MEDIUM
    ) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.image_thumbnail)

        fun bind(goods: GoodsItem) {
            val radius = when (goodsSize) {
                GoodsSize.MEDIUM -> itemView.context.getDimensionSize(60)
                GoodsSize.SMALL -> itemView.context.getDimensionSize(40)
            }

            when (goodsShape) {
                GoodsShape.ROUND -> {
                    val round = itemView.context.getDimensionSize(4)

                    Picasso.with(itemView.context)
                        .load(goods.thumbnailUrl)
                        .placeholder(R.drawable.img_profile_default)
                        .transform(RoundedCornersTransformation(round, 0))
                        .resize(radius, radius)
                        .centerCrop()
                        .into(thumbnail)
                }
                GoodsShape.CIRCLE -> {
                    Picasso.with(itemView.context)
                        .load(goods.thumbnailUrl)
                        .placeholder(R.drawable.img_profile_default)
                        .transform(CropCircleTransformation())
                        .resize(radius, radius)
                        .centerCrop()
                        .into(thumbnail)
                }
            }
        }
    }
}