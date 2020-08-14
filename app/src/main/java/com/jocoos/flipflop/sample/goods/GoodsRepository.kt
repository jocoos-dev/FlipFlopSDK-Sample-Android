package com.jocoos.flipflop.sample.goods

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GoodsItem(val id: Long, val title: String, val thumbnailUrl: String, val price: Int) :
    Parcelable

@Parcelize
class GoodsInfo(val goodsList: List<GoodsItem>) : Parcelable

/**
 * fake repository for goods
 */
class GoodsRepository {
    private val goodsList = listOf(
        GoodsItem(1, "무드 레시피 페이스 블러쉬", "https://shop-phinf.pstatic.net/20190115_111/skintoktalk_1547534269607gspMb_JPEG/70841429225391267_1684561795.jpg?type=m510", 11150),
        GoodsItem(2, "해피바스 한장 샤워티슈", "https://shop-phinf.pstatic.net/20190528_286/outrun_1559002838548CKVPz_JPEG/41721697397965005_925796858.jpg?type=m510", 4900),
        GoodsItem(3, "[밀리마쥬] 무료배송 베스풀 블러셔", "https://shop-phinf.pstatic.net/20180319_79/hcmnt01_1521432991594x1Rtm_JPEG/44740151216675877_466425841.jpg?type=m450", 12000),
        GoodsItem(4, "려 자양윤모 트리트먼트 200ml", "https://shop-phinf.pstatic.net/20190510_295/outrun_1557451917414UQYjN_JPEG/80758218041478328_924887128.jpg?type=m510", 5000),
        GoodsItem(5, "[3CE] 벨벳 립 틴트 4g", "https://shop-phinf.pstatic.net/20181228_46/skintoktalk_1545962390562vaoSw_JPEG/28593570204060548_313533979.jpg?type=m510", 9170)
    )

    fun getGoodsList(): List<GoodsItem> = goodsList

    fun getGoods(id: Long): GoodsItem? {
        return goodsList.firstOrNull { it.id == id }
    }
}