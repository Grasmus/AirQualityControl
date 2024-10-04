package com.airqualitycontrol.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airqualitycontrol.R
import com.airqualitycontrol.common.base.view.BaseFragment
import com.airqualitycontrol.common.constans.DATE_TIME_FORMATTER_PATTERN
import com.airqualitycontrol.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class HomeFragment: BaseFragment() {

    @Inject
    lateinit var viewModel: HomeViewModel

    private val binding: FragmentHomeBinding by viewBinding(CreateMethod.INFLATE)

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

    private fun observeData() {
        viewModel.temperature.observe(viewLifecycleOwner, ::onTemperatureUpdate)
        viewModel.humidity.observe(viewLifecycleOwner, ::onHumidityUpdate)
        viewModel.airQuality.observe(viewLifecycleOwner, ::onAirQualityUpdate)
        viewModel.dustConcentration.observe(viewLifecycleOwner, ::onDustConcentrationUpdate)
        viewModel.pressureInHPA.observe(viewLifecycleOwner, ::onPressureUpdate)
        viewModel.gasLeaks.observe(viewLifecycleOwner, ::onGasLeaksUpdate)
        viewModel.date.observe(viewLifecycleOwner, ::onDateUpdate)
        viewModel.error.observe(viewLifecycleOwner, ::onErrorMessage)
        viewModel.isLoading.observe(viewLifecycleOwner, ::showLoadingDialog)
        viewModel.uiEvent.observe(viewLifecycleOwner, ::onUiEvent)
    }

    private fun setUiLogic() {
        binding.homePressureTextView.setOnClickListener {
            if (viewModel.getIsCurrentPressureInHPa()) {
                viewModel.pressureInMmHG.value?.let { pressure ->
                    binding.homePressureTextView.text = getString(R.string.pressure_mmHg_text, pressure)

                    viewModel.setIsCurrentPressureINHPa(false)
                }
            } else {
                viewModel.pressureInHPA.value?.let { pressure ->
                    binding.homePressureTextView.text = getString(R.string.pressure_hPa_text, pressure)

                    viewModel.setIsCurrentPressureINHPa(true)
                }
            }
        }
    }

    private fun onUiEvent(uiEvent: HomeViewModel.UiEvent) {
        when (uiEvent) {
            HomeViewModel.UiEvent.OnConnectionError -> {
                onConnectionError()
            }

            HomeViewModel.UiEvent.OnUnexpectedError -> {
                onUnexpectedError()
            }
        }
    }

    private fun onTemperatureUpdate(temperature: Double) {
        binding.homeTemperatureTextView.text = getString(R.string.temperature_text, temperature)
    }

    private fun onHumidityUpdate(humidity: Double) {
        binding.homeHumidityTextView.text = getString(R.string.humidity_text, humidity)
        binding.homeHumidityLinearProgressIndicator.setProgress(humidity.toInt(), true)
        binding.homeHumidityLinearProgressIndicator.setIndicatorColor(evaluateProgressColor(humidity, 40, 25))
    }

    private fun onAirQualityUpdate(airQuality: Double) {
        val text = String.format("%.1f%%", airQuality)

        binding.homeAirQualityTextView.text = text
        binding.homeAirQualityCircularProgressIndicator.setProgress(airQuality.toInt(), true)
        binding.homeAirQualityCircularProgressIndicator.setIndicatorColor(evaluateProgressColor(airQuality, 60, 40))
    }

    private fun onDustConcentrationUpdate(dustConcentration: Double) {
        binding.homeDustConcentrationTextView.text = getString(R.string.dust_concentration_text, dustConcentration)
    }

    private fun onPressureUpdate(pressure: Double) {
        binding.homePressureTextView.text = getString(R.string.pressure_hPa_text, pressure)
    }

    private fun onGasLeaksUpdate(gasLeaks: Boolean) {
        binding.homeGasLeakingTextView.text = getString(R.string.gas_leaking_text, gasLeaks.toString())
    }

    private fun onDateUpdate(date: Date) {
        val dateFormatter = SimpleDateFormat(DATE_TIME_FORMATTER_PATTERN, Locale.getDefault())

        binding.homeDateTextView.text = getString(R.string.last_reading_text, dateFormatter.format(date))
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

    private fun evaluateProgressColor(percentage: Double, middlePercentage: Int, bottomPercentage: Int): Int {
        return if (percentage > middlePercentage) {
            requireContext().getColor(R.color.green_700)
        } else if (percentage > bottomPercentage) {
            requireContext().getColor(R.color.yellow_A700)
        } else {
            requireContext().getColor(R.color.red_A900)
        }
    }
}
