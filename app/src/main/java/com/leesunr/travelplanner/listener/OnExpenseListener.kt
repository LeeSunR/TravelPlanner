package com.leesunr.travelplanner.listener

import com.leesunr.travelplanner.model.Expenses

interface OnExpenseListener {
    fun onDelete(expensesList: ArrayList<Expenses>)
}