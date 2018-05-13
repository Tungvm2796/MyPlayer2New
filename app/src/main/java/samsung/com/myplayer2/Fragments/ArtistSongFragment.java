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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.SongOfArtistAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Dialogs.AddToPlaylistDialog;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;
import samsung.com.myplayer2.lastfmapi.LastFmClient;
import samsung.com.myplayer2.lastfmapi.callbacks.ArtistInfoListener;
import samsung.com.myplayer2.lastfmapi.models.ArtistQuery;
import samsung.com.myplayer2.lastfmapi.models.LastfmArtist;

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

    Toolbar toolbar;

    String artistName;

    ImageView ArtistImg;
    SongOfArtistAdapter adapter;
    RecyclerView ArtistSongView;
    ArrayList<Song> SongListOfArtist;
    Function function = new Function();

    public static ArtistSongFragment getFragment(String artist) {
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

        toolbar = v.findViewById(R.id.toolbar);
        setupToolbar();

        ArtistImg = v.findViewById(R.id.artist_art);

        LastFmClient.getInstance(getContext()).getArtistInfo(new ArtistQuery(artistName), new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(LastfmArtist artist) {
                if (artist != null && artist.mArtwork != null) {

                    //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
                    ImageLoader.getInstance().displayImage(artist.mArtwork.get(2).mUrl, ArtistImg,
                            new DisplayImageOptions.Builder().cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .showImageOnFail(R.drawable.album_none)
                                    .resetViewBeforeLoading(true)
                                    .displayer(new FadeInBitmapDisplayer(400))
                                    .build());
                }
            }

            @Override
            public void artistInfoFailed() {

            }
        });

        SongListOfArtist = new ArrayList<>();
        ArtistSongView = v.findViewById(R.id.song_of_artist);

        adapter = new SongOfArtistAdapter((AppCompatActivity) getActivity(), SongListOfArtist, true);

        new GetSongOfArtist().execute();

        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getContext());
        ArtistSongView.setLayoutManager(mManager);

        return v;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(artistName);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_all_song_to_playlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_all_song_to_playlist:
                AddToPlaylistDialog.newInstance(adapter.getSongIds()).show(getActivity().getSupportFragmentManager(), "ADD_PLAYLIST");
                break;
        }
        return super.onOptionsItemSelected(item);
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
