package com.zybooks.androidassignments.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.zybooks.androidassignments.R
import com.zybooks.androidassignments.model.Expense

class ExpenseAdapter(
    private val expenseList: MutableList<Expense>,
    private val onItemDeleted: () -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val amount: TextView = itemView.findViewById(R.id.totalAmount)
        val deleteButton: Button = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.name.text = expense.name
        holder.amount.text = expense.totalAmount

        holder.itemView.setOnClickListener {
            val action = MainFragmentDirections
                .actionMainFragmentToExpenseDetailsFragment(expense.name, expense.totalAmount)
            holder.itemView.findNavController().navigate(action)
        }

        holder.deleteButton.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                expenseList.removeAt(pos)
                notifyItemRemoved(pos)
                onItemDeleted() // Save the updated list to file
            }
        }
    }

    override fun getItemCount(): Int = expenseList.size
}
