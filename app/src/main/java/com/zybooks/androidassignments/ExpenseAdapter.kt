package com.zybooks.androidassignments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenseList: MutableList<Expense>,
    private val onItemDeleted: () -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {


    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.name)
        val amountView: TextView = itemView.findViewById(R.id.totalAmount)
        val deleteButton: Button = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.nameView.text = expense.name
        holder.amountView.text = expense.totalAmount

        holder.deleteButton.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                expenseList.removeAt(pos)
                notifyItemRemoved(pos)
            onItemDeleted()  // save the updated file that when the app re-starts it save the changes
            }
        }
    }

    override fun getItemCount(): Int = expenseList.size
}
