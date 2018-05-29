package samsung.com.myplayer2.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.PlaylistFunction;

public class EditPlaylistNameDialog extends android.support.v4.app.DialogFragment {

    public static EditPlaylistNameDialog newInstance(long playlistId) {
        EditPlaylistNameDialog dialog = new EditPlaylistNameDialog();
        Bundle bundle = new Bundle();
        bundle.putLong("editPlaylistId", playlistId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity()).positiveText("Edit").negativeText("Cancel").input("Enter playlist name", "", false, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                long plID = getArguments().getLong("editPlaylistId");
                if (input.toString().length() == 0 || input.toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Playlist name can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    PlaylistFunction.editPlaylistName(getActivity(), plID, input.toString());
                    Intent reload2 = new Intent(Constants.TO_PLAYLIST);
                    reload2.setAction(Constants.RELOAD_PLAYLIST);
                    getActivity().sendBroadcast(reload2);
                }

            }
        }).build();
    }
}
