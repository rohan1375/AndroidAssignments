package com.zybooks.androidassignments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

class ExpenseDetailsFragment : Fragment() {

    private val args: ExpenseDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameText: TextView = view.findViewById(R.id.detailName)
        val amountText: TextView = view.findViewById(R.id.detailAmount)

        nameText.text = "Name: ${args.expenseName}"
        amountText.text = "Amount: ${args.expenseAmount}"
    }
}
