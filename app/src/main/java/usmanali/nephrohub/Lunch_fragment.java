package usmanali.nephrohub;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Lunch_fragment extends Fragment {


    public Lunch_fragment() {
        // Required empty public constructor
    }

ListView food_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.diet_plan_fragment_layout,container,false);
        food_list=(ListView) v.findViewById(R.id.food_list);
        return v;

    }

}
