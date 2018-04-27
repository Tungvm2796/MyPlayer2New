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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.SongInAlbumAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumSongFragment extends Fragment {


    public AlbumSongFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    Toolbar toolbar;

    long albumID;
    String albumName;

    ImageView AlbumImg;
    SongInAlbumAdapter adapter;
    RecyclerView AlbumSongView;
    ArrayList<Song> SongListOfAlbum;
    Function function = new Function();

    public static AlbumSongFragment getFragment(long albumId, String albumName) {
        AlbumSongFragment fragment = new AlbumSongFragment();

        Bundle args = new Bundle();
        args.putLong(Constants.ALBUM_ID, albumId);
        args.putString(Constants.ALBUM, albumName);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getLong(Constants.ALBUM_ID);
            albumName = getArguments().getString(Constants.ALBUM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album_song, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        setupToolbar();

        AlbumImg = v.findViewById(R.id.album_art);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        ImageLoader.getInstance().displayImage(function.getAlbumArtUri(albumID).toString(), AlbumImg,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.album_none)
                        .resetViewBeforeLoading(true).build());

        SongListOfAlbum = new ArrayList<>();
        AlbumSongView = v.findViewById(R.id.song_of_album);

        adapter = new SongInAlbumAdapter(getContext(), SongListOfAlbum);
        new GetSongOfAlbum().execute();

        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getContext());
        AlbumSongView.setLayoutManager(mManager);

        return v;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(albumName);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();

            //pass list
            myService.setSongListOfAlbum(SongListOfAlbum);

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

    class GetSongOfAlbum extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            function.getSongListOfAlbum(getContext(), albumID, SongListOfAlbum);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AlbumSongView.setAdapter(adapter);
        }
    }

}
