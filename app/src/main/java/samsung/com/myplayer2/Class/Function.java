package samsung.com.myplayer2.Class;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.Model.Artist;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.Model.Suggestion;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/20/2018.
 */

public class Function {

    public Function() {
    }

    private static void SortBySongName(ArrayList<Song> ArraySong) {
        Collections.sort(ArraySong, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().toLowerCase().compareTo(b.getTitle().toLowerCase());
            }
        });
    }

    private static void SortByAlbumName(ArrayList<Album> ArrayAlbum) {
        Collections.sort(ArrayAlbum, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                return a.getAlbumName().toLowerCase().compareTo(b.getAlbumName().toLowerCase());
            }
        });
    }

    private static void SortByArtistName(ArrayList<Artist> ArrayArtist) {
        Collections.sort(ArrayArtist, new Comparator<Artist>() {
            public int compare(Artist a, Artist b) {
                return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
            }
        });
    }


    public Bitmap GetBitmap(String filePath) {
        Bitmap image;
        MediaMetadataRetriever mData = new MediaMetadataRetriever();
        mData.setDataSource(filePath);

        try {
            byte art[] = mData.getEmbeddedPicture();
            image = BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (Exception e) {
            image = null;
        }

        return image;
    }

    public byte[] GetBitMapByte(String filePath) {
        MediaMetadataRetriever mData = new MediaMetadataRetriever();
        mData.setDataSource(filePath);

        //byte art[] = mData.getEmbeddedPicture();
        return mData.getEmbeddedPicture();
    }

    public byte[] BitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null)
            return bm;
        else {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }
    }


    //Get Data------------------------------------------------------------
    public static void getSongList(Context mContext, ArrayList<Song> ArraySong, String keyword) {
        //retrieve song info
        //MediaMetadataRetriever mr = new MediaMetadataRetriever();
        String customQuery = " AND " + keyword;

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] ColumnIndex = {"_id", "title", "artist", "album", "_data", "album_id"};

        String selectOption1 = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != ''";

        String selectOption2 = keyword == null ? selectOption1 : selectOption1 + customQuery;

        Cursor musicCursor = musicResolver.query(musicUri, ColumnIndex, selectOption2, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

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

//                Uri trackUri = ContentUris.withAppendedId(musicUri, thisId);
//
//                mr.setDataSource(mContext, trackUri);
//
//                String genres = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

                ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisAlbum, thisData, albumId, null));

            }
            while (musicCursor.moveToNext());

            //Close Cursor when done
            musicCursor.close();
        }
        //SortBySongName(ArraySong);
    }

    public static void getAlbumsLists(Context mContext, ArrayList<Album> albumList, String keyword) {
        String where = keyword;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

//        final String _id = MediaStore.Audio.Albums._ID;
//        final String album_name = MediaStore.Audio.Albums.ALBUM;
//        final String artist = MediaStore.Audio.Albums.ARTIST;
//        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
//        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;


        final String[] columns = {"_id", "album", "artist", "album_art", "numsongs"};
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);

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
        //SortByAlbumName(albumList);
    }

    public static void getArtist(Context mContext, ArrayList<Artist> artists, String keyword) {
        String where = keyword;

        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        //final String artist = MediaStore.Audio.Artists.ARTIST;

        final String[] columns = {"artist", "number_of_albums", "number_of_tracks"};
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, null, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);

        if (cursor != null && cursor.moveToFirst()) {

            do {

                String artist = cursor.getString(0);
                int albumCount = cursor.getInt(1);
                int songCount = cursor.getInt(2);
                artists.add(new Artist(artist, albumCount, songCount));

            } while (cursor.moveToNext());

            cursor.close();
        }
        //SortByArtistName(artists);
    }

    public static void getGenres(Context mContext, ArrayList<String> genres) {

        Uri genresUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

        String[] Column = {"name"};

        Cursor genresCursor = mContext.getContentResolver().query(genresUri, Column, null, null, MediaStore.Audio.Genres.DEFAULT_SORT_ORDER);
//        int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

        if (genresCursor != null && genresCursor.moveToFirst()) {
            do {
                String genresName = genresCursor.getString(0);
                genres.add(genresName);
            } while (genresCursor.moveToNext());

            genresCursor.close();
        }
    }

    public static void getSongListOfAlbum(Context mContext, long AlbumID, ArrayList<Song> ArraySong) {
        //retrieve song info
        //MediaMetadataRetriever mr = new MediaMetadataRetriever();

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] ColumnIndex = {"_id", "title", "artist", "album", "_data", "album_id"};

        String selectOption = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != '' AND album_id = '" + Long.toString(AlbumID) + "'";

        Cursor musicCursor = musicResolver.query(musicUri, ColumnIndex, selectOption, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

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
                //Bitmap songimg = GetBitmap(thisData);
                //Bitmap lastimg = getResizedBitmap(songimg, 55, 60);
                long albumId = musicCursor.getLong(5);

//                Uri trackUri = ContentUris.withAppendedId(musicUri, thisId);
//
//                mr.setDataSource(mContext, trackUri);
//
//                String genres = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

                ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisAlbum, thisData, albumId, null));

            }
            while (musicCursor.moveToNext());

            //Close Cursor when done
            musicCursor.close();
        }
        //SortBySongName(ArraySong);
    }

    public static void getSongListOfArtist(Context mContext, String artist_name, ArrayList<Song> ArraySong) {
        //retrieve song info
        //MediaMetadataRetriever mr = new MediaMetadataRetriever();

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] ColumnIndex = {"_id", "title", "artist", "album", "_data", "album_id"};

        String selectOption = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != '' AND artist = '" + artist_name + "'";

        Cursor musicCursor = musicResolver.query(musicUri, ColumnIndex, selectOption, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

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
                //Bitmap songimg = GetBitmap(thisData);
                //Bitmap lastimg = getResizedBitmap(songimg, 55, 60);
                long albumId = musicCursor.getLong(5);

//                Uri trackUri = ContentUris.withAppendedId(musicUri, thisId);
//
//                mr.setDataSource(mContext, trackUri);
//
//                String genres = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

                ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisAlbum, thisData, albumId, null));

            }
            while (musicCursor.moveToNext());

            //Close Cursor when done
            musicCursor.close();
        }
        //SortBySongName(ArraySong);
    }

    public static void getSongListOfGenres(Context mContext, String genres_name, ArrayList<Song> ArraySong) {
        //retrieve song info
        //MediaMetadataRetriever mr = new MediaMetadataRetriever();

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] ColumnIndex = {"_id", "title", "artist", "album", "_data", "album_id"};

        String selectOption = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != ''";

        Cursor musicCursor = musicResolver.query(musicUri, ColumnIndex, selectOption, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        //get collumn
//        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
//        int artisColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//        int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//        int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

        String[] Column = {"name"};

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //add song to list
            do {
                long thisId = musicCursor.getLong(0);
                String thisTitle = musicCursor.getString(1);
                String thisArtis = musicCursor.getString(2);
                String thisAlbum = musicCursor.getString(3);
                String thisData = musicCursor.getString(4);
                long albumId = musicCursor.getLong(5);

                Uri trackUri = MediaStore.Audio.Genres.getContentUriForAudioId("external", (int) thisId);
                Cursor genresCursor = mContext.getContentResolver().query(trackUri, Column, null, null, MediaStore.Audio.Genres.DEFAULT_SORT_ORDER);

                String genres;
                if (genresCursor != null && genresCursor.moveToFirst()) {
                    genres = genresCursor.getString(0);
                    genresCursor.close();
                } else {
                    genres = null;
                }

                if (genres != null && genres.equals(genres_name)) {
                    ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisAlbum, thisData, albumId, genres));
                }
            }
            while (musicCursor.moveToNext());

            //Close Cursor when done
            musicCursor.close();
        }
        //SortBySongName(ArraySong);
    }

    public static ArrayList<Song> getLastSongList(Context mContext, String keyword) {
        ArrayList<Song> ArraySong = new ArrayList<>();

        //retrieve song info
        //MediaMetadataRetriever mr = new MediaMetadataRetriever();
        String customQuery = " AND " + keyword;

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] ColumnIndex = {"_id", "title", "artist", "album", "_data", "album_id"};

        String selectOption1 = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != ''";

        String selectOption2 = keyword == null ? selectOption1 : selectOption1 + customQuery;

        Cursor musicCursor = musicResolver.query(musicUri, ColumnIndex, selectOption2, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

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

//                Uri trackUri = ContentUris.withAppendedId(musicUri, thisId);
//
//                mr.setDataSource(mContext, trackUri);
//
//                String genres = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

                ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisAlbum, thisData, albumId, null));

            }
            while (musicCursor.moveToNext());

            //Close Cursor when done
            musicCursor.close();
        }

        return ArraySong;
    }

    public static ArrayList<Song> getLastSongListOfGenres(Context mContext, String genres_name) {

        ArrayList<Song> ArraySong = new ArrayList<>();
        //retrieve song info
        MediaMetadataRetriever mr = new MediaMetadataRetriever();

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] ColumnIndex = {"_id", "title", "artist", "album", "_data", "album_id"};

        String selectOption = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != ''";

        Cursor musicCursor = musicResolver.query(musicUri, ColumnIndex, selectOption, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

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

                Uri trackUri = ContentUris.withAppendedId(musicUri, thisId);
                mr.setDataSource(mContext, trackUri);
                String genres = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

                if (genres != null && genres.equals(genres_name)) {
                    String thisTitle = musicCursor.getString(1);
                    String thisArtis = musicCursor.getString(2);
                    String thisAlbum = musicCursor.getString(3);
                    String thisData = musicCursor.getString(4);
                    //Bitmap songimg = GetBitmap(thisData);
                    //Bitmap lastimg = getResizedBitmap(songimg, 55, 60);
                    long albumId = musicCursor.getLong(5);


                    ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisAlbum, thisData, albumId, genres));
                }
            }
            while (musicCursor.moveToNext());

            //Close Cursor when done
            musicCursor.close();
        }
        return ArraySong;
    }



    public String getURLForResource(int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }


    public ArrayList<Suggestion> LoadDataForSuggest(Context context) {
        ArrayList<Suggestion> result = new ArrayList<>();

        ContentResolver musicResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        String[] SongColumnIndex = {"title"};
        String[] AlbumColumnIndex = {"album"};
        String[] ArtistColumnIndex = {"artist"};

        String selectOption = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != ''";

        Cursor songCursor = musicResolver.query(songUri, SongColumnIndex, selectOption, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                String thisTitle = songCursor.getString(0);
                result.add(new Suggestion(thisTitle));
            }
            while (songCursor.moveToNext());
            //Close Cursor when done
            songCursor.close();
        }

        Cursor albumCursor = musicResolver.query(albumUri, AlbumColumnIndex, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) {
            do {
                String thisAlbumName = albumCursor.getString(0);
                result.add(new Suggestion(thisAlbumName));
            }
            while (albumCursor.moveToNext());
            //Close Cursor when done
            albumCursor.close();
        }

        Cursor artistCursor = musicResolver.query(artistUri, ArtistColumnIndex, null, null, null);
        if (artistCursor != null && artistCursor.moveToFirst()) {
            do {
                String thisArtist = artistCursor.getString(0);
                result.add(new Suggestion(thisArtist));
            }
            while (artistCursor.moveToNext());
            //Close Cursor when done
            artistCursor.close();
        }

        return result;
    }


    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }




    //Get All Song ----------------------------------------------------------

    public static void getSongs(Context context, String selection, ArrayList<Song> arrayList) {
        getSongsForCursor(makeSongCursor(context, selection, null), arrayList);
    }

    public static void getSongsForCursor(Cursor musicCursor, ArrayList<Song> arrayList) {
        if ((musicCursor != null) && (musicCursor.moveToFirst()))
            do {
                long thisId = musicCursor.getLong(0);
                String thisTitle = musicCursor.getString(1);
                String thisArtis = musicCursor.getString(2);
                String thisAlbum = musicCursor.getString(3);
                String thisData = musicCursor.getString(4);
                long albumId = musicCursor.getLong(5);

                arrayList.add(new Song(thisId, thisTitle, thisArtis, thisAlbum, thisData, albumId, null));
            }
            while (musicCursor.moveToNext());
        if (musicCursor != null)
            musicCursor.close();
    }

    public static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {

        return makeSongCursor(context, selection, paramArrayOfString, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    private static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {

        String defaultSelection = "is_music = 1 AND title != ''";

        String[] ColumnIndex = {"_id", "title", "artist", "album", "_data", "album_id"};

        String finalSelection = selection == null ? defaultSelection : defaultSelection + " AND " + selection;

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ColumnIndex, finalSelection, paramArrayOfString, sortOrder);

    }
}
