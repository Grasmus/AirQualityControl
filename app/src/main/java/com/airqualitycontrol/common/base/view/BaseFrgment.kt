package com.airqualitycontrol.common.base.view

import androidx.fragment.app.Fragment
import com.airqualitycontrol.R
import com.airqualitycontrol.common.constans.LOADING_DIALOG_TAG
import com.airqualitycontrol.common.dialogs.LoadingIndicatorDialogFragment
import com.airqualitycontrol.domain.entities.exception.ApiErrorException
import com.airqualitycontrol.feature.AirQualityApp
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseFragment: Fragment() {
    private val loadingDialog = LoadingIndicatorDialogFragment()
    private var isLoadingDialogActive = false

    protected fun showLoadingDialog(isLoading: Boolean) {
        if (!isLoadingDialogActive && isLoading) {
            loadingDialog.show(childFragmentManager, LOADING_DIALOG_TAG)

            isLoadingDialogActive = true
        } else if (isLoadingDialogActive && !isLoading) {
            loadingDialog.dismiss()

            isLoadingDialogActive = false
        }
    }

    private fun showMessageDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    protected fun onErrorMessage(error: ApiErrorException) {
        showMessageDialog(
            error.error ?: getString(R.string.unknown_error),
            error.errorDescription ?: getString(R.string.something_wrong)
        )
    }

    protected fun getAppComponent() =
        (requireContext().applicationContext as AirQualityApp).appComponent
}
