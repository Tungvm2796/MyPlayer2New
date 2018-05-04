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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenresSongFragment extends Fragment {


    public GenresSongFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    Toolbar toolbar;

    ImageView genresImg;
    RecyclerSongAdapter genresSongAdt;
    RecyclerView genresSongView;

    ArrayList<Song> SongListOfGenres;
    Function function = new Function();

    String genres_name;

    public static GenresSongFragment getFragment(String genres) {
        GenresSongFragment fragment = new GenresSongFragment();

        Bundle args = new Bundle();
        args.putString(Constants.GENRES, genres);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genres_name = getArguments().getString(Constants.GENRES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_genres_song, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        setupToolbar();

        genresImg = v.findViewById(R.id.genres_art);

        int resID = getContext().getResources().getIdentifier(genres_name.toLowerCase()+"_music", "drawable", getContext().getPackageName());
        Glide.with(getContext()).load(resID)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).skipMemoryCache(true)
                .error(R.drawable.music_frame).into(genresImg);

        SongListOfGenres = new ArrayList<>();
        genresSongView = v.findViewById(R.id.song_of_genres);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        genresSongView.setLayoutManager(manager);

        genresSongAdt = new RecyclerSongAdapter(getContext(), SongListOfGenres, false, true, false, true);
        new GetSongOfGenres().execute();

        return v;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(genres_name);
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
            myService.setSongListOfGenres(SongListOfGenres);

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

    class GetSongOfGenres extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            function.getSongListOfGenres(getContext(), genres_name, SongListOfGenres);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            genresSongView.setHasFixedSize(true);
//
//            AnimationSet set = new AnimationSet(true);
//
//            Animation animation = new AlphaAnimation(0.0f, 1.0f);
//            animation.setDuration(500);
//            set.addAnimation(animation);
//
//            animation = new TranslateAnimation(
//                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                    Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
//            );
//            animation.setDuration(100);
//            set.addAnimation(animation);
//
//            LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
//
//            genresSongView.setLayoutAnimation(controller);

            genresSongView.setAdapter(genresSongAdt);
        }
    }
}
