package com.example.fileencryptionsuite.Layout

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.fileencryptionsuite.EncryptionThings.EncryptFile
import com.example.fileencryptionsuite.R
import com.example.fileencryptionsuite.databinding.ActivityHashingBinding

class UserHash: AppCompatActivity() {
    private lateinit var binding: ActivityHashingBinding
    private lateinit var openFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileUri: Uri
    private lateinit var fileContent: ByteArray
    private lateinit var fileName: String

    private var fileReady = false

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 101
        private const val REQUEST_PERMISSION_CODE = 123
        private const val TAG = "UserHash"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHashingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutHashValue.visibility = View.GONE

        binding.btnBackHashFile.setOnClickListener{finish()}

        binding.fab.setOnClickListener{
            val intent = Intent(this, KeyList::class.java)
            startActivity(intent)
        }

        binding.btnSelectFile.setOnClickListener {
            openFilePicker()
        }

        binding.btnHashFile.setOnClickListener{
            if (!fileReady){
                Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashValue = EncryptFile().hash(fileContent)

            binding.layoutHashValue.visibility = View.VISIBLE
            binding.txtHashValue.text = getString(R.string.hash_value, hashValue).toEditable()

            binding.btnCopyHash.setOnClickListener copy@{
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", hashValue)
                    ?: return@copy
                clipboard.setPrimaryClip(clip)

                Toast.makeText(this, "Hash Value Copied", Toast.LENGTH_SHORT).show()
            }
        }

        openFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    binding.txtHashValue.text = "".toEditable()
                    fileUri = uri
                    handleSelectedFile(uri)
                }
            }
        }
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun handleSelectedFile(uri: Uri){

        //region read file content
        val inputStream = contentResolver.openInputStream(uri)
        try {
            inputStream?.use { stream ->
                val byteArray = stream.readBytes()
                fileContent = byteArray
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleSelectedFile: ", e)
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        //endregion

        //region set file name
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    val displayName = it.getString(displayNameIndex) //get file name

                    fileName = displayName

                    fileName = displayName
                    binding.textFileName.visibility = View.VISIBLE
                    binding.textFileName.text = displayName.toEditable()

                    var buttonDrawable: Drawable = binding.btnHashFile.background
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable)
                    DrawableCompat.setTint(
                        buttonDrawable,
                        resources.getColor(R.color.darkerMainTheme, null)
                    )
                    binding.btnHashFile.background = buttonDrawable

                    fileReady = true

                }
            }
        }
        //endregion
    }

    private fun openFilePicker() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_PERMISSION_CODE
            )
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        openFileLauncher.launch(intent)
    }
}