How to import the project?
A: Use Android Studio to open the directory "FileEncryptionSuite"

What is the purpose of this project
A: This project is to create a file encryption application for Android users to use, and for Android developers to look at if they wish to expand this project.

How to use the app?
A: The app has three main functions, file hashing, encryption, and decryption.
  Hashing can be accessed by clicking "Hash a File". You can select a file then generate the hash value (SHA-256) for the file.
  Encryption works by selecting a file, giving a key name, and key seeds. The application will use the key seed to generate a key by implementing ECDH concepts. The final public key will be used for AES encryption. After the encryption is finished, the public key value will be saved in database with the key name provided by the user.
  Decryption Works similarly, user can select an encrypted file and use the key value of the key that is used to encrypt the file to successfully ecrypt the file. The value can be found in key list, which can be accessed by clicking the floating button on the bottom right. The key value can be copied to the clipboard and past as the ky value to decrypt the file.
  All files that are saved to device can be found in the "Downloads" folder.
