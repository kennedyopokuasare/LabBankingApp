package com.mobicomp2020.labbankingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mobicomp2020.bankingapp.db.PaymentInfo
import com.mobicomp2020.labbankingapp.databinding.PaymentHistoryItemBinding


class PaymentHistoryAdaptor(context: Context, private val list: List<PaymentInfo>) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View? {
        var rowBinding = PaymentHistoryItemBinding.inflate(inflater, container, false)
        //set payment info values to the list item
        rowBinding.txtAccountName.text = list[position].name
        rowBinding.txtAccountNumber.text = list[position].accountNumber
        rowBinding.txtPaymentDate.text = list[position].date
        rowBinding.txtPaymentAmount.text = list[position].amount

        return rowBinding.root
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}
