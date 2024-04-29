package com.example.fileencryptionsuite.Layout

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.androidFileEncryptionSuite.encryptionThings.CurveAndPoint
import com.example.fileencryptionsuite.Database.DatabaseManager
import com.example.fileencryptionsuite.EncryptionThings.ECDH
import com.example.fileencryptionsuite.EncryptionThings.EncryptFile
import com.example.fileencryptionsuite.EncryptionThings.Point
import com.example.fileencryptionsuite.R
import com.example.fileencryptionsuite.databinding.ActivityEncryptBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UserEncrypt: AppCompatActivity() {
    private lateinit var binding: ActivityEncryptBinding
    private lateinit var openFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileContent: ByteArray
    private lateinit var fileName: String
    private lateinit var fileUri: Uri
    private lateinit var dbManager: DatabaseManager
    private lateinit var file: File
    private lateinit var outputStream: FileOutputStream

    private val ecdh = ECDH();
    private var fileReady = false

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 101
        private const val REQUEST_PERMISSION_CODE = 123
        private const val TAG = "UserEncrypt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEncryptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbManager = DatabaseManager(this)
        binding.seedLayout.visibility = View.GONE

        binding.fab.setOnClickListener{
            val toKeyList = Intent(this, KeyList::class.java)
            startActivity(toKeyList)
        }

        binding.btnSelectFile.setOnClickListener { openFilePicker() }

        binding.btnBackEncryptFile.setOnClickListener { finish() }

        binding.showSeed.setOnClickListener{
            if (binding.showSeed.isChecked){
                binding.seedLayout.visibility = View.VISIBLE
            } else {
                binding.seedLayout.visibility = View.GONE
            }
        }
        
        binding.btnEncryptFile.setOnClickListener {
            if (!fileReady){
                Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.editTextKeyName.text.toString().ifEmpty {
                Toast.makeText(this, "Please Enter a Key Name for the Key", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fileReady = false
            var buttonDrawable: Drawable = binding.btnEncryptFile.background
            buttonDrawable = DrawableCompat.wrap(buttonDrawable)
            DrawableCompat.setTint(
                buttonDrawable,
                resources.getColor(R.color.lightGrey, null)
            )
            binding.btnEncryptFile.background = buttonDrawable
            binding.btnEncryptFile.setOnClickListener {  }

            Toast.makeText(this, "Encrypting File... Please Wait, it will take a while", Toast.LENGTH_SHORT).show()

            Thread {
                val encryptedFileContent: ByteArray
                val publicKey: Point? = if (!binding.showSeed.isChecked) { //two random seed
                    val keyArray1 = ecdh.makeKeypair(null, CurveAndPoint().curve)
                    val keyArray2 = ecdh.makeKeypair(null, CurveAndPoint().curve)
                    ecdh.sharedKeygeneration(keyArray1, keyArray2, CurveAndPoint().curve)
                } else { //non random seed
                    val seedOne = binding.editTextFirstSeed.text.toString().ifEmpty { null }
                    val seedTwo = binding.editTextSecondSeed.text.toString().ifEmpty { null }

                    val firstKeyPair = ecdh.makeKeypair(seedOne, CurveAndPoint().curve)
                    val secondKeyPair = ecdh.makeKeypair(seedTwo, CurveAndPoint().curve)
                    ecdh.sharedKeygeneration(firstKeyPair, secondKeyPair, CurveAndPoint().curve)
                }

                if (publicKey != null) {
                    encryptedFileContent = EncryptFile().encrypt(fileContent, publicKey)
                    runOnUiThread {
                        dbManager.insertNewKey(
                            binding.editTextKeyName.text.toString(),
                            publicKey.getX(),
                            "Public Key"
                        )

                        try {
                            outputStream.write(encryptedFileContent)
                            outputStream.close()
                        } catch (e: Exception) {
                            Log.e(TAG, "createFile: ", e)
                        }

                        Toast.makeText(this, "File Successfully Encrypted, Saved in Downloads", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                } else {
                    Toast.makeText(this, "Please try again, Public Key is Null", Toast.LENGTH_SHORT)
                        .show()
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

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun openFilePicker() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_PERMISSION_CODE)
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        openFileLauncher.launch(intent)
    }

    private fun recursiveAddFileName(fileName: String, extension:String){
        val newFileName = fileName.substring(0, fileName.lastIndexOf(".")) +  "(1)"
        val finalName = "$newFileName$extension.aes"
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

                    val newFileName = "$fileName.aes"
                    file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), newFileName)
                    if (!file.exists()) {
                        file.createNewFile()
                    }else{
                        val extension = fileName.substring(fileName.lastIndexOf("."), fileName.length)
                        recursiveAddFileName(fileName, extension)
                    }

                    outputStream = FileOutputStream(file)

                    binding.textFileName.visibility = View.VISIBLE
                    binding.textFileName.text = displayName.toEditable()

                    var buttonDrawable: Drawable = binding.btnEncryptFile.background
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable)
                    DrawableCompat.setTint(
                        buttonDrawable,
                        resources.getColor(R.color.darkerMainTheme, null)
                    )
                    binding.btnEncryptFile.background = buttonDrawable

                    fileReady = true

                }
            }
        }
        //endregion
    }
}