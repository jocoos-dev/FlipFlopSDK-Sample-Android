package com.jocoos.flipflop.sample.live

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.goods.GoodsItem
import com.jocoos.flipflop.sample.util.getDimensionSize
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class GoodsWithSelected(val item: GoodsItem, var isSelected: Boolean)

class GoodsListSelectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var goodsList: List<GoodsItem> = emptyList()
        set(value) {
            _goodsList = value.map {
                GoodsWithSelected(it, false)
            }
            field = value
        }
    var listener: ((GoodsItem, Boolean) -> Unit)? = null

    private var _goodsList: List<GoodsWithSelected> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goods_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(_goodsList[position])


        holder.itemView.setOnClickListener {
            listener?.invoke(_goodsList[position].item, !_goodsList[position].isSelected)
            _goodsList[position].isSelected = !_goodsList[position].isSelected
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return goodsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.image_thumbnail)
        private val title: TextView = itemView.findViewById(R.id.text_title)
        private val price: TextView = itemView.findViewById(R.id.text_price)

        fun bind(goods: GoodsWithSelected) {
            val radius = itemView.context.getDimensionSize(80)
            Picasso.with(itemView.context)
                .load(goods.item.thumbnailUrl)
                .placeholder(R.drawable.img_profile_default)
                .transform(CropCircleTransformation())
                .resize(radius, radius)
                .centerCrop()
                .into(thumbnail)
            title.text = goods.item.title
            price.text = "${goods.item.price.withComma()}원"

            if (goods.isSelected) {
                itemView.setBackgroundResource(R.drawable.round_violet_4)
            } else {
                itemView.setBackgroundResource(R.drawable.round_white50_4)
            }
        }
    }
}

fun Int.withComma(): String = String.format("%,d", this)