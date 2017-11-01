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
        /* 在数组中存放数据 */
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
		
		//添加item长按菜单
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
	            public void onCreateContextMenu(ContextMenu menu, View v,
	                    ContextMenuInfo menuInfo) {
	                menu.setHeaderTitle("选择操作");
	                menu.add(0, 0, 0, "设为当前采集线路");
	                
	            }
	        });
		
		setHandler();
        return view;
    }  
  
    @Override
    public boolean onContextItemSelected(MenuItem item) 
    {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        //info.id得到listview中选择的条目绑定的id
        String id = String.valueOf(info.id); 
        HashMap<String, Object> selectedRoute = (HashMap<String, Object>)lv.getAdapter().getItem(info.position);
        
        switch (item.getItemId()) 
        {
        	case 0: //设定采集路线
        		//updateDialog(id);  //更新事件的方法
        		String selectedRouteID = selectedRoute.get(SpirideDBHelper.COLUMN_NAME_ROUTEID).toString();
        		String selectedRouteName = selectedRoute.get(SpirideDBHelper.COLUMN_NAME_ROUTENAME).toString();
        		((MainActivity)this.getActivity()).setSelectedRouteID(Integer.valueOf(selectedRouteID));
        		
        		
        		Toast.makeText(this.getActivity(), selectedRouteName + "已被设置为当前采集线路", 1).show();
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
					case 0: //更新列表
						listItem.clear();
                    	dbHelper.getAllRouteData(listItem); 
                    	Toast.makeText(ct,"列表已更新", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};
    }
}  
