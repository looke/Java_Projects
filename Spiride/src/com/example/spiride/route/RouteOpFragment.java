package com.example.spiride.route;

import java.util.jar.Attributes.Name;

import com.example.spiride.R;
import com.example.spiride.R.id;
import com.example.spiride.R.layout;

import junit.framework.Test;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class RouteOpFragment extends Fragment 
{  
	
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.fragment_route_op, null);  
        
        Button btn = (Button)view.findViewById(R.id.btn_route_add);
        
        btn.setOnClickListener(new OnClickListener(){
        	
        	public void onClick(View v) {

                //响应事件，启动线路创建Activity
        		Intent intent=new Intent();
        		intent.setClass(RouteOpFragment.this.getActivity(),RouteAddActivity.class);//当前的Activity为Test,目标Activity为Name
        		//从下面这行开始是将数据传给新的Activity,如果不传数据，只是简单的跳转，这几行代码请注释掉
        		Bundle bundle=new Bundle();
        		bundle.putString("RouteName","value1");//key1为名，value1为值
        		bundle.putString("StartPlace","value2");
        		bundle.putString("EndPlace","value2");
        		intent.putExtras(bundle);
        		//传数据结束
        		startActivity(intent);
            }
        });
        return view;  
    }
  
    
}  
