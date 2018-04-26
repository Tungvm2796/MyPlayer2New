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

import samsung.com.myplayer2.Adapter.RecyclerAlbumAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.R;


/**
 * A simple {@link Fragment} subclass.
 */

public class AlbumFragment extends Fragment implements RecyclerAlbumAdapter.AlbumClickListener {


    public AlbumFragment() {
        // Required empty public constructor
    }

    private ArrayList<Album> albumList;

    RecyclerView albumView;
    Context context;
    LinearLayout lin1;
    LinearLayout lin2;

    Function function;

    Toolbar toolbar;

    RecyclerAlbumAdapter albumAdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album, container, false);

        function = new Function();

        context = super.getActivity();
        albumView = v.findViewById(R.id.albumView);


        lin1 = v.findViewById(R.id.lin1);
        lin2 = v.findViewById(R.id.lin2);

        setRetainInstance(true);

        albumList = new ArrayList<>();

//        View tabcontainer = new MainFragment().getView().findViewById(R.id.tabcontainer);
//        toolbar = new MainFragment().getView().findViewById(R.id.toolbar);
//        View lasttab = new MainFragment().getView().findViewById(R.id.viewpagertab);
//        View coloredBackgroundView = new MainFragment().getView().findViewById(R.id.colored_background_view);

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        albumView.setLayoutManager(mManager);

        albumAdt = new RecyclerAlbumAdapter(getContext(), albumList, true);
        albumAdt.setAlbumClickListener(AlbumFragment.this);

        new GetAlbum().execute();

        //albumView.addOnScrollListener(new ToolbarHidingOnScrollListener(getActivity(), tabcontainer, toolbar, lasttab, coloredBackgroundView));

        return v;
    }

    @Override
    public void onAlbumClick(View view, int position) {
        NavigationHelper.navigateToSongAlbum(getActivity(), albumList.get(position).getId());
    }

    private class GetAlbum extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            function.getAlbumsLists(getContext(), albumList, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            albumView.setAdapter(albumAdt);
        }
    }
}
