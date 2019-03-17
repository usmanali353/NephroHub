package usmanali.nephrohub.Adapters;

import android.content.Intent;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import usmanali.nephrohub.Model.Prescription;
import usmanali.nephrohub.R;

/**
 * Created by HelloWorldSolution on 5/14/2018.
 */

public class Prescription_adapter extends BaseAdapter {
    ArrayList<Prescription> prescription_list;

    public Prescription_adapter(ArrayList<Prescription> prescription_arraylist) {
        this.prescription_list=prescription_arraylist;
    }

    @Override
    public int getCount() {
        return prescription_list.size();
    }

    @Override
    public Object getItem(int position) {
        return prescription_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        prescription_list_viewholder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prescription_layout, parent, false);
            holder=new prescription_list_viewholder();
            holder.prescription=(TextView) convertView.findViewById(R.id.doctor_instructions);
            convertView.setTag(holder);
        }else{
            holder=(prescription_list_viewholder) convertView.getTag();
        }
        holder.prescription.setText(prescription_list.get(position).getMedicine_name()+" "+prescription_list.get(position).getDosage()+" mg"+"\n"+prescription_list.get(position).getDoctor_instructions());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
                parent.getContext().startActivity(openClockIntent);
            }
        });
        return convertView;
    }
}
class prescription_list_viewholder{
    TextView prescription;
}