package com.eventoscercanos.osmdroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.eventoscercanos.osmdroid.nuevo.ActivityGaleria;
import com.eventoscercanos.osmdroid.nuevo.ImagenDetalleActivity;
import com.eventoscercanos.osmdroid.syncSQLiteMySQL.utils.Constantes;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ActivityPagina extends AppCompatActivity {

    Date dateActual;
    SimpleDateFormat simpleDateFormat;
    private ImageLoader imageLoader;
    private NetworkImageView networkImageView;
    int id;//no privatizar
    private String icono, marcador;
    private LayoutInflater inflater;
    private String nombre, descripcion;
    String foto_portada;
    private String carpetaPortada = "";
    private int height, recomendados;
    String urlPortada;
    private boolean cancelarTodo, isNuevo;
    private LinearLayout contentComentarios, contenedorPagina;
    private EditText editTextComent;
    int id_userLocal;
    private Double  latitud, longitud;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_pagina);
        setSupportActionBar(toolbar);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        height = dm.widthPixels;
        Intent intent;

        contentComentarios = findViewById(R.id.comentarios);
        contenedorPagina = findViewById(R.id.linearLayoutPag);
        intent = getIntent();
        latitud = intent.getDoubleExtra("latitud", 0);
        longitud = intent.getDoubleExtra("longitud", 0);
        nombre = intent.getStringExtra("nombre");
        descripcion = intent.getStringExtra("descripcion");
        id = intent.getIntExtra("id", 0);
        icono = intent.getStringExtra("icono");
        marcador = intent.getStringExtra("marcador");
        isNuevo = intent.getBooleanExtra("isNuevo", false);
        mostrarTodo();
        if(isNuevo) {
            Button button = (Button) findViewById(R.id.boton_submit_pagina);
            button.setVisibility(View.VISIBLE);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Excelente!")
                    .setMessage("Todo ha salido bien.");
// Add the buttons
            builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SessionManager.getInstancia(getBaseContext()).setNewAddedToTrue();
                    Intent intent;
                    intent = new Intent(getApplicationContext(), MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
            final AlertDialog dialog = builder.create();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });
        }

        foto_portada = intent.getStringExtra("foto_portada");

        assert foto_portada != null;
        if (foto_portada.equals("null")){
            urlPortada = Constantes.URL_IMAGENES + "eventos/portadaDefault/thumbs/portadaDefault.png";
            carpetaPortada = Constantes.URL_IMAGENES + "eventos/portadaDefault/";
            foto_portada = "portadaDefault.png";
        }else {
            urlPortada = Constantes.URL_IMAGENES + "eventos/" + id + "/portada/thumbs/" + foto_portada;
            carpetaPortada = Constantes.URL_IMAGENES + "eventos/" + id + "/portada/";
        }
        final String[] arrayFoto = new String[]{foto_portada};
        networkImageView.setImageUrl(urlPortada, imageLoader);
        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImagenDetalleActivity.class);
                intent.putExtra("carpeta", carpetaPortada);
                intent.putExtra("nombre", nombre);
                intent.putExtra("num_fotos", 1 );//foto de portada
                intent.putExtra("nom_fotos",  arrayFoto);
                intent.putExtra("id_foto", id );
                intent.putExtra("extra_image", 0);//indice
                intent.putExtra("marcador", marcador );
                startActivity(intent);
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>(){
                    public void onActivityResult(ActivityResult result){
                        enviarComentario();
                    }

                }
        );
        Button btnSubmitComent = findViewById(R.id.submitComentario);
        btnSubmitComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("coment", "boton clickado");
                if (SessionManager.getInstancia(getBaseContext()).isLoggedIn()){
                    Log.d("coment", "usuario está logueado");
                    enviarComentario();
                }else{
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("origen", "comentar");
                    activityResultLauncher.launch(intent);
//                            startActivityForResult(intent, Constantes.COMENTAR);
                }
            }
        });

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    }

    @Override
    protected void onStart() {
        super.onStart();
        cancelarTodo = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelarTodo = true;
    }

    private void procesar_respuesta(JSONObject response){
        String telefono1, info, ciudad;
        LinearLayout linearLayout;
        String horario = "";
        String hora_evento;
        String fecha_evento, fecha_actual;
        try {
            int resultJson = response.getInt("estado");
            if (resultJson == 1) {
                JSONObject info_json = response.getJSONObject("info");
                info = info_json.getString("informacion");
                String fecha_creacion = info_json.getString("fecha_creacion");
                String fecha_modicacion = info_json.getString("fecha_modificacion");
//                String foto_portada = info_json.getString("foto_portada");
                int veces_vista = info_json.getInt("veces_vista");
                ciudad = info_json.getString("ciudad");
                id_userLocal = info_json.getInt("id_user");


                JSONArray telefonos = response.getJSONArray("telefonos");
                JSONArray cuentas = response.getJSONArray("cuentas");
                JSONArray categorias = response.getJSONArray("categorias");
                JSONArray comentarios = response.getJSONArray("comentarios");

            
                fecha_evento = info_json.getString("fecha_evento");
                hora_evento = info_json.getString("hora_evento");
                fecha_actual = response.getString("fecha_actual");
                dateActual = simpleDateFormat.parse(fecha_actual);
                CardView cuadroFecha = findViewById(R.id.cuadroFechaEvento);
                cuadroFecha.setVisibility(View.VISIBLE);
                TextView fechaEvento = findViewById(R.id.tv_fechaEvento);
                fechaEvento.setText(convertirFecha(fecha_evento));

                TextView horaEvento = findViewById(R.id.tv_horaEvento);
                hora_evento = hora_evento.substring(0, 5) + " hs.";
                horaEvento.setText(hora_evento);


                if (!info.equals("")) {

                    CardView cuadroInfo = (CardView) findViewById(R.id.cuadroInfo);
                    cuadroInfo.setVisibility(View.VISIBLE);
                    TextView tvInfo = (TextView) findViewById(R.id.tv_informacion);
                    tvInfo.setText(info);

                }

                if (telefonos.length() > 0 ) {

                    CardView cardViewTel = (CardView) findViewById(R.id.cuadroTelefonos);
                    cardViewTel.setVisibility(View.VISIBLE);
                    TextView tvTitleTelefonos = (TextView) findViewById(R.id.titleTelef);
                    tvTitleTelefonos.setText("Teléfonos");
                    TextView tvTelefonos = (TextView) findViewById(R.id.tv_telefono);
//                    tvTelefonos.setLineSpacing(5.0f, 1);
//                    String tels = "";
                    String html2 = "";

                    for (int i = 0; i < telefonos.length(); i++) {
                        if (i > 1) break;
                      telefono1 = telefonos.getJSONObject(i).getString("telefono");
//                      tels = tels + (telefono1 + "\n");
                      html2 = html2 + "<a href=tel:" + telefono1 + ">" + telefono1 + "</a>";
                        if (!(i == telefonos.length() - 1)){
                            html2 = html2 + "<br>";
                        }
                    }
                    tvTelefonos.setText(Html.fromHtml(html2));
//                   tvTelefonos.setText(tels);
                    tvTelefonos.setMovementMethod(LinkMovementMethod.getInstance());
//                    Linkify.addLinks(tvTelefonos, Linkify.PHONE_NUMBERS);
                }

                String cuenta = null;

                if (cuentas.length() > 0) {

                    CardView cardViewCuentas = (CardView) findViewById(R.id.card_cuentas);
                    cardViewCuentas.setVisibility(View.VISIBLE);
                    TextView textViewCuentas = (TextView) findViewById(R.id.tv_cuentas);
                    textViewCuentas.setLineSpacing(10.0f, 1);
                    textViewCuentas.setAutoLinkMask(Linkify.ALL);
                    String cuentass = "";
                    for (int i = 0; i < cuentas.length(); i++) {

                        cuenta = cuentas.getJSONObject(i).getString("cuenta");
                        cuentass = cuentass + cuenta ;
                        if (!(i == cuentas.length() - 1)){
                            cuentass = cuentass + "\n";
                        }

                    }
                    textViewCuentas.setText(cuentass);
                }

                JSONArray jsonArrayFotos = response.getJSONArray("fotos");
                if (jsonArrayFotos.length() > 0) {
                    linearLayout = findViewById(R.id.linearLayoutFotos);
                    linearLayout.setVisibility(View.VISIBLE);
                    final String[] nombresFotos = new String[jsonArrayFotos.length()];
                    String carpeta_imagenes = Constantes.URL_IMAGENES  + "eventos/" + id + "/";
                    for (int i = 0; i < jsonArrayFotos.length(); i++) {
                        String nombreFoto = jsonArrayFotos.getJSONObject(i).getString("nombre_foto");
                        nombresFotos[i] = nombreFoto;
                    }
                    for (int i = 0; i < jsonArrayFotos.length(); i++) {
                        String nombreFoto = jsonArrayFotos.getJSONObject(i).getString("nombre_foto");

                        if (i >= 3){
                            break;//solo mostrar 3 fotos
                        }
                        View view = inflater.inflate(R.layout.card_foto_pagina, linearLayout, false);
                        NetworkImageView n = (NetworkImageView) view.findViewById(R.id.niv_card_pagina);
                        n.setErrorImageResId(R.drawable.fondo_main);
                        n.setMaxHeight(height);
                        TextView tvDescrip = (TextView) view.findViewById(R.id.tv_descr_pagina);
                        final Intent intent = new Intent(getApplicationContext(), ImagenDetalleActivity.class);
                        intent.putExtra("carpeta", carpeta_imagenes);
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("marcador", marcador);
                        final int finalI = i;
                        n.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                intent.putExtra("extra_image", finalI);
                                intent.putExtra("num_fotos", nombresFotos.length);
                                intent.putExtra("nom_fotos", nombresFotos);

                                startActivity(intent);
                            }
                        });

                        n.setImageUrl(Constantes.URL_IMAGENES + "eventos/" + id + "/thumbs/" + nombreFoto, imageLoader);
                        String descripcionFoto = jsonArrayFotos.getJSONObject(i).getString("descripcion");

                        tvDescrip.setText(descripcionFoto);
                        linearLayout.addView(view);
                    }
                    if (jsonArrayFotos.length() > 3) {
                        Button verTodasLasFotos = new Button(this);
                        verTodasLasFotos.setText("Ver todas las fotos");

                        verTodasLasFotos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), ActivityGaleria.class);
                                intent.putExtra("id", id);
                                intent.putExtra("nombre", nombre);
                                intent.putExtra("marcador", marcador);
                                startActivity(intent);
                            }
                        });

                        linearLayout.addView(verTodasLasFotos);
                    }
                }

                if (comentarios.length() > 0){

                    for (int i = 0; i < comentarios.length(); i++){

                        JSONObject jsonObject = comentarios.getJSONObject(i);
                        agregarComentario(jsonObject, false, fecha_actual);
                    }
                }


                //  Fechas, categorias y veces vista
                //=================================================================================================
            if (!fecha_creacion.equals("") && !isNuevo) {
                findViewById(R.id.cuadrofechaCreacion).setVisibility(View.VISIBLE);
                TextView tvFechaCreacion = findViewById(R.id.tv_fechaCreacion);
                tvFechaCreacion.setText(convertirFecha(fecha_creacion));
                if (!fecha_creacion.equals(fecha_modicacion)) {
                    LinearLayout cuadroFechaModificacion = findViewById(R.id.cuadroFechaModificacion);
                    cuadroFechaModificacion.setVisibility(View.VISIBLE);
                    TextView tvFechaModificacion = findViewById(R.id.tv_fechaModificacion);
                    tvFechaModificacion.setText(convertirFecha(fecha_modicacion));
                }


                TextView vecesVista = findViewById(R.id.tv_vecesVista);
                String sufijo = " personas";
                if (veces_vista == 1) {
                    sufijo = " persona";
                }
                String text = veces_vista + sufijo;
                vecesVista.setText(text);
            }

                if (categorias.length() > 0) {
                    CardView cardViewcat = findViewById(R.id.cuadroCategorias);
                    cardViewcat.setVisibility(View.VISIBLE);
                    TextView tv_categorias = findViewById(R.id.tv_categorias);
                    String cats = "";
                    for (int i = 0; i < categorias.length(); i++) {
                        cats = cats + categorias.getJSONObject(i).getString("categoria");
                        if (!(i == categorias.length() - 1)) {
                            cats = cats + " - ";
                        }
                    }
                    tv_categorias.setText(cats);
                }


                recomendados = info_json.getInt("recomendado");
                if (recomendados != 0) {
                    actualizarRecomendados(recomendados);
                }
                //======================================================================================
                    if (!ciudad.isEmpty()){
                        CardView layout_ciudad = findViewById(R.id.cuadroCiudad);
                        layout_ciudad.setVisibility(View.VISIBLE);
                        TextView ciudadLocal = findViewById(R.id.txtCiudad);
                        ciudadLocal.setText(ciudad);
                    }
             }

            } catch(JSONException e){
                e.printStackTrace();
            } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }


    private void initCollapsingToolbar(final String title) {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_pagina);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
       appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                   if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                 if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(title);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }



    String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO",
            "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
    private String convertirFecha(String fechaMysql){
        String[] array = fechaMysql.split("-");
        return array[2] + " - " +meses[Integer.valueOf(array[1]) - 1] + " - " + array[0];
    }

