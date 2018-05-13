package samsung.com.myplayer2.Class;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import java.io.File;

import samsung.com.myplayer2.R;

public class ToolFunction {

    public ToolFunction() {
    }

    public static int getBlackWhiteColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness >= 0.5) {
            return Color.WHITE;
        } else return Color.BLACK;
    }


    public static final String makeLabel(final Context context, final int pluralInt, final int number) {
        return context.getResources().getQuantityString(pluralInt, number, number);
    }

    public static final String makeCombinedString(final Context context, final String first, final String second) {
        final String formatter = context.getResources().getString(R.string.combine_two_strings);
        return String.format(formatter, first, second);
    }

    public static void shareTrack(final Context context, String myFilePath) {

        File file = new File(myFilePath);
        if (file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + myFilePath));
            context.startActivity(Intent.createChooser(share, "Share"));
        }
    }

    public static Uri getSongUri(Context context, long id) {
        final String[] projection = new String[]{
                BaseColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM_ID
        };
        final StringBuilder selection = new StringBuilder();
        selection.append(BaseColumns._ID + " IN (");
        selection.append(id);
        selection.append(")");
        final Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(),
                null, null);

        if (c == null) {
            return null;
        }
        c.moveToFirst();


        try {

            Uri uri = Uri.parse(c.getString(1));
            c.close();

            return uri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
