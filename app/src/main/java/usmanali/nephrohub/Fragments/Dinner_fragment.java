package usmanali.nephrohub.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

import usmanali.nephrohub.Model.Food;
import usmanali.nephrohub.R;
import usmanali.nephrohub.Adapters.food_list_adapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class Dinner_fragment extends Fragment {

    ArrayList<String> heading_list;
    ExpandableListAdapter adapter;
    HashMap<String,ArrayList<Food>> childs_list;
    public Dinner_fragment() {
        // Required empty public constructor
    }

ExpandableListView food_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.diet_plan_fragment_layout,container,false);
        food_list=(ExpandableListView) v.findViewById(R.id.food_list);
        heading_list=new ArrayList<>();
        childs_list=new HashMap<>();
        prepare_food_list();
        adapter=new food_list_adapter(heading_list,childs_list);
        food_list.setAdapter(adapter);
        food_list.expandGroup(0);
        food_list.expandGroup(1);
        food_list.expandGroup(2);
        return v;
    }
    public void prepare_food_list(){
        heading_list.add("Menu 1");
        heading_list.add("Menu 2");
        heading_list.add("Menu 3");
        ArrayList<Food> menu1=new ArrayList<>();
        ArrayList<Food> menu2=new ArrayList<>();
        ArrayList<Food> menu3=new ArrayList<>();
        menu1.add(new Food(" سیب",R.drawable.apple));
        menu1.add(new Food("brinjal",R.drawable.brinjal));
        menu1.add(new Food("Sphgetti",R.drawable.spaghetti));
        menu2.add(new Food("گاجر",R.drawable.carrots));
        menu2.add(new Food("مچھلی",R.drawable.fish));
        menu2.add(new Food("آدھا کپ کھیر",R.drawable.kheer));
        menu3.add(new Food("Custard",R.drawable.custard));
        menu3.add(new Food(" سفید چاول",R.drawable.rice));
        menu3.add(new Food("آلو بحارا",R.drawable.plum));
        childs_list.put(heading_list.get(0),menu1);
        childs_list.put(heading_list.get(1),menu2);
        childs_list.put(heading_list.get(2),menu3);
    }
}
