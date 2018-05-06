package samsung.com.myplayer2.Fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    Function function;

    RecyclerArtistAdapter artistAdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist, container, false);
        function = new Function();
        context = super.getActivity();
        artistView = v.findViewById(R.id.artistView);

        setRetainInstance(true);

        artists = new ArrayList<>();

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        artistView.setLayoutManager(mManager);



        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
            new GetArtist().execute();
        } else {
            new GetArtist().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return v;
    }

    @Override
    public void onArtistClick(RecyclerArtistAdapter.MyRecyclerArtistHolder view, int position) {
        NavigationHelper.navigateToSongArtist(getActivity(), artists.get(position).getName(), view.artistImg);
    }

    private class GetArtist extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            function.getArtist(getContext(), artists, null);
            artistAdt = new RecyclerArtistAdapter(getContext(), artists, true);
            artistAdt.setArtistClickListener(ArtistFragment.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            artistView.setAdapter(artistAdt);
        }
    }
}
