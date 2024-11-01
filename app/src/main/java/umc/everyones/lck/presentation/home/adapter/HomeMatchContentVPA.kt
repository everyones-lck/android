package umc.everyones.lck.presentation.home.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.everyones.lck.R
import umc.everyones.lck.databinding.ItemHomeMatchContentBinding
import umc.everyones.lck.domain.model.response.home.HomeTodayMatchModel
import umc.everyones.lck.domain.model.todayMatch.LckMatch
import umc.everyones.lck.util.extension.setOnSingleClickListener
import umc.everyones.lck.util.extension.toOrdinal

class HomeMatchContentVPA(private val items: List<HomeTodayMatchModel.TodayMatchesModel>,
                          private val onClick: () -> Unit):
    RecyclerView.Adapter<HomeMatchContentVPA.HomeMatchContentViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeMatchContentVPA.HomeMatchContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeMatchContentBinding.inflate(inflater, parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return HomeMatchContentViewHolder(binding)
    }
    override fun onBindViewHolder(holder: HomeMatchContentViewHolder, position: Int) {
        if (items.isEmpty()) {
            holder.bindNoMatch()
        } else {
            holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size // 아이템이 없으면 1개의 'No Match' 뷰를 보여줌
    }

    inner class HomeMatchContentViewHolder(private val binding: ItemHomeMatchContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeTodayMatchModel.TodayMatchesModel) {
            binding.layoutHomeNoMatch.visibility = View.GONE
            binding.layoutHomeMatchContainer.visibility = View.VISIBLE

            binding.tvHomeTodayMatchContent.text = "${item.seasonInfo} ${item.matchNumber.toOrdinal()} Match"
            binding.tvHomeTodayMatchDate.text = item.matchDate

            Glide.with(binding.root.context)
                .load(item.team1LogoUrl)
                .into(binding.ivHomeTodayMatchLogo1)
            Glide.with(binding.root.context)
                .load(item.team2LogoUrl)
                .into(binding.ivHomeTodayMatchLogo2)

            binding.tvHomeTodayMatchTeam1.text = item.team1Name
            binding.tvHomeTodayMatchTeam2.text = item.team2Name

            val context = binding.root.context
            // 팀 색이 정해지지 않았을 때 디폴트 값 설정
            val team1Color = ContextCompat.getColor(context, teamColorMap[item.team1Name] ?: R.color.t1)
            val team2Color = ContextCompat.getColor(context, teamColorMap[item.team2Name] ?: R.color.gray_indicator)
            binding.ivHomeTodayMatchBar1.backgroundTintList = ColorStateList.valueOf(team1Color)
            binding.ivHomeTodayMatchBar2.backgroundTintList = ColorStateList.valueOf(team2Color)

            itemView.setOnSingleClickListener {
                onClick()
            }
        }

        fun bindNoMatch() {
            binding.layoutHomeNoMatch.visibility = View.VISIBLE
            binding.layoutHomeMatchContainer.visibility = View.GONE

            itemView.setOnSingleClickListener {
                onClick()
            }
        }

    }
    private val teamColorMap = mapOf(
        "Gen.G" to R.color.gen_g,
        "GEN" to R.color.gen_g,
        "HLE" to R.color.hanhwa,
        "DK" to R.color.gray_indicator,
        "T1" to R.color.t1,
        "KT" to R.color.kt_rolster,
        "KDF" to R.color.kwangdong_freecs,
        "BNK" to R.color.bnk,
        "NS" to R.color.ns,
        "DRX" to R.color.drx,
        "BRO" to R.color.ok_brion
        // 추가적인 팀과 색상 매핑
    )
}