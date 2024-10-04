package com.airqualitycontrol.common.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airqualitycontrol.R
import com.airqualitycontrol.databinding.FragmentDialogLoadingIndicatorBinding

class LoadingIndicatorDialogFragment: DialogFragment() {
    private val binding: FragmentDialogLoadingIndicatorBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(
            requireContext(),
            R.style.AirQualityControl_LoadingDialogStyle
        ).apply {
            setView(binding.root)
            isCancelable = false
        }.create()
    }
}
