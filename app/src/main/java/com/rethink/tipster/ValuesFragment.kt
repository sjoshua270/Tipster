package com.rethink.tipster

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import java.math.BigDecimal

class ValuesFragment : Fragment() {
    private lateinit var bill: EditText
    private lateinit var percentTip: EditText
    private lateinit var tip: TextView
    private lateinit var total: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_values,
                                    container,
                                    false)
        bill = view.findViewById(R.id.bill)
        percentTip = view.findViewById(R.id.percent)
        tip = view.findViewById(R.id.tip)
        total = view.findViewById(R.id.total)

        bill.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                makeCalc()
            }

        })

        percentTip.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                makeCalc()
            }

        })
        makeCalc()
        return view
    }

    private fun Double.roundTo2DecimalPlaces() =
            BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()

    fun makeCalc() {
        var fBill = 0.0
        var fPercent = 0.0
        if (bill.text.toString() != "") {
            fBill = bill.text.toString().toDouble()
        }
        if (percentTip.text.toString() != "") {
            fPercent = percentTip.text.toString().toDouble() / 100
        }
        val fTip: Double = (fBill * fPercent).roundTo2DecimalPlaces()
        val fTotal: Double = (fTip + fBill).roundTo2DecimalPlaces()
        val tipText = "\$$fTip"
        val totalText = "\$$fTotal"
        tip.text = tipText
        total.text = totalText
    }

}