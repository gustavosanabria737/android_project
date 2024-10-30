package com.eventoscercanos.osmdroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;


import com.eventoscercanos.osmdroid.cuenta.constants.OpenStreetMapConstants;
import com.eventoscercanos.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;

import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public  class MapFragment extends Fragment implements OpenStreetMapConstants {//, MapEventsReceiver

    public static MapView mMapView;
    private BoundingBox boxE6;
    private MyLocationNewOverlay miPos;
    private IGeoPoint mapCenter;
    private static String url ;
    static JSONArray eventsBoxJSON;
    static String tipo = "eventos";
    static double zoomLevel;
    static int id_categoria;
    private static float densidad;
    private static Activity cntx;
    static double latNor,latSur,lonEst,lonOes;
    long ultimaLamada = 0;
    private static final int LOCALES = 0;
    private static final int EVENTOS = 1;
    private static final int PROMOCIONADOS = 2;
    private static final int ESPECIALES = 3;
    private BoundingBox box;
    private boolean recienCreado;
    IMapController mc;
    SharedPreferences mPrefs;
    int categoriaEnProceso;
    private boolean cancelarTodo;
    RelativeLayout contenedorMapa;
    double previusZoom;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsBoxJSON = new JSONArray();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMapView = new MapView(inflater.getContext());
        mc = new MapController(mMapView);
        recienCreado = true;
        mPrefs = inflater.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        IConfigurationProvider osmConf = Configuration.getInstance();
        File basePath = new File(inflater.getContext().getFilesDir(), "osmdroid");
        osmConf.setOsmdroidBasePath(basePath);
        File tileCache = new File(osmConf.getOsmdroidBasePath().getAbsolutePath(), "tile");
        osmConf.setOsmdroidTileCache(tileCache);
        return mMapView;
    }


    @Override
    public final void onStart() {
        super.onStart();
        //final DisplayMetrics dm = context.getResources().getDisplayMetrics();= -23.45197308500738,-58.40936477342291
        // I/OsmDroid: 1541249882849 onZoom 6.319808483123779
        String center =  mPrefs.getString(PREFS_MAP_CENTER, "-23.45197308500738,-58.40936477342291");
        mapCenter = GeoPoint.fromDoubleString(center, ',');
        mc.setCenter(mapCenter);
        mc.setZoom(mPrefs.getFloat(PREFS_ZOOM_LEVEL, 6.2f));
        zoomLevel = mMapView.getZoomLevelDouble();
        previusZoom = zoomLevel;
        mMapView.setMultiTouchControls(true);
        mMapView.setMinZoomLevel(2.0);
//        mMapView.scrollTo((int)(mapCenter.getLongitude() + 0.00000001), (int)(mapCenter.getLatitude() + 0.00000001));
        Log.i("geoPoint", center);

//        mMapView.getController().animateTo(mapCenter);
        mMapView.addMapListener(new MapListener() {

               int x = 0, y = 0;

            @Override
            public boolean onScroll(ScrollEvent event) {

                x = event.getX();
                y = event.getY();

                if(event.getX()!= 0){// &&  zoomLevel>11
                    mapCenter = mMapView.getMapCenter();

                    if (!BoxContainsPoint()) {
                    //el punto centro inicial ha salido de la pantalla

                        actualizarBoxes();
                        obtenerMarcadores();

                    }
                }

                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {//se llama por primera vez al arrancar la aplicacion
                zoomLevel = mMapView.getZoomLevelDouble();
                actualizarBoxes();
                if (zoomLevel < previusZoom) {//para obtener marcadores solamente cuando se hace zoom de mas a menos
                    if ((previusZoom - (int)zoomLevel >= 1)) {
                        obtenerMarcadores();
                        previusZoom =(int) zoomLevel;
                    }
                }else if(zoomLevel>previusZoom){
                    previusZoom =(int) zoomLevel;
                }
                return true;
            }
        });
        cntx = getActivity();
        contenedorMapa = getActivity().findViewById(R.id.contenedor_mapa);
        miPos = new MyLocationNewOverlay(new GpsMyLocationProvider(cntx), mMapView);
        DisplayMetrics dm = new DisplayMetrics();
        cntx.getWindowManager().getDefaultDisplay().getMetrics(dm);
        densidad = dm.density;
        //para cargar al iniciar
        id_categoria = -2;
        actualizarBoxes();
        obtenerMarcadores();
    }

    private void actualizarBoxes() {

            box = mMapView.getBoundingBox();

            latNor = box.getLatNorth();
            lonEst = box.getLonEast();
            latSur = box.getLatSouth();
            lonOes = box.getLonWest();

    }

    private boolean BoxContainsPoint() {
        boolean b = false;
        if (box.contains(mapCenter)) {
            b = true;
        }
        return b;
    }

    @Override
    public void onPause() {
        super.onPause();
        miPos.disableMyLocation();
        mMapView.onPause();
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putFloat(LAT_NORTH, (float) latNor);
        editor.putFloat(LAT_SUR, (float) latSur);
        editor.putFloat(LONG_EAST, (float) lonEst);
        editor.putFloat(LONG_WEST, (float) lonOes);
        editor.apply();
        cancelarTodo = true;

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.getOverlays().add(miPos);
        miPos.enableMyLocation();
     //  MapEventsOverlay mapOverlay = new MapEventsOverlay( getContext(), this);
//       mMapView.getOverlays().add(mapOverlay);

        mMapView.onResume();
        if (recienCreado){
            latNor = mPrefs.getFloat(LAT_NORTH, 0.0f);
            latSur = mPrefs.getFloat(LAT_SUR, 0.0f);
            lonEst = mPrefs.getFloat(LONG_EAST, 0.0f);
            lonOes = mPrefs.getFloat(LONG_WEST, 0.0f);
            box = new BoundingBox(latNor, lonEst, latSur, lonOes);
            recienCreado = false;
        }else {
            actualizarBoxes();
        }
        cancelarTodo = false;
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }


    public  MapView getMapView() {
        return mMapView;
    }


    public  void getMarkersByBox(final Context context, boolean sinMensajeEspera, final boolean sinToast) {

            if (id_categoria == 0 || zoomLevel < 7 ){
                return;
            }
            eventsBoxJSON = null;
        //       String poligono = "\"" + latSur + "\"" + " " + "\"" + lonOes + "\"," + "\"" + latSur + "\"" + " " + "\"" + lonEst + "\"," + "\"" + latNor + "\"" + " " + "\"" + lonEst + "\"," + "\"," + latNor + "\"" + " " + "\"" + lonEst + "\"," + "\"" + latSur + "\"" + " " + "\"" + lonOes + "\"";
//        String center = "\"" + mapCenter.getLatitudeE6() + "\"" + " " + "\"" + mapCenter.getLongitudeE6() + "\"";

            mMapView.removeAllViews();
            List<Overlay> list = mMapView.getOverlays();
            for (Overlay o:list){
                if (! (o instanceof MyLocationNewOverlay)){
                    list.remove(o);
                }
            }

        if (MainActivity.openedFragmentCard == 1  ) { //&& MainActivity.currentFragment == MainActivity.CARD_FRAGMENT
            RecyclerCardAdaptador.data = null;
            CardFragment.adaptador.notifyDataSetChanged();
        }

            JSONObject jsonForm = new JSONObject();
            try {

                jsonForm.put("id_categoria", id_categoria);

                jsonForm.put("latNort", latNor);
                jsonForm.put("latSur", latSur);
                jsonForm.put("longEst", lonEst);
                jsonForm.put("longOest", lonOes);

                jsonForm.put("fecha", obtener_fecha("today"));

                if (id_categoria == Constantes.WEEK_EVENTS){

                    jsonForm.put("fecha", obtener_fecha("this_week"));

                }else if (id_categoria == Constantes.MONTH_EVENTS){

                    jsonForm.put("fecha", obtener_fecha("this_month"));

                }
                Log.d("MapFragment JSON", jsonForm.toString());

            } catch (JSONException e) {

                e.printStackTrace();
            }
            refreshUrl();
        final Snackbar snackbar = Snackbar.make(contenedorMapa, "Un momento...", Snackbar.LENGTH_INDEFINITE);
        if (!sinMensajeEspera){
                snackbar.show();
            }
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonForm, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            snackbar.dismiss();
                            try {
                                int idcatSolicitada = response.getInt("id_categoria");

                                if (id_categoria == idcatSolicitada && !cancelarTodo) {
                                    procesar_respuesta(response, false, sinToast);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            snackbar.dismiss();
                          if (!cancelarTodo) {
                              error.printStackTrace();
                              Toast.makeText(context, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();
                          }
                        }
                    }
                    );
            MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
            Log.i("URL: ", url);
    }

    static void procesar_respuesta(JSONObject response, boolean isSearch, boolean sinToast) {

        try {
            int resultJSON = response.getInt("estado");   // estado es el nombre del campo en el JSON
            //int num_registros=response.getInt("num_filas");
            if (resultJSON == 1) {
               eventsBoxJSON = response.getJSONArray("eventos");  // estado es el nombre del campo en el JSON

                if(eventsBoxJSON.length() > 0) {

                    for (int i = 0; i < eventsBoxJSON.length(); i++) {
                        final int id;
                        try {

                            id = eventsBoxJSON.getJSONObject(i).getInt("id_evento");

                            final double latitud = eventsBoxJSON.getJSONObject(i).getDouble("latitud");
                            final double longitud = eventsBoxJSON.getJSONObject(i).getDouble("longitud");
                            final String marcador = eventsBoxJSON.getJSONObject(i).getString("marcador");
                            final String icono = eventsBoxJSON.getJSONObject(i).getString("icono");

                            final String nombre = eventsBoxJSON.getJSONObject(i).getString("nombre");
                            final String descripcion = eventsBoxJSON.getJSONObject(i).getString("descripcion");
                            final String foto_portada = eventsBoxJSON.getJSONObject(i).getString("foto_portada");
                            final Marker marker = new Marker(mMapView);
                            Bitmap markerBitmap = null;
                            Bitmap iconoBitmap = null;
                            try {
                                markerBitmap = BitmapFactory.decodeStream(cntx.getAssets().open(marcador + ".png"));
                                Matrix matrix = new Matrix();
                                if (densidad > 1) {
                                    if (densidad > 1.5){
                                        matrix.postScale(2.5f, 2.5f);
                                    }else {
                                        matrix.postScale(2.0f, 2.0f);
                                    }
                                }else {
                                    matrix.postScale(1.2f, 1.2f);
                                }
                                markerBitmap = Bitmap.createBitmap(markerBitmap, 0, 0, 32, 37, matrix, false);

                                if (!icono.contains(".png") && !icono.equals("null")){
                                        iconoBitmap = BitmapFactory.decodeStream(cntx.getAssets().open(icono + ".png"));
                                    }

                            } catch (IOException e) {
                                e.printStackTrace();
                            };
                          //  marker.setIcon(markerBitmap);
                            marker.setIcon(AppCompatResources.getDrawable(cntx, R.drawable.mk_pers_male));
                            //marker.setLabelFontSize(0);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            marker.setPosition(new GeoPoint(latitud, longitud));

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
//                             Button btnMoreInfo = (Button) mView.findViewById(R.id.bubble_moreinfo);
                                    TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
                                    TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);

                                    if (finalIconoBitmap != null){
                                        imagenView.setImageBitmap(finalIconoBitmap);

                                    }else {
                                        if (icono.equals("null"))
                                        {
                                            try {
                                                imagenView.setImageBitmap(BitmapFactory.decodeStream(cntx.getAssets().open("icono.png")));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            imagenView.setVisibility(View.GONE);
                                            NetworkImageView networkImageView = (NetworkImageView) mView.findViewById(R.id.bubble_netImage);
                                            networkImageView.setDefaultImageResId(R.drawable.fondo_main);
                                            networkImageView.setErrorImageResId(R.drawable.fondo_main);
                                            networkImageView.setVisibility(View.VISIBLE);
                                            networkImageView .setImageUrl(Constantes.URL_IMAGENES  + "eventos/" + id + "/logo/" + icono, MySingleton.getInstance(cntx).getImageLoader());
                                         }
                                    }

                                    txtTitle.setText(nombre);
//                               btnMoreInfo.setVisibility(View.VISIBLE);
                                    if (!descripcion.isEmpty()){
                                        txtDescription.setText(descripcion);
                                    }else{
                                        txtDescription.setVisibility(View.GONE);
                                    }
                                    // txtSubdescription.setText(subdescripccion);
                                    layout.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Intent intent = new Intent(cntx, ActivityPagina.class);
                                            intent.putExtra("id", id);
                                            intent.putExtra("latitud", latitud);
                                            intent.putExtra("longitud", longitud);
                                            intent.putExtra("marcador", marcador);
                                            intent.putExtra("icono", icono);
                                            intent.putExtra("nombre", nombre);
                                            intent.putExtra("descripcion", descripcion);
                                            intent.putExtra("isNuevo", false);
                                            intent.putExtra("foto_portada", foto_portada);
                                            intent.putExtra("tipo", tipo);
                                            cntx.startActivityForResult(intent, 93);
                                            // Override Marker's onClick behaviour here
                                        }
                                    });

                                }

                                @Override
                                public void onClose() {

                                }
                            };
//                       infoWindow.getView().setTag(marker);

                            marker.setInfoWindow(infoWindow);
                            marker.setInfoWindowAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                            marker.setRelatedObject(id);
                            marker.showInfoWindow();
                            mMapView.getOverlays().add(marker);
                            mMapView.invalidate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (MainActivity.openedFragmentCard == 1 ) {// && MainActivity.currentFragment == MainActivity.CARD_FRAGMENT
                    CardFragment.adaptador.actualizarDataset();
                }
            } else if (resultJSON == 2) {
                //devuelve = "No hay qué mostrar";
                if (isSearch){
                    Toast.makeText(cntx, response.getString("mensaje"), Toast.LENGTH_SHORT).show();

                }else if(!sinToast) {//no mostrar mensaje en caso  de zoom

                    Toast.makeText(cntx, "Sin resultados para esta zona", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        public final GeoPoint getmyPosition(){

        return miPos.getMyLocation();
    }

    private void refreshUrl(){

                switch (id_categoria){

                    case Constantes.ALL_EVENTS:
                        url = Constantes.URL_BASE + "eventos/obtenerEventosTodos.php";
                        break;
                    case Constantes.TODAY_EVENTS:
                        url = Constantes.URL_BASE + "eventos/obtenerEventosDeHoy.php";
                        break;
                    case Constantes.MONTH_EVENTS:
                        url = Constantes.URL_BASE + "eventos/obtenerEventosDelMes.php";
                        break;
                    case Constantes.WEEK_EVENTS:
                        url = Constantes.URL_BASE + "eventos/obtenerEventosDeLaSemana.php";
                        break;
                    default:
                        url = Constantes.URL_BASE + "eventos/obtenerEventosPorCategoria.php";
                }
    }



    private void obtenerMarcadores(){
        Log.i("MapFragment", "llamando a obtenerMarcadores");
        if (MainActivity.procesando_busqueda || MainActivity.procesandoNuevo)  {
            return;
        }
            if ((System.currentTimeMillis() - ultimaLamada) > 2000 ) {
                if (zoomLevel > 4 ) {
                    Log.i("MapFragment", "llamando a getMarkers");
                    getMarkersByBox(cntx, true, true);
                }
                ultimaLamada = System.currentTimeMillis();
            }
    }


    static String obtener_fecha(String type) { //today, +week, month

        final Calendar c = Calendar.getInstance();


        switch (type){

            case "this_week":
                c.add(Calendar.DAY_OF_MONTH, 7);
                break;

            case "this_month":
                c.add(Calendar.MONTH, 1);
                break;


        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + (month + 1) + "-" + day;//fecha mysql

    }
}




