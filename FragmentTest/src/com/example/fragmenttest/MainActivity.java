package com.example.fragmenttest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;

public class MainActivity extends Activity
{
	
	private FragmentManager fragmentManager;  
    private RadioGroup radioGroup;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_main);
		
		  
        
        //setContentView(R.layout.weibo_tab);  
        
        fragmentManager = getFragmentManager();  
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab);  
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
                FragmentTransaction transaction = fragmentManager.beginTransaction();  
                Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId);  
                transaction.replace(R.id.content, fragment);  
                transaction.commit();  
            }  
        });  
	}
}
