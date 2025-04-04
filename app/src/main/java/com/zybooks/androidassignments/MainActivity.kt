package com.zybooks.androidassignments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var addButton: Button
    private lateinit var tipsButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private val expenseList = mutableListOf<Expense>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Lifecycle", "onCreate called")

        nameInput = findViewById(R.id.name)
        amountInput = findViewById(R.id.totalAmount)
        addButton = findViewById(R.id.add)
        tipsButton = findViewById(R.id.tips)
        recyclerView = findViewById(R.id.expenses)

        adapter = ExpenseAdapter(expenseList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val amount = amountInput.text.toString().trim()

            if (name.isNotEmpty() && amount.isNotEmpty()) {
                val expense = Expense(name, amount)
                expenseList.add(expense)
                adapter.notifyItemInserted(expenseList.size - 1)
                nameInput.text.clear()
                amountInput.text.clear()
            }
        }

        tipsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.investopedia.com/"))
            startActivity(intent)
        }

        // Dynamically add fragments
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_container, HeaderFragment())
            .replace(R.id.footer_container, FooterFragment(expenseList))
            .commit()
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy called")
    }
}
