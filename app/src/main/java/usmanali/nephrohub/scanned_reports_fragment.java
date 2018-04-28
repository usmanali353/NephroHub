package usmanali.nephrohub;

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

import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * Created by SAJIDCOMPUTERS on 4/6/2018.
 */

public class scanned_reports_fragment extends Fragment {
    FirebaseDatabase db;
    DatabaseReference Scanned_reports;
    ListView list;
    ArrayList<Scanned_reports> reportsArrayList;
    ProgressBar pb;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.reports_fragment_layout,container,false);
        list=(ListView) v.findViewById(R.id.medical_records_list);
        pb=(ProgressBar) v.findViewById(R.id.pb);
        Paper.init(getActivity());
        reportsArrayList=new ArrayList<>();
        db = FirebaseDatabase.getInstance();
        pb.setVisibility(View.VISIBLE);
        Scanned_reports = db.getReference("Scanned Reports");
        Scanned_reports.child(Paper.book().read("Registration_number").toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :dataSnapshot.getChildren() ){
                    Scanned_reports s=snapshot.getValue(Scanned_reports.class);
                    reportsArrayList.add(s);
                }
                if(reportsArrayList.size()>0) {
                    pb.setVisibility(View.GONE);
                    list.setAdapter(new scanned_reports_adapter(reportsArrayList,getActivity()));
                }else{
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"No Reports Added Yet",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }
}
