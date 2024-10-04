package com.airqualitycontrol.feature.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airqualitycontrol.R
import com.airqualitycontrol.common.base.view.BaseFragment
import com.airqualitycontrol.common.constans.DATE_FORMATTER_PATTERN
import com.airqualitycontrol.common.constans.DATE_PICKER_TAG
import com.airqualitycontrol.common.utils.GraphDateValidator
import com.airqualitycontrol.databinding.FragmentChartBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class ChartFragment: BaseFragment(), OnItemSelectedListener {

    @Inject
    lateinit var viewModel: ChartViewModel

    private val binding: FragmentChartBinding by viewBinding(CreateMethod.INFLATE)

    private val periods = arrayOf(5, 10, 30)
    private val periodsToSelect = arrayOf("5 days", "10 days", "30 days")
    private val viewByToSelect = arrayOf("View by days", "View by hours")

    private var isChooseByDays = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getAppComponent().inject(this)

        setUiLogic()
        observeData()

        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            binding.graphChooseDurationSpinner.id -> {
                viewModel.getAirQuality(periods[position])
            }

            binding.graphChooseViewBySpinner.id -> {
                when (position) {
                    0 -> {
                        onSelectViewByDays()
                    }

                    1 -> {
                        onSelectViewByHours()
                    }
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun observeData() {
        viewModel.airQualityList.observe(viewLifecycleOwner, ::onAirQualityUpdate)
        viewModel.temperatureList.observe(viewLifecycleOwner, ::onTemperatureUpdate)
        viewModel.humidityList.observe(viewLifecycleOwner, ::onHumidityUpdate)
        viewModel.dustConcentrationList.observe(viewLifecycleOwner, ::onDustConcentrationUpdate)
        viewModel.pressureList.observe(viewLifecycleOwner, ::onPressureUpdate)
        viewModel.error.observe(viewLifecycleOwner, ::onErrorMessage)
        viewModel.isLoading.observe(viewLifecycleOwner, ::showLoadingDialog)
        viewModel.uiEvent.observe(viewLifecycleOwner, ::onUiEvent)
    }

    private fun onUiEvent(uiEvent: ChartViewModel.UiEvent) {
        when (uiEvent) {
            ChartViewModel.UiEvent.OnConnectionError -> {
                onConnectionError()
            }

            ChartViewModel.UiEvent.OnUnexpectedError -> {
                onUnexpectedError()
            }
        }
    }

    private fun onAirQualityUpdate(airQualityList: List<Double?>) {
        binding.airQualityChart.setChartData(airQualityList, "Air quality, %", "%")
    }

    private fun onTemperatureUpdate(temperatureList: List<Double?>) {
        binding.temperatureChart.setChartData(temperatureList, "Temperature, °C", "°C")
    }

    private fun onHumidityUpdate(humidityList: List<Double?>) {
        binding.humidityChart.setChartData(humidityList, "Humidity, %", "%")
    }

    private fun onDustConcentrationUpdate(dustConcentrationList: List<Double?>) {
        binding.dustConcentrationChart.setChartData(dustConcentrationList, "Dust concentration, ug/m3", "ug/m3")
    }

    private fun onPressureUpdate(pressureList: List<Double?>) {
        binding.pressureChart.setChartData(pressureList, "Pressure, hPa", "hPa")
    }

    private fun setUiLogic() {
        binding.airQualityChart.setChartStyles()
        binding.dustConcentrationChart.setChartStyles()
        binding.humidityChart.setChartStyles()
        binding.temperatureChart.setChartStyles()
        binding.pressureChart.setChartStyles()

        binding.graphChooseDurationSpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, periodsToSelect).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }

            onItemSelectedListener = this@ChartFragment
        }

        binding.graphChooseViewBySpinner.apply {
            adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, viewByToSelect).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }

            onItemSelectedListener = this@ChartFragment
        }

        binding.graphChooseDateButton.setOnClickListener {
            chooseDate()
        }
    }

    private fun onSelectViewByDays() {
        binding.graphChooseDurationSpinner.visibility = View.VISIBLE
        binding.graphChooseDateButton.visibility = View.GONE

        isChooseByDays = true

        viewModel.getAirQuality(periods[0])

        binding.graphChooseDateButton.text = getString(R.string.button_choose_date)
    }

    private fun onSelectViewByHours() {
        binding.graphChooseDurationSpinner.visibility = View.GONE
        binding.graphChooseDateButton.visibility = View.VISIBLE

        isChooseByDays = false

        clearCharts()

        setDate(Date().time)
    }

    private fun BarChart.setChartStyles() {
        isAutoScaleMinMaxEnabled = true
        setBackgroundColor(resources.getColor(R.color.black))
        setNoDataTextColor(resources.getColor(R.color.white))
        isDragEnabled = true

        xAxis.apply {
            textColor = resources.getColor(R.color.white)
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 1f
        }

        axisLeft.textColor = resources.getColor(R.color.white)

        axisRight.isEnabled = false
        description.isEnabled = false

        legend.textColor = resources.getColor(R.color.white)
    }

    private fun BarChart.setChartData(inputData: List<Double?>, lable: String, valueLable: String) {
        clear()
        invalidate()
        data?.clearValues()

        val entryData = ArrayList<BarEntry>()
        val dates = viewModel.dates.value

        inputData.forEachIndexed { index, dust ->
            entryData.add(BarEntry(
                index.toFloat(),
                dust?.toFloat() ?: 0f
            ))
        }

        xAxis.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val dateFormatter = if (isChooseByDays) SimpleDateFormat("dd/MM", Locale.getDefault())
                else SimpleDateFormat("HH:mm", Locale.getDefault())

                if (value.roundToInt() < 0) {
                    return ""
                }

                dates?.let { dateList ->
                    val index: Int = if (value.roundToInt() >= dateList.size) {
                        dateList.size - 1
                    } else {
                        value.roundToInt()
                    }

                    return dateFormatter.format(dates[index])
                } ?: return "Unknown"
            }
        }

        xAxis.axisMinimum = -0.5f

        val inputDataSet = BarDataSet(entryData, lable).apply {
            valueFormatter = object: ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "$value $valueLable"
                }
            }

            setDataSetStyles()
        }

        data = BarData(inputDataSet)

        animateY(1000)
    }

    private fun BarDataSet.setDataSetStyles() {
        setColor(resources.getColor(R.color.green_700))
        valueTextColor = resources.getColor(R.color.white)
    }

    private fun setDate(dateMillis: Long) {
        val formatter = SimpleDateFormat(DATE_FORMATTER_PATTERN, Locale.getDefault())
        val date = Date(dateMillis)

        binding.graphChooseDateButton.text = formatter.format(date)

        viewModel.getAirQualityByDate(date)
    }

    private fun onConnectionError() {
        Snackbar
            .make(binding.root, getString(R.string.check_network), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.button_text_try_again)) {
                viewModel.tryAgain()
            }.show()
    }

    private fun onUnexpectedError() {
        Snackbar.make(binding.root,
            getString(R.string.unknown_error),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun chooseDate() {
        val calendarConstraints = CalendarConstraints
            .Builder()
            .setValidator(GraphDateValidator())

        MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.date_pick_graph_day_title))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(calendarConstraints.build())
            .setTheme(R.style.AirQualityControl_DatePickerThemeOverlay)
            .build()
            .apply {
                addOnPositiveButtonClickListener { dateMillis ->
                    setDate(dateMillis)
                }
            }.show(parentFragmentManager, DATE_PICKER_TAG)
    }

    private fun clearCharts() {
        binding.humidityChart.clear()
        binding.temperatureChart.clear()
        binding.airQualityChart.clear()
        binding.dustConcentrationChart.clear()
        binding.pressureChart.clear()
    }
}
