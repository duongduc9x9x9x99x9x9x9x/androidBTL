package com.example.pokemongo1;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // sử dụng mediaplayer để cung cấp service phát nhạc
    LocationManager locationManager;
    LocationListener locationListener;
    MediaPlayer player;
    private GoogleMap mMap;
    //ListView hisOfpoke;
    //ArrayList<String> dsPoke;

    PokemonAdapter pokemonAdapter;
    ArrayList<Pokemon> dsPoke = new ArrayList<>();
    ArrayList<String> arr = new ArrayList<>();

    ArrayList<String> usernames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost);

        //activity_maps
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        //fix tabhost kia`
        // minh dinh 1 tab để map 1 tab để listview nhưng lỗi chỗ đó ý
        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("Map");
        tabHost.addTab(tab1);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("pokemon đã bắt");
        tabHost.addTab(tab2);


        //ko khai bao ham` nay`
        // nho khai bao ham` nay` nhe
        // ko la ko len du lieu dau..ok ban minh hieu r. Cam on b nhieu
        // ok

        // 2 thằng dưới được thêm khi hàm onCreate triển khai
        // CheckUserPermsions là hàm để yêu cầu xác nhận yêu cầu GPS
        // addPokemon là hàm dùng arraylist để app pokemon kèm tọa độ
        // vào 1 arraylist, từ arraylist này mình sẽ set lên google map
        // recycleview() dùng để hiện thị ảnh, các con pokemon đã bắt ở tab " pokemon đã bắt"
        CheckUserPermsions();
        addPokemon();
        recycleview();



    }
    public void recycleview(){
        // cái này để set cho recycleview, tìm hiểu thêm ở trang chủ dev android,
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        // 3 dòng code dưới để tạo gạch kẻ ngang cho mỗi item trong recycle view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getApplication(),R.drawable.custom_divider);
        dividerItemDecoration.setDrawable(drawable);

        recyclerView.addItemDecoration(dividerItemDecoration);
        pokemonAdapter = new PokemonAdapter(dsPoke,getApplicationContext(),getApplicationContext());
        recyclerView.setAdapter(pokemonAdapter);




    }

    // 2 hàm dưới dùng để cấp quyền vào GPS của người dùng
    void CheckUserPermsions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        mylocation();
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mylocation();//cập nhật vị trí của bạn



                } else {
                    Toast.makeText(this, "Bạn vui lòng cấp quyền GPS cho mình", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // cập nhật định vị vị trí cho người chơi
    public void mylocation() {

        MyLocationListener myloc = new MyLocationListener();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 10, myloc);
        Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ParseObject request = new ParseObject("Request");
        request.put("username", ParseUser.getCurrentUser().getUsername());
        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        request.put("location",parseGeoPoint);
        request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
     // tạo 1 luồng thread để cập nhật vị trí = việc sử dụng runOnUiThread(new Runable(){....}) -> cập nhật trên Thread UI
        // và cho luồng đó chạy thôi
        thread a = new thread();
        a.start();

    }

    // nếu không có oldloc thì numOfpokemon tăng liên tục do vòng lặp
    Location oldloc;

    // tạo thread để thay đổi UI trên màn hình
    // nếu k sử dụng thread thì máy sẽ bị dừng lại k hoạt động được
    class thread extends Thread {
        thread(){
            // cái này set thuộc tính cho vị trí oldloc để so sánh thôi
            oldloc = new Location("...");
            oldloc.setLongitude(0);
            oldloc.setLatitude(0);
        }

        @Override
        public void run() {
            // đến khi break ???
            while (true) {

                try {
                    Thread.sleep(1000);
                    if (oldloc.distanceTo(MyLocationListener.location) == 0) {
                        continue;
                    }
                    oldloc = MyLocationListener.location;
                    runOnUiThread(new Runnable() {
                        //@Override
                        public void run() {

                            //xóa vị trí người dùng
                            //mMap.clear();
                            MyLocationListener myloc = new MyLocationListener();
                            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 10, myloc);
                            Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
                            final ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            query.whereNear("location",parseGeoPoint);
                            //query.setLimit(10);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e == null) {

                                        if (objects.size() > 0) {
                                            for (ParseObject object : objects) {

                                                ParseGeoPoint requestLocation = (ParseGeoPoint) object.get("location");
                                                //usernames.add(object.getString("username"));
                                                if(!usernames.contains(object.getString("username"))) {
                                                    Log.i("...............",".....................");
                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(requestLocation.getLatitude(), requestLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.mez)));
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(requestLocation.getLatitude(), requestLocation.getLongitude()), 17));
                                                }
                                                usernames.add(object.getString("username"));


                                            }
                                        }
                                      }
                                    }
                                                   });



                            // cập nhật vị trí
                            //LatLng sydney = new LatLng(MyLocationListener.location.getLatitude(), MyLocationListener.location.getLongitude());
                            //mMap.addMarker(new MarkerOptions().position(sydney).title("ĐÂY LÀ BẠN").icon(BitmapDescriptorFactory.fromResource(R.drawable.mez)));
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

                            // tải pokemon lên map




                            for (int i = 0; i < list.size(); i++) {
                                // tạo đối tượng pokemon để get các thành phần trong arraylist
                                Pokemon pokemon = list.get(i);
                               //
                                if (pokemon.isCatch() == false) {
                                    // 3 dòng code duoi để gán pokemon lên map thôi !!!
                                    LatLng locofpokemon = new LatLng(pokemon.getLocation().getLatitude(), pokemon.getLocation().getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(locofpokemon).title(pokemon.getName()).icon(BitmapDescriptorFactory.fromResource(pokemon.getImage())));
                                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
                                    // khi bắt được Pokemon
                                    SharedPreferences  mPrefs = getPreferences(0);
                                    if (MyLocationListener.location.distanceTo(pokemon.getLocation()) < 1500) {
                                        //Lưu pokemon vào list
                                        // kiểm tra xem đã tồn tại phần tử ???? và add pokemon vào dsPoke. dspoke là 1 arraylist để lưu các pokemon trong mảng thôi. Thông qua adapter
                                        //nó sẽ đưa lên app (qua adapter ở hàm onCreate)
                                        if (!arr.contains(pokemon.getName())) {
                                            numOfPokemon = numOfPokemon + 1;
                                            // 2 dòng code dưới để add pokemon vào recycle view đã tạo bên trên
                                            dsPoke.add(pokemon);
                                            arr.add(pokemon.getName());
                                            // khi catch đc pokeom thì nó phát nhạc
                                            player = MediaPlayer.create(MapsActivity.this,R.raw.mario);
                                            player.start();
                                        }

                                        //tạo notification chứa tên con pokemon vừa bắt và tổng số pokemon hiện có
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this);
                                        builder.setSmallIcon(R.drawable.pikachu);
                                        builder.setContentTitle("Bạn vừa bắt được " + pokemon.getName());
                                        builder.setContentText("Bạn hiện đang có " + numOfPokemon + " .Hãy vào túi kiểm tra");
                                        Intent intent = new Intent(MapsActivity.this, MapsActivity.class);

                                        //  MapsActivity.class
                                        PendingIntent pendingIntent = PendingIntent.getActivity(MapsActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                        builder.setContentIntent(pendingIntent);
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        notificationManager.notify("...", 123, builder.build());




                                    }


                                }


                            }


                            // .. set lại adapter mỗi lần thay đổi
                            pokemonAdapter.notifyDataSetChanged();
                        }
                    });


                } catch (Exception e) {

                }

            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    int numOfPokemon = 0;
    // TẠO arraylist thêm pokemon
    ArrayList<Pokemon> list = new ArrayList<>();

    public void addPokemon() {



        list.add(new Pokemon(R.drawable.bulbasaurz, "Bulbasaur", "45",false, 21.040450, 105.783106));
        list.add(new Pokemon(R.drawable.charmanderz, "Charmander","32", false, 21.039949, 105.780231));
        list.add(new Pokemon(R.drawable.metapodz, "Metapod","25", false, 21.037045, 105.784351));
        list.add(new Pokemon(R.drawable.pidgeotz, "Pidgeot", "45",false, 21.037556, 105.784748));
        list.add(new Pokemon(R.drawable.poliwrathz, "Poliwrathz","60", false, 21.035483, 105.783729));
        list.add(new Pokemon(R.drawable.arbok ,"Arbok","48",false,21.008645,105.814592));
        list.add(new Pokemon(R.drawable.bellsprout,"bellsprout","59",false,21.022596,105.803273));
        list.add(new Pokemon(R.drawable.diglett,"diglett","78",false,21.026402,105.796160));
        list.add(new Pokemon(R.drawable.dodrio,"dodrio","56",false,21.036266,105.789358));
        list.add(new Pokemon(R.drawable.dragonite,"dragonite","89",false,21.035785,105.786204));
        list.add(new Pokemon(R.drawable.exeggutor,"exeggutor","19",false,21.032030,105.784305));
        list.add(new Pokemon(R.drawable.gengar,"gengar","57",false,21.028475,105.779895));
        list.add(new Pokemon(R.drawable.growlithe,"growlithe","77",false,21.027423,105.778318));
        list.add(new Pokemon(R.drawable.haunter,"haunter","74",false,21.017218,105.790813));
        list.add(new Pokemon(R.drawable.hitmonlee,"hitmonlee","90",false,21.009772,105.797779));
        list.add(new Pokemon(R.drawable.jolteon,"jolteon","78",false,21.006119,105.801079));
        list.add(new Pokemon(R.drawable.koffing,"koffing","64",false,21.004637,105.798826));
        list.add(new Pokemon(R.drawable.krabby,"krabby","86",false,21.002203,105.800875));
        list.add(new Pokemon(R.drawable.magnemite,"magnemite","76",false,20.998267,105.802989));
        list.add(new Pokemon(R.drawable.mankey,"mankey","39",false,20.986466,105.814105));
        list.add(new Pokemon(R.drawable.nidoran,"nidoran","64",false,21.000149,105.857124));
        list.add(new Pokemon(R.drawable.poliwag,"poliwag","78",false,20.999207,105.854517));
        list.add(new Pokemon(R.drawable.ponyta,"ponyta","65",false,20.998796,105.853498));
        list.add(new Pokemon(R.drawable.sandshrew,"sandshrew","65",false,20.997354,105.850290));
        list.add(new Pokemon(R.drawable.scyther,"scyther","67",false,20.996032, 105.845494));
        list.add(new Pokemon(R.drawable.snorlax,"snorlax","87",false,20.991114,105.855300));
        list.add(new Pokemon(R.drawable.venomoth,"venomoth","98",false,21.005291,105.845651));
        list.add(new Pokemon(R.drawable.vulpix,"vulpix","76",false,21.002697,105.851080));
    }




}
