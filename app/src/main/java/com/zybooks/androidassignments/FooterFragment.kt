package com.zybooks.androidassignments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FooterFragment(private val expenses: List<Expense>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_footer, container, false)
        val total = expenses.sumOf { it.totalAmount.toDoubleOrNull() ?: 0.0 }
        view.findViewById<TextView>(R.id.totalText).text = "Total: $total"
        return view
    }
}
