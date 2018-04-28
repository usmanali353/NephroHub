package usmanali.nephrohub;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import android.support.v7.widget.RecyclerView.Adapter;

/**
 * Created by SAJIDCOMPUTERS on 4/10/2018.
 */

public class scanned_report_popup_adapter extends BaseAdapter {
    ArrayList<String> testlist;
    ArrayList<String> resultlist;

    public scanned_report_popup_adapter(ArrayList<String> testlist, ArrayList<String> resultlist) {
        this.testlist = testlist;
        this.resultlist = resultlist;
    }

    @Override
    public int getCount() {
        return testlist.size();
    }

    @Override
    public Object getItem(int position) {
        return testlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.scanned_report_popup_layout,parent,false);
        }
        TextView tests=(TextView) convertView.findViewById(R.id.tests);
        TextView results=(TextView) convertView.findViewById(R.id.results);
        tests.setText(testlist.get(position));
        results.setText(resultlist.get(position));
        return convertView;
    }
}
