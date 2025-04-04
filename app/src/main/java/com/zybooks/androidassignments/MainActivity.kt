package com.zybooks.androidassignments

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var editName: EditText
    private lateinit var editAmount: EditText
    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseList: MutableList<Expense>
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editName = findViewById(R.id.name)
        editAmount = findViewById(R.id.totalAmount)
        addButton = findViewById(R.id.add)
        recyclerView = findViewById(R.id.expenses)

        expenseList = mutableListOf()
        adapter = ExpenseAdapter(expenseList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            val name = editName.text.toString().trim()
            val amount = editAmount.text.toString().trim()

            if (name.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expense = Expense(name, amount)
            expenseList.add(expense)
            adapter.notifyItemInserted(expenseList.size - 1)

            editName.text.clear()
            editAmount.text.clear()
        }
    }
}
