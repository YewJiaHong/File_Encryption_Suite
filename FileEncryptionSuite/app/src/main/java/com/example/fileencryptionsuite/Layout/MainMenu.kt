package com.example.fileencryptionsuite.Layout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fileencryptionsuite.databinding.ActivityMainMenuBinding

class MainMenu: AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnHashing.setOnClickListener{
            val toHashFile = Intent(this, UserHash::class.java)
            startActivity(toHashFile)
        }

        binding.btnEncrypt.setOnClickListener {
            val toEncryptFile = Intent(this, UserEncrypt::class.java)
            startActivity(toEncryptFile)
        }

        binding.btnDecrypt.setOnClickListener{
            val toDecryptFile = Intent(this, UserDecrypt::class.java)
            startActivity(toDecryptFile)
        }

        binding.fab.setOnClickListener {
            val toKeyList = Intent(this, KeyList::class.java)
            startActivity(toKeyList)
        }
    }
}