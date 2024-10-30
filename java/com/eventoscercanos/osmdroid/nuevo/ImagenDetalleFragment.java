package com.eventoscercanos.osmdroid.nuevo;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eventoscercanos.osmdroid.MySingleton;
import com.eventoscercanos.osmdroid.R;
import com.eventoscercanos.osmdroid.TouchImageView;
import com.eventoscercanos.osmdroid.syncSQLiteMySQL.utils.Constantes;

public class ImagenDetalleFragment extends Fragment{
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private String mImageUrl;
    private TouchImageView mNetImageView;
    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */

    public static ImagenDetalleFragment newInstance(String imageUrl) {
        final ImagenDetalleFragment f = new ImagenDetalleFragment();

        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        f.setArguments(args);

        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImagenDetalleFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : Constantes.URL_IMAGENES + "/eventos/portadaDefault/portadaDefault.png";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mNetImageView = v.findViewById(R.id.netImageView);
        mNetImageView.setErrorImageResId(R.drawable.fondo_main);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        if (!memoryInfo.lowMemory) {
            mNetImageView.setImageUrl(mImageUrl, MySingleton.getInstance(getContext()).getImageLoader());
        }else{
            Toast.makeText(getContext(), "No se puede cargar la imagen. Queda poca memoria", Toast.LENGTH_SHORT).show();
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (getActivity() instanceof View.OnClickListener) {
            mNetImageView.setOnClickListener((View.OnClickListener) getActivity());
        }
    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
}
