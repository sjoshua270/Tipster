package com.rethink.tipster

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.analytics.FirebaseAnalytics
import java.math.BigDecimal

class ValuesFragment : Fragment() {
    private val TAG: String = "ValuesFragment"
    private lateinit var fbAnalytics: FirebaseAnalytics
    private lateinit var bill: EditText
    private lateinit var percentTip: EditText
    private lateinit var tip: TextView
    private lateinit var total: TextView
    private lateinit var adView: AdView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_values,
                                    container,
                                    false)
        fbAnalytics = FirebaseAnalytics.getInstance(context)

        bill = view.findViewById(R.id.bill)
        percentTip = view.findViewById(R.id.percent)
        tip = view.findViewById(R.id.tip)
        total = view.findViewById(R.id.total)
        adView = view.findViewById(R.id.ad_view)
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val prefs: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        percentTip.setText(prefs.getString("percent",
                                           ""))

        bill.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.VALUE,
                                 p0.toString())
                fbAnalytics.logEvent("change_bill",
                                     bundle)
                makeCalc()
            }

        })

        percentTip.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val percentText: String = percentTip.text.toString()

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.VALUE,
                                 percentText)
                prefs.edit().putString("percent",
                                       percentText).apply()
                fbAnalytics.logEvent("change_percent",
                                     bundle)
                makeCalc()
            }

        })
        makeCalc()
        return view
    }

    private fun Double.roundTo2DecimalPlaces() =
            BigDecimal(this).setScale(2,
                                      BigDecimal.ROUND_HALF_UP).toDouble()

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

        val bundle = Bundle()
        bundle.putDouble("tip",
                         fTip)
        bundle.putDouble("total",
                         fTotal)
        fbAnalytics.logEvent("calculate_totals",
                             bundle)
    }

}