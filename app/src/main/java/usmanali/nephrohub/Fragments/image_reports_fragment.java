package usmanali.nephrohub.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import io.paperdb.Paper;
import usmanali.nephrohub.Adapters.medical_records_list_adapter;
import usmanali.nephrohub.R;
import usmanali.nephrohub.Model.Reports;
import usmanali.nephrohub.Model.dbhelper;

/**
 * Created by SAJIDCOMPUTERS on 4/6/2018.
 */

public class image_reports_fragment extends Fragment {
    ArrayList<Reports> reportsArrayList;
    FirebaseDatabase db;
    DatabaseReference image_reports;
     ListView list;
     ProgressBar pb;
    String json;
    dbhelper dbh;
    Gson g;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.reports_fragment_layout,container,false);
        list=(ListView) v.findViewById(R.id.medical_records_list);
        Paper.init(getActivity());
        String reg_num= Paper.book().read("user_id","Not Found").toString();
        dbh=new dbhelper(getActivity());
        g=new Gson();
            reportsArrayList=new ArrayList<>();
        db = FirebaseDatabase.getInstance();
       pb=(ProgressBar) v.findViewById(R.id.pb);
       // pb.setVisibility(View.VISIBLE);
        image_reports = db.getReference("Image Reports");
     if(!reg_num.equals("Not Found")&&isNetworkAvailable()) {
         pb.setVisibility(View.VISIBLE);
            image_reports.child(Paper.book().read("user_id").toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Reports report = snapshot.getValue(Reports.class);
                        reportsArrayList.add(report);
                    }
                    if (reportsArrayList.size() > 0) {
                        pb.setVisibility(View.GONE);
                        //Gson g=new Gson();
                         //json=g.toJson(reportsArrayList);
                        Collections.reverse(reportsArrayList);
                        Paper.book().write("Offline_image_reports_list",reportsArrayList);
                        list.setAdapter(new medical_records_list_adapter(getActivity(), reportsArrayList));
                    } else {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "No Reports Added Yet", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else if(!reg_num.equals("Not Found")&&!isNetworkAvailable()){
         pb.setVisibility(View.GONE);
         ArrayList<Reports> offline_image_reports_list=Paper.book().read("Offline_image_reports_list");
         list.setAdapter(new medical_records_list_adapter(getActivity(),offline_image_reports_list));
     }else if(reg_num.equals("Not Found")){

        fetch_image_reports_guest();
     }
        return v;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void fetch_image_reports_guest(){
        Cursor reports_cursor=dbh.read_image_reports();
        if(reports_cursor.getCount()==0){
            Toast.makeText(getActivity(), "No Reports Added Yet", Toast.LENGTH_LONG).show();
        }else{
            while (reports_cursor.moveToNext()) {
                String json=reports_cursor.getString(1);
              Reports  r =g.fromJson(json, Reports.class);
                reportsArrayList.add(r);
            }
            if(reportsArrayList.size()>0){
                Collections.reverse(reportsArrayList);
                list.setAdapter(new medical_records_list_adapter(getActivity(),reportsArrayList));
            }
        }
        }
    }


