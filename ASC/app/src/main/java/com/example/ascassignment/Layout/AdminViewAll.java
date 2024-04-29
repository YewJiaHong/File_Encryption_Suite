package com.example.ascassignment.Layout;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ascassignment.Adapter.EmployeeListAdapter;
import com.example.ascassignment.Database.Credentials;
import com.example.ascassignment.R;

public class AdminViewAll extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_all_employees);

        Credentials dbAcess = Credentials.getInstance(this);

        //region Toolbar
        ImageButton mButtonBackAdminViewEmployee = findViewById(R.id.buttonBackAdminViewEmployee);
        TextView mAdminName = findViewById(R.id.adminName);

        mAdminName.setText(LoginPage.userCredentials.name);
        mButtonBackAdminViewEmployee.setOnClickListener(v -> {
            onBackPressed();
        });
        //endregion

        RecyclerView mEmployeeList = findViewById(R.id.employeeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        EmployeeListAdapter adapter = new EmployeeListAdapter(dbAcess.getAllCredentials());
        mEmployeeList.setLayoutManager(layoutManager);
        mEmployeeList.setAdapter(adapter);
    }


}
