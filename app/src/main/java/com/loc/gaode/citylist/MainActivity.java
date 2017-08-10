package com.loc.gaode.citylist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.loc.gaode.R;
import com.loc.gaode.citylist.adapter.DividerItemDecoration;
import com.loc.gaode.citylist.adapter.PositionsAdapter;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, PoiSearch.OnPoiSearchListener {

    /**
     * 北京
     */
    private TextView mCityTv;
    private EditText mCityInfoEt;
    private RecyclerView mPositionRv;
    private String keyWord = "";
    private PoiSearch.Query query;// Poi查询条件类
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch poiSearch;
    private ArrayList<PoiItem> poiItems;//搜索周边返回的数据
    private PositionsAdapter adapter;
    private AMapLocationClient locationClient = null;
    private LatLonPoint latlngP;
    private String currentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initView();
        initLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        mCityTv = (TextView) findViewById(R.id.city_tv);
        mCityTv.setOnClickListener(this);
        mCityInfoEt = (EditText) findViewById(R.id.city_info_et);
        mPositionRv = (RecyclerView) findViewById(R.id.position_rv);
        adapter = new PositionsAdapter(MainActivity.this);
        adapter.setPositionOnClick(new PositionsAdapter.PositionLLCallBack() {
            @Override
            public void positionOnClick(String addrInfo) {
                mCityInfoEt.setText(addrInfo);
            }
        });
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        keyWord = mCityInfoEt.getText().toString().trim();
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (latlngP != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(latlngP, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }


    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    // 取得第一页的poiitem数据，页数从数字0开始
                    poiItems = result.getPois();
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    mPositionRv.setLayoutManager(manager);
                    mPositionRv.setAdapter(adapter);
                    adapter.setAdapterData(poiItems);
                    mPositionRv.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.HORIZONTAL_LIST));

                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        startLocation();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
//		mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
//		mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        return mOption;
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
//		//根据控件的选择，重新设置定位参数
//		resetOption();
        // 设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            stopLocation();
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                currentCity = loc.getCity();
                mCityTv.setText(currentCity);
                latlngP = new LatLonPoint(loc.getLatitude(), loc.getLongitude());
                doSearchQuery();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_tv:

                Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 88) {
            if (data != null) {
                String city = data.getStringExtra("city")+"市";
                mCityTv.setText(city);
                mCityInfoEt.setText("");
                if (city.equals(currentCity)) {
                    mPositionRv.setVisibility(View.VISIBLE);
                    adapter.setAdapterData(poiItems);
                }else{
                    mPositionRv.setVisibility(View.GONE);
                }
            }
        }


    }
}
