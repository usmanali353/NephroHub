package usmanali.nephrohub;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.paperdb.Paper;


/**
 * A simple {@link Fragment} subclass.
 */
public class KCG_Reports extends Fragment {

FirebaseDatabase db;
    DatabaseReference LabReportsref;

    public KCG_Reports() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_kcg_reports, container, false);
        Paper.init(getActivity());
        db=FirebaseDatabase.getInstance();
        SimpleDateFormat df =new SimpleDateFormat("dd MMMM yyyy");
        Date now = new Date();
        String date = df.format(now);

        LabReportsref=db.getReference("Labreport/"+Paper.book().read("user_id"));
        LabReportsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               Log.e("labreports", String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("labreports",databaseError.getMessage());
            }
        });
        return v;
    }

}
