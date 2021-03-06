package com.example.drhappy.witcherpedia;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
	
	public HomeFragment() {
		// Required empty public constructor
	}
	
	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment HomeFragment.
	 */
	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();

		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((Witcherpedia)getActivity().getApplicationContext()).setCurrent_view("Witcherpedia");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_home, container, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(((Witcherpedia)getActivity().getApplicationContext()).getCurrent_view());
		Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setSubtitle("");
	}
}
