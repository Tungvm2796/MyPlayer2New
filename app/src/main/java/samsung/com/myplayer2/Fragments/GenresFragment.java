package samsung.com.myplayer2.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerGenresAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends Fragment implements RecyclerGenresAdapter.GenresClickListener {


    public GenresFragment() {
        // Required empty public constructor
    }

    RecyclerGenresAdapter recyclerGenresAdapter;
    RecyclerView genresView;
    ArrayList<String> genList;
    Function function;

    Toolbar toolbar;

    ArrayList<Song> AllSong;
    ArrayList<Song> songList;
    LinearLayout lin1;
    LinearLayout lin2;
    RecyclerView songOfGenres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_genres, container, false);
        function = new Function();

        lin1 = v.findViewById(R.id.lin1);
        lin2 = v.findViewById(R.id.lin2);


        songList = new ArrayList<>();

        AllSong = new ArrayList<>();
        //AllSong = ((MainActivity) getActivity()).getAllSong();

        genList = new ArrayList<>();
        //function.getGenres(getContext(), genList);

        genresView = v.findViewById(R.id.genresView);

        songOfGenres = v.findViewById(R.id.song_of_genres);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        songOfGenres.setLayoutManager(manager);
        songOfGenres.setAdapter(null);

//        View tabcontainer = new MainFragment().getView().findViewById(R.id.tabcontainer);
//        toolbar = new MainFragment().getView().findViewById(R.id.toolbar);
//        View lasttab = new MainFragment().getView().findViewById(R.id.viewpagertab);
//        View coloredBackgroundView = new MainFragment().getView().findViewById(R.id.colored_background_view);

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        genresView.setLayoutManager(mManager);

        new GetGenres().execute();

        //genresView.addOnScrollListener(new ToolbarHidingOnScrollListener(getActivity(), tabcontainer, toolbar, lasttab, coloredBackgroundView));

        return v;
    }

    @Override
    public void onGenresClick(View view, int position) {

    }

    private class GetGenres extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            function.getGenres(getContext(), genList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerGenresAdapter = new RecyclerGenresAdapter(getContext(), genList);
            recyclerGenresAdapter.setGenresClickListener(GenresFragment.this);
            genresView.setAdapter(recyclerGenresAdapter);
        }
    }
}
