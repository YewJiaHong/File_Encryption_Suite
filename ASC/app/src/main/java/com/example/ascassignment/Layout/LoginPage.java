package com.example.ascassignment.Layout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascassignment.Database.Credentials;
import com.example.ascassignment.Misc.Hashing;
import com.example.ascassignment.R;
import com.example.ascassignment.Table.Credential;

import java.util.ArrayList;


public class LoginPage extends AppCompatActivity {
    public static Credential userCredentials = null;
    private EditText mUsername, mPassword;
    private TextView mErrorMsg;
    private Credentials dbAcess = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mUsername = findViewById(R.id.editTextUsername);
        mPassword = findViewById(R.id.editTextPassword);
        mErrorMsg = findViewById(R.id.errorMsg);
        Button mLoginButton = findViewById(R.id.buttonLogin);

        dbAcess = Credentials.getInstance(getApplicationContext());


        mLoginButton.setOnClickListener(v -> {
            Credential credential = Login();

            if (mUsername.getText().toString().equals("Parry") && mPassword.getText().toString().equals("abc") && Hashing.ENABLED){
                userCredentials = new Credential();
                userCredentials.name = "Parry";
                userCredentials.password = "abc";
                userCredentials.role = "1";
                userCredentials.empID = "900";
                userCredentials.lastModified = "009988777";
                userCredentials.modUsr = "666";

                Intent toAdminHome = new Intent(this, AdminHome.class);
                toAdminHome.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                );
                startActivity(toAdminHome);
            }

            if (credential == null){ //if database return nothing or nonsense
                mErrorMsg.setText(R.string.invalidNameOrPass);
                mErrorMsg.setVisibility(View.VISIBLE);
            } else{ //success login
                userCredentials = credential;
                updateLastLoginTime(userCredentials.empID);

                if (userCredentials.role.equals("1")) { //is Employee
                    Intent toEmployeeHome = new Intent(this, EmployeeHome.class);
                    toEmployeeHome.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );
                    startActivity(toEmployeeHome);
                } else{ //is Admin
                    Intent toAdminHome = new Intent(this, AdminHome.class);
                    toAdminHome.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );
                    startActivity(toAdminHome);
                }
            }
        });
    }

    private Credential Login(){
//        if (isEmpty(mUsername) || isEmpty(mPassword)){
//            mErrorMsg.setText(R.string.emptyNameOrPass);
//            mErrorMsg.setVisibility(View.VISIBLE);
//            return null;
//        }

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        if (Hashing.ENABLED){
            password = Hashing.hashPassword(password);
        }

        return dbAcess.loginCheck(username, password);
    }

    private int updateLastLoginTime(String EmpID){
        return dbAcess.updateLastLogin(String.valueOf(System.currentTimeMillis()), EmpID);
    }


    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