private void mostrarTodo(){
    Log.e("A. Pág", "inside mostrarTodo()");
    initCollapsingToolbar(nombre);
    TextView tvTitDescrip = (TextView)findViewById(R.id.tvTitleDescrip);
    tvTitDescrip.setText(nombre);

    if(!descripcion.isEmpty()) {

        TextView tvDescrip = (TextView) findViewById(R.id.tv_descripcion);
        tvDescrip.setVisibility(View.VISIBLE);
        tvDescrip.setText(descripcion);
    }

    String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("id_evento", id);
        jsonObject.put("android_id", android_id);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    String url = Constantes.URL_BASE + "eventos/obtenerEventoPorId.php";

    final Snackbar snackbar = Snackbar.make(contenedorPagina, "Un momento...", Snackbar.LENGTH_INDEFINITE);
    snackbar.show();

    JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(!cancelarTodo) {
                        snackbar.dismiss();
                        procesar_respuesta(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(!cancelarTodo) {
                        snackbar.dismiss();
                        Toast.makeText(ActivityPagina.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            );
    MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    imageLoader = MySingleton.getInstance(getApplicationContext()).getImageLoader();
    networkImageView = (NetworkImageView) findViewById(R.id.netImgView_port);
    networkImageView.setErrorImageResId(R.drawable.fondo_main);
    inflater = LayoutInflater.from(this);
}

private void enviarComentario(){

    editTextComent = findViewById(R.id.edtxComent);
    String coment = editTextComent.getText().toString().trim();
    if(coment.equals("")){
        return;
    }
    final Snackbar snackbar = Snackbar.make(contenedorPagina, "Un momento...", Snackbar.LENGTH_INDEFINITE);
    snackbar.show();

    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("id_user", SessionManager.getInstancia(this).getUserId());
        jsonObject.put("token", SessionManager.getInstancia(this).getToken());
        jsonObject.put("comentario", coment);
        jsonObject.put("id_evento", id);
        Log.i("A. página", jsonObject.toString());
        String url = Constantes.URL_BASE + "eventos/insertar_comentario.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!cancelarTodo) {
                    snackbar.dismiss();
                    procesarNuevoComent(response);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(!cancelarTodo) {
                    snackbar.dismiss();
                    Toast.makeText(ActivityPagina.this, getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                }
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    } catch (JSONException e) {
        e.printStackTrace();
    }
}

private void procesarNuevoComent(JSONObject response){

    try {
        int estado = response.getInt("estado");
        if (estado == 1){
            JSONObject comentNuevo = response.getJSONObject("comentario");
            String fecha_actual = response.getString("fecha_actual");
            agregarComentario(comentNuevo, true, fecha_actual);
            editTextComent.setText("");
        }else{
            Toast.makeText(this, "Ha ocurrido un error\nVuelve a intentarlo luego", Toast.LENGTH_SHORT).show();
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }
}

private void agregarComentario(JSONObject jsonObject, boolean isNuevo, String fecha_actual){
    String nombreUser = null;
    String fechaHora = "", comentario = "";
    try {
        nombreUser = jsonObject.getString("nombre");
        fechaHora = jsonObject.getString("fecha_hora");
        comentario = jsonObject.getString("comentario");
       // fechaActual  = response.getString("fecha_actual");
        Log.i("A. página", jsonObject.toString());
    } catch (JSONException e) {
        e.printStackTrace();
    }



    View view = inflater.inflate(R.layout.item_coment, contentComentarios, false);
    TextView tvNnombreUser = view.findViewById(R.id.comentUser);
    TextView tvfechaComent = view.findViewById(R.id.fechaComent);
    TextView tvComent = view.findViewById(R.id.textComentario);
    //    SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance();

    //TimeZone utcZone = TimeZone.getTimeZone("UTC");
  //  Log.i("A. página", utcZone.toString());
   // simpleDateFormat.setTimeZone(utcZone);//
    try {
        Date dateComent = simpleDateFormat.parse(fechaHora);


      // simpleDateFormat.setTimeZone(TimeZone.getDefault());
       // String formattedDate = simpleDateFormat.format(myDate);
       //tvfechaComent.setText(formattedDate.substring(0, 16));
        if (isNuevo) {
            Date date_actual = simpleDateFormat.parse(fecha_actual);
            tvfechaComent.setText(getDiferencia(dateComent, date_actual));
        }else {
            tvfechaComent.setText(getDiferencia(dateComent, dateActual));
        }
    } catch (ParseException e) {
        e.printStackTrace();
    }
    //tvfechaComent.setText(fechaHora);

    tvNnombreUser.setText(nombreUser + ":");
//    tvNnombreUser.setText(nombreUser + " " + id_user + ":");

    tvComent.setText(comentario);
    if (isNuevo){
        contentComentarios.addView(view, 0);
    }else {
        contentComentarios.addView(view);
    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int FUE_EDITADO = 9;
        final int DESDE_PAGINA = 88;
        if (requestCode == DESDE_PAGINA){
            if (resultCode == FUE_EDITADO) {
                setResult(FUE_EDITADO);
                finish();
            }else{
                 finish();
             }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

    if (isNuevo){
        return  false;
    }

    if (menu != null) menu.clear();
     getMenuInflater().inflate(R.menu.menu_pagina, menu);

            return true;
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        int DENUNCIAR_PAGINA = 2;
        if (itemId == DENUNCIAR_PAGINA){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final int id_local = id;
            builder.setTitle("Estimado usuario")
                    .setMessage("Si hay algo inadecuado en esta página haga click en Denunciar, para que los moderadores de la aplicación puedan identificarla. Nadie más lo notará.");
// Add the buttons
            builder.setPositiveButton("Denunciar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    enviar_denuncia();
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        int RECOMENDAR = 5;
        if (itemId == RECOMENDAR){//

            recomendar();
        }
        return super.onOptionsItemSelected(item);
    }


// Suma o resta las horas recibidos a la fecha

    public Date sumarRestarHorasFecha(Date fecha, int horas){

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha); // Configuramos la fecha que se recibe

        calendar.add(Calendar.HOUR, horas);  // numero de horas a añadir, o restar en caso de horas<0

        return calendar.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas

    }

    public String getDiferencia(Date fechaInicial, Date fechaFinal){

        long diferencia = fechaFinal.getTime() - fechaInicial.getTime();

        long segsMilli = 1000;
        long minsMilli = segsMilli * 60;
        long horasMilli = minsMilli * 60;
        long diasMilli = horasMilli * 24;
        String resultText;
        long diasTranscurridos = diferencia / diasMilli;

        if (diasTranscurridos > 0){
            if (diasTranscurridos == 1) resultText = "hace 1 día";
            else resultText = "hace " + diasTranscurridos + " días";
        }else{
            diferencia = diferencia % diasMilli;
            long horasTranscurridos = diferencia / horasMilli;
            if (horasTranscurridos > 0){
                if (horasTranscurridos == 1) resultText = "hace 1 hora";
                else resultText = "hace " + horasTranscurridos + " horas";
            }else {
                diferencia = diferencia % horasMilli;
                long minutosTranscurridos = diferencia / minsMilli;
                if (minutosTranscurridos > 0){
                    if (minutosTranscurridos == 1) resultText = "hace 1 minuto";
                    else resultText = "hace " + minutosTranscurridos + " minutos";
                }else {
                    diferencia = diferencia % minsMilli;
                    long segsTranscurridos = diferencia / segsMilli;
                    if (segsTranscurridos == 1) resultText = "hace 1 segundo";
                    else resultText = "hace " + segsTranscurridos + " segundos";
                }
            }
        }

        return resultText;

    }

    private void enviar_denuncia(){

        final String DENUNCIA_URL = Constantes.URL_BASE + "eventos/denunciar.php";
        JSONObject json = new JSONObject();
        String token = SessionManager.getInstancia(this).getToken();
        int id_user = SessionManager.getInstancia(this).getUserId();
        final ProgressBar progressBar;
        try {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            json.put("android_id", androidId);
            json.put("id_user", id_user);
            json.put("token", token);
            json.put("id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressBar = new ProgressBar(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);
        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        DENUNCIA_URL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!cancelarTodo) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    procesar_respuesta_denuncia(response);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(!cancelarTodo) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
        );
    }

    private void procesar_respuesta_denuncia(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                Toast.makeText(this, "La denuncia ha sido recibida", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Ha ocurrido un problema.\nVuelva a intentarlo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void suspendEvent(){// TODO: 07/08/2023 use this code for suspended events
// TODO: 25/08/2023 implement moverEventos. The php code is already done 
        final String URL = Constantes.URL_BASE + "eventos/suspenderEvento.php";
        JSONObject json = new JSONObject();
        String token = SessionManager.getInstancia(this).getToken();
        int id_user = SessionManager.getInstancia(this).getUserId();
        ProgressBar progressBar = new ProgressBar(getApplicationContext());
        try {
//            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//            json.put("android_id", androidId);
            json.put("id_user", id_user);
            json.put("token", token);
            json.put("id_evento", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //progressBar = ProgressBar.inflate()//TRY WITH THIS IF THE OTHER DOES NOT WORK
        progressBar.setVisibility(View.VISIBLE);
        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        URL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!cancelarTodo) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.success_message), Toast.LENGTH_SHORT).show();
                                //    showSuspendedLabel();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(!cancelarTodo) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
        );
    }



    private void recomendar(){

        final String recomend = Constantes.URL_BASE + "eventos/recomendar.php";
        JSONObject json = new JSONObject();
        ProgressBar progressBar;
        try {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            json.put("android_id", androidId);
            json.put("id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar = new ProgressBar(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);
        MySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        recomend, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!cancelarTodo) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    procesar_recomendar(response);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(!cancelarTodo) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_ocurrido), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
        );
    }

    private void procesar_recomendar(JSONObject response){

        try {
            int estado = response.getInt("estado");
            if(estado == 1){
                //Toast.makeText(this, "Operación exitosa.\nEl número de las recomendaciones a aumentado a " + (recomendados + 1), Toast.LENGTH_LONG).show();
                actualizarRecomendados(recomendados + 1);
                //recomendedByMe = true;//
            }else{
                if (estado == 2){
                   // Toast.makeText(this, "Ya ha recomendado este evento", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actualizarRecomendados(int recomendados){

        LinearLayout cuadroRecomendados = findViewById(R.id.cuadroRecomendados);
        cuadroRecomendados.setVisibility(View.VISIBLE);
        TextView recomendaciones = findViewById(R.id.recomendaciones);
        String sufijo = " personas";
        if (recomendados == 1) {
            sufijo = " persona";
        }
        recomendaciones.setText(recomendados + sufijo);
    }




}

// TODO: 11/11/2023 consider to add babges
// TODO: 07/10/2023 counting views doesn't work