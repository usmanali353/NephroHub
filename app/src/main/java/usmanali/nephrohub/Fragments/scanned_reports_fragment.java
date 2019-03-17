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
import usmanali.nephrohub.Adapters.scanned_reports_adapter;
import usmanali.nephrohub.R;
import usmanali.nephrohub.Model.Scanned_reports;
import usmanali.nephrohub.Model.dbhelper;

/**
 * Created by SAJIDCOMPUTERS on 4/6/2018.
 */

public class scanned_reports_fragment extends Fragment {
    FirebaseDatabase db;
    DatabaseReference Scanned_reports;
    ListView list;
    ArrayList<usmanali.nephrohub.Model.Scanned_reports> reportsArrayList;
    ProgressBar pb;
    String json;
    dbhelper dbh;
    usmanali.nephrohub.Model.Scanned_reports sr;
    Gson g;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.reports_fragment_layout,container,false);
        list=(ListView) v.findViewById(R.id.medical_records_list);
        pb=(ProgressBar) v.findViewById(R.id.pb);
        Paper.init(getActivity());
        dbh=new dbhelper(getActivity());
        String reg_num= Paper.book().read("user_id","Not Found").toString();
        g=new Gson();
        reportsArrayList=new ArrayList<>();
        db = FirebaseDatabase.getInstance();

        Scanned_reports = db.getReference("Scanned Reports");
        if(!reg_num.equals("Not Found")&&isNetworkAvailable()) {
            pb.setVisibility(View.VISIBLE);
            Scanned_reports.child(Paper.book().read("user_id").toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Scanned_reports s = snapshot.getValue(Scanned_reports.class);
                        reportsArrayList.add(s);
                    }
                    if (reportsArrayList.size() > 0) {
                        pb.setVisibility(View.GONE);
                        Collections.reverse(reportsArrayList);
                        Paper.book().write("Offline_scanned_reports_list",reportsArrayList);
                        list.setAdapter(new scanned_reports_adapter(reportsArrayList, getActivity()));
                    } else {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "No Reports Added Yet", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                 pb.setVisibility(View.GONE);
                }
            });
        }else if(!reg_num.equals("Not Found")&&!isNetworkAvailable()){
            ArrayList<Scanned_reports> offline_image_reports_list=Paper.book().read("Offline_scanned_reports_list");
            list.setAdapter(new scanned_reports_adapter(offline_image_reports_list,getActivity()));
            pb.setVisibility(View.GONE);
        }else if(reg_num.equals("Not Found")){
            Cursor reports_cursor=dbh.read_scanned_reports();
            if(reports_cursor.getCount()==0){
                Toast.makeText(getActivity(), "No Reports Added Yet", Toast.LENGTH_LONG).show();
            }else{
                while (reports_cursor.moveToNext()) {
                String json=reports_cursor.getString(1);
                sr =g.fromJson(json, usmanali.nephrohub.Model.Scanned_reports.class);
                reportsArrayList.add(sr);
            }
            if(reportsArrayList.size()>0){
                Collections.reverse(reportsArrayList);
                list.setAdapter(new scanned_reports_adapter(reportsArrayList,getActivity()));
            }
        }
        }
        return v;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
