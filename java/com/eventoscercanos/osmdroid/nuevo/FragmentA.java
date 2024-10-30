package com.eventoscercanos.osmdroid.nuevo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.eventoscercanos.osmdroid.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentA#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentA extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "activity_origen";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String activity_origen;
    private int grid_number;

    OnFragmentInteractionListener mListener;

    public FragmentA() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentA.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentA newInstance(String param1, int param2) {
        FragmentA fragment = new FragmentA();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity_origen = getArguments().getString(ARG_PARAM1);
            grid_number = getArguments().getInt(ARG_PARAM2);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //String[] items = getResources().getStringArray(R.array.tab_A);
        GridView gridView1 =  view.findViewById(R.id.gridview1);
        switch (grid_number){
            case 0:
                gridView1.setAdapter( new ImageAdapterGridview1(getContext()));
                break;
            case 1:
                gridView1.setAdapter( new ImageAdapterGridview2(getContext()));
                break;
            case 2:
                gridView1.setAdapter( new ImageAdapterGridview3(getContext()));
                break;
            case 3:
                gridView1.setAdapter( new ImageAdapterGridview4(getContext()));
                break;
            case 4:
                gridView1.setAdapter( new ImageAdapterGridview5(getContext()));
                break;
        }




        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    String icono = (String)parent.getItemAtPosition(position) ;
                    mListener.onFragmentInteraction(position, icono, activity_origen);
                }
            }

        });

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int pos, String icono, String activity_origen);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
