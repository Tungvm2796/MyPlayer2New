package samsung.com.myplayer2.Class;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import java.util.ArrayList;

import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.Model.Song;

public class RcFunction {

    public RcFunction() {
    }

    public static void getRecentSongs(Context mContext, ArrayList<Song> ArraySong) {
        //String keyword = makeQueryOption(getListId(mContext, Constants.RECENT_SONG_ID));
        String[] keyword = getListId(mContext, Constants.RECENT_SONG_ID);
        String customQuery = " AND (" + keyword + ")";

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] ColumnIndex = {"_id", "title", "artist", "album", "_data", "album_id", "duration"};

        String selectOption1 = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != ''";

        //String selectOption2 = (keyword == null || keyword.trim().equals("")) ? selectOption1 : selectOption1 + customQuery;

        for (int i = 0; i < keyword.length; i++) {

            String option = " AND _id = '" + keyword[i] + "'";

            Cursor musicCursor = musicResolver.query(musicUri, ColumnIndex, selectOption1 + option, null, null);

            //get collumn
//        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
//        int artisColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//        int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//        int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            if (musicCursor != null && musicCursor.moveToFirst()) {
                //add song to list
                do {
                    long thisId = musicCursor.getLong(0);
                    String thisTitle = musicCursor.getString(1);
                    String thisArtis = musicCursor.getString(2);
                    String thisAlbum = musicCursor.getString(3);
                    String thisData = musicCursor.getString(4);
                    long albumId = musicCursor.getLong(5);
                    int duration = musicCursor.getInt(6);
                    ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisAlbum, thisData, albumId, duration, null));

                }
                while (musicCursor.moveToNext());

                //Close Cursor when done
                musicCursor.close();
            }
        }
    }

    public static void getRecentAlbums(Context mContext, ArrayList<Album> albumList) {

        String[] keyword = getListId(mContext, Constants.RECENT_ALBUM_ID);

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

//        final String _id = MediaStore.Audio.Albums._ID;
//        final String album_name = MediaStore.Audio.Albums.ALBUM;
//        final String artist = MediaStore.Audio.Albums.ARTIST;
//        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
//        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;


        final String[] columns = {"_id", "album", "artist", "album_art", "numsongs"};

        for (int i = 0; i < keyword.length; i++) {

            String option = "_id = '" + keyword[i] + "'";

            Cursor cursor = mContext.getContentResolver().query(uri, columns, option, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                do {

                    long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    String artist = cursor.getString(2);
                    String artPath = cursor.getString(3);
                    //Bitmap art = BitmapFactory.decodeFile(artPath);
                    int nr = Integer.parseInt(cursor.getString(4));

                    albumList.add(new Album(id, name, artist, nr, null));

                } while (cursor.moveToNext());

                cursor.close();
            }
        }
    }

    public static String makeQueryOption(String[] ids) {

        int check = 0;

        String option = "";

        for (int i = 0; i < ids.length; i++) {
            if (!ids[i].equals("") && !ids[i].equals("-1")) {
                option += "_id = ";
                option += "'" + ids[i] + "'";
                check = i;
                break;
            }
        }

        for (int i = check; i < ids.length; i++)
            if (!ids[i].equals("") && !ids[i].equals("-1"))
                option += " OR _id = '" + ids[i] + "'";

        if (option.trim().equals("")) {
            return "_id = '-1'";
        } else {
            return option;
        }
    }

    public static String[] getListId(Context context, String type) {
        String[] get = new String[10];

        for (int i = 0; i < 10; i++) {
            try {
                get[i] = Long.toString(getLong(context, type + Integer.toString(i)));
            } catch (Exception e) {
                get[i] = "";
            }
        }

        return get;
    }

    private static Long getLong(Context mContext, String key) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getLong(key, -1);
    }


    public static void AddRecent(Context mContext, String type, long intput) {
        SharedPreferences shared3;
        SharedPreferences.Editor editor3;
        shared3 = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor3 = shared3.edit();

        int index = 9;

        while (index >= 0) {
            if (!shared3.contains(type + Integer.toString(index)) || getLong(mContext, type + Integer.toString(index)).equals(Long.valueOf(-1))) {
                editor3.putLong(type + Integer.toString(index), intput);
                editor3.apply();
                break;
            }

            if (index == 0) {
                if (getLong(mContext, type + Integer.toString(index)) != intput) {
                    for (int i = 1; i < 10; i++) {
                        editor3.putLong(type + Integer.toString(i), getLong(mContext, type + Integer.toString(i - 1)));
                    }
                    editor3.putLong(type + Integer.toString(index), intput);
                    editor3.apply();
                }
            }

            index--;
        }
    }

}
