package usmanali.nephrohub;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SAJIDCOMPUTERS on 4/10/2018.
 */

public class scanned_reports_adapter extends BaseAdapter {
    ArrayList<Scanned_reports> reportslist;
    Context c;
    public scanned_reports_adapter(ArrayList<Scanned_reports> reportslist,Context con) {
        this.reportslist = reportslist;
        this.c=con;
    }

    @Override
    public int getCount() {
        return reportslist.size();
    }

    @Override
    public Object getItem(int position) {
        return reportslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        final Scanned_reports medical_records=reportslist.get(position);
        scanned_reports_viewholder holder;
        LayoutInflater inflater=LayoutInflater.from(c);
        if(convertView==null) {
            convertView=inflater.inflate(R.layout.medical_records_list_layout, parent, false);
            holder=new scanned_reports_viewholder();
            holder.doctor_name=(TextView) convertView.findViewById(R.id.doctor_name);
            holder.report_date=(TextView) convertView.findViewById(R.id.report_date);
             holder.report_title=(TextView) convertView.findViewById(R.id.report_title);
            convertView.setTag(holder);
        }else{
            holder=(scanned_reports_viewholder) convertView.getTag();
        }
        holder.report_title.setText(medical_records.getReport_title());
        holder.doctor_name.setText(medical_records.getRef_by());
        holder.report_date.setText(medical_records.getDate());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater=LayoutInflater.from(c);
                View sv = inflater.inflate(R.layout.scanned_reporrt_layout,null);
                AlertDialog.Builder scanned_report_popup =new AlertDialog.Builder(c);
                scanned_report_popup.setTitle(medical_records.getReport_title());
                ListView rv=(ListView) sv.findViewById(R.id.testsandresults);
                View hv=LayoutInflater.from(c).inflate(R.layout.scanned_report_popup_layout,null);
                rv.addHeaderView(hv);
                rv.setAdapter(new scanned_report_popup_adapter(medical_records.getTests(),medical_records.getResults()));
                scanned_report_popup .setView(sv);
                scanned_report_popup.show();
            }
        });
        return convertView;
    }
}
class scanned_reports_viewholder{
    TextView doctor_name;
    TextView report_date;
    TextView report_title;
}