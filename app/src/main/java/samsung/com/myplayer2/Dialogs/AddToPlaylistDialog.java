package samsung.com.myplayer2.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.PlaylistFunction;
import samsung.com.myplayer2.Model.Playlist;
import samsung.com.myplayer2.Model.Song;

public class AddToPlaylistDialog extends android.support.v4.app.DialogFragment {

    public static AddToPlaylistDialog newInstance(Song song) {
        long[] songs = new long[1];
        songs[0] = song.getID();
        return newInstance(songs);
    }

    public static AddToPlaylistDialog newInstance(long[] songList) {
        AddToPlaylistDialog dialog = new AddToPlaylistDialog();
        Bundle bundle = new Bundle();
        bundle.putLongArray("songs", songList);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final List<Playlist> playlists = PlaylistFunction.getPlaylists(getActivity());
        CharSequence[] chars = new CharSequence[playlists.size() + 1];
        chars[0] = "Create new playlist";

        for (int i = 0; i < playlists.size(); i++) {
            chars[i + 1] = playlists.get(i).getName();
        }
        return new MaterialDialog.Builder(getActivity()).title("Add to playlist").items(chars).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                long[] songs = getArguments().getLongArray("songs");
                if (which == 0) {
                    CreatePlaylistDialog.newInstance(songs).show(getActivity().getSupportFragmentManager(), "CREATE_PLAYLIST");
                    return;
                }

                int inserted = PlaylistFunction.addToPlaylist(getActivity(), songs, playlists.get(which - 1).getListid());

                if(inserted > 0){
                    Toast.makeText(getActivity(), Integer.toString(inserted) + " songs added", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();

            }
        }).build();
    }

    private String getSubString(String entry){
        return entry.substring(0, entry.indexOf(Constants.MYPLAYER_PL_CR));
    }
}
