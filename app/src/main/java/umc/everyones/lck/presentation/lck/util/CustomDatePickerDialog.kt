package umc.everyones.lck.presentation.lck.util

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.FrameLayout
import umc.everyones.lck.R

class CustomDatePickerDialog(
    context: Context,
    private val year: Int,
    private val month: Int,
    private val dayOfMonth: Int,
    private val dateSelectedCallback: (Int, Int, Int) -> Unit // 콜백 함수 추가
) : DatePickerDialog(context, R.style.Widget_LCK_DatePicker, null, year, month, dayOfMonth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DatePickerDialog의 상단 헤더 영역을 숨김 처리
        try {
            val datePickerField = DatePickerDialog::class.java.getDeclaredField("mDatePicker")
            datePickerField.isAccessible = true
            val datePicker = datePickerField.get(this) as DatePicker
            val headerView = datePicker.findViewById<View>(
                Resources.getSystem().getIdentifier("date_picker_header", "id", "android")
            )
            val container = headerView.parent as ViewGroup
            container.removeView(headerView)

            val buttonsLayout = findViewById<View>(
                Resources.getSystem().getIdentifier("android:id/buttonPanel", null, null)
            )
            buttonsLayout?.visibility = View.GONE

            // DatePicker 외부 클릭 시 다이얼로그 종료
            setCanceledOnTouchOutside(true)

            // DatePicker에서 날짜 선택 시 다이얼로그 종료
            datePicker.init(year, month, dayOfMonth) { _, selectedYear, selectedMonth, selectedDay ->
                dateSelectedCallback(selectedYear, selectedMonth, selectedDay) // 콜백 호출
                dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
