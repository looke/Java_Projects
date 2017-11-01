package com.example.fragmenttest;

import android.app.Fragment;

public class FragmentFactory {  
    public static Fragment getInstanceByIndex(int index) {  
        Fragment fragment = null;  
        switch (index) {  
            case 1:  
                fragment = new AttentionFragment();  
                break;  
            case 2:  
                fragment = new AtmeFragment();  
                break;  
            
        }  
        return fragment;  
    }  
}  
