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
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.analytics.FirebaseAnalytics
import java.math.BigDecimal

class ValuesFragment : Fragment() {
    private val TAG: String = "ValuesFragment"
    private lateinit var fbAnalytics: FirebaseAnalytics
    private lateinit var amountField: EditText
    private lateinit var percentTipField: EditText
    private lateinit var tipField: EditText
    private lateinit var totalView: TextView
    private lateinit var peopleField: EditText
    private lateinit var personView: TextView
    private lateinit var addPersonButton: ImageButton
    private lateinit var removePersonButton: ImageButton
    private lateinit var adView: AdView
    private var appChanged: Boolean = false
    private var lastEdited: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_values,
                                    container,
                                    false)
        fbAnalytics = FirebaseAnalytics.getInstance(context)

        amountField = view.findViewById(R.id.amount)
        percentTipField = view.findViewById(R.id.percent_tip)
        tipField = view.findViewById(R.id.tip)
        totalView = view.findViewById(R.id.total)
        peopleField = view.findViewById(R.id.people)
        personView = view.findViewById(R.id.person)
        addPersonButton = view.findViewById(R.id.add_person)
        removePersonButton = view.findViewById(R.id.remove_person)
        adView = view.findViewById(R.id.ad_view)
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val prefs: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        tipField.setText(prefs.getString("tip",
                                         ""))
        percentTipField.setText(prefs.getString("percent_tip",
                                                ""))
        lastEdited = prefs.getString("last_edited",
                                     "percent_tip")
        peopleField.setText(1.toString())

        amountField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!appChanged) {
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.VALUE,
                                     p0.toString())
                    fbAnalytics.logEvent("change_amount",
                                         bundle)
                    makeCalc(amountField.id)
                }
            }
        })

        percentTipField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!appChanged) {
                    val percentText: String = percentTipField.text.toString()
                    prefs.edit().putString("percent_tip",
                                           percentText).apply()
                    lastEdited = "percent_tip"
                    prefs.edit().putString("last_edited",
                                           lastEdited).apply()

                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.VALUE,
                                     percentText)
                    fbAnalytics.logEvent("change_percent_tip",
                                         bundle)

                    makeCalc(percentTipField.id)
                }
            }
        })

        tipField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!appChanged) {
                    val tipText: String = tipField.text.toString()
                    prefs.edit().putString("tip",
                                           tipText).apply()
                    lastEdited = "tip"
                    prefs.edit().putString("last_edited",
                                           lastEdited).apply()

                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.VALUE,
                                     tipText)
                    fbAnalytics.logEvent("change_tip",
                                         bundle)

                    makeCalc(tipField.id)
                }
            }
        })

        peopleField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!appChanged) {
                    val peopleText: String = peopleField.text.toString()

                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.VALUE,
                                     peopleText)
                    fbAnalytics.logEvent("change_people",
                                         bundle)

                    makeCalc(peopleField.id)
                }
            }
        })

        addPersonButton.setOnClickListener {
            var people = peopleField.text.toString().toInt()
            people += 1
            peopleField.setText(people.toString())
        }

        removePersonButton.setOnClickListener {
            var people = peopleField.text.toString().toInt()
            if (people > 1) {
                people -= 1
            } else if (people < 1) {
                people = 1
            }
            peopleField.setText(people.toString())
        }
        makeCalc(0)
        return view
    }

    private fun Double.roundTo2DecimalPlaces() =
            BigDecimal(this).setScale(2,
                                      BigDecimal.ROUND_HALF_UP).toDouble()

    fun makeCalc(view: Int) {
        appChanged = true
        var amount = 0.0
        var tip = 0.0
        var percentTip = 0.0
        var people = 1.0
        if (this.amountField.text.toString() != "") {
            amount = this.amountField.text.toString().toDouble()
        }
        if (tipField.text.toString() != "") {
            tip = tipField.text.toString().toDouble()
        }
        if (percentTipField.text.toString() != "") {
            percentTip = percentTipField.text.toString().toDouble() / 100
        }
        if (peopleField.text.toString() != "") {
            people = peopleField.text.toString().toDouble()
        }

        if (view == amountField.id) {
            if (lastEdited == "tip") {
                percentTip = tip / amount * 100
                percentTipField.setText(percentTip.roundTo2DecimalPlaces().toString())
            } else if (lastEdited == "percent_tip") {
                tip = amount * percentTip
                tipField.setText(tip.roundTo2DecimalPlaces().toString())
            }
        }
        if (view == percentTipField.id) {
            if (amount > 0.0) {
                tip = amount * percentTip
                tipField.setText(tip.roundTo2DecimalPlaces().toString())
            }
        }
        if (view == tipField.id) {
            if (amount > 0.0) {
                percentTip = tip / amount * 100
                percentTipField.setText(percentTip.roundTo2DecimalPlaces().toString())

            }
        }
        val total = amount + tip
        totalView.text = getString(R.string.usd).format(total)
        personView.text = getString(R.string.usd).format(total / people)
        appChanged = false

        val bundle = Bundle()
        bundle.putDouble("tip",
                         tip)
        bundle.putDouble("total",
                         total)
        fbAnalytics.logEvent("calculate_totals",
                             bundle)
    }

}