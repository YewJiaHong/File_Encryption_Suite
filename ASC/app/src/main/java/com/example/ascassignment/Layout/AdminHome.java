package com.example.ascassignment.Layout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascassignment.R;

public class AdminHome extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        TextView mAdminName = findViewById(R.id.adminName);

        Button mButtonViewAllEmployees = findViewById(R.id.buttonViewAllEmployees);
        Button mButtonSearchEmployees = findViewById(R.id.buttonSearchEmployees);
        Button mButtonAddEmployees = findViewById(R.id.buttonAddEmployees);
        Button mButtonLogout = findViewById(R.id.adminHomeLogout);

        mAdminName.setText(LoginPage.userCredentials.name);

        mButtonViewAllEmployees.setOnClickListener(v -> {
            Intent toViewEmployees = new Intent(this, AdminViewAll.class);
            startActivity(toViewEmployees);
        });

        mButtonSearchEmployees.setOnClickListener(v -> {
            Intent toSearchEmployee = new Intent(this, AdminSearchEmployee.class);
            startActivity(toSearchEmployee);
        });

        mButtonAddEmployees.setOnClickListener(v -> {
            Intent toAddEmployee = new Intent(this, AdminAddEmployee.class);
            startActivity(toAddEmployee);
        });

        mButtonLogout.setOnClickListener(v -> {
            Logout();
        });
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

    public void onBackPressed() {
        Logout();
    }
}
