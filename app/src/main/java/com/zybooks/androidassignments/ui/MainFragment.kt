package com.zybooks.androidassignments.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zybooks.androidassignments.R
import com.zybooks.androidassignments.model.Expense
import com.zybooks.androidassignments.network.CurrencyService
import kotlinx.coroutines.*
import java.io.File

class MainFragment : Fragment() {

    private lateinit var nameInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var switchConvert: Switch
    private lateinit var textConvertedCost: TextView
    private lateinit var spinnerCurrency: Spinner

    private lateinit var adapter: ExpenseAdapter
    private var expenses = mutableListOf<Expense>()

    private val gson = Gson()
    private var fileJob: Job? = null
    private val fileName = "expenses.json"

    private var currencyRates = mutableMapOf<String, Double>()
    private var selectedCurrency = "CAD"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Views
        nameInput = view.findViewById(R.id.name)
        amountInput = view.findViewById(R.id.totalAmount)
        addButton = view.findViewById(R.id.add)
        recyclerView = view.findViewById(R.id.expenses)
        switchConvert = view.findViewById(R.id.switch_convert)
        spinnerCurrency = view.findViewById(R.id.spinner_currency)
        textConvertedCost = view.findViewById(R.id.text_converted_cost)

        adapter = ExpenseAdapter(expenses) {
            saveExpensesToFile()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadExpensesFromFile()

        // Switch listener
        switchConvert.setOnCheckedChangeListener { _, isChecked ->
            textConvertedCost.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            updateConvertedCosts()
        }

        // Spinner listener will be set after rates are fetched

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val amount = amountInput.text.toString().trim()

            if (name.isNotEmpty() && amount.isNotEmpty()) {
                val baseAmount = amount.toDoubleOrNull() ?: 0.0
                val rate = currencyRates[selectedCurrency] ?: 1.0
                val converted = baseAmount * rate

                val expense = Expense(name, amount, selectedCurrency, converted)
                expenses.add(expense)
                adapter.notifyItemInserted(expenses.size - 1)

                nameInput.text.clear()
                amountInput.text.clear()
                saveExpensesToFile()
            }
        }
        // Fetch rates and setup spinner
        fetchCurrencyRates()
    }

    private fun fetchCurrencyRates() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = CurrencyService.api.getRates()
                currencyRates = response.rates.toMutableMap()
                Log.d("CurrencyRates", "Rates loaded: ${currencyRates}")


                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        currencyRates.keys.sorted()
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCurrency.adapter = adapter
                    spinnerCurrency.setSelection(0)

                    spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                        ) {
                            selectedCurrency = parent?.getItemAtPosition(position).toString()
                            updateConvertedCosts()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                    updateConvertedCosts()
                }
            } catch (e: Exception) {
                Log.e("MainFragment", "Error fetching currency rates: ${e.message}")
                showErrorToast()
            }
        }
    }

    private fun updateConvertedCosts() {
        if (switchConvert.isChecked && currencyRates.isNotEmpty()) {
            val rate = currencyRates[selectedCurrency] ?: 1.0

            expenses.forEach { expense ->
                val baseAmount = expense.totalAmount.toDoubleOrNull() ?: 0.0
                expense.convertedCost = baseAmount * rate
                expense.currency = selectedCurrency
            }

            adapter.notifyDataSetChanged()

            if (expenses.isNotEmpty()) {
                val lastConverted = expenses.last().convertedCost
                textConvertedCost.text = "Converted: %.2f %s".format(lastConverted, selectedCurrency)
            } else {
                textConvertedCost.text = "No expenses to convert"
            }
        }
    }

    private fun saveExpensesToFile() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val json = gson.toJson(expenses)
                    requireContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
                        it.write(json.toByteArray())
                    }
                }

                withContext(Dispatchers.Main) {
                    Snackbar.make(requireView(), "Expenses saved successfully.", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(requireView(), "Error saving file: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
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

    private fun showErrorToast() {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(requireContext(), "Error fetching currency data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fileJob?.cancel()
    }
}
