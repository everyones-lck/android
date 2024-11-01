package umc.everyones.lck.presentation.match.adapter

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.everyones.lck.R
import umc.everyones.lck.databinding.ItemLckMatchContentBinding
import umc.everyones.lck.domain.model.response.match.TodayMatchInformationModel
import umc.everyones.lck.domain.model.todayMatch.LckMatch
import umc.everyones.lck.util.extension.setOnSingleClickListener
import umc.everyones.lck.util.extension.toOrdinal

class LckMatchContentRVA(private val items: List<TodayMatchInformationModel.MatchResponsesModel>,
                         private val onPredictClick: (Long) -> Unit,
                         private val onPogClick: (Long) -> Unit
) :
    RecyclerView.Adapter<LckMatchContentRVA.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLckMatchContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemLckMatchContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TodayMatchInformationModel.MatchResponsesModel) {
            binding.tvTodayMatchLckMatch1Content.text = "${item.seasonInfo} ${item.matchNumber.toOrdinal()} Match"
            binding.tvTodayMatchLckMatch1Date.text = item.matchDate
            binding.tvTodayMatchLckMatchTeam1Name.text = item.team1Name
            binding.tvTodayMatchLckMatchTeam2Name.text = item.team2Name
            // Glide를 사용해 이미지 로드
            Glide.with(binding.root.context)
                .load(item.team1LogoUrl)
                .into(binding.ivTodayMatchLckMatchTeam1Logo)

            Glide.with(binding.root.context)
                .load(item.team2LogoUrl)
                .into(binding.ivTodayMatchLckMatchTeam2Logo)

            // 소수점 첫째 자리까지만 퍼센트 출력
            binding.tvTodayMatchTeam1Percent.text = String.format("%.1f%%", item.team1VoteRate)
            binding.tvTodayMatchTeam2Percent.text = String.format("%.1f%%", item.team2VoteRate)

            // 승부 예측하기 버튼 클릭 시, matchId 전달
            binding.tvTodayMatch1Prediction.setOnSingleClickListener {
                onPredictClick(item.matchId)
            }
            binding.tvTodayMatch1Pog.setOnSingleClickListener {
                onPogClick(item.matchId)
            }

            val context = binding.root.context
            val team1Color = ContextCompat.getColor(context, teamColorMap[item.team1Name] ?: R.color.white)
            val team2Color = ContextCompat.getColor(context, teamColorMap[item.team2Name] ?: R.color.black)

            // 팀 승률 텍스트 색상 설정
            binding.tvTodayMatchTeam1Percent.setTextColor(team1Color)
            binding.tvTodayMatchTeam2Percent.setTextColor(team2Color)
            // 팀 승률 바 색상 설정
            binding.tvTodayMatchTeam1Bar.backgroundTintList = ColorStateList.valueOf(team1Color)
            binding.tvTodayMatchTeam2Bar.backgroundTintList = ColorStateList.valueOf(team2Color)

            // 팀 승률에 따라 승률 바의 비율을 설정
            val team1WinRate = item.team1VoteRate / 100f
            val team2WinRate = item.team2VoteRate / 100f

            val params1 = binding.tvTodayMatchTeam1Bar.layoutParams as LinearLayout.LayoutParams
            params1.weight = team1WinRate.toFloat()
            binding.tvTodayMatchTeam1Bar.layoutParams = params1

            val params2 = binding.tvTodayMatchTeam2Bar.layoutParams as LinearLayout.LayoutParams
            params2.weight = team2WinRate.toFloat()
            binding.tvTodayMatchTeam2Bar.layoutParams = params2
        }
    }
    private val teamColorMap = mapOf(
        "Gen.G" to R.color.gen_g,
        "GEN" to R.color.gen_g,
        "HLE" to R.color.hanhwa,
        "DK" to R.color.dplus_kia,
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