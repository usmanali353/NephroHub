package usmanali.nephrohub;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import io.paperdb.Paper;

/**
 * Created by SAJIDCOMPUTERS on 4/6/2018.
 */

public class prescriptions_fragment extends Fragment {
    ListView prescription_list;
    dbhelper dbh;
    String reg_num;
    Gson g;
    ArrayList<Prescription> prescription_arraylist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.reports_fragment_layout, container, false);
        prescription_list = (ListView) v.findViewById(R.id.medical_records_list);
        dbh = new dbhelper(getActivity());
        Paper.init(getActivity());
        reg_num = Paper.book().read("Registration_number", "Not Found");
        prescription_arraylist=new ArrayList<>();
        g = new Gson();
        if (reg_num.equals("Not Found")) {
            Cursor reports_cursor = dbh.read_prescription();
            if (reports_cursor.getCount() == 0) {
                Toast.makeText(getActivity(), "No Reports Added Yet", Toast.LENGTH_LONG).show();
            } else {
                while (reports_cursor.moveToNext()) {
                    String json = reports_cursor.getString(1);
                    Prescription sr = g.fromJson(json, Prescription.class);
                    prescription_arraylist.add(sr);
                }
                if (prescription_arraylist.size() > 0) {
                    Collections.reverse(prescription_arraylist);
                    prescription_list.setAdapter(new Prescription_adapter(prescription_arraylist));
                }
            }

        }
        return v;
    }
}

