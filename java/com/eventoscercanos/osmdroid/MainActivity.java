package com.eventoscercanos.osmdroid;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;

import com.eventoscercanos.osmdroid.cuenta.constants.OpenStreetMapConstants;
import com.eventoscercanos.osmdroid.cuenta.ActivityCuenta;
import com.eventoscercanos.osmdroid.monetizacion.CheckoutActivity;
import com.eventoscercanos.osmdroid.nuevo.FragmentA;
import com.eventoscercanos.osmdroid.nuevo.Iconos_fragment;
import com.eventoscercanos.osmdroid.nuevo.NuevoEvento;
import com.eventoscercanos.osmdroid.nuevo.TabbedActivity;
import com.eventoscercanos.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.eventoscercanos.osmdroid.syncSQLiteMySQL.utils.Utilidades;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

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

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eventoscercanos.osmdroid.syncSQLiteMySQL.sync.SyncAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.eventoscercanos.osmdroid.MapFragment.id_categoria;
import static com.eventoscercanos.osmdroid.MapFragment.latNor;
import static com.eventoscercanos.osmdroid.MapFragment.latSur;
import static com.eventoscercanos.osmdroid.MapFragment.eventsBoxJSON;
import static com.eventoscercanos.osmdroid.MapFragment.lonEst;
import static com.eventoscercanos.osmdroid.MapFragment.lonOes;
import static com.eventoscercanos.osmdroid.MapFragment.mMapView;
import static com.eventoscercanos.osmdroid.MapFragment.zoomLevel;


public class MainActivity extends AppCompatActivity implements OpenStreetMapConstants, FragmentA.OnFragmentInteractionListener, CardFragment.OnFragmentInteraction {

    private static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG";
    private static final String FRAGMENT_ICONOS = "FRAGMENT_ICONOS";
    private static final String FRAGMENT_CARD = "FRAGMENT_CARD";
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 22;
    FragmentManager fm;
    private SharedPreferences mPrefs;
    private boolean sesion_iniciada;
    MapFragment mapFragment;
    CardFragment fragmentCard;
    Marker mark;
    Iconos_fragment fragment;
    private DrawerLayout mDrawerLayout;
    static ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private int currentFragment;
    public static int openedFragmentCard;
    private boolean primeraVez;
    private CharSequence
            mTitle;
    public static CustomDrawerAdapter adapter;
    List<DrawerItem> dataList;
    AlertDialog alert;
    public static SQLiteDatabase db;
    SessionManager sesionManager;
    private SearchView searchView;
    private Context context;
    static boolean procesandoNuevo;
    final int MAP_FRAGMENT = 0;
    final int CARD_FRAGMENT = 1;
    final int ICONOS_FRAGMENT = 2;
    private RelativeLayout contenedor;
    static boolean procesando_busqueda;
    ActivityResultLauncher<Intent> startActivityIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar_mainActivity);
        setSupportActionBar (toolbar);
        contenedor = (RelativeLayout) findViewById(R.id.contenedor_mapa);
        fm = this.getSupportFragmentManager();
/*
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchManager.setOnDismissListener(new SearchManager.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });*/
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        handleIntent(getIntent());  // Assumes current activity is the searchable activity
        dataList = new ArrayList<>();
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        mDrawerList = (ListView) findViewById(R.id.nav_view);
        sesionManager = SessionManager.getInstancia(this);
        primeraVez = sesionManager.isFirstTime();
        long lastSync = sesionManager.getUltimaSicronizacion();

        db = BdSingleton.getInstance(getApplicationContext()).getSqLiteBD().getReadableDatabase();

        boolean sicronizar = (System.currentTimeMillis() - lastSync) > 259200000; //mayor a tres dias
        if (sicronizar || primeraVez) {
            SyncAdapter.sincronizarAhora(this, false);
            sesionManager.setUltimaSicronizacion(System.currentTimeMillis());
        }

        // ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
