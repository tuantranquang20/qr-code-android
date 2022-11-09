package vn.app.qrcode.ui.studio.event

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.FORMAT_DATETIME_NO_SECOND_DISPLAY
import vn.app.qrcode.data.constant.FORMAT_DATE_DISPLAY
import vn.app.qrcode.data.constant.TIME_FORMAT
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.databinding.FragmentCreatorEventBinding
import vn.app.qrcode.utils.AppUtils.hideKeyboard

class EventFragment : BaseMVVMFragment<CommonEvent, FragmentCreatorEventBinding, EventViewModel>() {

    companion object {
        fun newInstance() = EventFragment()
    }

    lateinit var qrCodeTypeArg: QRCodeType
    private val args: EventFragmentArgs by navArgs()

    override val layoutId: Int
        get() = R.layout.fragment_creator_event
    override val viewModel: EventViewModel by viewModel()

    private val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrCodeTypeArg = args.qrCodeType
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewDataBinding.apply {
            qrCodeType = qrCodeTypeArg
            eventFragment = this@EventFragment
        }
        super.onViewCreated(view, savedInstanceState)
        setupViewEvent()
    }

    fun createQrCode() {
        viewDataBinding.apply {
            if (isEventFormValidate()) {
                val startTime =
                    formatDateTime(
                        editTextStartTime.text.toString(),
                        editTextStartDate.text.toString()
                    )
                val endTime =
                    formatDateTime(editTextEndTime.text.toString(), editTextEndDate.text.toString())
                viewModel.addNewQrCode(
                    type = qrCodeTypeArg,
                    eventName = editTextNameEvent.text.trim().toString(),
                    startTime = startTime,
                    endTime = endTime,
                    location = editTextLocation.text.trim().toString(),
                    description = editTextDescription.text.trim().toString()
                )

                findNavController().navigate(
                    EventFragmentDirections.actionEventFragmentToResultCreatorFragment(viewModel.eventResultCreator)
                )
            }
        }
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            editTextStartTime.setOnClickListener {
                showTimePickerDialog(
                    resources.getString(R.string.label_event_start_datetime),
                    resources.getString(R.string.tag_start_time_picker_dialog),
                    editTextStartTime
                )
            }

            editTextEndTime.setOnClickListener {
                showTimePickerDialog(
                    resources.getString(R.string.label_event_end_datetime),
                    resources.getString(R.string.tag_end_time_picker_dialog),
                    editTextEndTime
                )
            }

            editTextStartDate.setOnClickListener {
                showDatePickerDialog(
                    resources.getString(R.string.label_event_start_datetime),
                    resources.getString(R.string.tag_start_date_picker_dialog),
                    editTextStartDate
                )
            }

            editTextEndDate.setOnClickListener {
                showDatePickerDialog(
                    resources.getString(R.string.label_event_end_datetime),
                    resources.getString(R.string.tag_end_date_picker_dialog),
                    editTextEndDate
                )
            }
        }
    }

    fun onBack() {
        findNavController().popBackStack()
    }

    private fun isEventFormValidate(): Boolean {
        val isEventNameNotEmpty = isEventNameNotEmpty()
        val isStartTimeNotEmpty = isStartTimeNotEmpty() && isStartDateNotEmpty()
        val isEndTimeValidate = isEndTimeNoEmpty() && isEndDateNotEmpty()
        val isLocationNotEmpty = isLocationNotEmpty()
        val isDescriptionNotEmpty = isDescriptionNotEmpty()

        val isEndDateTimeValidate = if (isStartTimeNotEmpty && isEndTimeValidate) {
            isEndDateTimeValidate()
        } else {
            false
        }

        return isEventNameNotEmpty &&
                isEndDateTimeValidate &&
                isLocationNotEmpty &&
                isDescriptionNotEmpty
    }

    private fun isEndDateTimeValidate(): Boolean {
        viewDataBinding.apply {
            val startTime =
                formatDateTime(
                    editTextStartTime.text.toString(),
                    editTextStartDate.text.toString()
                )
            val endTime =
                formatDateTime(editTextEndTime.text.toString(), editTextEndDate.text.toString())
            if (startTime > endTime) {
                setErrorText(
                    viewDataBinding.tvErrorEndTime,
                    getString(R.string.txt_error_event_date_time)
                )
                return false
            }
            setErrorText(
                viewDataBinding.tvErrorEndTime,
                getString(R.string.txt_error_empty)
            )
            return true
        }
    }

    private fun isEventNameNotEmpty(): Boolean {
        val eventName = viewDataBinding.editTextNameEvent.text.trim().toString()
        if (eventName.isEmpty()) {
            setErrorText(
                viewDataBinding.tvErrorEventName,
                getString(R.string.txt_error_required)
            )
            return false
        }
        setErrorText(
            viewDataBinding.tvErrorEventName,
            getString(R.string.txt_error_empty)
        )
        return true
    }

    private fun isStartTimeNotEmpty(): Boolean {
        val eventName = viewDataBinding.editTextStartTime.text.trim().toString()
        if (eventName.isEmpty()) {
            setErrorText(
                viewDataBinding.tvErrorStartTime,
                getString(R.string.txt_error_required)
            )
            return false
        }
        setErrorText(
            viewDataBinding.tvErrorStartTime,
            getString(R.string.txt_error_empty)
        )
        return true
    }

    private fun isStartDateNotEmpty(): Boolean {
        val eventName = viewDataBinding.editTextStartDate.text.trim().toString()
        if (eventName.isEmpty()) {
            setErrorText(
                viewDataBinding.tvErrorStartTime,
                getString(R.string.txt_error_required)
            )
            return false
        }
        setErrorText(
            viewDataBinding.tvErrorStartTime,
            getString(R.string.txt_error_empty)
        )
        return true
    }

    private fun isEndTimeNoEmpty(): Boolean {
        val eventName = viewDataBinding.editTextEndTime.text.trim().toString()
        if (eventName.isEmpty()) {
            setErrorText(
                viewDataBinding.tvErrorEndTime,
                getString(R.string.txt_error_required)
            )
            return false
        }
        setErrorText(
            viewDataBinding.tvErrorEndTime,
            getString(R.string.txt_error_empty)
        )
        return true
    }

    private fun isEndDateNotEmpty(): Boolean {
        val eventName = viewDataBinding.editTextEndDate.text.trim().toString()
        if (eventName.isEmpty()) {
            setErrorText(
                viewDataBinding.tvErrorEndTime,
                getString(R.string.txt_error_required)
            )
            return false
        }
        setErrorText(
            viewDataBinding.tvErrorEndTime,
            getString(R.string.txt_error_empty)
        )
        return true
    }

    private fun isLocationNotEmpty(): Boolean {
        val eventName = viewDataBinding.editTextLocation.text.trim().toString()
        if (eventName.isEmpty()) {
            setErrorText(
                viewDataBinding.tvErrorLocation,
                getString(R.string.txt_error_required)
            )
            return false
        }
        setErrorText(
            viewDataBinding.tvErrorLocation,
            getString(R.string.txt_error_empty)
        )
        return true
    }

    private fun isDescriptionNotEmpty(): Boolean {
        val eventName = viewDataBinding.editTextDescription.text.trim().toString()
        if (eventName.isEmpty()) {
            setErrorText(
                viewDataBinding.tvErrorDescription,
                getString(R.string.txt_error_required)
            )
            return false
        }
        setErrorText(
            viewDataBinding.tvErrorDescription,
            getString(R.string.txt_error_empty)
        )
        return true
    }

    private fun formatDateTime(time: String, date: String): Long {
        return SimpleDateFormat(FORMAT_DATETIME_NO_SECOND_DISPLAY).parse("$time $date").time
    }

    private fun showTimePickerDialog(title: String, tag: String, timeTextView: AppCompatTextView) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(cal.get(Calendar.HOUR_OF_DAY))
            .setMinute(cal.get(Calendar.MINUTE))
            .setTitleText(title)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            timeTextView.text = SimpleDateFormat(TIME_FORMAT).format(cal.time)
        }
        timePicker.show(requireActivity().supportFragmentManager, tag)
    }

    private fun showDatePickerDialog(title: String, tag: String, dateTextView: AppCompatTextView) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            dateTextView.text = SimpleDateFormat(FORMAT_DATE_DISPLAY).format(it)
        }

        datePicker.show(requireActivity().supportFragmentManager, tag)
    }

    private fun setErrorText(textView: TextView, error: String) {
        if (error.isEmpty()) {
            textView.visibility = View.GONE
            textView.text = getString(R.string.txt_error_empty)
        } else {
            textView.visibility = View.VISIBLE
            textView.text = error
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}
