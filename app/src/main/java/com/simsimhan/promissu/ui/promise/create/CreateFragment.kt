package com.simsimhan.promissu.ui.promise.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.FragmentCreatePromise1Binding
import com.simsimhan.promissu.databinding.FragmentCreatePromise2Binding
import com.simsimhan.promissu.databinding.FragmentCreatePromise3Binding
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.ui.map.LocationSearchActivity
import com.simsimhan.promissu.util.NavigationUtil
import com.simsimhan.promissu.util.StringUtil
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_create_promise_1.view.*
import kotlinx.android.synthetic.main.fragment_create_promise_2.view.*
import kotlinx.android.synthetic.main.fragment_create_promise_3.view.*
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CreateFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private var pageKey: Int? = null
    private var response: Promise.Response? = null
    private var username: String? = null
    private var now: DateTime? = null
    private var startDateEditText: TextInputEditText? = null
    private var startTimeEditText: TextInputEditText? = null
    private var endDateEditText: TextInputEditText? = null
    private var endTimeEditText: TextInputEditText? = null
    private var promisePlace: TextInputEditText? = null
    private var x: Double = 0.toDouble()
    private var y: Double = 0.toDouble()
    private var locationText: String? = null
    private var startSelectedDate: DateTime? = null
    private var startSelectedDateTime: DateTime? = null
    private var endSelectedDate: DateTime? = null
    private var endSelectedDateTime: DateTime? = null
    private lateinit var binding: ViewDataBinding
    //    private lateinit var viewModel: CreateViewModel
    private val viewModel: CreateViewModel by sharedViewModel()

    companion object {
        fun newInstance(position: Int): Fragment {
            val fragment = CreateFragment()
            val args = Bundle()
            args.putInt("Page_key", position)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(position: Int, response: Promise.Response): Fragment {
            val fragment = CreateFragment()
            val args = Bundle()
            args.putInt("Page_key", position)
            args.putParcelable("Response", response)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            // get arguments and set here
            pageKey = arguments!!.getInt("Page_key")
            response = arguments!!.getParcelable("Response")
        }


        username = PromissuApplication.diskCache!!.userName
        now = DateTime()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        when (pageKey) {
            0 -> setupTitleView(inflater, container)
            1 -> setupWhenView(inflater, container)
            2 -> setupLocationView(inflater, container)
        }

        return binding.root
    }

    private fun setupTitleView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCreatePromise1Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
            eventListener = this@CreateFragment.viewModel
        }
        val question = binding.root.create_question1_text
        question.text = Html.fromHtml(getString(R.string.create_question_1, username))
        if (response != null) {
            binding.root.promise_title_edit_text.setText(response!!.title)
            viewModel.setTitle(response!!.title)
        }
    }

    private fun setupWhenView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCreatePromise2Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
            eventListener = this@CreateFragment.viewModel
        }

        val question = binding.root.create_question2_text
        question.text = Html.fromHtml(getString(R.string.create_question_2))
        startDateEditText = binding.root.promise_start_date.apply {
            setOnClickListener {
                val now = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog.newInstance(
                        this@CreateFragment,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH))
                now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.minDate = now
                datePickerDialog.showYearPickerFirst(true)
                datePickerDialog.show(fragmentManager!!, "StartDatePickerDialog")
            }
        }

        startTimeEditText = binding.root.promise_start_time_edit_text.apply {
            setOnClickListener {
                if (startSelectedDate == null) {
                    Toast.makeText(requireContext(), "시작 일자를 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val timePickerDialog = TimePickerDialog.newInstance(this@CreateFragment,
                            now!!.hourOfDay,
                            now!!.minuteOfHour,
                            true
                    )
                    timePickerDialog.show(fragmentManager!!, "StartTimePickerDialog")
                }
            }
        }

        endDateEditText = binding.root.promise_end_date.apply {
            setOnClickListener {
                if (startSelectedDateTime == null) {
                    Toast.makeText(requireContext(), "약속 시작시간을 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val now = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog.newInstance(
                            this@CreateFragment,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH))

                    val startCalendar = Calendar.getInstance()
                    startCalendar.set(startSelectedDate!!.year, startSelectedDate!!.monthOfYear - 1, startSelectedDate!!.dayOfMonth)
                    datePickerDialog.minDate = startCalendar
                    datePickerDialog.showYearPickerFirst(true)
                    datePickerDialog.show(fragmentManager!!, "EndDatePickerDialog")
                }
            }
        }

        endTimeEditText = binding.root.promise_end_time_edit_text.apply {
            setOnClickListener {
                if (endSelectedDate == null) {
                    Toast.makeText(requireContext(), "종료 일자를 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val timePickerDialog = TimePickerDialog.newInstance(this@CreateFragment,
                            now!!.hourOfDay,
                            now!!.minuteOfHour,
                            true
                    )
                    timePickerDialog.show(fragmentManager!!, "EndTimePickerDialog")
                }
            }
        }

        if (response != null) {
            startDateEditText!!.setText("${response!!.start_datetime.year + 1900}년 ${response!!.start_datetime.month + 1}월 ${response!!.start_datetime.date}일")
            startTimeEditText!!.setText("${StringUtil.addPaddingIfSingleDigit(response!!.start_datetime.hours)}:${StringUtil.addPaddingIfSingleDigit(response!!.start_datetime.minutes)}")
            endDateEditText!!.setText("${response!!.end_datetime.year + 1900}년 ${response!!.end_datetime.month + 1}월 ${response!!.end_datetime.date}일")
            endTimeEditText!!.setText("${StringUtil.addPaddingIfSingleDigit(response!!.end_datetime.hours)}:${StringUtil.addPaddingIfSingleDigit(response!!.end_datetime.minutes)}")
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
            val formattedstartDate = sdf.format(response!!.start_datetime)
            val formattedendDate = sdf.format(response!!.end_datetime)
            viewModel.setStartDateTime(formattedstartDate)
            viewModel.setEndDateTime(formattedendDate)
            startSelectedDate = now!!.withYear(response!!.start_datetime.year + 1900).withMonthOfYear(response!!.start_datetime.month + 1).withDayOfMonth(response!!.start_datetime.date)
            startSelectedDateTime = DateTime(response!!.start_datetime)
            endSelectedDate = now!!.withYear(response!!.end_datetime.year + 1900).withMonthOfYear(response!!.end_datetime.month + 1).withDayOfMonth(response!!.end_datetime.date)
            endSelectedDateTime = DateTime(response!!.end_datetime)

        }

    }

    private fun setupLocationView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCreatePromise3Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
            eventListener = this@CreateFragment.viewModel
        }
        val question = binding.root.create_question3_text
        question.text = Html.fromHtml(getString(R.string.create_question_3))

        promisePlace = binding.root.promise_location_edit_text.apply {
            setOnClickListener {
                val intent = Intent(activity, LocationSearchActivity::class.java)
                startActivityForResult(intent, NavigationUtil.REQUEST_MAP_SEARCH)
            }
        }

        if (response != null) {
            viewModel.setCreateInfo(response!!.location_lat, response!!.location_lon, response!!.location, response!!.location_name)
            setPromisePlace(response!!.location)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NavigationUtil.REQUEST_MAP_SEARCH) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val location = data.getStringExtra("location")
                val locationName = data.getStringExtra("locationName")
                val x = data.getDoubleExtra("x", 0.0)
                val y = data.getDoubleExtra("y", 0.0)
                viewModel.setCreateInfo(y, x, location, locationName)
                setPromisePlace(location)
            } else {
                Toast.makeText(context, "약속 장소를 선택해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setPromisePlace(placeText: String) {
        Timber.d("setPromisePlace(): $placeText")
        if (promisePlace != null) {
            this.locationText = placeText
            promisePlace!!.setText(placeText)
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (view!!.tag == "StartDatePickerDialog") {
            startSelectedDate = now!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            // get date shit shit and set
            if (startDateEditText != null) {
                startDateEditText!!.setText(year.toString() + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일")
            }
            endDateEditText!!.text = null
            endTimeEditText!!.text = null
            endSelectedDate = null
            endSelectedDateTime = null
            viewModel.setEndDateTime(null)

        } else {
            endSelectedDate = now!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            // get date shit shit and set
            if (endDateEditText != null) {
                endDateEditText!!.setText(year.toString() + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일")
            }
        }
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        if (view!!.tag == "StartTimePickerDialog") {
            startSelectedDateTime = now!!.withHourOfDay(hourOfDay)
                    .withMinuteOfHour(minute)
            if ((startSelectedDate!!.isEqual(now)) && ((now!!.hourOfDay + 1 > hourOfDay) || ((now!!.hourOfDay + 1 == hourOfDay) && (now!!.minuteOfHour >= minute)))) {
                Toast.makeText(requireContext(), "최소 1시간 이후로 설정해주세요", Toast.LENGTH_SHORT).show()
            } else {
                if (startTimeEditText != null) {
                    startTimeEditText!!.setText(StringUtil.addPaddingIfSingleDigit(hourOfDay) + ":" + StringUtil.addPaddingIfSingleDigit(minute))
                }
                val requestStartDateTime = startSelectedDate!!.withHourOfDay(startSelectedDateTime!!.hourOfDay).withMinuteOfHour(startSelectedDateTime!!.minuteOfHour).withSecondOfMinute(0).toDate()
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
                val formattedDate = sdf.format(requestStartDateTime)
                viewModel.setStartDateTime(formattedDate)
                // end 시간을 설정 후 start 변경 시 end 초기화
                endTimeEditText!!.text = null
                endDateEditText!!.text = null
                endSelectedDate = null
                endSelectedDateTime = null
                viewModel.setEndDateTime(null)
            }
        } else {
            endSelectedDateTime = now!!.withHourOfDay(hourOfDay)
                    .withMinuteOfHour(minute)
            if ((startSelectedDate == endSelectedDate) && (endSelectedDateTime!!.isBefore(startSelectedDateTime))) { //시작 종료일이 같고 종료시간이 시작시간보다 빠르다면
                Toast.makeText(requireContext(), "시작 시간보다 종료 시간이 늦도록 설정해주세요", Toast.LENGTH_SHORT).show()
            } else if ((startSelectedDate == endSelectedDate) && (Minutes.minutesBetween(startSelectedDateTime, endSelectedDateTime).minutes < 60)) {
                Toast.makeText(requireContext(), "약속시간이 적어도 1시간 이상이 되도록 설저해주세요", Toast.LENGTH_SHORT).show()
            } else {
                if (endTimeEditText != null) {
                    endTimeEditText!!.setText(StringUtil.addPaddingIfSingleDigit(hourOfDay) + ":" + StringUtil.addPaddingIfSingleDigit(minute))
                }
                val requestEndDateTime = endSelectedDate!!.withHourOfDay(endSelectedDateTime!!.hourOfDay).withMinuteOfHour(endSelectedDateTime!!.minuteOfHour).withSecondOfMinute(0).toDate()
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
                val formattedDate = sdf.format(requestEndDateTime)
                viewModel.setEndDateTime(formattedDate)
            }
        }
    }

}