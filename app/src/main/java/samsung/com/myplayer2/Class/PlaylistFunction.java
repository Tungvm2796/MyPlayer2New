package samsung.com.myplayer2.Class;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import java.util.ArrayList;

import samsung.com.myplayer2.Model.Playlist;
import samsung.com.myplayer2.Model.Song;

public class PlaylistFunction {

    public static final String MUSIC_ONLY_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";

    private static ContentValues[] mContentValuesCache = null;

    static ArrayList<Playlist> mPlaylistList;
    private static Cursor mCursor;

    private static long mPlaylistID;
    private static Context context;

    public PlaylistFunction() {
    }

    //Get Data of Playlist------------------------------------------------------------
    public static ArrayList<Playlist> getPlaylists(Context context) {

        mPlaylistList = new ArrayList<>();

        mCursor = makePlaylistCursor(context);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(0);

                final String name = mCursor.getString(1);

                final int songCount = getSongCountForPlaylist(context, id);

                final Playlist playlist = new Playlist(id, name, songCount);

                mPlaylistList.add(playlist);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mPlaylistList;
    }

    public static int addToPlaylist(final Context context, final long[] ids, final long playlistid) {
        final int size = ids.length;

        final ContentResolver resolver = context.getContentResolver();

        final String[] projection = new String[]{
                "max(" + "play_order" + ")",
        };

        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);

        Cursor cursor = null;
        int base = 0;

        try {
            cursor = resolver.query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                base = cursor.getInt(0) + 1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        int numinserted = 0;
        for (int offSet = 0; offSet < size; offSet += 1000) {
            makeInsertItems(ids, offSet, 1000, base);
            numinserted += resolver.bulkInsert(uri, mContentValuesCache);
        }

        return numinserted;
    }

    public static void makeInsertItems(final long[] ids, final int offset, int len, final int base) {
        if (offset + len > ids.length) {
            len = ids.length - offset;
        }

        if (mContentValuesCache == null || mContentValuesCache.length != len) {
            mContentValuesCache = new ContentValues[len];
        }
        for (int i = 0; i < len; i++) {
            if (mContentValuesCache[i] == null) {
                mContentValuesCache[i] = new ContentValues();
            }
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids[offset + i]);
        }
    }

    public static final long createPlaylist(final Context context, final String name) {
        if (name != null && name.length() > 0) {
            final ContentResolver resolver = context.getContentResolver();

            final String[] projection = new String[]{
                    MediaStore.Audio.PlaylistsColumns.NAME
            };

            final String selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'";

            Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    projection, selection, null, null);

            if (cursor.getCount() <= 0) {
                final ContentValues values = new ContentValues(1);
                values.put(MediaStore.Audio.PlaylistsColumns.NAME, name);
                final Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        values);
                return Long.parseLong(uri.getLastPathSegment());
            }

            if (cursor != null) {
                cursor.close();
                cursor = null;
            }

            return -1;
        }

        return -1;
    }

    public static final int getSongCountForPlaylist(final Context context, final long playlistId) {
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                new String[]{BaseColumns._ID}, MUSIC_ONLY_SELECTION, null, null);

        if (c != null) {
            int count = 0;
            if (c.moveToFirst()) {
                count = c.getCount();
            }
            c.close();
            c = null;
            return count;
        }

        return 0;
    }

    public static final Cursor makePlaylistCursor(final Context context) {
        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.PlaylistsColumns.NAME
                }, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
    }
//"name LIKE '%"+Constants.MYPLAYER_PL_CR+"'"
    public static void deletePlaylists(Context context, long playlistId) {
        Uri localUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("_id IN (");
        localStringBuilder.append((playlistId));
        localStringBuilder.append(")");
        context.getContentResolver().delete(localUri, localStringBuilder.toString(), null);
    }

    public static void removeFromPlaylist(Context context, long id, long playlistId) {
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        final ContentResolver resolver = context.getContentResolver();
        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = ? ", new String[]{
                Long.toString(id)
        });
    }




    //Get Song of Playlist----------------------------------------------------

    public static void getSongsInPlaylist(Context context, long playlistID, ArrayList<Song> mSongList) {

        mPlaylistID = playlistID;

        //final int playlistCount = countPlaylist(context, mPlaylistID);

        mCursor = makePlaylistSongCursor(context, mPlaylistID);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID));

                final String songName = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE));

                final String artist = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));

                final String album = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM));

                final String data = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA));

                final long albumId = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID));


//                final long duration = mCursor.getLong(mCursor
//                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));
//
//                final int durationInSecs = (int) duration / 1000;

                final Song song = new Song(id, songName, artist, album, data, albumId, null);

                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    public static final Cursor makePlaylistSongCursor(final Context context, final Long playlistID) {
        final StringBuilder mSelection = new StringBuilder();
        mSelection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        mSelection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        return context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID),
                new String[]{
                        MediaStore.Audio.Playlists.Members._ID,
                        MediaStore.Audio.Playlists.Members.AUDIO_ID,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        //MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                }, mSelection.toString(), null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
    }



    public static ArrayList<Song> getLastSongsInPlaylist(Context context, long playlistID) {
        ArrayList<Song> mSongList = new ArrayList<>();

        mPlaylistID = playlistID;

        //final int playlistCount = countPlaylist(context, mPlaylistID);

        mCursor = makeLastPlaylistSongCursor(context, mPlaylistID);



        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID));

                final String songName = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE));

                final String artist = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));

                final String album = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM));

                final String data = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA));

                final long albumId = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID));


//                final long duration = mCursor.getLong(mCursor
//                        .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));
//
//                final int durationInSecs = (int) duration / 1000;

                final Song song = new Song(id, songName, artist, album, data, albumId, null);

                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return mSongList;
    }

    public static final Cursor makeLastPlaylistSongCursor(final Context context, final Long playlistID) {
        final StringBuilder mSelection = new StringBuilder();
        mSelection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        mSelection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        return context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID),
                new String[]{
                        MediaStore.Audio.Playlists.Members._ID,
                        MediaStore.Audio.Playlists.Members.AUDIO_ID,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        //MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                }, mSelection.toString(), null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
    }
}
