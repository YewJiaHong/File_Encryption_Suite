package com.example.ascassignment.Layout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascassignment.Database.Credentials;
import com.example.ascassignment.Misc.Hashing;
import com.example.ascassignment.R;
import com.example.ascassignment.Table.Credential;

public class AdminAddEmployee extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "AdminAddEmployee";
    private Credentials dbAccess = null;
    private EditText mAddEmployeeUsername;
    private EditText mAddEmployeePassword;
    private final Credential credential = new Credential();
    private TextView mErrorMsg;
    private static final String[] paths = {"Employee", "Admin"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_employee);

        dbAccess = Credentials.getInstance(getApplicationContext());

        //region Toolbar
        ImageButton mButtonBackAdminAddEmployee = findViewById(R.id.buttonBackAdminAddEmployee);
        TextView mAdminName = findViewById(R.id.adminName);

        mAdminName.setText(LoginPage.userCredentials.name);
        mButtonBackAdminAddEmployee.setOnClickListener(v -> {
            onBackPressed();
        });
        //endregion

        mAddEmployeeUsername = findViewById(R.id.addEmployeeUsername);
        mAddEmployeePassword = findViewById(R.id.addEmployeePassword);
        Spinner mAddEmployeeRole = findViewById(R.id.addEmployeeRole);
        EditText mAddEmployeeEmpID = findViewById(R.id.addEmployeeEmpID);
        Button mAdminButtonAddEmp = findViewById(R.id.adminButtonAddEmp);
        mErrorMsg = findViewById(R.id.errorMsg);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminAddEmployee.this,
                android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAddEmployeeRole.setAdapter(adapter);
        mAddEmployeeRole.setOnItemSelectedListener(this);
        mAddEmployeeRole.setSelection(0);

        mAdminButtonAddEmp.setOnClickListener(v -> {
            if (LoginPage.isEmpty(mAddEmployeeUsername) || LoginPage.isEmpty(mAddEmployeePassword)
                    || LoginPage.isEmpty(mAddEmployeeEmpID)){
                mErrorMsg.setText(R.string.fillUpAll);
                return;
            }

            if(Hashing.ENABLED) {
                passwordHashing(mAddEmployeePassword.getText().toString(), mAddEmployeeEmpID.getText().toString());
            } else{
                credential.name = mAddEmployeeUsername.getText().toString();
                credential.password = mAddEmployeePassword.getText().toString();
                credential.empID = mAddEmployeeEmpID.getText().toString();
                credential.lastLogin = "0";
                credential.lastModified = String.valueOf(System.currentTimeMillis());
                credential.modUsr = LoginPage.userCredentials.empID;
            }

            Long ret = addEmployee(credential);
            if (ret > 0){
                Toast.makeText(this, "Employee Successfully Added!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else{
                Toast.makeText(this, "Fail to Add Employee. Code: " + ret, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "addEmployee: Failed to add. Code: " + ret);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                credential.role = "1";
                break;
            case 1:
                credential.role = "0";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void passwordHashing(String ptPassword, String empID){
        String hashedPassword = Hashing.hashPassword(ptPassword);

        credential.name = mAddEmployeeUsername.getText().toString();
        credential.password = hashedPassword;
        credential.empID = empID;
        credential.lastLogin = "0";
        credential.lastModified = String.valueOf(System.currentTimeMillis());
        credential.modUsr = LoginPage.userCredentials.empID;
    }

    private Long addEmployee(Credential credential){
        return dbAccess.insertCredentials(credential);
    }
}
