package com.eventoscercanos.osmdroid.nuevo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eventoscercanos.osmdroid.syncSQLiteMySQL.utils.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SubirIcono {//todo use executor instead
    private final String nom_img;
    private final String nom_carpeta;
    private ProgressBar progressBar;

    private JSONObject result;
    private final File file;
    private final Context context;

     SubirIcono( File file, Context ctx, String nom_carpeta){
        this.file = file;
        context = ctx;
        nom_img = String.valueOf(System.currentTimeMillis());
        this.nom_carpeta = nom_carpeta;

    }
    Handler handler = new Handler(Looper.getMainLooper());
    Future<?> future = Executors.newSingleThreadExecutor().submit((new Runnable() {
        @Override
        public void run() {
                progressBar = new ProgressBar(context);
                progressBar.setVisibility(View.VISIBLE);

                //Background work here
                String url = Constantes.URL_BASE + "eventos/subirIconoEvento.php";

                HttpFileUploader uploader = new HttpFileUploader(url, nom_img, nom_carpeta, context );
                try {
                    result =  uploader.doStart(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    //UI Thread work here
                    progressBar.setVisibility(View.INVISIBLE);
                    int estado = 0;
                    try {
                        estado = result.getInt("estado");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (estado == 1){
                        Activity_iconos.fijarIcono(nom_img);
                    } else {
                        Toast.makeText(context, "Operación fallida ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }));

}

