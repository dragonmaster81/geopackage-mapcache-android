package mil.nga.mapcache;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import mil.nga.mapcache.filter.InputFilterDecimalMinMax;
import mil.nga.mapcache.filter.InputFilterMinMax;
import mil.nga.geopackage.tiles.features.FeatureTiles;

public class GeoPackageUtils {

    /**
     * Show a message with an OK button
     *
     * @param activity
     * @param title
     * @param message
     */
    public static void showMessage(Activity activity, String title,
                                   String message) {
        if (title != null || message != null) {
            new AlertDialog.Builder(activity)
                    .setTitle(title != null ? title : "")
                    .setMessage(message != null ? message : "")
                    .setNeutralButton(
                            activity.getString(R.string.button_ok_label),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }).show();
        }
    }

    /**
     * Prepare tile load inputs
     *
     * @param activity
     * @param minZoomInput
     * @param maxZoomInput
     * @param button
     * @param nameInput
     * @param urlInput
     * @param compressFormatInput
     * @param compressQualityInput
     * @param setZooms
     * @param maxFeaturesLabel
     * @param maxFeaturesInput
     * @param supportsMaxFeatures
     * @param featuresIndexed
     */
    public static void prepareTileLoadInputs(final Activity activity,
                                             final EditText minZoomInput, final EditText maxZoomInput,
                                             Button button, final EditText nameInput, final EditText urlInput,
                                             final Spinner compressFormatInput,
                                             final EditText compressQualityInput,
                                             final boolean setZooms,
                                             TextView maxFeaturesLabel,
                                             EditText maxFeaturesInput,
                                             boolean supportsMaxFeatures,
                                             boolean featuresIndexed) {

        int minZoom = activity.getResources().getInteger(
                R.integer.load_tiles_min_zoom_default);
        int maxZoom = activity.getResources().getInteger(
                R.integer.load_tiles_max_zoom_default);
        minZoomInput.setFilters(new InputFilter[]{new InputFilterMinMax(
                minZoom, maxZoom)});
        maxZoomInput.setFilters(new InputFilter[]{new InputFilterMinMax(
                minZoom, maxZoom)});

        if (setZooms) {
            minZoomInput.setText(String.valueOf(activity.getResources().getInteger(
                    R.integer.load_tiles_default_min_zoom_default)));
            maxZoomInput.setText(String.valueOf(activity.getResources().getInteger(
                    R.integer.load_tiles_default_max_zoom_default)));
        }

        compressQualityInput
                .setFilters(new InputFilter[]{new InputFilterMinMax(0, 100)});
        compressQualityInput.setText(String.valueOf(activity.getResources()
                .getInteger(R.integer.load_tiles_compress_quality_default)));

        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            activity, android.R.layout.select_dialog_item);
                    adapter.addAll(activity.getResources().getStringArray(
                            R.array.preloaded_tile_url_labels));
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(activity
                            .getString(R.string.load_tiles_preloaded_label));
                    builder.setAdapter(adapter,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    if (item >= 0) {
                                        String[] urls = activity
                                                .getResources()
                                                .getStringArray(
                                                        R.array.preloaded_tile_urls);
                                        String[] names = activity
                                                .getResources()
                                                .getStringArray(
                                                        R.array.preloaded_tile_url_names);
                                        int[] minZooms = activity
                                                .getResources()
                                                .getIntArray(
                                                        R.array.preloaded_tile_url_min_zoom);
                                        int[] maxZooms = activity
                                                .getResources()
                                                .getIntArray(
                                                        R.array.preloaded_tile_url_max_zoom);
                                        int[] defaultMinZooms = activity
                                                .getResources()
                                                .getIntArray(
                                                        R.array.preloaded_tile_url_default_min_zoom);
                                        int[] defaultMaxZooms = activity
                                                .getResources()
                                                .getIntArray(
                                                        R.array.preloaded_tile_url_default_max_zoom);
                                        if (nameInput != null) {
                                            nameInput.setText(names[item]);
                                        }
                                        urlInput.setText(urls[item]);

                                        int minZoom = minZooms[item];
                                        int maxZoom = maxZooms[item];
                                        minZoomInput
                                                .setFilters(new InputFilter[]{new InputFilterMinMax(
                                                        minZoom, maxZoom)});
                                        maxZoomInput
                                                .setFilters(new InputFilter[]{new InputFilterMinMax(
                                                        minZoom, maxZoom)});

                                        if (setZooms) {
                                            minZoomInput.setText(String
                                                    .valueOf(defaultMinZooms[item]));
                                            maxZoomInput.setText(String
                                                    .valueOf(defaultMaxZooms[item]));
                                        } else {
                                            int currentMin = Integer.valueOf(minZoomInput.getText().toString());
                                            int currentMax = Integer.valueOf(maxZoomInput.getText().toString());

                                            currentMin = Math.max(currentMin, minZoom);
                                            currentMin = Math.min(currentMin, maxZoom);
                                            currentMax = Math.max(currentMax, minZoom);
                                            currentMax = Math.min(currentMax, maxZoom);

                                            minZoomInput.setText(String
                                                    .valueOf(currentMin));
                                            maxZoomInput.setText(String
                                                    .valueOf(currentMax));
                                        }
                                    }
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }

        if(supportsMaxFeatures){
            if(featuresIndexed) {
                int maxFeatures = activity.getResources().getInteger(
                        R.integer.feature_tiles_load_max_features_per_tile_default);
                if (maxFeatures >= 0) {
                    maxFeaturesInput.setText(String.valueOf(maxFeatures));
                }
            }
        }else{
            maxFeaturesLabel.setVisibility(View.GONE);
            maxFeaturesInput.setVisibility(View.GONE);
        }
    }

    /**
     * Prepare the lat and lon input filters
     *
     * @param activity
     * @param minLatInput
     * @param maxLatInput
     * @param minLonInput
     * @param maxLonInput
     * @param preloadedButton
     */
    public static void prepareBoundingBoxInputs(final Activity activity,
                                                final EditText minLatInput, final EditText maxLatInput,
                                                final EditText minLonInput, final EditText maxLonInput,
                                                Button preloadedButton) {

        minLatInput
                .setFilters(new InputFilter[]{new InputFilterDecimalMinMax(
                        -90.0, 90.0)});
        maxLatInput
                .setFilters(new InputFilter[]{new InputFilterDecimalMinMax(
                        -90.0, 90.0)});

        minLonInput
                .setFilters(new InputFilter[]{new InputFilterDecimalMinMax(
                        -180.0, 180.0)});
        maxLonInput
                .setFilters(new InputFilter[]{new InputFilterDecimalMinMax(
                        -180.0, 180.0)});

        preloadedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        activity, android.R.layout.select_dialog_item);
                adapter.addAll(activity.getResources().getStringArray(
                        R.array.preloaded_bounding_box_labels));
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity
                        .getString(R.string.bounding_box_preloaded_label));
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item >= 0) {
                                    String[] locations = activity
                                            .getResources()
                                            .getStringArray(
                                                    R.array.preloaded_bounding_box_locations);
                                    String location = locations[item];
                                    String[] locationParts = location
                                            .split(",");
                                    minLonInput.setText(locationParts[0].trim());
                                    minLatInput.setText(locationParts[1].trim());
                                    maxLonInput.setText(locationParts[2].trim());
                                    maxLatInput.setText(locationParts[3].trim());
                                }
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    /**
     * Determine if the exception is caused from a missing function or module in
     * SQLite versions 4.2.0 and later. Lollipop uses version 3.8.4.3 so these
     * are not supported in Android.
     *
     * @param e
     * @return
     */
    public static boolean isFutureSQLiteException(Exception e) {
        boolean isFuture = false;
        String message = e.getMessage();
        if (message != null) {
            isFuture = message.contains("no such function: ST_IsEmpty")
                    || message.contains("no such module: rtree");
        }
        return isFuture;
    }

    /**
     * Prepare the provided feature tiles
     *
     * @param featureTiles
     */
    public static void prepareFeatureTiles(FeatureTiles featureTiles) {

        // TODO The projection for 27700 returns different values when going to and from web mercator
        // Buffer the pixels around the image when querying the feature index
        if (featureTiles.getFeatureDao().getProjection().getEpsg() == 27700) {
            featureTiles.setHeightDrawOverlap(featureTiles.getHeightDrawOverlap() + 100);
        }

    }

}
