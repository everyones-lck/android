package umc.everyones.lck.presentation.community.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import umc.everyones.lck.databinding.SpinnerInnerViewBinding
import umc.everyones.lck.databinding.SpinnerOuterViewBinding


class SpinnerAdapter(context: Context, private val list: List<String>?) :
    BaseAdapter() {
    private val inflater: LayoutInflater

    // 스피너에서 선택된 아이템을 액티비티에서 꺼내오는 메서드
    private var item: String? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return list?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return list!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 화면에 들어왔을 때 보여지는 텍스트뷰 설정
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val binding = SpinnerOuterViewBinding.inflate(inflater, parent, false)
        if (convertView == null) convertView = binding.root
        if (list != null) {
            item = list[position]
            binding.tv.text = "$item "
        }
        return convertView
    }

    // 클릭 후 나타나는 텍스트뷰 설정
    @SuppressLint("SetTextI18n")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val binding = SpinnerInnerViewBinding.inflate(inflater, parent, false)
        if (convertView == null) convertView = binding.root
        if (list != null) {
            item = list[position]
            binding.spinnerInnerTv.text = "$item "
        }
        return convertView
    }
}
