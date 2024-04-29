package com.example.fileencryptionsuite.Layout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidFileEncryptionSuite.tables.Key
import com.example.fileencryptionsuite.Adapter.KeyListAdapter
import com.example.fileencryptionsuite.Database.DatabaseManager
import com.example.fileencryptionsuite.databinding.ActivityKeyListBinding

class KeyList: AppCompatActivity(){
    private lateinit var binding: ActivityKeyListBinding
    private lateinit var dbManager: DatabaseManager
    private lateinit var keyList: ArrayList<Key>
    private var keyPresent = false
    private lateinit var listener:KeyListAdapter.NotifyDatasizeIsZero

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbManager = DatabaseManager(this)

        binding.btnBackKeyList.setOnClickListener {
            finish()
        }

        listener = object : KeyListAdapter.NotifyDatasizeIsZero {
            override fun notifyLayout() {
                binding.recyclerViewKeyList.visibility = View.GONE
                binding.txtNoKeys.visibility = View.VISIBLE
                keyPresent = false
            }

        }

        keyList = dbManager.getKeyList()

        if (keyList.isEmpty()){
            binding.recyclerViewKeyList.visibility = View.GONE
            keyPresent = false
        } else {
            binding.txtNoKeys.visibility = View.GONE
            keyPresent = true

            val layoutManager =LinearLayoutManager(this)
            val adapter = KeyListAdapter(keyList, listener)
            binding.recyclerViewKeyList.layoutManager = layoutManager
            binding.recyclerViewKeyList.adapter = adapter
        }


    }
}