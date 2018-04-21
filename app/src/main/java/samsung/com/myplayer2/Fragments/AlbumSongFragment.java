package samsung.com.myplayer2.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import samsung.com.myplayer2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumSongFragment extends Fragment {


    public AlbumSongFragment() {
        // Required empty public constructor
    }

    public static AlbumSongFragment getFragmen(long albumId){
        AlbumSongFragment fragment = new AlbumSongFragment();



        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album_song, container, false);
    }

}
