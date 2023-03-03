package com.step.videoplayerrecyclerviewapplication

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import com.step.videoplayerrecyclerviewapplication.databinding.DialogVideoQualityBinding

class QualitySelectionDialog(
    var context: Context,
    var quality: Int = 0,
    var onQualitySelected: OnQualitySelected
) {

    private var dialog: Dialog? = null
    lateinit var dialogBinding: DialogVideoQualityBinding

    //    val HI_BITRATE = 2097152
//    val MI_BITRATE = 1048576
//    val LO_BITRATE = 524288
    val TAG = "QualitySelectionDialog"

    init {
        context = context
        dialogBinding = DialogVideoQualityBinding.inflate(LayoutInflater.from(context))

        dialog = Dialog(context).also {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(dialogBinding.root)
//            it.setContentView(R.layout.dialog_video_quality)
            it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            it.setCancelable(true)
            it.setCanceledOnTouchOutside(true)

            initViews(it, quality)


        }


    }


    private fun initViews(dialog: Dialog, quality: Int) {
        setDialogQuality(quality)


        dialogBinding.lowRadioButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resetAll()
                checkLowButton()
            }
            val qual = context.getString(R.string.low_quality)
            onQualitySelected.onQualitySelectedListener(qual.toInt())
            dismiss()
        }

        dialogBinding.mediumRadioButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resetAll()
                checkMediumButton()
            }
            val qual = context.getString(R.string.medium_quality)
            onQualitySelected.onQualitySelectedListener(qual.toInt())
            dismiss()
        }

        dialogBinding.highRadioButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resetAll()
                checkHighButton()
            }
            val qual = context.getString(R.string.high_quality)
            onQualitySelected.onQualitySelectedListener(qual.toInt())
            dismiss()
        }
        dialogBinding.autoRadioButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resetAll()
                checkAutoButton()
            }
            val qual = context.getString(R.string.low_quality)
            onQualitySelected.onQualitySelectedListener(qual.toInt())
            dismiss()
        }


//        dialog.findViewById<RadioGroup>(R.id.quality_group_layout)
//            .setOnCheckedChangeListener { radioGroup, p1 ->
//                when (radioGroup?.checkedRadioButtonId) {
//                    R.id.low_radio_button -> {
//                        try {
//                            val qual = context.getString(R.string.low_quality)
//                            onQualitySelected.onQualitySelectedListener(qual.toInt())
//
//                        } catch (e: Exception) {
//                            Log.d(TAG, "initializePlayer: ")
//
//                        }
//                        dismiss()
//                    }
//                    R.id.medium_radio_button -> {
//
//                        try {
//                            val qual = context.getString(R.string.medium_quality)
//                            onQualitySelected.onQualitySelectedListener(qual.toInt())
//
//                        } catch (e: Exception) {
//                            Log.d(TAG, "initializePlayer: ")
//
//                        }
//                        dismiss()
//                    }
//                    R.id.high_radio_button -> {
//                        try {
//                            val qual = context.getString(R.string.high_quality)
//                            onQualitySelected.onQualitySelectedListener(qual.toInt())
//
//                        } catch (e: Exception) {
//                            Log.d(TAG, "initializePlayer: ")
//
//                        }
//                        dismiss()
//
//                    }
//
//                    R.id.auto_radio_button -> {
//                        try {
////                            val qual = context.getString(R.string.medium_quality)
//                            onQualitySelected.onQualitySelectedListener(0)
//
//                        } catch (e: Exception) {
//                            Log.d(TAG, "initializePlayer: ")
//
//                        }
//                        dismiss()
//
//                    }
//
//                }
//            }

    }


    fun show() {
        dialog!!.show()
    }

