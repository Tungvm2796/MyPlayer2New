package samsung.com.myplayer2.Fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerArtistAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.Model.Artist;
import samsung.com.myplayer2.R;


/**
 * A simple {@link Fragment} subclass.
 */

public class ArtistFragment extends Fragment implements RecyclerArtistAdapter.ArtistClickListener {


    public ArtistFragment() {
        // Required empty public constructor
    }

    private ArrayList<Artist> artists;

    RecyclerView artistView;
    Context context;
    LinearLayout lin1;
    LinearLayout lin2;
    Function function;

    Toolbar toolbar;

    RecyclerArtistAdapter artistAdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist, container, false);
        function = new Function();
        context = super.getActivity();
        artistView = v.findViewById(R.id.artistView);

        lin1 = v.findViewById(R.id.lin1);
        lin2 = v.findViewById(R.id.lin2);

        setRetainInstance(true);



        //function.getSongList(getActivity(), songListTake);
        //songListTake = ((MainActivity)getActivity()).getAllSong();

        artists = new ArrayList<>();
        //function.getArtist(getActivity(), artists);

//        View tabcontainer = new MainFragment().getView().findViewById(R.id.tabcontainer);
//        toolbar = new MainFragment().getView().findViewById(R.id.toolbar);
//        View lasttab = new MainFragment().getView().findViewById(R.id.viewpagertab);
//        View coloredBackgroundView = new MainFragment().getView().findViewById(R.id.colored_background_view);

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        artistView.setLayoutManager(mManager);

        artistAdt = new RecyclerArtistAdapter(getContext(), artists, true);
        artistAdt.setArtistClickListener(ArtistFragment.this);

        new GetArtist().execute();

        //artistView.addOnScrollListener(new ToolbarHidingOnScrollListener(getActivity(), tabcontainer, toolbar, lasttab, coloredBackgroundView));

        return v;
    }

    @Override
    public void onArtistClick(View view, int position) {
        NavigationHelper.navigateToSongArtist(getActivity(), artists.get(position).getName());
    }

    private class GetArtist extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            function.getArtist(getContext(), artists, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            artistView.setAdapter(artistAdt);
        }
    }
}
