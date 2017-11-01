package com.example.spiride.route;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.example.spiride.MainActivity;
import com.example.spiride.R;
import com.example.spiride.R.id;
import com.example.spiride.R.layout;
import com.example.spiride.util.MyListAdapter;
import com.example.spiride.util.MyListView;
import com.example.spiride.util.SpirideDBHelper;
import com.example.spiride.util.MyListView.OnRefreshListener;

public class RouteListFragment extends Fragment 
{  
	private ArrayList<HashMap<String, Object>> listItem;
	private MyListAdapter myAdapter;
	private MyListView lv;
	private SpirideDBHelper dbHelper;
	
	private Handler handler;
	
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.fragment_route_list, null);  
        
        dbHelper = new SpirideDBHelper(this.getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //ListView lv= (ListView)view.findViewById(R.id.route_List);
        lv= (MyListView)view.findViewById(R.id.Mylv);
        /* �������д������ */
		listItem = new ArrayList<HashMap<String, Object>>();
        
		dbHelper.getAllRouteData(listItem);
		
		
		//SimpleAdapter smAdapter = new SimpleAdapter(this.getActivity(), listItem,
		//		R.layout.fragment_route_list_item, new String[] { "id", "name", "start",
		//				"end","number" }, new int[] { R.id.route_list_item_routeID, R.id.route_list_item_routeName,
		//				R.id.route_list_item_routeStart, R.id.route_list_item_routeEnd, R.id.route_list_item_routeSampleNum});
		//lv.setAdapter(smAdapter);
		myAdapter = new MyListAdapter(this.getActivity(), listItem);
		lv.setAdapter(myAdapter);
		
		lv.setonRefreshListener(new OnRefreshListener() 
		{  

            @Override  
            public void onRefresh() 
            {  
                new AsyncTask<Void, Void, Void>() 
                {  
                    protected Void doInBackground(Void... params) 
                    {  
                    	Message msg = new Message();
                    	msg.what = 0;
                    	handler.sendMessage(msg);
                    	//listItem.clear();
                    	//dbHelper.getAllRouteData(listItem); 
                        return null;
                    }
  
                    @Override
                    protected void onPostExecute(Void result)
                    {
                    	myAdapter.notifyDataSetChanged();  
                        lv.onRefreshComplete();  
                    }
                }.execute(null, null, null);  
            }
        });
		
		//���item�����˵�
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
	            public void onCreateContextMenu(ContextMenu menu, View v,
	                    ContextMenuInfo menuInfo) {
	                menu.setHeaderTitle("ѡ�����");
	                menu.add(0, 0, 0, "��Ϊ��ǰ�ɼ���·");
	                
	            }
	        });
		
		setHandler();
        return view;
    }  
  
    @Override
    public boolean onContextItemSelected(MenuItem item) 
    {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        //info.id�õ�listview��ѡ�����Ŀ�󶨵�id
        String id = String.valueOf(info.id); 
        HashMap<String, Object> selectedRoute = (HashMap<String, Object>)lv.getAdapter().getItem(info.position);
        
        switch (item.getItemId()) 
        {
        	case 0: //�趨�ɼ�·��
        		//updateDialog(id);  //�����¼��ķ���
        		String selectedRouteID = selectedRoute.get(SpirideDBHelper.COLUMN_NAME_ROUTEID).toString();
        		String selectedRouteName = selectedRoute.get(SpirideDBHelper.COLUMN_NAME_ROUTENAME).toString();
        		((MainActivity)this.getActivity()).setSelectedRouteID(Integer.valueOf(selectedRouteID));
        		
        		
        		Toast.makeText(this.getActivity(), selectedRouteName + "�ѱ�����Ϊ��ǰ�ɼ���·", 1).show();
        		return true;
        	default:
        		return super.onContextItemSelected(item);
        }
    }
    
    @Override
    public void onHiddenChanged(boolean hidden)
    {
    	Toast.makeText(this.getActivity(),"HiddenChanged:"+hidden, Toast.LENGTH_SHORT).show();
    }
    
    private void setHandler()
    {
    	final Context ct = this.getActivity().getApplicationContext();
    	
    	handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				
				switch (msg.what)
				{
					case 0: //�����б�
						listItem.clear();
                    	dbHelper.getAllRouteData(listItem); 
                    	Toast.makeText(ct,"�б��Ѹ���", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};
    }
}  
