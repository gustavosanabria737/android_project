package com.eventoscercanos.osmdroid.nuevo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eventoscercanos.osmdroid.MySingleton;
import com.eventoscercanos.osmdroid.SessionManager;
import com.eventoscercanos.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SubirFoto extends AsyncTask<String, String, JSONObject> {
    private final String nom_img;
    private final String nom_carpeta;
    private final String descripcion;
    private ProgressDialog pDialog;
    private JSONObject result;
    private String tipo;
    private final File file;
    private final int thumbWidth;
    private final int thumbHeight;
    private Context context;
    private boolean isPortada;
    private final String origen;

     public SubirFoto(@NonNull File file, Context ctx, String nom_carpeta, int thumbWidth, int thumbHeight, String descripcion, String origen){
        this.file = file;
        this.origen = origen;
        context = ctx;
        nom_img =  file.getName();
/*        if (nom_img.contains(" ")){
            nom_img = nom_img.replace(" ", "_");
        }
        if (nom_img.contains("ñ")){
            nom_img = nom_img.replace("ñ", "n");
        }*/
        this.tipo = tipo;
        this.nom_carpeta = nom_carpeta;
        this.thumbWidth = thumbWidth;
        this.thumbHeight = thumbHeight;
        this.descripcion = descripcion;
    }


    @Override
    protected JSONObject doInBackground(String... args) {

        HttpFileUploader uploader;
        String url;
        if(nom_carpeta.contains("portada")){
            url = obtenerUrl(true);
        }else{
            url = obtenerUrl(false);
        }
        uploader = new HttpFileUploader(url, nom_img, nom_carpeta, thumbWidth, thumbHeight, descripcion, context);
        try {
          result =  uploader.doStart(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Un momento..." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
//        pDialog.dismiss();
        int estado;
        String nombreFoto;
        try {
            if (result != null){
                estado = result.getInt("estado");

                if (estado == 1){
                    int lasId;
                    nombreFoto = result.getString("filename");
                    if (!origen.equals("portada") && !descripcion.equals("")){
                        lasId = result.getInt("lastId");
                        actualizarDescripcion(lasId);
                    }else{
                        pDialog.dismiss();
                        switch (origen){
                            case "portada":
                                ActivityPortada.actualizarImagen(nom_img);
                                break;

                            case "fotos":
                                lasId = result.getInt("lastId");
                                ActivityFotos.agregarItem(lasId, nombreFoto, "");
                                break;

                            case "galeria":
                                lasId = result.getInt("lastId");
                                ActivityGaleriaEditable.agregarItem(lasId, nombreFoto, "");
                                break;
                        }
                    }

                } else {
                    pDialog.dismiss();
                        if (isPortada)    {
                            ActivityPortada.removerImagen();
                        }
                        Toast.makeText(context, "No se pudo subir la imagen", Toast.LENGTH_SHORT).show();
                    }
            }else {
                pDialog.dismiss();
                Toast.makeText(context, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            pDialog.dismiss();
            Toast.makeText(context, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    private String obtenerUrl(boolean isPortada){

        this.isPortada = isPortada;
        String url = "";
        String urlPortada = "";

        url = Constantes.URL_BASE +"eventos/subirFotoEvento.php";
        urlPortada = Constantes.URL_BASE + "eventos/subirFotoPortadaEvento.php";

        if(isPortada){
            return urlPortada;
        }else {
            return url;
        }
    }
private void actualizarImagen(){

    String nombreFoto;
    try {
        nombreFoto = result.getString("filename");
        int id_foto = result.getInt("lastId");
        switch (origen){

            case "fotos":
                ActivityFotos.agregarItem(id_foto, nombreFoto, descripcion);
                break;

            case "galeria":
                ActivityGaleriaEditable.agregarItem(id_foto, nombreFoto, descripcion);
                break;
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }


}

    private void actualizarDescripcion(int lastId){

        String ruta = Constantes.URL_BASE + tipo + "/actualizarDescripcion.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", SessionManager.getInstancia(context).getToken());
            jsonObject.put("id_user", SessionManager.getInstancia(context).getUserId());
            jsonObject.put("id_foto", lastId);
            jsonObject.put("descripcion", descripcion);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    ruta, jsonObject, jsonObject1 -> {

                        pDialog.dismiss();
                        try {
                            int estado = jsonObject1.getInt("estado");
                            if (estado == 1) {
                                SubirFoto.this.actualizarImagen();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, volleyError -> {

                    pDialog.dismiss();
                    Toast.makeText(context, "Hubo un problema con la descripción", Toast.LENGTH_SHORT).show();
            });
            MySingleton.getInstance(context).addToRequestQueue(request);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

