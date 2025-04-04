package com.zybooks.androidassignments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File

class MainFragment : Fragment() {

    private lateinit var nameInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private var expenses = mutableListOf<Expense>()

    private val gson = Gson()
    private var fileJob: Job? = null
    private val fileName = "expenses.json"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInput = view.findViewById(R.id.name)
        amountInput = view.findViewById(R.id.totalAmount)
        addButton = view.findViewById(R.id.add)
        recyclerView = view.findViewById(R.id.expenses)

        adapter = ExpenseAdapter(expenses){
            saveExpensesToFile()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadExpensesFromFile()

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val amount = amountInput.text.toString().trim()

            if (name.isNotEmpty() && amount.isNotEmpty()) {
                val expense = Expense(name, amount)
                expenses.add(expense)
                adapter.notifyItemInserted(expenses.size - 1)
                nameInput.text.clear()
                amountInput.text.clear()
                saveExpensesToFile()
            }
        }
    }

    private fun saveExpensesToFile() {
        fileJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = gson.toJson(expenses)
                requireContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(json.toByteArray())
                }
            } catch (e: Exception) {
                Log.e("MainFragment", "Error saving file: ${e.message}")
            }
        }
    }

    private fun loadExpensesFromFile() {
        fileJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = File(requireContext().filesDir, fileName)
                if (file.exists()) {
                    val json = file.readText()
                    val listType = object : TypeToken<MutableList<Expense>>() {}.type
                    val loadedExpenses: MutableList<Expense> = gson.fromJson(json, listType)

                    withContext(Dispatchers.Main) {
                        expenses.clear()
                        expenses.addAll(loadedExpenses)
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainFragment", "Error loading file: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fileJob?.cancel()
    }
}
