package com.zybooks.androidassignments

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ExpenseDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_details)

        val name = intent.getStringExtra("expenseName")
        val amount = intent.getStringExtra("expenseAmount")

        findViewById<TextView>(R.id.name).text = "Name: $name"
        findViewById<TextView>(R.id.amount).text = "Amount: $amount"
    }
}
