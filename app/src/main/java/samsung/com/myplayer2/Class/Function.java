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
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.Model.Artist;
import samsung.com.myplayer2.Model.Song;
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

    private void SortByAlbumName(ArrayList<Album> ArrayAlbum) {
        Collections.sort(ArrayAlbum, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                return a.getAlbumName().toLowerCase().compareTo(b.getAlbumName().toLowerCase());
            }
        });
    }

    private void SortByArtistName(ArrayList<Artist> ArrayArtist) {
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

    public byte[] BitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public byte[] GetBitMapByte(String filePath) {
        MediaMetadataRetriever mData = new MediaMetadataRetriever();
        mData.setDataSource(filePath);

        //byte art[] = mData.getEmbeddedPicture();
        return mData.getEmbeddedPicture();
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

    public void getSongList(Context mContext, ArrayList<Song> ArraySong) {
        //retrieve song info
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        Cursor musicCursor;

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] ColumnIndex = {"_id", "title", "artist", "_data", "album_id"};

        String selectOption = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND title != ''";

        musicCursor = musicResolver.query(musicUri, ColumnIndex, selectOption, null, null);

        //get collumn
//        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
//        int artisColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//        int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//        int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

        if (musicCursor.moveToFirst()) {
            //add song to list
            do {
                long thisId = musicCursor.getLong(0);
                String thisTitle = musicCursor.getString(1);
                String thisArtis = musicCursor.getString(2);
                String thisData = musicCursor.getString(3);
                //Bitmap songimg = GetBitmap(thisData);
                //Bitmap lastimg = getResizedBitmap(songimg, 55, 60);
                long albumId = musicCursor.getLong(4);

                Uri trackUri = ContentUris.withAppendedId(musicUri, thisId);

                mr.setDataSource(mContext, trackUri);

                String genres = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

                ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisData, albumId, genres));

            }
            while (musicCursor.moveToNext());
        }
        SortBySongName(ArraySong);
        musicCursor.close();
    }

    public void getAlbumsLists(Context mContext, ArrayList<Album> albumList) {
        String where = null;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        final String[] columns = {_id, album_name, artist, albumart, tracks};
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            do {

                long id = cursor.getLong(cursor.getColumnIndex(_id));
                String name = cursor.getString(cursor.getColumnIndex(album_name));
                String artist2 = cursor.getString(cursor.getColumnIndex(artist));
                String artPath = cursor.getString(cursor.getColumnIndex(albumart));
                Bitmap art = BitmapFactory.decodeFile(artPath);
                int nr = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));

                albumList.add(new Album(id, name, artist2, nr, art));

            } while (cursor.moveToNext());

            cursor.close();
        }
        SortByAlbumName(albumList);
    }

    public void getArtist(Context mContext, ArrayList<Artist> artists) {
        String where = null;

        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String artist = MediaStore.Audio.Artists.ARTIST;

        final String[] columns = {artist};
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            do {

                String artist2 = cursor.getString(cursor.getColumnIndex(artist));
                artists.add(new Artist(artist2));

            } while (cursor.moveToNext());

            cursor.close();
        }
        SortByArtistName(artists);
    }

    public void getGenres(Context mContext, ArrayList<String> genres) {
        Cursor genresCursor;
        Uri genresUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        genresCursor = mContext.getContentResolver().query(genresUri, null, null, null, null);
        int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

        if (genresCursor.moveToFirst()) {
            do {
                String genresName = genresCursor.getString(genre_column_index);
                genres.add(genresName);
            } while (genresCursor.moveToNext());
        }

        genresCursor.close();
    }

    public String getURLForResource(int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }




    public ArrayList<Song> getAllSongs(Context context) {
        return getSongsForCursor(makeSongCursor(context, null, null, null));
    }

    public ArrayList<Song> getSongsForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String data = cursor.getString(3);
                long albumId = cursor.getInt(4);

                arrayList.add(new Song(id, title, artist, data, albumId, null));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();

        SortBySongName(arrayList);

        return arrayList;
    }

    private Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        String selectionStatement = "is_music = 1 AND title != ''";

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "artist", "_data", "album_id"}, selectionStatement, paramArrayOfString, sortOrder);

    }
}
