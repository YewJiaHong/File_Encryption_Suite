package com.example.ascassignment.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ascassignment.R;
import com.example.ascassignment.Table.Credential;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.ViewHolder>{
    private static final String TAG = "EmployeeListAdapter";
    private ArrayList<Credential> localDataSet;
    private View view = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mEmpName, mLastLogin, mEmpID, mRole, mLastMod, mModUsr;
        private ImageView mBtnCopy;

        public ViewHolder(View view) {
            super(view);

            ConstraintLayout empItem = view.findViewById(R.id.empItem);
            mEmpName = view.findViewById(R.id.empName);
            mLastLogin = view.findViewById(R.id.lastLogin);
            mEmpID = view.findViewById(R.id.empID);
            mRole = view.findViewById(R.id.role);
            mLastMod = view.findViewById(R.id.lastModified);
            mModUsr = view.findViewById(R.id.modUsr);
            mBtnCopy = view.findViewById(R.id.btnCopy);
        }

        public TextView getEmpName(){
            return mEmpName;
        }
    }

    public EmployeeListAdapter(ArrayList<Credential> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.employee_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String lastLoginMillis = localDataSet.get(position).lastLogin;

        viewHolder.getEmpName().setText(localDataSet.get(position).name);
        viewHolder.mEmpID.setText("Employee ID :" + localDataSet.get(position).empID);
        viewHolder.mRole.setText("Role: "+ ((localDataSet.get(position).role).equals("1") ? "Employee" : "Admin"));
        viewHolder.mModUsr.setText("Modified By: " + localDataSet.get(position).modUsr);

        Date date = new Date(Long.parseLong(localDataSet.get(position).lastModified));
        DateFormat form = new SimpleDateFormat("dd:MM:YYYY");
        form.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateModified = form.format(date);

        viewHolder.mLastMod.setText("Last Modified: " + dateModified);


        if (lastLoginMillis.equals("0")) {
            viewHolder.mLastLogin.setText("Last Login: Never");
        } else{
            Date date2 = new Date(Long.parseLong(lastLoginMillis));
            DateFormat formatter = new SimpleDateFormat("dd:MM:YYYY");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String dateFormatted = formatter.format(date);


            viewHolder.mLastLogin.setText("Last Login: " + dateFormatted);
        }

        viewHolder.mBtnCopy.setOnClickListener(v -> {
            if (view == null){
                Log.e(TAG, "onBindViewHolder: view is null");
                return;
            }
            ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", localDataSet.get(position).empID);
            if (clipboard == null || clip == null) return;
            clipboard.setPrimaryClip(clip);

            Toast.makeText(view.getContext(), "Employee ID Copied", Toast.LENGTH_SHORT).show();
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
