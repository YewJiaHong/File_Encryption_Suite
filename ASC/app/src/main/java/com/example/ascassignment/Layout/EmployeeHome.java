package com.example.ascassignment.Layout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascassignment.Database.Credentials;
import com.example.ascassignment.Misc.Hashing;
import com.example.ascassignment.R;
import com.example.ascassignment.Table.Credential;

public class EmployeeHome extends AppCompatActivity {
    private Credentials dbAcess = null;
    private EditText mUsername, mPassword, mEmpID, mRole;
    private TextView mErrorMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);

        mUsername = findViewById(R.id.empEditEmpName);
        mPassword = findViewById(R.id.empEditEmpPassword);
        mRole = findViewById(R.id.empEditEmpRole);
        mEmpID = findViewById(R.id.empEditEmpID);

        Button mUpdate = findViewById(R.id.empButtonUpdateEmp);
        Button mLogout = findViewById(R.id.empButtonLogout);

        mErrorMsg = findViewById(R.id.empErrorMsg);
        TextView mEmployeeName = findViewById(R.id.employeeName);

        dbAcess = Credentials.getInstance(getApplicationContext());

        mUsername.setText(LoginPage.userCredentials.name);
        mRole.setText("Employee");
        mEmpID.setText(LoginPage.userCredentials.empID);
        mEmployeeName.setText(LoginPage.userCredentials.name);

        mUpdate.setOnClickListener(v -> {
            if (LoginPage.isEmpty(mUsername) || LoginPage.isEmpty(mPassword)
                    || LoginPage.isEmpty(mEmpID) || LoginPage.isEmpty(mRole)){
                mErrorMsg.setText(R.string.fillUpAll);
                return;
            }

            Credential credential = new Credential();
            credential.name = mUsername.getText().toString();

            if (Hashing.ENABLED){
                credential.password = passwordHashing(mPassword.getText().toString());
            } else{
                credential.password = mPassword.getText().toString();
            }

            credential.empID = LoginPage.userCredentials.empID;
            credential.role = LoginPage.userCredentials.role;
            credential.lastLogin = LoginPage.userCredentials.lastLogin;
            credential.lastModified = String.valueOf(System.currentTimeMillis());
            credential.modUsr = LoginPage.userCredentials.empID;

            int ret = update(credential);
            if (ret == 1) {
                Toast.makeText(this, "Credentials Successfully Updated!", Toast.LENGTH_SHORT).show();
                Logout();
            } else {
                mErrorMsg.setText(getString(R.string.wrongRowsAffected, ret));
            }
        });

        mLogout.setOnClickListener(v-> {
            Logout();
        });
    }

    private int update(Credential credential){
        return dbAcess.updateCredentials(credential, Integer.parseInt(LoginPage.userCredentials.empID));
    }

    private void Logout(){
        LoginPage.userCredentials = null;
        Intent backToLogin = new Intent(this, LoginPage.class);
        backToLogin.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(backToLogin);
    }

    private String passwordHashing(String pt){
        return Hashing.hashPassword(pt);
    }

    @Override
    public void onBackPressed() {
        Logout();
    }
}
