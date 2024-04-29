package com.example.fileencryptionsuite.Adapter

import androidx.appcompat.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.androidFileEncryptionSuite.tables.Key
import com.example.fileencryptionsuite.Database.DatabaseManager
import com.example.fileencryptionsuite.Layout.KeyList
import com.example.fileencryptionsuite.R

class KeyListAdapter(private val localDataSet: ArrayList<Key>, private val listener: NotifyDatasizeIsZero): RecyclerView.Adapter<KeyListAdapter.ViewHolder>() {
    private lateinit var view: View
    private lateinit var dbManager: DatabaseManager


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mKeyName: TextView
        val mKeyType: TextView
        val mKeyValue: TextView
        val mBtnDelete: ImageView
        val mBtnCopy: ImageView
        var columnID: String? = null

        init {
            mKeyName = view.findViewById(R.id.keyName)
            mKeyType = view.findViewById(R.id.keyType)
            mKeyValue = view.findViewById(R.id.keyValue)
            mBtnDelete = view.findViewById(R.id.btnDelete)
            mBtnCopy = view.findViewById(R.id.btnCopy)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Create a new view, which defines the UI of the list item
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.key_item, parent, false)
        dbManager = DatabaseManager(view.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mKeyName.text = localDataSet[position].keyNameCol
        holder.mKeyValue.text = localDataSet[position].keyCol
        holder.mKeyType.text = localDataSet[position].keyTypeCol
        holder.columnID = localDataSet[position].idCol

        holder.mBtnCopy.setOnClickListener{
            val clipboard =
                view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", localDataSet[position].keyCol)
                ?: return@setOnClickListener
            clipboard.setPrimaryClip(clip)

            Toast.makeText(view.context, "Key Value Copied", Toast.LENGTH_SHORT).show()
        }

        holder.mBtnDelete.setOnClickListener{
            val dialogBuilder = AlertDialog.Builder(view.context)
                .setTitle("Delete Key?")
                .setMessage("Are you sure you want to delete this key?")
                .setIcon(R.drawable.ic_delete)
                .setCancelable(true)
                .setNegativeButton("No") { _,_ ->}
                .setPositiveButton("Yes") { _, _ ->
                    val count = dbManager.deleteKeyFromList(holder.columnID!!)
                    if (count == 1){
                        Toast.makeText(view.context, "Key Successfully Deleted", Toast.LENGTH_SHORT).show()
                        localDataSet.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, localDataSet.size)

                        if (localDataSet.size == 0)
                            listener.notifyLayout()
                    } else{
                        Toast.makeText(view.context, "Something went wrong, $count number of rows affected", Toast.LENGTH_SHORT).show()
                    }
                }
          val alertDialog = dialogBuilder.create()
          alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return localDataSet.size
    }

    interface NotifyDatasizeIsZero {
        fun notifyLayout()
    }
}