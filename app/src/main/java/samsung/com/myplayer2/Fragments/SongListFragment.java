package samsung.com.myplayer2.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {

    public SongListFragment() {
        // Required empty public constructor
    }

    public ArrayList<Song> SongList;
    private RecyclerView songView;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    Function function;

    RecyclerSongAdapter songAdt;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_song_list, container, false);

        function = new Function();

        songView = v.findViewById(R.id.song_list);

        SongList = new ArrayList<>();

        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getActivity());
        songView.setLayoutManager(mManager);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
            new GetSong().execute();
        } else {
            new GetSong().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return v;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();

            //pass list
            myService.setAllSongs(SongList);

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

    private class GetSong extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            function.getSongs(getActivity(), null, SongList);
            songAdt = new RecyclerSongAdapter(getActivity(), SongList, false, false, false, false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            songView.setAdapter(songAdt);
        }

        @Override
        protected void onPreExecute() {
        }
    }

}