//                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

            public void onDrawerOpened(View drawerView) {

            }
        };


        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mPrefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        dataList.add(new DrawerItem(sesionManager.getUserName(), sesionManager.getEmail()));   //adding image header to
        //dataList.add(new DrawerItem(true));  // adding a spinner to the list
        dataList.add(new DrawerItem("Últimos Eventos"));    // adding a header to the list
        dataList.add(new DrawerItem("Categorías"));    // adding a header to the list

        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        context = this.getApplicationContext();
        sesion_iniciada = sesionManager.isLoggedIn();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 11;
        switch (requestCode) {
/*            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was grantedm
//                  mostrarMapa();
                    mMapView.invalidate();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (!primeraVez) {
                        Snackbar.make(contenedor, "Debe conceder el permiso de almacenamiento para cargar el mapa", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Permisos", v -> {
                                    goToSettings(); // o solicitar permiso de nuevo
//                                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                }).show();
                    }
                }
                break;*/

            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    mostrar_ubicacion();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(contenedor, "Debe conceder el permiso de Ubicación para ver su posición", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Permisos", v -> {
                                goToSettings(); // o solicitar permiso de nuevo
//                                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            }).show();
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(procesandoNuevo){
            if (mMapView != null){
                mMapView.removeAllViews();
                List<Overlay> list = mMapView.getOverlays();
                for (Overlay o:list){
                    if (! (o instanceof MyLocationNewOverlay)){
                        list.remove(o);
                    }
                }
                invalidateOptionsMenu();
                procesandoNuevo = false;
            }
        }
        if (sesion_iniciada != sesionManager.isLoggedIn())//cambio de usuario
        {
            CustomDrawerAdapter.drawerItemList.remove(0);
            CustomDrawerAdapter.drawerItemList.add(0, new DrawerItem(sesionManager.getUserName(), sesionManager.getEmail()));
            MainActivity.adapter.notifyDataSetChanged();
            sesion_iniciada = sesionManager.isLoggedIn();
        }
        if (sesionManager.isNewAdded()){
            id_categoria = sesionManager.getIdCatOfNewAdded();
            CustomDrawerAdapter.currentSelected = sesionManager.getTipeOfNewAdded();
            mapFragment.getMarkersByBox(this, true, false);
            sesionManager.setIsNewAddedToFalse();
            //setTitle("Localiza");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mostrarMapa();
//        if (!Utilidades.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
//            solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//        }

        startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Add same code that you want to add in onActivityResult method
                    if (result.getResultCode() == Constantes.LOGIN){
                        Intent intent = new Intent(getApplicationContext(), ActivityCuenta.class);
                        startActivity(intent);
                    }
                });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }
