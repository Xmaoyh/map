package map;

import java.util.ArrayList;
import java.util.List;

import map.entities.Parent;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.example.map.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class MainActivity extends Activity {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private InfoWindow mInfoWindow;
	private int height;// 屏幕高度(px)
	private int width;// 屏幕宽度(px)
	private ArrayList<LatLng> markerList = new ArrayList<LatLng>();// 所有的marker
	private ArrayList<LatLng> markerListInView = new ArrayList<LatLng>();// 视野内的marker
	private List<Parent> citys;
	private List<Parent> countrys;
	private static int FLAG = 1;
	private static final int DITU = 1;
	protected static final int MARK = 0;
	private static final int LOAD_PROVINCE = 0x110;
	private static final int LOAD_COUNTRYS = 0x111;
	private static final int LOAD_POINTS = 0X112;
	private static final int RESULT_PROVINCE = 0X113;
	private static final int RESULT_COUNTRYS = 0X114;

	MapStatus ms;
	DbUtils db;
	private String[][] areas = {
			{ "330000", "0", "1", "浙江省", "29.159494", "119.957202" },
			{ "330100", "330000", "2", "杭州市", "30.259244", "120.219375" },

			{ "330102", "330100", "3", "上城区", "30.232358", "120.180126" },
			{ "330103", "330100", "3", "下城区", "30.310288", "120.186535" },
			{ "330104", "330100", "3", "江干区", "30.315832", "120.303823" },
			{ "330105", "330100", "3", "拱墅区", "30.344732", "120.158845" },
			{ "330106", "330100", "3", "西湖区", "28.657326", "115.898948" },
			{ "330108", "330100", "3", "滨江区", "0.187588", "120.19237" },
			{ "330109", "330100", "3", "萧山区", "30.172894", "120.389081" },
			{ "330110", "330100", "3", "余杭区", "30.38812", "119.998089" },
			{ "330122", "330100", "3", "桐庐县", "29.836582", "119.560462" },
			{ "330127", "330100", "3", "淳安县", "29.614714", "118.895765" },
			{ "330182", "330100", "3", "建德市", "29.487115", "119.379533" },
			{ "330183", "330100", "3", "富阳市", "30.001094", "119.846692" },
			{ "330185", "330100", "3", "临安市", "30.207684", "119.350295" },
			{ "330200", "330000", "2", "宁波市", "29.885259", "121.579006" },

			{ "330203", "330200", "3", "海曙区", "29.876801", "121.535395" },
			{ "330204", "330200", "3", "江东区", "29.875392", "121.598001" },
			{ "330205", "330200", "3", "江北区", "29.966392", "121.493299" },
			{ "330206", "330200", "3", "北仑区", "29.868332", "121.889419" },
			{ "330211", "330200", "3", "镇海区", "9.995449", "121.61663" },
			{ "330212", "330200", "3", "鄞州区", "29.785459", "121.537835" },
			{ "330225", "330200", "3", "象山县", "29.378771", "121.858666" },
			{ "330226", "330200", "3", "宁海县", "29.314474", "121.463624" },
			{ "330281", "330200", "3", "余姚市", "29.996457", "121.152779" },
			{ "330282", "330200", "3", "慈溪市", "30.189257", "121.338408" },
			{ "330283", "330200", "3", "奉化市", "29.617073", "121.377186" },
			{ "330300", "330000", "2", "温州市", "28.002838", "120.690635" },

			{ "330302", "330300", "3", "鹿城区", "28.067865", "120.565799" },
			{ "330303", "330300", "3", "龙湾区", "27.913341", "120.811078" },
			{ "330304", "330300", "3", "瓯海区", "27.972177", "120.558404" },
			{ "330322", "330300", "3", "洞头县", "27.903664", "121.125826" },
			{ "330324", "330300", "3", "永嘉县", "28.33639", "120.668809" },
			{ "330326", "330300", "3", "平阳县", "27.637701", "120.389387" },
			{ "330327", "330300", "3", "苍南县", "27.434436", "120.445543" },
			{ "330328", "330300", "3", "文成县", "27.812713", "120.028422" },
			{ "330329", "330300", "3", "泰顺县", "27.536407", "119.884868" },
			{ "330381", "330300", "3", "瑞安市", "7.829231", "120.46834" },
			{ "330382", "330300", "3", "乐清市", "28.261839", "121.016175" },
			{ "330400", "330000", "2", "嘉兴市", "30.773992", "120.760428" },

			{ "330402", "330400", "3", "南湖区", "30.716358", "120.844535" },
			{ "330411", "330400", "3", "秀洲区", "30.777679", "120.691907" },
			{ "330421", "330400", "3", "嘉善县", "30.905748", "120.908873" },
			{ "330424", "330400", "3", "海盐县", "30.526043", "120.885576" },
			{ "330481", "330400", "3", "海宁市", "30.442177", "120.618727" },
			{ "330482", "330400", "3", "平湖市", "30.716529", "121.105839" },
			{ "330483", "330400", "3", "桐乡市", "30.612341", "120.490411" },
			{ "330500", "330000", "2", "湖州市", "30.877925", "120.137243" },

			{ "330502", "330500", "3", "吴兴区", "30.808545", "120.088919" },
			{ "330503", "330500", "3", "南浔区", "30.766831", "120.309147" },
			{ "330521", "330500", "3", "德清县", "30.567583", "120.049831" },
			{ "330522", "330500", "3", "长兴县", "0.983353", "119.81942" },
			{ "330523", "330500", "3", "安吉县", "30.62637", "119.583158" },
			{ "330600", "330000", "2", "绍兴市", "30.002365", "120.592467" },

			{ "330602", "330600", "3", "越城区", "30.015793", "120.618327" },
			{ "330621", "330600", "3", "绍兴县", "29.968789", "120.572451" },
			{ "330624", "330600", "3", "新昌县", "29.414314", "120.975702" },
			{ "330681", "330600", "3", "诸暨市", "29.6994", "120.281434" },
			{ "330682", "330600", "3", "上虞市", "29.97804", "120.889432" },
			{ "330683", "330600", "3", "嵊州市", "29.591008", "120.761431" },
			{ "330700", "330000", "2", "金华市", "29.102899", "119.652576" },

			{ "330702", "330700", "3", "婺城区", "28.98454", "119.517572" },
			{ "330703", "330700", "3", "金东区", "29.155526", "119.809227" },
			{ "330723", "330700", "3", "武义县", "28.774056", "119.720833" },
			{ "330726", "330700", "3", "浦江县", "29.526266", "119.910488" },
			{ "330727", "330700", "3", "磐安县", "29.044202", "120.567447" },
			{ "330781", "330700", "3", "兰溪市", "29.284103", "119.533338" },
			{ "330782", "330700", "3", "义乌市", "29.306444", "120.067296" },
			{ "330783", "330700", "3", "东阳市", "29.237427", "120.380818" },
			{ "330784", "330700", "3", "永康市", "28.940177", "120.108684" },
			{ "330800", "330000", "2", "衢州市", "28.95691", "118.875842" },

			{ "330802", "330800", "3", "柯城区", "28.998535", "118.813003" },
			{ "330803", "330800", "3", "衢江区", "28.941983", "118.939044" },
			{ "330822", "330800", "3", "常山县", "8.973666", "118.54767" },
			{ "330824", "330800", "3", "开化县", "9.189938", "118.33165" },
			{ "330825", "330800", "3", "龙游县", "28.997079", "119.198664" },
			{ "330881", "330800", "3", "江山市", "28.58197", "118.607086" },
			{ "330900", "330000", "2", "舟山市", "30.03601", "122.169872" },

			{ "330902", "330900", "3", "定海区", "30.064847", "122.073024" },
			{ "330903", "330900", "3", "普陀区", "31.263743", "121.398443" },
			{ "330921", "330900", "3", "岱山县", "30.319416", "122.260359" },
			{ "330922", "330900", "3", "嵊泗县", "30.705004", "122.481686" },
			{ "331000", "330000", "2", "台州市", "28.668283", "121.440613" },

			{ "331002", "331000", "3", "椒江区", "28.657016", "121.467376" },
			{ "331003", "331000", "3", "黄岩区", "28.604655", "121.088318" },
			{ "331004", "331000", "3", "路桥区", "28.548659", "121.450242" },
			{ "331021", "331000", "3", "玉环县", "28.179738", "121.284426" },
			{ "331022", "331000", "3", "三门县", "29.017744", "121.488229" },
			{ "331023", "331000", "3", "天台县", "29.151779", "120.985563" },
			{ "331024", "331000", "3", "仙居县", "28.738742", "120.640606" },
			{ "331081", "331000", "3", "温岭市", "28.400554", "121.421046" },
			{ "331082", "331000", "3", "临海市", "28.857389", "121.221919" },
			{ "331100", "330000", "2", "丽水市", "28.4563", "119.929576" },

			{ "331102", "331100", "3", "莲都区", "28.447361", "119.849952" },
			{ "331121", "331100", "3", "青田县", "28.208429", "120.146738" },
			{ "331122", "331100", "3", "缙云县", "28.666326", "120.191882" },
			{ "331123", "331100", "3", "遂昌县", "28.52541", "119.089342" },
			{ "331124", "331100", "3", "松阳县", "28.41158", "119.441013" },
			{ "331125", "331100", "3", "云和县", "8.13132", "119.54173" },
			{ "331126", "331100", "3", "庆元县", "27.628046", "119.157619" },
			{ "331127", "331100", "3", "景宁畲族自治县", "7.896053", "119.61929" },
			{ "331181", "331100", "3", "龙泉市", "28.050639", "119.082297" } };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.layout_main);
		/* 获取屏幕长宽相关 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		// 初始化控件
		initViews();
		// 数据库
		initDb();
		// 百度地图设置

		MapStatus ms = new MapStatus.Builder()
				.target(new LatLng(30.2817240000, 119.9980410000)).zoom(8)
				.build();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));// 设置初始地图状态
		mBaiduMap.setMyLocationEnabled(true);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mBaiduMap.clear();

				try {

					List<Parent> citys = db.findAll(Selector.from(Parent.class)
							.where("level", "=", 2));
					for (int i = 0; i < citys.size(); i++) {
						addProvince(new LatLng(citys.get(i).getLatitude(),
								citys.get(i).getLongitude()), 100);

					}

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		// 设置地图状态改变的监听事件

		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				// TODO Auto-generated method stub
				float zoom = mBaiduMap.getMapStatus().zoom;

				Log.e("zoom", String.valueOf(zoom));
				if (zoom < 11) {
					new LoadDataTask().execute(LOAD_PROVINCE);
					// new Thread(new Runnable() {
					//
					// @Override
					// public void run() {
					// // TODO Auto-generated method stub
					// mBaiduMap.clear();
					// try {
					//
					// List<Parent> citys = db.findAll(Selector.from(
					// Parent.class).where("level", "=", 2));
					// for (int i = 0; i < citys.size(); i++) {
					// addProvince(new LatLng(citys.get(i)
					// .getLatitude(), citys.get(i)
					// .getLongitude()), 100);
					//
					// }
					//
					// } catch (DbException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					//
					// }
					// }).start();

				}

				if (zoom >= 11 && zoom < 12) {
					new LoadDataTask().execute(LOAD_COUNTRYS);
					// new Thread(new Runnable() {
					//
					// @Override
					// public void run() {
					// // TODO Auto-generated method stub
					// mBaiduMap.clear();
					// try {
					//
					// List<Parent> countrys = db.findAll(Selector
					// .from(Parent.class).where("level", "=",
					// 3));
					// for (int i = 0; i < countrys.size(); i++) {
					// addCountry(new LatLng(countrys.get(i)
					// .getLatitude(), countrys.get(i)
					// .getLongitude()), 50);
					// }
					//
					// } catch (DbException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					//
					// }
					// }).start();

				} else if (zoom >= 12 && zoom < 13) {
					new LoadDataTask().execute(LOAD_POINTS);
					// new Thread(new Runnable() {
					//
					// @Override
					// public void run() {
					// // TODO Auto-generated method stub
					// // FLAG == 1:拖拽地图动作; FLAG == 0:点击标记点动作;
					// if (FLAG == 1) {
					// mBaiduMap.clear();
					// Projection projection = mBaiduMap
					// .getProjection();
					// Point p = null;
					// markerList.clear();//
					// 每次生成markerList先清空，要不然每次都增加100，造成很多很多点
					// for (int i = 0; i < 100; i++) {
					//
					// markerList.add(new LatLng(
					// 30.2561160000 + Math.random(),
					// 120.1555860000 + Math.random()));
					//
					// // addPoint(
					// // new LatLng(30.2561160000 + Math
					// // .random(),
					// // 120.1555860000 + Math
					// // .random()), 1);
					// }
					// Log.e("markerList.size",
					// String.valueOf(markerList.size()));
					// markerListInView.clear();//
					// 每次生成markerListInView先清空，要不然造成很多很多点
					// for (LatLng ltl : markerList) {
					// //
					// .toScreenLocation返回一个从地图位置转换来的屏幕位置。这个屏幕位置是相对于地图的左上角的，不是相对于整个屏幕的。
					// p = projection.toScreenLocation(ltl);
					// if (p.x < 0 || p.y < 0 || p.x > width
					// || p.y > height) {
					// // 不添加到计算的列表中
					// } else {
					// markerListInView.add(ltl);
					// }
					// }
					// for (int j = 0; j < markerListInView.size(); j++) {
					// addPoint(markerListInView.get(j), 1);
					// }
					// Log.e("markerListInView.size",
					// String.valueOf(markerListInView.size()));
					//
					// }
					// mBaiduMap
					// .setOnMarkerClickListener(new OnMarkerClickListener() {
					//
					// @Override
					// public boolean onMarkerClick(
					// final Marker marker) {
					//
					// FLAG = 0;
					// // 如果marker的title是“随机”才设置弹出框
					// if (marker.getTitle().equals("随机")) {
					// OnInfoWindowClickListener listener = new
					// OnInfoWindowClickListener() {
					// public void onInfoWindowClick() {
					// mBaiduMap
					// .hideInfoWindow();
					//
					// }
					// };
					// // TODO Auto-generated method
					// // stub
					//
					// InfoWindow mInfoWindow = new InfoWindow(
					// BitmapDescriptorFactory
					// .fromView(popInfo(
					// R.drawable.pop_bg,
					// "这里显示的是地址",
					// marker.getPosition())),
					// marker.getPosition(),
					// -100, listener);
					// mBaiduMap
					// .showInfoWindow(mInfoWindow);
					// MapStatus ms = new MapStatus.Builder()
					// .target(new LatLng(
					// marker.getPosition().latitude,
					// marker.getPosition().longitude))
					// .build();
					// mBaiduMap
					// .animateMapStatus(MapStatusUpdateFactory
					// .newMapStatus(ms));
					// }
					//
					// return false;
					// }
					// });
					//
					// FLAG = 1;
					//
					// }
					// }).start();

				}

			}

			@Override
			public void onMapStatusChange(MapStatus arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 数据库相关
	 */
	private void initDb() {
		// TODO Auto-generated method stub

		db = DbUtils.create(this);
		db.configAllowTransaction(true);
		db.configDebug(true);

		com.lidroid.xutils.db.table.Table mTable = com.lidroid.xutils.db.table.Table
				.get(db, Parent.class);
		if (mTable.isCheckedDatabase() == false) {

			for (int i = 0; i < areas.length; i++) {
				Parent data = new Parent();
				data.setCode(Integer.parseInt(areas[i][0]));
				data.setParentCode(Integer.parseInt(areas[i][1]));
				data.setLevel(Integer.parseInt(areas[i][2]));
				data.setName(areas[i][3]);
				data.setLatitude(Float.parseFloat(areas[i][4]));
				data.setLongitude(Float.parseFloat(areas[i][5]));
				try {
					db.save(data);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 初始化组件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

	}

	/**
	 * 增加省标记点方法
	 * 
	 * @param location
	 * @param size
	 */
	private void addProvince(LatLng location, int size) {
		// TODO Auto-generated method stub

		OverlayOptions province = new MarkerOptions()
				.title("市")
				.position(location)
				.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView(
						size, R.drawable.marker_cluster_100))));
		mBaiduMap.addOverlay(province);

	}

	/**
	 * 增加市标记点方法
	 * 
	 * @param location
	 * @param size
	 */
	private void addCountry(LatLng location, int size) {
		// TODO Auto-generated method stub

		OverlayOptions country = new MarkerOptions()
				.title("区")
				.position(location)
				.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView(
						size, R.drawable.marker_cluster_50))));
		mBaiduMap.addOverlay(country);

	}

	/**
	 * 增加随机标记点方法
	 * 
	 * @param location
	 * @param size
	 */
	private void addPoint(LatLng location, int size) {
		// TODO Auto-generated method stub

		OverlayOptions point = new MarkerOptions()
				.title("随机")
				.position(location)
				.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView(
						size, R.drawable.icon_gcoding))));
		mBaiduMap.addOverlay(point);

	}

	/**
	 * getview方法 填充布局
	 * 
	 * @param carNum
	 * @param resourceId
	 * @return
	 */
	public View getView(int carNum, int resourceId) {
		View view = this.getLayoutInflater().inflate(R.layout.point, null);
		TextView carNumTextView = (TextView) view.findViewById(R.id.my_car_num);
		RelativeLayout backGround = (RelativeLayout) view
				.findViewById(R.id.my_car_bg);
		backGround.setBackgroundResource(resourceId);
		carNumTextView.setText(String.valueOf(carNum));

		return view;
	}

	/**
	 * 点击标记点弹出框的UI布局
	 * 
	 * @param resourceId
	 * @param name
	 * @param location
	 * @return
	 */
	public View popInfo(int resourceId, String name, LatLng location) {
		View mlayoutPop = this.getLayoutInflater().inflate(
				R.layout.layout_popup, null);
		class ViewHolder {
			TextView tName;
			TextView tLocation;
			ImageView popImg;
		}
		ViewHolder viewHolder = null;
		if (mlayoutPop.getTag() == null) {
			viewHolder = new ViewHolder();
			viewHolder.popImg = (ImageView) mlayoutPop
					.findViewById(R.id.pop_img);
			viewHolder.tLocation = (TextView) mlayoutPop
					.findViewById(R.id.id_pop_text_location);
			viewHolder.tName = (TextView) mlayoutPop
					.findViewById(R.id.id_pop_text_name);
			mlayoutPop.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) mlayoutPop.getTag();
		viewHolder.popImg.setImageResource(resourceId);
		viewHolder.tLocation.setText(String.valueOf(location));
		viewHolder.tName.setText(name);

		return mlayoutPop;
	}

	/**
	 * 把一个view转化成bitmap对象
	 */
	public static Bitmap getViewBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.mapmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 地图选项
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.id_map_common:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;

		case R.id.id_map_sate:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;

		case R.id.id_map_traffic:
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("实时交通开启");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("实时交通关闭");
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public class LoadDataTask extends AsyncTask<Integer, Void, Integer> {

		

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			switch (params[0]) {
			case LOAD_PROVINCE:
				mBaiduMap.clear();

				try {

					citys = db.findAll(Selector.from(Parent.class).where(
							"level", "=", 2));
					for (int i = 0; i < citys.size(); i++) {
						addProvince(new LatLng(citys.get(i).getLatitude(),
								citys.get(i).getLongitude()), 100);

					}

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				 return RESULT_PROVINCE;
			

			case LOAD_COUNTRYS:
				mBaiduMap.clear();
				try {

					countrys = db.findAll(Selector.from(Parent.class).where(
							"level", "=", 3));
					for (int i = 0; i < countrys.size(); i++) {
						addCountry(new LatLng(countrys.get(i).getLatitude(),
								countrys.get(i).getLongitude()), 50);
					}

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return RESULT_COUNTRYS;
			
				

			case LOAD_POINTS:
				// FLAG == DITU:拖拽地图动作; FLAG == 0:点击标记点动作;
				if (FLAG == DITU) {
					mBaiduMap.clear();
					Projection projection = mBaiduMap.getProjection();
					Point p = null;
					markerList.clear();// 每次生成markerList先清空，要不然每次都增加100，造成很多很多点
					for (int i = 0; i < 100; i++) {

						markerList.add(new LatLng(
								30.2561160000 + Math.random(),
								120.1555860000 + Math.random()));

						// addPoint(
						// new LatLng(30.2561160000 + Math
						// .random(),
						// 120.1555860000 + Math
						// .random()), 1);
					}
					Log.e("markerList.size", String.valueOf(markerList.size()));
					markerListInView.clear();// 每次生成markerListInView先清空，要不然造成很多很多点
					for (LatLng ltl : markerList) {
						// .toScreenLocation返回一个从地图位置转换来的屏幕位置。这个屏幕位置是相对于地图的左上角的，不是相对于整个屏幕的。
						p = projection.toScreenLocation(ltl);
						if (p.x < 0 || p.y < 0 || p.x > width || p.y > height) {
							// 不添加到计算的列表中
						} else {
							markerListInView.add(ltl);
						}
					}
					for (int j = 0; j < markerListInView.size(); j++) {
						addPoint(markerListInView.get(j), 1);
					}
					Log.e("markerListInView.size",
							String.valueOf(markerListInView.size()));

				}
				mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(final Marker marker) {

						FLAG = MARK;
						// 如果marker的title是“随机”才设置弹出框
						if (marker.getTitle().equals("随机")) {
							OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
								public void onInfoWindowClick() {
									mBaiduMap.hideInfoWindow();

								}
							};
							// TODO Auto-generated method
							// stub

							InfoWindow mInfoWindow = new InfoWindow(
									BitmapDescriptorFactory.fromView(popInfo(
											R.drawable.pop_bg, "这里显示的是地址",
											marker.getPosition())), marker
											.getPosition(), -100, listener);
							mBaiduMap.showInfoWindow(mInfoWindow);
							MapStatus ms = new MapStatus.Builder().target(
									new LatLng(marker.getPosition().latitude,
											marker.getPosition().longitude))
									.build();
							mBaiduMap.animateMapStatus(MapStatusUpdateFactory
									.newMapStatus(ms));
						}

						return false;
					}
				});

				FLAG = DITU;

			}
			return null;
		}

	}

}
