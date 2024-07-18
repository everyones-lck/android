package umc.everyones.lck.presentation.community

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentWritePostBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.community.adapter.MediaRVA
import umc.everyones.lck.presentation.community.adapter.SpinnerAdapter
import umc.everyones.lck.util.extension.showCustomSnackBar
import umc.everyones.lck.util.extension.validateMaxLength


class WritePostFragment : BaseFragment<FragmentWritePostBinding>(R.layout.fragment_write_post) {
    private val navigator by lazy {
        findNavController()
    }
    private val writePostViewModel: WritePostViewModel by activityViewModels()

    private var _mediaRVA: MediaRVA? = null
    private val mediaRVA get() = _mediaRVA
    override fun initObserver() {

    }

    override fun initView() {
        initMediaRVAdapter()
        initSpinnerAdapter()
        validatePostTitle()
        writeDone()

        binding.ivWriteClose.setOnClickListener {
            navigator.navigateUp()
        }
    }

    private fun validatePostTitle(){
        binding.etWriteTitle.validateMaxLength(20) { showCustomSnackBar("제목은 최대 20자까지 입력할 수 있어요") }
    }

    private fun initMediaRVAdapter() {
        _mediaRVA = MediaRVA { }
        binding.rvWriteMedia.adapter = mediaRVA
        mediaRVA?.submitList(listOf(Uri.EMPTY, Uri.EMPTY))
    }

    private fun initSpinnerAdapter() {
        binding.spinnerWriteCategory.adapter = SpinnerAdapter(requireContext(), spinnerList)
    }

    private fun writeDone() {
        binding.ivWriteDone.setOnClickListener {
            writePostViewModel.setSelectedCategory(binding.spinnerWriteCategory.selectedItem.toString())
            navigator.navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mediaRVA = null
    }

    companion object {
        private val spinnerList = listOf("잡담", "응원", "FA", "거래", "질문", "후기")
    }
}