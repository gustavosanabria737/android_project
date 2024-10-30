package com.eventoscercanos.osmdroid;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {

        static int currentSelected;
        static String mTitle;
        Context context;
        private List<SpinnerItem> spinerList;
        static List<DrawerItem> drawerItemList;
        private int layoutResID;
        private final int LOCALES = 0;
        private final int EVENTOS = 1;
        private final int PROMOCIONADOS = 2;
        private final int ESPECIAL = 3;
        private Collection<DrawerItem> collectionCat, collectionCatEvent, collectionCatPromo, collectionCatEspecial,collectionHoyTodosEvent, currentCollection;
        private boolean primer_llamado = true;


        CustomDrawerAdapter(Context context, int layoutResourceID, List<DrawerItem> listItems) {
            super(context, layoutResourceID, listItems);
            this.context = context;
            this.layoutResID = layoutResourceID;
            drawerItemList = listItems;
            cargarColecciones();
        }

            private void cargarColecciones(){
            try {

                Cursor cat_event = MainActivity.db.rawQuery("SELECT categoria, icono, id_remota FROM categorias_evento ORDER BY categoria", null);
                collectionCatEvent = getCollection(cat_event);
                currentCollection = new ArrayList<>();
                SessionManager sessionManager =  SessionManager.getInstancia(getContext());

                collectionHoyTodosEvent = new ArrayList<>();
                collectionHoyTodosEvent.add(new DrawerItem("Todos los eventos", "mk_fest_fireworks", -2));//categoria,id_remota
                collectionHoyTodosEvent.add(new DrawerItem("Eventos de hoy", "mk_fest_party", -3));//categoria,id_remota
                collectionHoyTodosEvent.add(new DrawerItem("Esta semana", "mk_fest_fireworks", -4));//categoria,id_remota
                collectionHoyTodosEvent.add(new DrawerItem("Este mes", "mk_fest_party", -5));//categoria,id_remota

                drawerItemList.addAll(2, collectionHoyTodosEvent);
                drawerItemList.addAll(7, collectionCatEvent);//               collectionHoyTodosEvent.add(new DrawerItem("Categorías"));//categoria,id_remota
                notifyDataSetChanged();
            }catch (SQLiteException e){
             }
        }


        private Collection<DrawerItem> getCollection(Cursor c){

            Collection<DrawerItem> collection = null;
            if (c.moveToFirst()) {
                collection = new ArrayList<DrawerItem>();
                //Recorremos el cursor hasta que no haya más registros
                do {
                    collection.add(new DrawerItem(c.getString(0), c.getString(1), c.getInt(2)));//categoria,id_remota

                } while(c.moveToNext());
            }
            c.close();
            return  collection;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final DrawerItemHolder drawerHolder;
            View view = convertView;
           // Toast.makeText(context, "Llamando a getView",  Toast.LENGTH_SHORT).show();

            if (view == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                drawerHolder = new DrawerItemHolder();
               // Toast.makeText(context, "convertView resultó null",  Toast.LENGTH_SHORT).show();
                view = inflater.inflate(layoutResID, parent, false);
                drawerHolder.ItemName = (TextView) view.findViewById(R.id.drawer_itemName);
                drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);
                drawerHolder.title = (TextView) view.findViewById(R.id.drawerTitle);
                drawerHolder.textViewHeaderName = (TextView) view.findViewById(R.id.tv_header_name);
                drawerHolder.textViewHaderEmail = (TextView) view.findViewById(R.id.tv_header_email);
                drawerHolder.headerLayout = (LinearLayout) view.findViewById(R.id.headerLayout);
                drawerHolder.itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);

                drawerHolder.imageLayout = (RelativeLayout) view.findViewById(R.id.imageLayout);
                view.setTag(drawerHolder);
            } else {
                drawerHolder = (DrawerItemHolder) view.getTag();
            }

            DrawerItem dItem = (DrawerItem) drawerItemList.get(position);



            if (dItem.getTitle() != null) {//es titulo cabecera
                drawerHolder.headerLayout.setVisibility(LinearLayout.VISIBLE);
                drawerHolder.itemLayout.setVisibility(LinearLayout.GONE);
                //drawerHolder.spinnerLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.imageLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.title.setText(dItem.getTitle());


            } else if(dItem.isCabecera){

                drawerHolder.imageLayout.setVisibility(LinearLayout.VISIBLE);
                drawerHolder.headerLayout.setVisibility(LinearLayout.GONE);
               // drawerHolder.spinnerLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.itemLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.textViewHeaderName.setText(dItem.getUserName());
                drawerHolder.textViewHaderEmail.setText(dItem.getUserEmail());

            }
              else {//es item categoria
                drawerHolder.imageLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.headerLayout.setVisibility(LinearLayout.GONE);
               // drawerHolder.spinnerLayout.setVisibility(LinearLayout.GONE);
                drawerHolder.itemLayout.setVisibility(LinearLayout.VISIBLE);

//                drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(dItem.getImgResID()));
//                drawerHolder.icon.setImageResource(dItem.getImgResID());
                drawerHolder.ItemName.setText(dItem.getItemName());
                try {
                    drawerHolder.icon.setImageBitmap(BitmapFactory.decodeStream(context.getAssets().open(dItem.getIcoName() + ".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return view;
        }

        private static class DrawerItemHolder {

            private TextView ItemName, title;
            TextView textViewHeaderName, textViewHaderEmail;
            ImageView icon;
            LinearLayout headerLayout;
            LinearLayout itemLayout;
           // LinearLayout spinnerLayout;
            RelativeLayout imageLayout;
           // Spinner spinner;
        }

    public String getTitle() {
        return mTitle;
    }
}
