package samsung.com.myplayer2.Class;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class PreferencesUtility {
    private static final String THEME_PREFERNCE = "theme_preference";
    private static final String START_PAGE_INDEX = "start_page_index";
    private static final String ARTIST_ALBUM_IMAGE = "artist_album_image";
    private static final String ARTIST_ALBUM_IMAGE_MOBILE = "artist_album_image_mobile";
    private static final String ALWAYS_LOAD_ALBUM_IMAGES_LASTFM = "always_load_album_images_lastfm";

    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;
    private static Context context;
    private ConnectivityManager connManager = null;

    public PreferencesUtility(final Context context) {
        this.context = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    public String getTheme() {
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }

    public int getStartPageIndex() {
        return mPreferences.getInt(START_PAGE_INDEX, 0);
    }

    public void setStartPageIndex(final int index) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(START_PAGE_INDEX, index);
        editor.apply();
    }

    private void setSortOrder(final String key, final String value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean loadArtistAndAlbumImages() {
        if (mPreferences.getBoolean(ARTIST_ALBUM_IMAGE, true)) {
            if (!mPreferences.getBoolean(ARTIST_ALBUM_IMAGE_MOBILE, true)) {
                if (connManager == null)
                    connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = connManager.getActiveNetworkInfo();
                return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
            }
            return true;
        }
        return false;
    }

    public boolean alwaysLoadAlbumImagesFromLastfm() {
        return mPreferences.getBoolean(ALWAYS_LOAD_ALBUM_IMAGES_LASTFM, false);
    }
}
