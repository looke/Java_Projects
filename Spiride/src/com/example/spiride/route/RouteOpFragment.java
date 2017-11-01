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

                //��Ӧ�¼���������·����Activity
        		Intent intent=new Intent();
        		intent.setClass(RouteOpFragment.this.getActivity(),RouteAddActivity.class);//��ǰ��ActivityΪTest,Ŀ��ActivityΪName
        		//���������п�ʼ�ǽ����ݴ����µ�Activity,����������ݣ�ֻ�Ǽ򵥵���ת���⼸�д�����ע�͵�
        		Bundle bundle=new Bundle();
        		bundle.putString("RouteName","value1");//key1Ϊ����value1Ϊֵ
        		bundle.putString("StartPlace","value2");
        		bundle.putString("EndPlace","value2");
        		intent.putExtras(bundle);
        		//�����ݽ���
        		startActivity(intent);
            }
        });
        return view;  
    }
  
    
}  
