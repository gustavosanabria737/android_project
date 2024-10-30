

package com.eventoscercanos.osmdroid.nuevo;

import android.app.ActionBar;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.eventoscercanos.osmdroid.R;

import java.io.IOException;

public class ImagenDetalleActivity extends FragmentActivity implements View.OnClickListener {

    public static final String EXTRA_CURRENT_IMAGE = "extra_image";

    private String carpeta = "";
    private String[] nomFotos;
    private ViewPager2 mPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);

        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen


        carpeta = getIntent().getStringExtra("carpeta");
        nomFotos = getIntent().getStringArrayExtra("nom_fotos");
        int num_fotos = getIntent().getIntExtra("num_fotos", 0);
        ImagePagerAdapter mAdapter;
        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(this, num_fotos);
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(2);

        // Set up activity to go full screen
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience
//        if (Utilidades.hasHoneycomb()) {
            final ActionBar actionBar = getActionBar();
//
//            // Hide title text and set home as up
//           actionBar.setDisplayShowTitleEnabled(true);
        assert actionBar != null;
        actionBar.setTitle(getIntent().getStringExtra("nombre"));
        try {
            Drawable icono = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(this.getAssets().open(getIntent().getStringExtra("marcador") + ".png")));
            actionBar.setIcon(icono);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

//          actionBar.setDisplayHomeAsUpEnabled(true);
//
//            // Hide and show the ActionBar as the visibility changes
            mPager.setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int vis) {
                            if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                                actionBar.hide();
                            } else {
                                actionBar.show();
                            }
                        }
                    });

            // Start low profile mode and hide ActionBar
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            actionBar.hide();

        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_CURRENT_IMAGE, -1);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class ImagePagerAdapter extends FragmentStateAdapter {
        private final int mSize;

         ImagePagerAdapter(FragmentActivity fragmentActivity, int size) {
             super(fragmentActivity);


             mSize = size;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ImagenDetalleFragment.newInstance(carpeta + nomFotos[position]);
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
    /**
     * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
     * when the ImageView is touched.
     */

    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
