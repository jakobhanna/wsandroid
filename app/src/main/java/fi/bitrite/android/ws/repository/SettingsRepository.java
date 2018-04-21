package fi.bitrite.android.ws.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;
import javax.inject.Singleton;

import fi.bitrite.android.ws.R;

@Singleton
public class SettingsRepository {
    public enum DistanceUnit {
        KILOMETER,
        MILES,
    }

    private final SharedPreferences mSharedPreferences;

    private final static String KEY_MAP_LAST_LOCATION = "map_last_location";
    private final static String KEYSUFFIX_LOCATION_LATITUDE = "_latitude";
    private final static String KEYSUFFIX_LOCATION_LONGITUDE = "_longitude";
    private final static String KEYSUFFIX_LOCATION_ZOOM = "_zoom";

    private final String mKeyDistanceUnit;
    private final String mKeyGaCollectStats;
    private final String mKeyDevSimulateNoNetwork;

    private final String mDefaultDistanceUnit;
    private final boolean mDefaultGaCollectStats;
    private final boolean mDefaultDevSimulateNoNetwork;

    private final float mDefaultMapLocationLatitude;
    private final float mDefaultMapLocationLongitude;
    private final int mDefaultMapLocationZoom;

    private final String mDistanceUnitKilometerRaw;
    private final String mDistanceUnitMilesRaw;
    private final String mDistanceUnitKilometerShort;
    private final String mDistanceUnitMilesShort;
    private final String mDistanceUnitKilometerLong;
    private final String mDistanceUnitMilesLong;

    @Inject
    SettingsRepository(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        final Resources res = context.getResources();
        mKeyDistanceUnit = res.getString(R.string.prefs_distance_unit_key);
        mKeyGaCollectStats = res.getString(R.string.prefs_ga_collect_stats_key);
        mKeyDevSimulateNoNetwork = res.getString(R.string.prefs_dev_simulate_no_network_key);

        mDefaultDistanceUnit = res.getString(R.string.prefs_distance_unit_default);
        mDefaultGaCollectStats = res.getBoolean(R.bool.prefs_ga_collect_stats_default);
        mDefaultDevSimulateNoNetwork = res.getBoolean(R.bool.prefs_dev_simulate_no_network_default);

        mDefaultMapLocationLatitude =
                Float.parseFloat(res.getString(R.string.map_default_latitude));
        mDefaultMapLocationLongitude =
                Float.parseFloat(res.getString(R.string.map_default_longitude));
        mDefaultMapLocationZoom = res.getInteger(R.integer.map_initial_zoom);

        mDistanceUnitKilometerRaw = res.getString(R.string.distance_unit_kilometers_short_raw);
        mDistanceUnitMilesRaw = res.getString(R.string.distance_unit_miles_short_raw);
        mDistanceUnitKilometerShort = res.getString(R.string.distance_unit_kilometers_short);
        mDistanceUnitMilesShort = res.getString(R.string.distance_unit_miles_short);
        mDistanceUnitKilometerLong = res.getString(R.string.distance_unit_kilometers_long);
        mDistanceUnitMilesLong = res.getString(R.string.distance_unit_miles_long);
    }

    public DistanceUnit getDistanceUnit() {
        String distanceUnitStr =
                mSharedPreferences.getString(mKeyDistanceUnit, mDefaultDistanceUnit);

        if (TextUtils.equals(distanceUnitStr, mDistanceUnitKilometerRaw)) {
            return DistanceUnit.KILOMETER;
        } else if (TextUtils.equals(distanceUnitStr, mDistanceUnitMilesRaw)) {
            return DistanceUnit.MILES;
        } else {
            assert false;
            return null;
        }
    }
    public String getDistanceUnitLong() {
        DistanceUnit distanceUnit = getDistanceUnit();
        switch (distanceUnit) {
            case KILOMETER: return mDistanceUnitKilometerLong;
            case MILES: return mDistanceUnitMilesLong;
            default:
                assert false;
                return null;
        }
    }
    public String getDistanceUnitShort() {
        DistanceUnit distanceUnit = getDistanceUnit();
        switch (distanceUnit) {
            case KILOMETER: return mDistanceUnitKilometerShort;
            case MILES: return mDistanceUnitMilesShort;
            default:
                assert false;
                return null;
        }
    }

    private void setLocation(String key, CameraPosition position) {
        mSharedPreferences
                .edit()
                .putFloat(key + KEYSUFFIX_LOCATION_LATITUDE, (float) position.target.latitude)
                .putFloat(key + KEYSUFFIX_LOCATION_LONGITUDE, (float) position.target.longitude)
                .putFloat(key + KEYSUFFIX_LOCATION_ZOOM, position.zoom)
                .apply();

    }

    public CameraPosition getLastMapLocation(boolean defaultIfNone) {
        if (!mSharedPreferences.contains(KEY_MAP_LAST_LOCATION + KEYSUFFIX_LOCATION_LATITUDE)
            && !defaultIfNone) {
            return null;
        }
        float latitude = mSharedPreferences.getFloat(
                KEY_MAP_LAST_LOCATION + KEYSUFFIX_LOCATION_LATITUDE, mDefaultMapLocationLatitude);
        float longitude = mSharedPreferences.getFloat(
                KEY_MAP_LAST_LOCATION + KEYSUFFIX_LOCATION_LONGITUDE, mDefaultMapLocationLongitude);
        float zoom = mSharedPreferences.getFloat(
                KEY_MAP_LAST_LOCATION + KEYSUFFIX_LOCATION_ZOOM, mDefaultMapLocationZoom);

        return new CameraPosition(new LatLng(latitude, longitude), zoom, 0, 0);
    }
    public void setLastMapLocation(CameraPosition position) {
        setLocation(KEY_MAP_LAST_LOCATION, position);
    }

    public boolean canCollectStats() {
        return mSharedPreferences.getBoolean(mKeyGaCollectStats, mDefaultGaCollectStats);
    }

    public boolean isDevSimulateNoNetwork() {
        return mSharedPreferences.getBoolean(
                mKeyDevSimulateNoNetwork, mDefaultDevSimulateNoNetwork);
    }


    public void registerOnChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        // Does an initial call with key==null.
        listener.onSharedPreferenceChanged(null, null);
    }
    public void unregisterOnChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public String getDistanceUnitKey() {
        return mKeyDistanceUnit;
    }
    public String getCanCollectStatsKey() {
        return mKeyGaCollectStats;
    }
    public String getDevSimulateNoNetworkKey() {
        return mKeyDevSimulateNoNetwork;
    }
}
