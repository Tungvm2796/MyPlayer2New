package samsung.com.myplayer2.Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.SongOfArtistAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistSongFragment extends Fragment {


    public ArtistSongFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    String artistName;

    SongOfArtistAdapter adapter;
    RecyclerView ArtistSongView;
    ArrayList<Song> SongListOfArtist;
    Function function = new Function();

    public static ArtistSongFragment getFragment(String artist){
        ArtistSongFragment fragment = new ArtistSongFragment();

        Bundle args = new Bundle();
        args.putString(Constants.ARTIST, artist);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistName = getArguments().getString(Constants.ARTIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist_song, container, false);

        SongListOfArtist = new ArrayList<>();
        ArtistSongView = v.findViewById(R.id.song_of_artist);

        adapter = new SongOfArtistAdapter(getContext(), SongListOfArtist);

        new GetSongOfArtist().execute();

        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getContext());
        ArtistSongView.setLayoutManager(mManager);

        return v;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();

            //pass list
            myService.setSongListOfArtist(SongListOfArtist);

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        playintent = new Intent(getActivity(), MyService.class);
        getActivity().startService(playintent);
        getActivity().bindService(playintent, musicConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (musicBound) {
            getActivity().unbindService(musicConnection);
            musicBound = false;
        }
    }

    class GetSongOfArtist extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            function.getSongListOfArtist(getContext(), artistName, SongListOfArtist);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArtistSongView.setAdapter(adapter);
        }
    }
}
