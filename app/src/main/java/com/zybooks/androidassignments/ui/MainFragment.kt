package com.zybooks.androidassignments.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zybooks.androidassignments.R
import com.zybooks.androidassignments.model.CurrencyResponse
import com.zybooks.androidassignments.model.Expense
import com.zybooks.androidassignments.network.CurrencyService
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

    // Currency variables
    private var currencyRates = mutableMapOf<String, Double>()
    private var selectedCurrency = "CAD"

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

        // UI elements for currency conversion
        val switchConvert = view.findViewById<Switch>(R.id.switch_convert)
        val spinnerCurrency = view.findViewById<Spinner>(R.id.spinner_currency)
        val textConvertedCost = view.findViewById<TextView>(R.id.text_converted_cost)

        adapter = ExpenseAdapter(expenses) {
            saveExpensesToFile()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadExpensesFromFile()

        // Handle currency conversion switch
        switchConvert.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Enable currency conversion
                textConvertedCost.visibility = View.VISIBLE
                updateConvertedCosts()
            } else {
                // Disable currency conversion
                textConvertedCost.visibility = View.INVISIBLE
            }
        }

        // Handle currency selection spinner
        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCurrency = parentView?.getItemAtPosition(position).toString()
                updateConvertedCosts()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

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

        // Fetch exchange rates when the fragment is created
        fetchCurrencyRates()
    }

    // Fetch currency rates from the API
    private fun fetchCurrencyRates() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = CurrencyService.api.getRates()
                currencyRates = response.rates.toMutableMap()

                withContext(Dispatchers.Main) {
                    updateConvertedCosts()  // Update conversion once rates are fetched
                }
            } catch (e: Exception) {
                Log.e("MainFragment", "Error fetching currency rates: ${e.message}")
                showErrorToast()
            }
        }
    }

    // Update converted costs based on selected currency
    private fun updateConvertedCosts() {
        val switchConvert = view?.findViewById<Switch>(R.id.switch_convert)
        val textConvertedCost = view?.findViewById<TextView>(R.id.text_converted_cost)

        if (switchConvert?.isChecked == true && currencyRates.isNotEmpty()) {
            expenses.forEach { expense ->
                val rate = currencyRates[selectedCurrency] ?: 1.0
                val convertedAmount = expense.totalAmount.toDouble() * rate
                expense.convertedCost = convertedAmount
            }
            adapter.notifyDataSetChanged()

            // Show the converted cost for the first expense as an example
            if (expenses.isNotEmpty()) {
                textConvertedCost?.text = "Converted cost: ${expenses[0].convertedCost} $selectedCurrency"
            }
        }
    }


    // Save expenses to file
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

    // Load expenses from file
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
    // Show error message in case of failure
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
