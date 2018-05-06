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
    Function function;

    RecyclerAlbumAdapter albumAdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album, container, false);

        function = new Function();

        context = super.getActivity();
        albumView = v.findViewById(R.id.albumView);


        setRetainInstance(true);

        albumList = new ArrayList<>();

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        albumView.setLayoutManager(mManager);



        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
            new GetAlbum().execute();
        } else {
            new GetAlbum().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return v;
    }

    @Override
    public void onAlbumClick(RecyclerAlbumAdapter.MyRecyclerAlbumHolder view, int position) {
        NavigationHelper.navigateToSongAlbum(getActivity(), albumList.get(position).getId()
                , albumList.get(position).getAlbumName(), view.albumImg);
    }

    private class GetAlbum extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            function.getAlbumsLists(getContext(), albumList, null);
            albumAdt = new RecyclerAlbumAdapter(getContext(), albumList, true);
            albumAdt.setAlbumClickListener(AlbumFragment.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            albumView.setAdapter(albumAdt);
        }
    }
}
