package com.example.fileencryptionsuite.Layout

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.example.fileencryptionsuite.EncryptionThings.Point
import com.example.fileencryptionsuite.R
import com.example.fileencryptionsuite.databinding.ActivityDecryptBinding
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger

class UserDecrypt: AppCompatActivity() {
    private lateinit var binding: ActivityDecryptBinding
    private lateinit var openFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileContent: ByteArray
    private lateinit var fileName: String
    private lateinit var fileUri: Uri
    private lateinit var file: File
    private lateinit var outputStream: FileOutputStream

    private var fileReady = false

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 101
        private const val REQUEST_PERMISSION_CODE = 123
        private const val TAG = "UserDecrypt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDecryptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener{
            val toKeyList = Intent(this, KeyList::class.java)
            startActivity(toKeyList)
        }

        binding.btnBackDecryptFile.setOnClickListener { finish() }

        binding.btnSelectFile.setOnClickListener { openFilePicker() }

        binding.btnDecryptFile.setOnClickListener {
            if (!fileReady){
                Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if ((binding.editTextKeyValue.text.isEmpty()) || (!isNumeric(binding.editTextKeyValue.text.toString()))) {
                Toast.makeText(this, "Please enter a Valid Key Value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var buttonDrawable: Drawable = binding.btnDecryptFile.background
            buttonDrawable = DrawableCompat.wrap(buttonDrawable)
            DrawableCompat.setTint(
                buttonDrawable,
                resources.getColor(R.color.lightGrey, null)
            )
            binding.btnDecryptFile.background = buttonDrawable
            binding.btnDecryptFile.setOnClickListener {  }

            Toast.makeText(this, "Decrypting File... Please Wait, it will take a while", Toast.LENGTH_SHORT).show()


            Thread {
                val decryptedFileContent: ByteArray
                val publicKey = Point(BigInteger(binding.editTextKeyValue.text.toString()), BigInteger.ZERO)

                try {
                    decryptedFileContent = EncryptFile().decrypt(fileContent, publicKey)
                } catch(e: Exception){
                    runOnUiThread{
                        Toast.makeText(this, "Fatal Error Occurred, Make sure you are using the correct kay value", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "DecryptFile: ",e )
                        finish()
                    }
                    return@Thread
                }

                runOnUiThread{
                    try {
                        outputStream.write(decryptedFileContent)
                        outputStream.close()
                        Toast.makeText(this, "File Successfully Decrypted, Saved in Downloads", Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (e: Exception) {
                        Log.e(TAG, "createFile: ", e)
                    }
                }
            }.start()
        }

        openFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    fileUri = uri
                    handleSelectedFile(uri)
                }
            }
        }
    }

    private fun isNumeric(strNum: String?): Boolean {
        if (strNum == null) {
            return false
        }
        try {
            strNum.toDouble()
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun openFilePicker() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)
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

    private fun recursiveAddFileName(fileName: String, extension:String){
        val newFileName = fileName.substring(0, fileName.lastIndexOf(".")) +  "(1)"
        val finalName = newFileName + extension
        file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            finalName)

        if (file.exists())
            recursiveAddFileName(newFileName,extension)
        else
            file.createNewFile()
    }

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

                    val newFileName = fileName.substring(0, fileName.lastIndexOf("."))//remove .aes from file name
                    file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        newFileName)
                    if (!file.exists()) {
                        file.createNewFile()
                    } else{
                        val extension = newFileName.substring(newFileName.lastIndexOf("."), newFileName.length)
                        recursiveAddFileName(newFileName, extension)
                    }

                    outputStream = FileOutputStream(file)

                    fileName = displayName
                    binding.textFileName.visibility = View.VISIBLE
                    binding.textFileName.text = displayName.toEditable()

                    var buttonDrawable: Drawable = binding.btnDecryptFile.background
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable)
                    DrawableCompat.setTint(
                        buttonDrawable,
                        resources.getColor(R.color.darkerMainTheme, null)
                    )
                    binding.btnDecryptFile.background = buttonDrawable

                    fileReady = true

                }
            }
        }
        //endregion
    }
}