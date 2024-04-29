package com.example.ascassignment.Layout;

import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ascassignment.Database.Credentials;
import com.example.ascassignment.Misc.Hashing;
import com.example.ascassignment.R;
import com.example.ascassignment.Table.Credential;

public class AdminSearchEmployee extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Credentials dbAccess = null;
    private Credential credential = null;
    private Credential cre = new Credential();

    private static final String[] paths = {"Employee", "Admin"};

    private ConstraintLayout mSearchInterface, mEmployeeDetailsInterface;
    private EditText mEditTextSearchEmpID, mEditAdminUsername, mEditAdminPassword, mEditAdminEmpID;
    private Spinner mEditAdminRole;
    private TextView mErrorMsg;
    private TextView mErrMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_search_employees);

        dbAccess = Credentials.getInstance(getApplicationContext());

        //region Toolbar
        ImageButton mButtonBackAdminSearchEmployee = findViewById(R.id.buttonBackAdminSearchEmployee);
        TextView mAdminName = findViewById(R.id.adminName);

        mAdminName.setText(LoginPage.userCredentials.name);
        mButtonBackAdminSearchEmployee.setOnClickListener(v -> {
            onBackPressed();
        });
        //endregion

        //region Search interface
        mSearchInterface = findViewById(R.id.searchInterface);
        mEditTextSearchEmpID = findViewById(R.id.editTextSearchEmpID);
        Button mButtonAdminSearchEmployees = findViewById(R.id.buttonAdminSearchEmployees);
        mErrorMsg = findViewById(R.id.errorMsg);

        mButtonAdminSearchEmployees.setOnClickListener(v -> {
            if (LoginPage.isEmpty(mEditTextSearchEmpID)){
                mErrorMsg.setText(R.string.fillUpAll);
                return;
            }

            if (TextUtils.isDigitsOnly(mEditTextSearchEmpID.getText())){
                credential = searchEmployee(mEditTextSearchEmpID.getText().toString());

                if (credential == null){ //unable to find employee
                    mErrorMsg.setText(R.string.employee_does_not_exist);
                    mErrorMsg.setVisibility(View.VISIBLE);
                    return;
                }
                mErrorMsg.setVisibility(View.GONE);
                searchInterfaceToCredentialsInterface();
            } else {
                mErrorMsg.setText(R.string.onlyNumbers);
            }
        });
        //endregion

        //region Employee details interface
        mEmployeeDetailsInterface = findViewById(R.id.employeeDetailsInterface);
        mEditAdminUsername = findViewById(R.id.editAdminUsername);
        mEditAdminPassword = findViewById(R.id.editAdminPassword);
        mEditAdminRole = findViewById(R.id.adminEditEmpRole);
        mEditAdminEmpID = findViewById(R.id.editAdminEmpID);

        Button mAdminButtonUpdate = findViewById(R.id.adminButtonUpdate);
        Button mAdminButtonDelete = findViewById(R.id.adminButtonDelete);
        Button mAdminButtonBack = findViewById(R.id.adminButtonBack);
        mErrMsg = findViewById(R.id.errMsg);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminSearchEmployee.this,
                android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditAdminRole.setAdapter(adapter);
        mEditAdminRole.setOnItemSelectedListener(this);


        mAdminButtonUpdate.setOnClickListener(v -> {
            if (LoginPage.isEmpty(mEditAdminUsername) || LoginPage.isEmpty(mEditAdminPassword)
                    || LoginPage.isEmpty(mEditAdminEmpID)){
                mErrMsg.setText(R.string.fillUpAll);
                return;
            }

            if (Hashing.ENABLED){
                cre.password = passwordHashing(mEditAdminPassword.getText().toString());
            }else{
                cre.password = mEditAdminPassword.getText().toString();
            }

            cre.name = mEditAdminUsername.getText().toString();
            cre.empID = credential.empID;
            cre.lastLogin = credential.lastLogin;
            cre.lastModified = String.valueOf(System.currentTimeMillis());
            cre.modUsr = LoginPage.userCredentials.empID;

            int ret = updateEmployee(cre);
            if (ret == 1) {
                Toast.makeText(this, "Credentials Successfully Updated!", Toast.LENGTH_SHORT).show();
                credentialsInterfaceToSearchInterface();
            } else {
                mErrMsg.setText(getString(R.string.wrongRowsAffected, ret));
            }
        });

        mAdminButtonDelete.setOnClickListener(v -> {
            int ret = deleteEmployee(credential.empID);
            if (ret == 1) {
                Toast.makeText(this, "Employee Successfully Deleted!", Toast.LENGTH_SHORT).show();
                credentialsInterfaceToSearchInterface();
            } else {
                mErrMsg.setText(getString(R.string.wrongRowsAffected, ret));
            }
        });

        mAdminButtonBack.setOnClickListener(v -> {
            credentialsInterfaceToSearchInterface();
        });
        //endregion


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                cre.role = "1";
                break;
            case 1:
                cre.role = "0";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void credentialsInterfaceToSearchInterface(){
        mSearchInterface.setVisibility(View.VISIBLE);
        mEmployeeDetailsInterface.setVisibility(View.GONE);

        mEditAdminUsername.setText("");
        mEditAdminEmpID.setText("");
    }

    private void searchInterfaceToCredentialsInterface(){
        mSearchInterface.setVisibility(View.GONE);
        mEmployeeDetailsInterface.setVisibility(View.VISIBLE);

        mEditAdminUsername.setText(credential.name);
        mEditAdminRole.setSelection((credential.role).equals("1") ? 0 : 1);
        mEditAdminEmpID.setText(credential.empID);
    }

    private String passwordHashing(String pt){
        return Hashing.hashPassword(pt);
    }

    private int deleteEmployee(String empID){
        return dbAccess.deleteCredentials(empID);
    }

    private Credential searchEmployee(String empID){
        return dbAccess.searchEmployee(empID);
    }

    private int updateEmployee(Credential credential){
        return dbAccess.updateCredentials(credential, Integer.parseInt(credential.empID));
    }
}
