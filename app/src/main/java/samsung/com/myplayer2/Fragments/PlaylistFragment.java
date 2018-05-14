package samsung.com.myplayer2.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerPlaylistAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.Class.PlaylistFunction;
import samsung.com.myplayer2.Dialogs.CreatePlaylistDialog;
import samsung.com.myplayer2.Model.Playlist;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements RecyclerPlaylistAdapter.PlaylistClickListener {


    public PlaylistFragment() {
        // Required empty public constructor
    }

    MyService myService;

    RecyclerView playListView;
    ArrayList<Playlist> playlists;
    EditText edt;
    RecyclerPlaylistAdapter PlaylistAdapter;
    int pos;

    Toolbar toolbar;

    private static Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);

        IntentFilter toPlaylist = new IntentFilter(Constants.TO_PLAYLIST);
        toPlaylist.addAction(Constants.RELOAD_PLAYLIST);
        getActivity().registerReceiver(PlaylistBroadcast, toPlaylist);

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        playListView = v.findViewById(R.id.PlayListView);

        playlists = new ArrayList<>();
        playlists = PlaylistFunction.getPlaylists(getActivity());

        initRecyclerView();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        alertDialog.setTitle("Add new Playlist");
        alertDialog.setMessage("Insert Playlist Name");

        edt = new EditText(getActivity());
        alertDialog.setView(edt);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final AlertDialog alert = alertDialog.create();

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alert.show();
//            }
//        });


        return v;
    }


    @Override
    public void onPlaylistClick(RecyclerPlaylistAdapter.MyRecyclerPlaylistHolder holder, int position) {
        NavigationHelper.navigateToSongPlaylist(getActivity(), playlists.get(position).getListid()
                , playlists.get(position).getName(), holder.ListImg);
    }

    @Override
    public void onPlaylistLongClick(RecyclerPlaylistAdapter.MyRecyclerPlaylistHolder holder, int position) {
        registerForContextMenu(playListView);
        pos = position;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_playlist, menu);
        menu.findItem(R.id.action_equalizer).setVisible(false);
        menu.findItem(R.id.action_hide_panel).setVisible(false);
        menu.findItem(R.id.action_show_panel).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_playlist:
                CreatePlaylistDialog.newInstance().show(getChildFragmentManager(), "CREATE_PLAYLIST");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Change Playlist Name");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete Playlist");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Change Playlist Name")
            Toast.makeText(getActivity(), "Will be done later", Toast.LENGTH_SHORT).show();
        else if (item.getTitle() == "Delete Playlist") {
            AlertDialog.Builder aat = new AlertDialog.Builder(getActivity());
            aat.setTitle("Delete ?")
                    .setMessage("Are you sure to delete ?")
                    .setCancelable(true)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                    Toast.makeText(getActivity(), "Playlist removed", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
            AlertDialog art = aat.create();
            art.show();
        } else {
            return false;
        }
        return true;
    }

    BroadcastReceiver PlaylistBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(Constants.RELOAD_PLAYLIST)) {
                    updatePlaylists();
                } else if (intent.getAction().equals("Unregister")) {
                    getActivity().unregisterReceiver(PlaylistBroadcast);
                }
            }
        }
    };

    public void reloadPlaylists() {
        playlists = PlaylistFunction.getPlaylists(mContext);
        int playlistcount = playlists.size();

        initRecyclerView();

    }

    public void updatePlaylists() {
        playlists = PlaylistFunction.getPlaylists(mContext);

        PlaylistAdapter.updateDataSet(playlists);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        playListView.setLayoutManager(mManager);
        PlaylistAdapter = new RecyclerPlaylistAdapter(getContext(), playlists, true);
        PlaylistAdapter.setPlaylistClickListener(this);
        playListView.setAdapter(PlaylistAdapter);
    }

    private String getSubString(String entry){
        return entry.substring(0, entry.indexOf(Constants.MYPLAYER_PL_CR));
    }
}
