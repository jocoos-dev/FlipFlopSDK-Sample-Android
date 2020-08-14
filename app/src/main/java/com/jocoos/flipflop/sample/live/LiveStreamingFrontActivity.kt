package com.jocoos.flipflop.sample.live

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.sample.FlipFlopSampleApp.Companion.CONTENT
import com.jocoos.flipflop.sample.FlipFlopSampleApp.Companion.TITLE
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.goods.GoodsItem
import com.jocoos.flipflop.sample.goods.GoodsRepository
import kotlinx.android.synthetic.main.live_streaming_front_activity.*

/**
 * Input title and content for live
 */
class LiveStreamingFrontActivity : AppCompatActivity() {
    private var title = TITLE
    private var content = CONTENT
    var permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    private val goodsRepository = GoodsRepository()
    private val goodsListAdapter = GoodsListSelectionAdapter()

    private var selectedGoods = mutableListOf<GoodsItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_streaming_front_activity)
        requestPermission(permissions)

        initView()

        loadGoods()
    }

    private fun initView() {
        editTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                title = s.toString().trim()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        editTitle.setText(title)

        editContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                content = s.toString().trim()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        editContent.setText(content)

        next.setOnClickListener {
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                showStreamer(title, content)
            }
        }

        // select goods
        val layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        goodsList.layoutManager = layoutManager
        goodsList.adapter = goodsListAdapter
        goodsListAdapter.listener = { item, isSelected ->
            if (isSelected) {
                selectedGoods.add(item)
            } else {
                selectedGoods.remove(item)
            }
        }
    }

    private fun loadGoods() {
        val goodsList = goodsRepository.getGoodsList()
        goodsListAdapter.goodsList = goodsList
        goodsListAdapter.notifyDataSetChanged()
    }

    private fun requestPermission(permissions: Array<String>) {
        var mustRequest = false
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                mustRequest = true
                break
            }
        }
        if (mustRequest) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) { // If request is cancelled, the result arrays are empty.
        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // permission was granted, yay! Do the
            // contacts-related task you need to do.
        } else { // permission denied, boo! Disable the
            // functionality that depends on this permission.
        }
        return
    }

    private fun showStreamer(title: String, content: String) {
        val intent = Intent(this, LiveStreamingActivity::class.java).apply {
            putExtra(TITLE, title)
            putExtra(CONTENT, content)
        }
        startActivity(intent)
        finish()
    }
}