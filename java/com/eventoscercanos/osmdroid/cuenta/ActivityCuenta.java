package com.eventoscercanos.osmdroid.cuenta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.eventoscercanos.osmdroid.DrawerItem;
import com.eventoscercanos.osmdroid.MySingleton;
import com.eventoscercanos.osmdroid.R;
import com.eventoscercanos.osmdroid.SessionManager;
import com.eventoscercanos.osmdroid.cuenta.constants.OpenStreetMapConstants;
import com.eventoscercanos.osmdroid.nuevo.ActivityPaginaEditable;
import com.eventoscercanos.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityCuenta extends AppCompatActivity
        implements OpenStreetMapConstants, CardFragmentCuenta.OnFragmentCardCuentaListener {

    List<DrawerItem> dataList;
    private int id_user;
    boolean fragmentCardIniciada;
    private String token;
    static MapView mMapView;
    CardFragmentCuenta fragmentCard;
    RelativeLayout relativeLayout;
    static String tipo_actual;
    FragmentManager fm;
    private CharSequence mTitle;
    CuentaDrawerAdapter adapter;
    ListView mDrawerList;
    private int seleccion_actual;
    private boolean cancelarTodo = false;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    static JSONArray misEventos;
    SessionManager sessionManager;
    private float densidad;
    final int MAP_FRAGMENT = 0;
    final int CARD_FRAGMENT = 1;
    int currentFragment;
    ActivityResultLauncher<Intent> startActivityIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_cuenta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cuenta);
        setSupportActionBar(toolbar);
        misEventos = new JSONArray();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_cuenta);
         toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };

        drawer.addDrawerListener(toggle);
        sessionManager = SessionManager.getInstancia(this);

        dataList = new ArrayList<>();
        mTitle = getTitle();
        mDrawerList = (ListView) findViewById(R.id.drawerList_cuenta);


        dataList.add(new DrawerItem(sessionManager.getUserName(), sessionManager.getEmail()));   //adding image header to
        dataList.add(new DrawerItem("Elementos"));
        dataList.add(new DrawerItem("Eventos creados", "obj_estrella" , -7));
        dataList.add(new DrawerItem("Mis Anuncios", "obj_estrella" , -8));

        adapter = new CuentaDrawerAdapter(this, R.layout.custom_drawer_item, dataList);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        id_user = sessionManager.getUserId();
        token = sessionManager.getToken();
        mMapView = (MapView) findViewById(R.id.map_cuenta);
        assert mMapView != null;
        mMapView.setMultiTouchControls(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMinZoomLevel(4.0);
        fm = getSupportFragmentManager();
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_for_snack);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        densidad = dm.density;

        startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Add same code that you want to add in onActivityResult method
                        obtenerMarkers();
                    }
                });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_cuenta);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == CARD_FRAGMENT) {
                currentFragment = MAP_FRAGMENT;
                invalidateOptionsMenu();
            }
              super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(menu != null){
            menu.clear();
        }

        if (currentFragment == CARD_FRAGMENT) {

                getMenuInflater().inflate(R.menu.mn_cuenta_card2, menu);

        }else {
            getMenuInflater().inflate(R.menu.mn_cuenta, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.cerrar_sesion:
                cerrarSesion();
                break;

            case R.id.modo_cards:
                Log.d("A. Cuenta", "inside menu modo_cards");
                String TAG_CARD_FRAGM_CUETA = "fragCardCuenta";
                if (fm.findFragmentByTag(TAG_CARD_FRAGM_CUETA) == null) {

                    fragmentCard = CardFragmentCuenta.newInstance("param1", "param2");
                    fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(TAG_CARD_FRAGM_CUETA).add(R.id.frame_cuenta, fragmentCard, TAG_CARD_FRAGM_CUETA).commit();
                    fragmentCardIniciada = true;
                    Log.d("A. Cuenta", "cardfragmentCuenta was created");
                }
                currentFragment = Constantes.CARD_FRAGMENT;
                invalidateOptionsMenu();
                break;

            case R.id.info:
                startActivity(new Intent(this, Informacion.class));
                break;

            case R.id.sugerencia:
                startActivity(new Intent(this, Sugerencias.class));
                break;

            case R.id.modo_mapa:
                onBackPressed();
                break;

         }
//TODO: CREAR ITEM "MIS ANUNCIOS" Y CON LA OPCIÓN DE CREAR ANUNCIO EN CASO DE QUE NO EXISTA NINGUNO, hacer lo mismo con Mis Eventos(mostrar opcion de crear un evento)
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        Objects.requireNonNull(getSupportActionBar()).setTitle(mTitle);
    }

    @Override
    public void onResume() {
        super.onResume(); // Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mMapView.getController().setCenter(GeoPoint.fromDoubleString(mPrefs.getString(PREFS_MAP_CENTER, "-25.388400000000,-57.136800000000"), ','));
        mMapView.getController().setZoom(mPrefs.getFloat(PREFS_ZOOM_LEVEL, 5.0f));
        mMapView.invalidate();
        cancelarTodo = false;
    }

    public void onPause() {

            GeoPoint geoPoint = (GeoPoint) mMapView.getMapCenter();     //mMapView.getProjection().fromPixels(mMapView.getWidth() / 2, mMapView.getHeight() / 2, punto_cero);
            final SharedPreferences.Editor edit = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            edit.putString(PREFS_MAP_CENTER, geoPoint.getLatitude() + "," + geoPoint.getLongitude());
            edit.putFloat(PREFS_ZOOM_LEVEL, (float) mMapView.getZoomLevelDouble());
            edit.apply();
        cancelarTodo = true;
        sessionManager.setCuentaDrawerItemSelected(seleccion_actual);
        super.onPause();
    }


    private void cerrarSesion(){

        String url = Constantes.URL_BASE + "usuarios/cerrar_sesion.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_user", id_user);
            jsonObject.put("token", token);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progress;
        progress = ProgressDialog.show(this, "Conectando con el servidor", "Por favor espere...", true, true);

        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                    if(!cancelarTodo) {
                        progress.dismiss();
                        procesar_respuesta(response);
                    }
                }, volleyError -> {
                    if(!cancelarTodo) {
                        progress.dismiss();
                        Toast.makeText(ActivityCuenta.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void procesar_respuesta(JSONObject respuesta){
        try {
            int estado = respuesta.getInt("estado");
            switch (estado){
                case 1:
                    Toast.makeText(ActivityCuenta.this, "Se ha cerrado sesión correctamente", Toast.LENGTH_SHORT).show();
                    SessionManager sesionManager = SessionManager.getInstancia(this);
                    sesionManager.setUserEmail("Correo electrónico");
                    sesionManager.setUserName("Nombre de usuario");
                    sesionManager.setLogin(false);
                    finish();
                    break;
                case 2:
                    Snackbar.make(relativeLayout, "Ha ocurrido un error", Snackbar.LENGTH_LONG).show();
                    break;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerMarkers(){
        mMapView.removeAllViews();
        mMapView.getOverlays().clear();
        misEventos = null;
        if (fragmentCardIniciada) { //&& MainActivity.currentFragment == MainActivity.CARD_FRAGMENT

                RecyclerCardCuentaAdaptador.data = null;
                CardFragmentCuenta.adaptador.notifyDataSetChanged();
        }

        String url = Constantes.URL_BASE  + "eventos/obtener_eventos_PorUsuario.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_user", id_user);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Snackbar snackbar = Snackbar.make(relativeLayout, "Un momento...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                snackbar.dismiss();
                if(!cancelarTodo) {
                    procesarmisEventos(jsonObject);
                }
            }
         }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                snackbar.dismiss();
                if(!cancelarTodo) {
                    Toast.makeText(ActivityCuenta.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                }
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private void procesarmisEventos(JSONObject json){

     int estado = 0;
        try {
            estado = json.getInt("estado");
            if(estado == 1) {

                misEventos = json.getJSONArray("mis_eventos");
                    for (int i = 0; i < misEventos.length(); i++) {
                        final int id = misEventos.getJSONObject(i).getInt("id");
                        final double latitud = misEventos.getJSONObject(i).getDouble("latitud");
                        final double longitud = misEventos.getJSONObject(i).getDouble("longitud");
                        final String marcador = misEventos.getJSONObject(i).getString("marcador");
                        final String icono = misEventos.getJSONObject(i).getString("icono");

                        final String nombre = misEventos.getJSONObject(i).getString("nombre");
                        final String descripcion = misEventos.getJSONObject(i).getString("descripcion");
                        final String foto_portada = misEventos.getJSONObject(i).getString("foto_portada");
                        final Marker marker = new Marker(mMapView);
                        Bitmap markerBitmap = null;
                        Bitmap iconoBitmap = null;
                        try {
                            markerBitmap = BitmapFactory.decodeStream(this.getAssets().open(marcador + ".png"));
                            Matrix matrix = new Matrix();
                            double zoomLevel = mMapView.getZoomLevelDouble();
                            if (densidad > 1) {
                                if (zoomLevel > 16) {
                                    matrix.postScale(1.6f, 1.6f);
                                } else if (zoomLevel > 12) {
                                    matrix.postScale(1.4f, 1.4f);
                                }else {
                                    matrix.postScale(1.2f, 1.2f);
                                }
                            }else {
                                matrix.postScale(1.2f, 1.2f);
                            }
                            markerBitmap = Bitmap.createBitmap(markerBitmap, 0, 0, 32, 37, matrix, false);

                            if (!icono.contains(".png") && !icono.contains("null")) {
                                    iconoBitmap = BitmapFactory.decodeStream(getAssets().open(icono + ".png"));
                                }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //marker.setIcon(markerBitmap);
                        marker.setIcon(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.mk_pers_male));
                        //marker.setLabelFontSize(0);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setPosition(new GeoPoint(latitud, longitud));
                        //  final Bitmap icono64 = BitmapFactory.decodeResource(getResources(), id_icono);
                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {

                                if (marker.isInfoWindowOpen()) {
                                    marker.closeInfoWindow();

                                } else {
                                    marker.showInfoWindow();
                                }
                                return false;
                            }
                        });

                        final Bitmap finalIconoBitmap = iconoBitmap;
                        InfoWindow infoWindow = new InfoWindow(R.layout.mi_bonuspack_bubble, mMapView) {

                            @Override
                            public void onOpen(Object item) { //aqui item es un MarkerLabeled

                                LinearLayout layout = (LinearLayout) mView.findViewById(R.id.mi_bubble_layout);

                                ImageView imagenView = (ImageView) mView.findViewById(R.id.bubble_image);
//                                Button btnMoreInfo = (Button) mView.findViewById(R.id.bubble_moreinfo);
                                TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
                                TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);

                                if (finalIconoBitmap != null) {
                                    imagenView.setImageBitmap(finalIconoBitmap);

                                } else {
                                    if (icono.equals("null")){
                                        try {
                                            imagenView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("icono.png")));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        imagenView.setVisibility(View.GONE);
                                        NetworkImageView networkImageView = (NetworkImageView) mView.findViewById(R.id.bubble_netImage);
                                        networkImageView.setDefaultImageResId(R.drawable.logo24x24);
                                        networkImageView.setErrorImageResId(R.drawable.logo24x24);
                                        networkImageView.setVisibility(View.VISIBLE);
                                        networkImageView.setImageUrl(Constantes.URL_IMAGENES + "eventos/" + id + "/logo/" + icono, MySingleton.getInstance(getBaseContext()).getImageLoader());
                                    }
                                }
                                txtTitle.setText(nombre);
                                if (!descripcion.isEmpty()) {
                                    txtDescription.setText(descripcion);
                                } else {
                                    txtDescription.setVisibility(View.GONE);
                                }

                                layout.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), ActivityPaginaEditable.class);
                                        intent.putExtra("id", id);
                                        intent.putExtra("latitud", latitud);
                                        intent.putExtra("longitud", longitud);
                                        intent.putExtra("marcador", marcador);
                                        intent.putExtra("icono", icono);
                                        intent.putExtra("nombre", nombre);
                                        intent.putExtra("descripcion", descripcion);
                                       // intent.putExtra("tipo", tipo);
                                        intent.putExtra("foto_portada", foto_portada);
                                    //    startActivityForResult(intent, FUE_EDITADO);

                                        startActivityIntent.launch(intent);
                                    }
                                });

                            }

                            @Override
                            public void onClose() {

                            }
                        };
//                        infoWindow.getView().setTag(marker);
                        marker.setInfoWindow(infoWindow);
                        marker.setInfoWindowAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                        marker.setRelatedObject(id);
                        mMapView.getOverlays().add(marker);
                        mMapView.invalidate();
                    }

                if (fragmentCardIniciada) {
                    CardFragmentCuenta.adaptador.actualizarDataset();
                }
            }else{
                Toast.makeText(this, "No tiene elementos en esta categoría", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }


    @Override
    public void onFragmentCardInteraction(double latitud, double longitud, int id) {

        List<Overlay> markerLabeleds =  mMapView.getOverlays();

        for (Overlay m: markerLabeleds){
            if (m instanceof Marker) {
                Marker markerLabeled = (Marker) m;
                if (!markerLabeled.getRelatedObject().equals(id)) {

                    markerLabeled.closeInfoWindow();
                } else {
                    markerLabeled.showInfoWindow();
                }
            }
        }
        mMapView.getController().animateTo(new GeoPoint(latitud, longitud));

        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0 && !CuentaDrawerAdapter.drawerItemList.get(position).isTitle){
                selectItem(position);
            }
        }
    }

    public void selectItem(int possition) {
 /*       if (procesandoNuevo) {
            mMapView.removeViewAt(mMapView.getChildCount() - 1);
            mMapView.getOverlays().remove(mark);
            countBackPressed = 0;
            procesandoNuevo = false;

        }*/
            mDrawerList.setItemChecked(possition, true);
            seleccion_actual = possition;
            String itemName = CuentaDrawerAdapter.drawerItemList.get(possition).getItemName();
            setTitle(itemName);

            obtenerMarkers();


        drawer.closeDrawer(mDrawerList);

    }
}

// TODO: 12/09/2023 Para la monetización podria ser: 
// permitir la creacion de anuncios genéricos sin necesidad de ubicación, ni de tipificación
// permitir la opción de publicitar un evento desde el menú privado
// Crear las páginas correspondientes en las redes sociales más utilizadas y publicar diariamente si es posible



