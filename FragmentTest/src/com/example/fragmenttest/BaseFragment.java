package com.example.fragmenttest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class BaseFragment extends Fragment {  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.fragment, null);  
        TextView textView = (TextView) view.findViewById(R.id.txt_content);  
        textView.setText(initContent());  
        return view;  
    }  
  
    public abstract String initContent();  
}  
