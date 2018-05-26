package samsung.com.myplayer2.Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import samsung.com.myplayer2.Adapter.RecyclerAlbumAdapter;
import samsung.com.myplayer2.Adapter.RecyclerArtistAdapter;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.KMPSearch;
import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.Model.Artist;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.Model.Suggestion;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements RecyclerAlbumAdapter.AlbumClickListener, RecyclerArtistAdapter.ArtistClickListener {


    public SearchFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;

    ArrayList<Suggestion> mSuggestion;

    public final Executor executor = Executors.newSingleThreadExecutor();

    private AsyncTask mSearchTask = null;

    private FloatingSearchView searchView;
    private String queryString;

    private ArrayList<Song> resultSong;
    private ArrayList<Album> resultAlbum;
    private ArrayList<Artist> resultArtist;

    private RecyclerSongAdapter songAdapter;
    private RecyclerAlbumAdapter albumAdapter;
    private RecyclerArtistAdapter artistAdapter;

    private RecyclerView songView;
    private RecyclerView albumView;
    private RecyclerView artistView;

    private TextView countAlbum;
    private TextView countArtist;
    private TextView countSong;

    private static String keyword = "";

    private Toolbar toolbar;


    Function function = new Function();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        initView(v);

        mSuggestion = new ArrayList<>();
        new SetSuggestion().execute();

        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
            }

            @Override
            public void onFocusCleared() {

            }
        });

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else {
                    searchView.showProgress();
                    searchView.swapSuggestions(getSuggestion(newQuery));
                    searchView.hideProgress();
                }
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Suggestion sug = (Suggestion) searchSuggestion;

                if (mSearchTask != null) {
                    mSearchTask.cancel(false);
                    mSearchTask = null;
                }

                queryString = sug.getBody();

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mSearchTask = new SearchTask().execute(queryString);
                }else {
                    mSearchTask = new SearchTask().executeOnExecutor(executor, queryString);
                }

                searchView.clearFocus();
            }

            @Override
            public void onSearchAction(String currentQuery) {

                if (mSearchTask != null) {
                    mSearchTask.cancel(false);
                    mSearchTask = null;
                }

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mSearchTask = new SearchTask().execute(currentQuery);
                }else {
                    mSearchTask = new SearchTask().executeOnExecutor(executor, currentQuery);
                }

            }
        });

        return v;
    }

    private void initView(View v) {
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        setupToolbar();

        songView = v.findViewById(R.id.song_result);
        albumView = v.findViewById(R.id.album_result);
        artistView = v.findViewById(R.id.artist_result);

//        countAlbum = v.findViewById(R.id.count_album);
//        countArtist = v.findViewById(R.id.count_artist);
//        countSong = v.findViewById(R.id.count_song);
//
//        countAlbum.setText(null);
//        countArtist.setText(null);
//        countSong.setText(null);

        resultSong = new ArrayList<>();
        resultAlbum = new ArrayList<>();
        resultArtist = new ArrayList<>();

        songAdapter = new RecyclerSongAdapter((AppCompatActivity) getActivity(), resultSong, true, false, false);
        albumAdapter = new RecyclerAlbumAdapter(getContext(), resultAlbum, false);
        artistAdapter = new RecyclerArtistAdapter(getContext(), resultArtist, false);

        albumAdapter.setAlbumClickListener(SearchFragment.this);
        artistAdapter.setArtistClickListener(SearchFragment.this);

        RecyclerView.LayoutManager songManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        songView.setLayoutManager(songManager);

        RecyclerView.LayoutManager albumManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        albumView.setLayoutManager(albumManager);

        RecyclerView.LayoutManager artistManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        artistView.setLayoutManager(artistManager);

        songView.setAdapter(songAdapter);
        songView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        albumView.setAdapter(albumAdapter);
        artistView.setAdapter(artistAdapter);

        searchView = v.findViewById(R.id.floating_search_view);
        searchView.setBackgroundColor(Color.LTGRAY);

    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();

            //pass list

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    private ArrayList<Suggestion> getSuggestion(String query) {
        ArrayList<Suggestion> suggestions = new ArrayList<>();

        for (int i = 0; i < mSuggestion.size(); i++) {
            KMPSearch kmpSearch = new KMPSearch();
            kmpSearch.Search(query.toLowerCase(), mSuggestion.get(i).getmName().toLowerCase());
            if (kmpSearch.GetSize() != 0 && kmpSearch.GetFirstIndex() == 0) {
                suggestions.add(mSuggestion.get(i));
            } else {
                kmpSearch.Search(" " + query.toLowerCase(), mSuggestion.get(i).getmName().toLowerCase());
                if (kmpSearch.GetSize() != 0 && kmpSearch.GetFirstIndex() != 0)
                    suggestions.add(mSuggestion.get(i));
            }
        }

        return suggestions;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Search Library");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        searchView.setSearchFocused(true);
        playintent = new Intent(getActivity(), MyService.class);
        getActivity().startService(playintent);
        getActivity().bindService(playintent, musicConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

    @Override
    public void onAlbumClick(RecyclerAlbumAdapter.MyRecyclerAlbumHolder view, int position) {
        NavigationHelper.navigateToSongAlbum(getActivity(), resultAlbum.get(position).getId()
                , resultAlbum.get(position).getAlbumName(), null);
    }

    @Override
    public void onArtistClick(RecyclerArtistAdapter.MyRecyclerArtistHolder view, int position) {
        NavigationHelper.navigateToSongArtist(getActivity(), resultArtist.get(position).getName(), null);
    }

    class SetSuggestion extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mSuggestion = function.LoadDataForSuggest(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onDestroyView() {

        if (mSearchTask != null && mSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
            mSearchTask.cancel(false);
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicBound) {
            getActivity().unbindService(musicConnection);
            musicBound = false;
        }
    }

    private class SearchTask extends AsyncTask<String, Void, ArrayList<Object>> {

        @Override
        protected ArrayList<Object> doInBackground(String... params) {
            if (resultSong != null)
                resultSong.clear();
            String key = "title LIKE '" + params[0] + "%'";
            function.getSongList(getContext(), resultSong, key);

            if (isCancelled()) {
                return null;
            }

            if (resultAlbum != null)
                resultAlbum.clear();
            String key2 = "album LIKE '" + params[0] + "%'";
            function.getAlbumsLists(getContext(), resultAlbum, key2);

            if (isCancelled()) {
                return null;
            }

            if (resultArtist != null)
                resultArtist.clear();
            String key3 = "artist LIKE '" + params[0] + "%'";
            function.getArtist(getContext(), resultArtist, key3);

            keyword = params[0];

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> objects) {
            super.onPostExecute(objects);
            mSearchTask = null;

//            if (resultSong.size() != 0)
//                countSong.setText("Found " + Integer.toString(resultSong.size()) + " Songs");
//            else
//                countSong.setText(null);

            songAdapter.updateListSong(resultSong);
            songAdapter.notifyDataSetChanged();


//            if (resultAlbum.size() != 0)
//                countAlbum.setText("Found " + Integer.toString(resultAlbum.size()) + " Albums");
//            else
//                countAlbum.setText(null);

            albumAdapter.updateListAlbum(resultAlbum);
            albumAdapter.notifyDataSetChanged();


//            if (resultArtist.size() != 0)
//                countArtist.setText("Found " + Integer.toString(resultArtist.size()) + " Artist");
//            else
//                countArtist.setText(null);

            artistAdapter.updateListArtist(resultArtist);
            artistAdapter.notifyDataSetChanged();

            myService.setSongListOfSearch(songAdapter.GetListSong());
            myService.setLastKeyword(keyword);
        }
    }
}
