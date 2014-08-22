package com.M.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		OnGetBusLineSearchResultListener, OnGetPoiSearchResultListener {

	// 搜索相关
	private PoiSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private BusLineSearch mBusLineSearch = null;

	//
	Button exampl = null;
	private BusLineResult route = null;// 保存驾车/步行路线数据的变量，供浏览节点时使用
	private List<String> busLineIDList = null;
	TextView text = null;

	//
	ArrayList<String>busStations=new ArrayList<String>();
	
	//定位相关：
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="gcj02";
	private LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		mSearch = PoiSearch.newInstance();
		mSearch.setOnGetPoiSearchResultListener(this);
		mBusLineSearch = BusLineSearch.newInstance();
		mBusLineSearch.setOnGetBusLineSearchResultListener(this);
		busLineIDList = new ArrayList<String>();
		text = (TextView) findViewById(R.id.textView1);
		
		
		
		//定位相关：
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		
		exampl = (Button) findViewById(R.id.button1);
		exampl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				InitLocation();
				if (mLocationClient == null) {
					return;
				}
				if (mLocationClient.isStarted()) {
					exampl.setText("Start");
					mLocationClient.stop();
				}else {
					exampl.setText("Stop");
					mLocationClient.start();
					/*
					 *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
					 *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
					 *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
					 *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
					 *定时定位时，调用一次requestLocation，会定时监听到定位结果。
					 */
					mLocationClient.requestLocation();
				}
				
				// TODO Auto-generated method stub
//				Intent intent=new Intent(MainActivity.this,StationsList.class);
//				Bundle bundle=new Bundle();
//				bundle.putStringArrayList("list", busStations);
//				intent.putExtras(bundle);
//				MainActivity.this.startActivityForResult(intent, 1);
			}

		
		});
		
//		mSearch.searchInCity(new PoiCitySearchOption().city("济南").keyword("119"));
//		PoiNearbySearchOption option1=new PoiNearbySearchOption().keyword("公交站").location(new LatLng(36.667689, 117.143811)).radius(1000);
//		mSearch.searchNearby(option1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println(resultCode+data.getStringExtra("theIdselected"));
		text.setText(""+resultCode);
		text.append(data.getStringExtra("theIdSelected"));
	}
	
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
	}

	@Override
	public void onGetBusLineResult(BusLineResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			System.out.println("no result");
			return;
		}
		route = result;
		List<BusLineResult.BusStation> st = route.getStations();
		for (Iterator<BusLineResult.BusStation> i = st.iterator(); i.hasNext();) {
			// System.out.println("station"+i.next().getTitle());
//			text.append("station" + i.next().getTitle());
			busStations.add(i.next().getTitle());
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void onGetPoiResult(PoiResult result) {
//		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//			System.out.println("no result");
//			return;
//		}
//		// 遍历所有poi，找到类型为公交线路的poi
//		busLineIDList.clear();
//		for (PoiInfo poi : result.getAllPoi()) {
//			if (poi.type == PoiInfo.POITYPE.BUS_LINE
//					|| poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
//				busLineIDList.add(poi.uid);
//			}
//		}
//		mBusLineSearch.searchBusLine(new BusLineSearchOption().city("济南").uid(
//				busLineIDList.get(0)));
//
//	}
	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			System.out.println("no result");
			return;
		}
		for (PoiInfo poi : result.getAllPoi()) {
			if (poi.type == PoiInfo.POITYPE.BUS_STATION) {
				System.out.println(poi.name+"name");
				text.append(poi.name);
			}
		}

	}
	
	
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
		int span=5000;
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		mLocationClient.setLocOption(option);
	}
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			System.out.println(location.getLatitude());
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			System.out.println(location.getLongitude());
			sb.append(location.getLongitude());
			
			
			PoiNearbySearchOption option1=new PoiNearbySearchOption().keyword("公交站").location(new LatLng(location.getLatitude(), location.getLongitude())).radius(500);
			mSearch.searchNearby(option1);
			
			
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			text.append(sb.toString());
			Log.i("BaiduLocationApiDem", sb.toString());
		}
		

	}
	
	
	
}