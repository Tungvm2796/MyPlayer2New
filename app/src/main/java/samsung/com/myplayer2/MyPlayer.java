package samsung.com.myplayer2;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyPlayer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));



//        SharedPreferences shared2;
//        SharedPreferences.Editor editor2;
//
//        shared2 = PreferenceManager.getDefaultSharedPreferences(this);
//        editor2 = shared2.edit();
//
//        for (int i = 0; i < 10; i++) {
//            editor2.putLong(Constants.RECENT_ALBUM_ID + Integer.toString(i), -1);
//            editor2.putLong(Constants.RECENT_SONG_ID + Integer.toString(i), -1);
//        }
//
//        editor2.apply();
    }
}
