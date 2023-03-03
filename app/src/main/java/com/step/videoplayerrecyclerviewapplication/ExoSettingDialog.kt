package com.step.videoplayerrecyclerviewapplication

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.step.videoplayerrecyclerviewapplication.databinding.DialogExoSettingBinding

class ExoSettingDialog(
    var context: Context,
    var qualityArray: Array<String>,
    var qualityValue: Int,
    var playBackSpeedArray: Array<String>,
    var speedValue: Float,
    var onSettingSelected: OnSettingSelected
) {

    private var dialog: Dialog? = null
    var dialogBinding: DialogExoSettingBinding
    val TAG = "QualitySelectionDialog"


    init {
        context = context
        dialogBinding = DialogExoSettingBinding.inflate(LayoutInflater.from(context))

        dialog = Dialog(context).also {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(dialogBinding.root)
            it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            it.setCancelable(true)
            it.setCanceledOnTouchOutside(true)

            initViews()


        }


    }

    private fun initViews() {

        val qualityAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            R.layout.spinner_item, qualityArray
        )

        qualityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        dialogBinding.qualitySpinner.setAdapter(qualityAdapter)
        dialogBinding.qualitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val quality = qualityArray.get(position).lowercase()

                    var qual = context.getString(R.string.low_quality)

                    when {
                        quality.contains("240") -> {
                            qual = context.getString(R.string.low_quality)


                        }
                        quality.contains("360") -> {
                            qual = context.getString(R.string.medium_low_quality)


                        }
                        quality.contains("480") -> {
                            qual = context.getString(R.string.medium_quality)


                        }
                        quality.contains("720") -> {
                            qual = context.getString(R.string.high_quality)


                        }

                        else -> {

                            qual = context.getString(R.string.low_quality)

                        }
                    }




                    onSettingSelected.onQualitySelectedListener(qual)


                }

            }


        var position = 4
        if (context.getString(R.string.low_quality) == qualityValue.toString()) {
            position = 3

        } else if (context.getString(R.string.medium_quality) == qualityValue.toString()) {
            position = 1

        } else if (context.getString(R.string.medium_quality) == qualityValue.toString()) {
            position = 1

        } else if (context.getString(R.string.medium_low_quality) == qualityValue.toString()) {
            position = 2

        } else if (context.getString(R.string.high_quality) == qualityValue.toString()) {
            position = 0
        } else {
            position = 4
        }

        dialogBinding.qualitySpinner.setSelection(position)


        val speedAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            R.layout.spinner_item, playBackSpeedArray
        )

        speedAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        dialogBinding.speedSpinner.setAdapter(speedAdapter)
        dialogBinding.speedSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {


                    var speed = 1F

                    if (playBackSpeedArray.get(position) == context.getString(R.string.zero_point_two_five_x_string)) {


                        speed = 0.25F

                    } else if (playBackSpeedArray.get(position) == context.getString(R.string.zero_point_five_x_string)) {
                        speed = 0.5F

                    } else if (playBackSpeedArray.get(position) == context.getString(R.string.one_x_string)) {
                        speed = 1.0F


                    } else if (playBackSpeedArray.get(position) == context.getString(R.string.one_point_five_x_string)) {
                        speed = 1.5F

                    } else if (playBackSpeedArray.get(position) == context.getString(R.string.two_x_string)) {
                        speed = 2.0F

                    } else {
                        speed = 1.0F

                    }



                    onSettingSelected.onSpeedSelectedListener(speed)


                }

            }

        var speedPosition = 0

        when {
            speedValue.toString() == "0.25" //0.25x
            -> {
                speedPosition = 0
            }
            speedValue.toString() == "0.5" //0.5x
            -> {
                speedPosition = 1

            }
            speedValue.toString() == "1.0" //1.0x
            -> {
                speedPosition = 2

            }
            speedValue.toString() == "1.5" //1.5x
            -> {
                speedPosition = 3

            }
            speedValue.toString() == "2.0" //2.0x
            -> {
                speedPosition = 4

            }
            else -> {
                speedPosition = 0

            }
        }
        dialogBinding.speedSpinner.setSelection(speedPosition)


    }


    fun show() {
        dialog!!.show()
    }

    fun dismiss() {
        dialog!!.dismiss()
    }


    public interface OnSettingSelected {
        fun onQualitySelectedListener(quality: String)
        fun onSpeedSelectedListener(speed: Float)
    }

}