/*
    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();

        appData.putBoolean("extraData", true);
        startSearch(null, false, appData, false);
        return true;
    }
*/

     void handleIntent(Intent intent) {                                                                                                              //---------
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//            searchView.setQuery(query, false);
            setTitle(query);
            if (query != null) {
                searchView.setQuery(query, true);
            }
        }
    }

    int countBackPressed = 0;
    @Override
    public void onBackPressed() {
        boolean cerrar = true;

        if (procesandoNuevo && countBackPressed == 1) {
//
            mMapView.removeAllViews();
            mMapView.getOverlays().remove(mark);
            procesandoNuevo = false;
            countBackPressed = 0;
            cerrar = false;
            invalidateOptionsMenu();
        }
        countBackPressed++;
/*
        if(countBackPressed >= 2){
            mMapView.removeViewAt(mMapView.getChildCount() - 1);
            mMapView.getOverlays().remove(mark);
            procesando_nuevo = false;
            countBackPressed = 0;
            cerrar = false;
            procesandoNuevo = false;
            invalidateOptionsMenu();
        }*/
/*        if (procesando_busqueda){
            cerrar = true;
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == CARD_FRAGMENT) {
                
                currentFragment = MAP_FRAGMENT;
                invalidateOptionsMenu();
            }else {
                if (currentFragment == ICONOS_FRAGMENT) {
                    currentFragment = MAP_FRAGMENT;
                    invalidateOptionsMenu();
                }
            }
            if(cerrar) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

          if(menu != null){
            menu.clear();
        }

        if (currentFragment == MAP_FRAGMENT) {

            assert menu != null;

            getMenuInflater().inflate(R.menu.main, menu);
            menu.add(Menu.NONE, Constantes.ACERCA_DE, 5, "Información");


        } else {

           if (currentFragment == CARD_FRAGMENT) {
                getMenuInflater().inflate(R.menu.menu_card, menu);

               assert menu != null;
               final MenuItem searchItem = menu.findItem(R.id.buscar);
//               searchItem.setTitle("Buscar " + obtenerTipo());
               searchView = (SearchView) searchItem.getActionView();

                   SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                   searchView = (SearchView) menu.findItem(R.id.buscar).getActionView();
                   // Assumes current activity is the searchable activity
                   searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                   searchView.setIconifiedByDefault(true); //
                   searchView.setQueryRefinementEnabled(true);
                   searchView.setSubmitButtonEnabled(true);
                   searchView.setQueryHint("Buscar Eventos...");
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                   @Override
                   public boolean onQueryTextSubmit(String query) {
//                       Toast.makeText(MainActivity.this, "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                       searchView.clearFocus();
                       procesando_busqueda = true;
                       setTitle(query);
                       JSONObject json_request = new JSONObject();
                       if (zoomLevel <= 5){
                           Toast.makeText(context, "Enfoque y aumente el zoom del mapa en la zona de su interés para realizar la búsqueda", Toast.LENGTH_LONG).show();
                           return true;
                       }
                       try {
                           json_request.put("palabras_clave", query);
                           json_request.put("latNort", latNor);
                           json_request.put("latSur", latSur);
                           json_request.put("longEst", lonEst);
                           json_request.put("longOest", lonOes);
                           if(MapFragment.tipo.equals("eventos")){
                               json_request.put("fecha", MapFragment.obtener_fecha("today"));
                           }

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       final String BUSQUEDA_URL = Constantes.URL_BASE + "eventos/buscar_eventos.php";
                       MySingleton.getInstance(context).addToRequestQueue(
                               new JsonObjectRequest(
                                       Request.Method.POST,
                                       BUSQUEDA_URL, json_request,
                                       response -> {
                                   mMapView.removeAllViews();
                                   List<Overlay> list = mMapView.getOverlays();
                                   for (Overlay o:list){
                                       if (! (o instanceof MyLocationNewOverlay)){
                                           list.remove(o);
                                       }
                                   }
                                   eventsBoxJSON = null;
                                   CardFragment.adaptador.actualizarDataset();
                                   MapFragment.procesar_respuesta(response, true, false);
                               },
                                       error -> Toast.makeText(context, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show()
                               )
                       );
                       //doMySearch(query);
                       SearchRecentSuggestions suggestions = new SearchRecentSuggestions(context, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                       suggestions.saveRecentQuery(query, null);

                       return true;
                   }

                   @Override
                   public boolean onQueryTextChange(String newText) {
                    //   Toast.makeText(MainActivity.this, "onQueryTextChange", Toast.LENGTH_SHORT).show();

                       return false;

                   }
               });

                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                   @Override
                   public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
//                       Toast.makeText(MainActivity.this, "onMenuItemActionExpand", Toast.LENGTH_SHORT).show();
                       menu.removeItem(R.id.eliminarHistorial);
                       searchView.clearFocus();
                       return true;
                   }

                   @Override
                   public boolean onMenuItemActionCollapse(MenuItem item) {
//                       Toast.makeText(MainActivity.this, "onMenuItemActionCollapse", Toast.LENGTH_SHORT).show();
                        menu.add(0, R.id.eliminarHistorial, 2, "Borrar historial de busqueda");
                       return true;
                   }
               });

           }
        }

        return true;
    }

    ActivityResultLauncher<Intent> startActivityIntent2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Add same code that you want to add in onActivityResult method
                    showMarkers();
                }
            });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Add same code that you want to add in onActivityResult method
                    //procesar_pago();
                }
            });
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (currentFragment == MAP_FRAGMENT) {
            if (id == R.id.modo_tarjetas) {

                if (fm.findFragmentByTag(FRAGMENT_CARD) == null) {
                    //Toast.makeText(getApplicationContext(), "iniciando fragmentCard", Toast.LENGTH_SHORT).show();
                    fragmentCard = CardFragment.newInstance("param1", "param2");
                    fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("fragmentCard").add(R.id.tarjetas, fragmentCard, FRAGMENT_CARD).commit();
                    openedFragmentCard = 1;
                }

                //frameLayout.setVisibility(View.VISIBLE);
                currentFragment = CARD_FRAGMENT;
                invalidateOptionsMenu();
            }


            if (id == R.id.nuevo_evento) {

                if (sesion_iniciada) {

                    showMarkers();

                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("origen", "nuevo_evento");

                    startActivityIntent2.launch(intent);
                }
            }


            if (id == R.id.mi_posicion) {
                if (Utilidades.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
                    mostrar_ubicacion();
                }else {
                    solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }
            if (id == Constantes.ACERCA_DE){
                startActivity(new Intent(this, Acerca.class));
            }
            if(id == Constantes.SINCRONIZAR){
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("Estimado usuario")
                        .setMessage("Esta acción actualizará los elementos del menú lateral. Los cambios se verán al reiniciar la aplicación");
// Add the buttons
                builder.setPositiveButton("Sincronizar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    SyncAdapter.sincronizarAhora(getApplicationContext(), false);
                        Toast.makeText(MainActivity.this, "Sincronizando...", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
// Set other dialog properties


// Create the AlertDialog
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
            }

            if (id == R.id.anunciar){
                
                Intent intent = new Intent(this, TabbedActivity.class);
//                intent.putExtra("origen", "nuevo_evento");

                activityResultLauncher.launch(intent);
            }
        } else {
            switch (id) {

                case R.id.buscar:
//                    aqui tambien se puede buscar
//                    Toast.makeText(context, "itemSelectedBuscar", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.mapMode:
                    onBackPressed();
                    break;

                case R.id.eliminarHistorial:
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                            MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                    suggestions.clearHistory();
                    Toast.makeText(context, "El historial ha sido borrado", Toast.LENGTH_SHORT).show();
                    break;
            }

            if (mDrawerToggle.onOptionsItemSelected(item)) {
                   return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMarkers() {
        if (fm.findFragmentByTag(FRAGMENT_ICONOS) == null) {
            fragment = Iconos_fragment.newInstance( "main_activity");
            fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("fragment").add(R.id.frame, fragment, FRAGMENT_ICONOS).commit();
}
        currentFragment = ICONOS_FRAGMENT;
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            final int FUE_EDITADO = 9;
            switch (resultCode) {

                case Constantes.ONCLICK_DRAWER_HEADER:

                    break;
                case Constantes.FIN_NUEVO:
                    mMapView.removeAllViews();
                    mMapView.getOverlays().clear();
                    break;
                case FUE_EDITADO:
                    mapFragment.getMarkersByBox(this, false, true);
                    break;
            }
        }

    public void onPause() {
        super.onPause();// Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();

           // GeoPoint geoPoint = (GeoPoint) MapFragment.mMapView.getProjection().fromPixels(MapFragment.mMapView.getWidth() / 2, MapFragment.mMapView.getHeight() / 2, punto_cero);

            GeoPoint geoPoint = (GeoPoint) mMapView.getMapCenter();
                final SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString(PREFS_MAP_CENTER, geoPoint.getLatitude() + "," + geoPoint.getLongitude());
                edit.putFloat(PREFS_ZOOM_LEVEL, (float) mMapView.getZoomLevelDouble());
                edit.apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null) {
            alert.dismiss();
        }

    }

    @Override
    public void onFragmentInteraction(int pos, final String icono, final String activity_origen) {

        currentFragment = MAP_FRAGMENT;
        mMapView.removeAllViews();
        mMapView.getOverlays().clear();
        if (openedFragmentCard == 1){
            eventsBoxJSON = null;
            CardFragment.adaptador.actualizarDataset();
        }
        procesandoNuevo = true;
        countBackPressed = 0;
        final Intent intent;
        Bitmap marker;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open(icono + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        matrix.postScale(2.5f, 2.5f);
        assert bitmap != null;
        marker = Bitmap.createBitmap(bitmap, 0, 0, 32, 37, matrix, false);

        mark = new Marker(mMapView);
        String Titulo = "Nuevo Evento";
        intent = new Intent(getBaseContext(), NuevoEvento.class);


        final String finalDescripcion = "Mantenga pulsado el marcador\npara arrastrarlo a la posición\ndeseada. Luego toque esta ventana\npara continuar";
        final String finalTitulo = Titulo;
        InfoWindow infoWindow = new InfoWindow(R.layout.mi_bonuspack_bubble, mMapView) {
            @Override
            public void onOpen(Object item) {
                LinearLayout layout = (LinearLayout) mView.findViewById(R.id.mi_bubble_layout);
                ImageView imagen = (ImageView) mView.findViewById(R.id.bubble_image);

                TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
                TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);
                //TextView txtSubdescription = (TextView) mView.findViewById(R.id.bubble_subdescription);

                imagen.setImageResource(R.drawable.icono64);
                txtTitle.setText(finalTitulo);
                txtDescription.setText(finalDescripcion);
                layout.setOnClickListener(v -> {
                    // Override Marker's onClick behaviour here

                    intent.putExtra("latitud", mark.getPosition().getLatitude());
                    intent.putExtra("longitud", mark.getPosition().getLongitude());
                    intent.putExtra("marcador", icono);
                    intent.putExtra("modoEdicion", false);
                    startActivity(intent);
                });
            }

            @Override
            public void onClose() {

            }
        };

        mark.setInfoWindow(infoWindow);
        mark.setPosition((GeoPoint) mMapView.getMapCenter());
        mark.setDraggable(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//             mark.setInfoWindowAnchor(Marker.ANCHOR_RIGHT, Marker.ANCHOR_TOP);
        mark.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.showInfoWindow();
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
            }
        });

//        mark.setIcon(marker);
//        mark.setLabelFontSize(0);
        mark.setIcon(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.mk_pers_male));
        mark.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView. getOverlays().add(mark);
        mark.showInfoWindow();

        mMapView.invalidate();
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title; // mTitle es usado cada vez que se cierra el Drawer
        Objects.requireNonNull(getSupportActionBar()).setTitle(mTitle);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
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
            Toast.makeText(getApplicationContext(), "drawer click", Toast.LENGTH_SHORT).show();



            if (position != 1 && position != 6) {
                  setTitle(adapter.getTitle());
                  SelectItem(position);
   

        }

     }
    }



    public void SelectItem(int possition) {

        MapFragment.id_categoria = CustomDrawerAdapter.drawerItemList.get(possition).getId_remota();
        if (fragment != null){
            if (fragment.isVisible()) {
                fm.popBackStack();
                currentFragment = MAP_FRAGMENT;
            }
        }
        switch (possition) {
            case 0: //

                if (sesion_iniciada) {
                    Intent intent = new Intent(this, ActivityCuenta.class);
                    startActivity(intent);
                } else {
                    Intent inten = new Intent(this, LoginActivity.class);
                    inten.putExtra("origen", "onClickDrawerHeader");
                    //startActivityForResult(inten, Constantes.ONCLICK_DRAWER_HEADER);

                    startActivityIntent.launch(inten);
                }
                break;

            default:

                //MapFragment.mMapView.getOverlays().removeAll(new ArrayList<Object>());

                if (procesandoNuevo) {
                    mMapView.removeAllViews();
                    mMapView.getOverlays().remove(mark);
                    countBackPressed = 0;
                    procesandoNuevo = false;

                }
                procesando_busqueda = false;

                mDrawerList.setItemChecked(possition, true);
                mTitle  = CustomDrawerAdapter.drawerItemList.get(possition).getItemName();
//                mSubTitle = CustomDrawerAdapter.mTitle;
                setTitleAndSubTitle(mTitle);
                if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
                    mapFragment = MapFragment.newInstance();
                    fm.beginTransaction().add(R.id.mapa, mapFragment, MAP_FRAGMENT_TAG).commit();
                    mMapView = mapFragment.getMapView();
                    assert mMapView != null;
                    mMapView.getController().setCenter(GeoPoint.fromDoubleString(mPrefs.getString(PREFS_MAP_CENTER, "-25.388400,-57.136800"), ','));
                    mMapView.getController().setZoom(mPrefs.getFloat(PREFS_ZOOM_LEVEL, 5.0f));
                } else {

                    mMapView = (MapView) Objects.requireNonNull(fm.findFragmentByTag(MAP_FRAGMENT_TAG)).getView();
                }
                assert mMapView != null;
                if (mMapView.getZoomLevelDouble() > 5 ) {// || currentFragment == CARD_FRAGMENT

                    mapFragment.getMarkersByBox(this, false, false);
                }else  {

                Toast.makeText(context, "Aumente el zoom para ver los elementos", Toast.LENGTH_SHORT).show();

            }
        }
        Log.i("Main Activity", "Hora de cerrar el drawer");
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setTitleAndSubTitle(CharSequence title){

        setTitle(title);

    }


    private void solicitarPermiso(final String permission, final int requestCode){

                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        requestCode);

    }

    private void mostrarMapa(){

            if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
                mapFragment = MapFragment.newInstance();
                fm.beginTransaction().add(R.id.mapa, mapFragment, MAP_FRAGMENT_TAG).commit();
                currentFragment = MAP_FRAGMENT;

            } else {

                mapFragment = (MapFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG);

            }

    }

    ActivityResultLauncher<Intent> startActivityIntent3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

            });

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);

        startActivityIntent3.launch(myAppSettings);
    }
private void mostrar_ubicacion(){

    GeoPoint p;
    if (mapFragment == null) {
        mapFragment = (MapFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG);
    }
    assert mapFragment != null;
    p = mapFragment.getmyPosition();
    if (p != null) {

        mMapView.getController().animateTo(p);

    } else {
        alert = null;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager == null) throw new AssertionError();
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Para una mejor experiencia favor activar el servicio de ubicación")
                    .setCancelable(true)
                    .setPositiveButton("Si", (dialog, id) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            alert = builder.create();
            alert.show();
        }else {
            Toast.makeText(context, "Esperando ubicación...", Toast.LENGTH_SHORT).show();
        }

    }
}

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}





   // todo User Favorites: Let users save favorite events.
    //    Push Notifications: Send notifications about new or popular events.