//    fun show(quality: Int) {
//
//        var id: Int
//
//        if (quality.toString() == context.getString(R.string.low_quality)) {
//
//            id = R.id.low_radio_button
//
//        } else if (quality.toString() == context.getString(R.string.medium_quality)) {
//
//            id = R.id.medium_radio_button
//
//        } else if (quality.toString() == context.getString(R.string.high_quality)) {
//
//            id = R.id.high_radio_button
//
//
//        } else {
//
//
//            id = R.id.auto_radio_button
//
//        }
//
//
//        dialog?.findViewById<RadioGroup>(R.id.quality_group_layout)
//            ?.check(R.id.auto_radio_button)
//
//        dialog!!.show()
//    }

    fun dismiss() {
        dialog!!.dismiss()
    }

    fun setDialogQuality(quality: Int) {

        resetAll()


//        var id: Int


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (quality.toString() == context.getString(R.string.low_quality)) {

//            id = R.id.low_radio_button

                checkLowButton()

            } else if (quality.toString() == context.getString(R.string.medium_quality)) {

//            id = R.id.medium_radio_button
                checkMediumButton()

            } else if (quality.toString() == context.getString(R.string.high_quality)) {

//            id = R.id.high_radio_button

                checkHighButton()
            } else {

                checkAutoButton()

//            id = R.id.auto_radio_button

            }
        }


//        dialog?.findViewById<RadioGroup>(R.id.quality_group_layout)
//            ?.check(R.id.auto_radio_button)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkAutoButton() {

//        dialogBinding.autoRadioButton.backgroundTintList =
//            ColorStateList.valueOf(context.getColor(R.color.text_Color));
        dialogBinding.autoRadioButton.setBackgroundColor(context.getColor(R.color.text_Color))

        dialogBinding.autoRadioButton.setTextColor(context.getColor(R.color.white))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkHighButton() {

//        dialogBinding.highRadioButton.backgroundTintList =
//            ColorStateList.valueOf(context.getColor(R.color.text_Color));
        dialogBinding.highRadioButton.setBackgroundColor(context.getColor(R.color.text_Color))

        dialogBinding.highRadioButton.setTextColor(context.getColor(R.color.white))


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkMediumButton() {

//        dialogBinding.mediumRadioButton.backgroundTintList =
//            ColorStateList.valueOf(context.getColor(R.color.text_Color));
        dialogBinding.mediumRadioButton.setBackgroundColor(context.getColor(R.color.text_Color))
        dialogBinding.mediumRadioButton.setTextColor(context.getColor(R.color.white))

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkLowButton() {

//        dialogBinding.lowRadioButton.backgroundTintList =
//            ColorStateList.valueOf(context.getColor(R.color.text_Color))
        dialogBinding.lowRadioButton.setBackgroundColor(context.getColor(R.color.text_Color))

        dialogBinding.lowRadioButton.setTextColor(context.getColor(R.color.white))

    }

    private fun resetAll() {

//        dialogBinding.autoRadioButton
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


//            dialogBinding.autoRadioButton.backgroundTintList =
//                ColorStateList.valueOf(context.getColor(R.color.main_color_grey_200))
            dialogBinding.autoRadioButton.setBackgroundColor(context.getColor(R.color.main_color_grey_200))
            dialogBinding.autoRadioButton.setTextColor(context.getColor(R.color.text_Color))


//            dialogBinding.highRadioButton.backgroundTintList =
//                ColorStateList.valueOf(context.getColor(R.color.main_color_grey_200));
            dialogBinding.highRadioButton.setBackgroundColor(context.getColor(R.color.main_color_grey_200))
            dialogBinding.highRadioButton.setTextColor(context.getColor(R.color.text_Color))


//            dialogBinding.mediumRadioButton.backgroundTintList =
//                ColorStateList.valueOf(context.getColor(R.color.main_color_grey_200));
            dialogBinding.mediumRadioButton.setBackgroundColor(context.getColor(R.color.main_color_grey_200))
            dialogBinding.mediumRadioButton.setTextColor(context.getColor(R.color.text_Color))


//            dialogBinding.lowRadioButton.backgroundTintList =
//                ColorStateList.valueOf(context.getColor(R.color.main_color_grey_200));
            dialogBinding.lowRadioButton.setBackgroundColor(context.getColor(R.color.main_color_grey_200))
            dialogBinding.lowRadioButton.setTextColor(context.getColor(R.color.text_Color))


        };


    }


    public interface OnQualitySelected {
        fun onQualitySelectedListener(quality: Int)
    }

}