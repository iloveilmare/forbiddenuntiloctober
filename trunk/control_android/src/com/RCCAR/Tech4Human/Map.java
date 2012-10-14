package com.RCCAR.Tech4Human;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity implements LocationListener, OnClickListener
{
    
    LocationManager location = null; // ��ġ
    
    boolean bGetteringGPS = false; // GPS�� ������̳�?
    
    private LocationManager locationManager = null; // ��ġ����
    
    private MapView mapView = null; // �ʺ�
    
    private MapController mapController; // ����Ʈ��
    
    private GeoPoint centerGP = null; // ��� ��ǥ
    
    private final double previousLat = 0; // ��ǥ
    
    private final double previousLng = 0; // ��ǥ
    
    private double currentLat = 0; // ���� ��ǥ
    
    private double currentLng = 0; // ���� ��ǥ
    
    private final Button btnConfirm = null; // �ּ� ��ư
    
    private final Button btnDistance = null;
    
    private final Button btnDelete = null;
    
    private Context context = null; // ���� â
    
    private EditText editAddress = null; // �ּ� ���
    
    private final Location maLocation = null;
    
    private final Location mbLocation = null;
    
    /*
     * private Path mPath; private Paint mPaint;
     * 
     * ArrayList mMyPathLocationArray;
     */
    
    private final double distance = 0;
    
    private String meter;
    
    /*
     * public void MyOverlay(Context context, MapView mapView) { mPath = new
     * Path(); mPath.reset(); mPaint = new Paint(); mPaint.setAntiAlias(true);
     * mPaint.setDither(true); mPaint.setColor(Color.BLUE);
     * mPaint.setStyle(Paint.Style.STROKE);
     * mPaint.setStrokeCap(Paint.Cap.ROUND);
     * mPaint.setStrokeJoin(Paint.Join.ROUND); mPaint.setStrokeWidth(3);
     * mPaint.setTextSize(20); mPaint.setStyle(Paint.Style.FILL);
     * mMyPathLocationArray = new ArrayList();
     * 
     * }
     */
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Ÿ��Ʋ�� �����
        this.context = this; // ����â���� ����
        // ���� â���� ��ġ�������񽺸� ����
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Iterator<String> providers = locationManager.getAllProviders().iterator();
        // ���� ��ġ�� ��� ���¿� ���� �α� ���
        while (providers.hasNext())
        {
            Log.d("Test", "provider " + providers.next());
        }
        // ��ġ���������� �䱸������ �����ϱ� ����
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT); // ���е�
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT); // �Һ����·�
        // ���ǿ� �´� ���ι��̴��� ���� ����
        String best = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(best, 1000, 0, this);
        // ���̾ƿ��� ����
        setContentView(R.layout.map);
        
        registerEventListener();
    }
    
    private void registerEventListener()
    {
        // �� ���̾ƿ��� ����
        mapView = (MapView) findViewById(R.id.map_view);
        mapController = mapView.getController();
        
        editAddress = (EditText) findViewById(R.id.edit_address);
        
    }
    
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        /*
         * switch (v.getId()) { case R.id.btn_confirm: CenterLocation(centerGP);
         * setLocationInfo(); break;
         * 
         * case R.id.btn_distance: Intent i = new
         * Intent("com.android.jdi.DISTANCE"); startActivity(i); break;
         * 
         * case R.id.btn_delete: mapView.getOverlays().clear();
         * editAddress.setText("\n\n"); mapView.invalidate();
         * 
         * break; }
         */
    }
    
    private void setMap()
    {
        
        centerGP = new GeoPoint((int) (currentLat * 1E6), (int) (currentLng * 1E6));
        
        mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
        int maxZoomlevel = mapView.getMaxZoomLevel();
        int zoomLevel = (int) ((maxZoomlevel + 1) / 1.15);
        mapController.setZoom(zoomLevel);
        mapController.setCenter(centerGP);
        mapView.invalidate();
        
        placeMarker((int) (currentLat * 1E6), (int) (currentLng * 1E6));
        setLocationInfo();
        
    }
    
    @Override
    protected boolean isRouteDisplayed()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void onLocationChanged(Location location)
    {
        // ���� �����ϴ� ��ġ�� �ٲ�� ������ ��Ŀ�� ���ŵ˴ϴ�.
        
        if (bGetteringGPS == false)
        {
            
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();
            
            Log.d("Test", "location " + currentLat + " " + currentLng);
            locationManager.removeUpdates(this);
            bGetteringGPS = true;
            
            setMap();
            
            /*
             * maLocation = mbLocation; mbLocation = location; if (maLocation !=
             * null && mbLocation != null && maLocation != mbLocation) {
             * mMyPathLocationArray.add(new MyPathLocation(maLocation,
             * mbLocation)); distance += maLocation.distanceTo(mbLocation);
             * meter = Double.toString(distance); } else return;
             */
        }
    }
    
    @Override
    public void onProviderDisabled(String provider)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onProviderEnabled(String provider)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // TODO Auto-generated method stub
        
    }
    
    // ��Ŀ�� ����
    private void placeMarker(int markerLatitude, int markerLongitude)
    {
        Drawable marker = getResources().getDrawable(R.drawable.carview);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        
        mapView.getOverlays().add(new SelectingLocations(marker, markerLatitude, markerLongitude));
    }
    
    // ���Ӱ� ��Ŀ�� ������� ��ġ������ ������.
    private void CenterLocation(GeoPoint centerGeoPoint)
    {
        mapController.animateTo(centerGeoPoint);
        
        placeMarker(centerGeoPoint.getLatitudeE6(), centerGeoPoint.getLongitudeE6());
        
        currentLat = centerGeoPoint.getLatitudeE6() / 1E6;
        currentLng = centerGeoPoint.getLongitudeE6() / 1E6;
    };
    
    // ������ �ʺ信 ���ο� ��Ŀ�� ���� �������� ��������
    private class SelectingLocations extends ItemizedOverlay<OverlayItem>
    {
        
        private final List<OverlayItem> locations = new ArrayList<OverlayItem>();
        
        private final Drawable marker;
        
        private final OverlayItem myOverlayItem;
        
        String path[];
        
        public SelectingLocations(Drawable defaultMarker, int LatitudeE6, int LongitudeE6)
        {
            super(defaultMarker);
            
            // TODO Auto-generated constructor stub
            this.marker = defaultMarker;
            // create locations of interest
            path = new String[5];
            
            for (int i = 0; i < 5; i++)
                path[i] = " ";
            
            GeoPoint myPlace = new GeoPoint(LatitudeE6, LongitudeE6);
            myOverlayItem = new OverlayItem(myPlace, "Hello", "CurrentPlace");
            locations.add(myOverlayItem);
            populate();
            
        }
        
        @Override
        protected OverlayItem createItem(int i)
        {
            // TODO Auto-generated method stub
            return locations.get(i);
        }
        
        @Override
        public int size()
        {
            // TODO Auto-generated method stub
            return locations.size();
        }
        
        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
        {
            // TODO Auto-generated method stub
            boundCenterBottom(marker);
            /*
             * if (maLocation != null && mbLocation != null) { mPath.reset();
             * canvas.drawPath(mPath, mPaint); updatePath();
             * mPaint.setStyle(Paint.Style.STROKE); }
             */
            return super.draw(canvas, mapView, shadow, when);
        }
        
        /*
         * public void updatePath() { for(int i=0; i <
         * mMyPathLocationArray.size(); i++) { Point startPoint = new Point();
         * Point endPoint = new Point(); MyPathLocation temp = (MyPathLocation)
         * mMyPathLocationArray.get(i); mapView.getProjection().toPixels(new
         * GeoPoint((int)(temp.maLocation.getLatitude()*1E6),
         * (int)(temp.mbLocation.getLongitude()*1E6)), startPoint);
         * mapView.getProjection().toPixels(new
         * GeoPoint((int)(temp.maLocation.getLatitude()*1E6),
         * (int)(temp.mbLocation.getLongitude()*1E6)), endPoint);
         * 
         * Path p = new Path(); p.reset(); p.moveTo(startPoint.x, startPoint.y);
         * p.lineTo(endPoint.x, endPoint.y); mPath.addPath(p); } }
         */
        
        // �ʿ� ���� Ŭ���̳� ��ġ �̺�Ʈ�� �ְ� ������
        // OnTouch�� OnClick ������ ��ſ� OnTap �޼ҵ带 �̿��Ѵ�.
        @Override
        public boolean onTap(GeoPoint p, MapView mapView)
        {
            // TODO Auto-generated method stub
            // Toast.makeText(Map.this,p.getLatitudeE6()+" : "+p.getLongitudeE6(),
            // Toast.LENGTH_SHORT);
            Log.d("loc", p.getLatitudeE6() + " : " + p.getLongitudeE6());
            mapView.getOverlays().add(this); // ������ �ʺ信 ��Ŀ�� �߰��Ѵ�.
            CenterLocation(p); // ���� �߽��� ��Ŀ�� ���� ������ ��´�.
            setLocationInfo();
            
            return true;
        }
        
    }// end overlay
    
    private void setLocationInfo()
    {
        Intent intent = new Intent(); //
        intent.putExtra("lat", currentLat);
        intent.putExtra("lng", currentLng);
        // ������ ��ǥ��
        Geocoder geocoder = new Geocoder(context);
        List<Address> address;
        try
        {
            address = geocoder.getFromLocation(currentLat, currentLng, 1);
            editAddress.setText(address.get(0).getAddressLine(0).toString() + "\n���� : " + currentLat + " �浵 : "
                    + currentLng);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}

/*
 * class MyPathLocation { public Location maLocation; public Location
 * mbLocation;
 * 
 * public MyPathLocation(Location aLocation, Location bLocation) { maLocation =
 * aLocation; mbLocation = bLocation; } }
 */