package com.example.helloworld

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_eat_register.*
import java.util.*

class EatRegister : AppCompatActivity() {
    companion object {
        val TAG = "EatRegister"
    }
    var day:String?=null
    var month:String?=null
    var year:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat_register)
            date_picker_eat.setOnClickListener {
                val getDate:Calendar= Calendar.getInstance()

                val datePicker= DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    val selectDate:Calendar = Calendar.getInstance()
                    selectDate.set(Calendar.YEAR,i)
                    selectDate.set(Calendar.MONTH,i2)
                    selectDate.set(Calendar.DAY_OF_MONTH,i3)
                    day=getDate.get(i3).toString()
                    month= getDate.get(i2).toString()
                    year= getDate.get(i).toString()

                },
                getDate.get(Calendar.YEAR),getDate.get(Calendar.MONTH),getDate.get(Calendar.DAY_OF_MONTH))
                datePicker.show()

            }
    }


    }