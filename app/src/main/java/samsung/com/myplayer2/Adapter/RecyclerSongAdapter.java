package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.BaseSongAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.Class.RcFunction;
import samsung.com.myplayer2.Class.ToolFunction;
import samsung.com.myplayer2.Dialogs.AddToPlaylistDialog;
import samsung.com.myplayer2.Handler.DatabaseHandler;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/10/2018.
 */

public class RecyclerSongAdapter extends BaseSongAdapter<RecyclerSongAdapter.MyRecyclerSongHolder> implements FastScrollRecyclerView.SectionedAdapter {


    private ArrayList<Song> songs;
    AppCompatActivity mContext;
    ArrayList<String> Namelist;
    ListView lv;
    Function function = new Function();
    boolean isResult;
    boolean isGenres;
    boolean animate;
    private int lastPosition = -1;
    private long[] songIDs;
    private int selectedPos = RecyclerView.NO_POSITION;

    public RecyclerSongAdapter(AppCompatActivity context, ArrayList<Song> data, boolean search, boolean genres, boolean anim) {
        this.mContext = context;
        this.songs = data;
        this.isResult = search;
        this.isGenres = genres;
        this.animate = anim;
        this.songIDs = getSongIds();
    }

    public RecyclerSongAdapter() {
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return songs.get(position).getTitle().substring(0, 1);
    }

    public class MyRecyclerSongHolder extends RecyclerView.ViewHolder {
        TextView songView, artistView;
        ImageView coverimg;
        ImageButton btn;

        public MyRecyclerSongHolder(View songLay) {
            super(songLay);
            //get title and artist views
            songView = songLay.findViewById(R.id.song_title);
            artistView = songLay.findViewById(R.id.song_artist);
            coverimg = songLay.findViewById(R.id.coverImg);
            btn = songLay.findViewById(R.id.menuSong);
        }
    }

    public MyRecyclerSongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //map to song layout
        View songView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song, parent, false);
        return new MyRecyclerSongHolder(songView);
    }

    @Override
    public void onBindViewHolder(final MyRecyclerSongHolder holder, int position) {

        //get song using position
        final Song currSong = songs.get(position);
        final int pos = position;

//        SharedPreferences getLight = PreferenceManager.getDefaultSharedPreferences(mContext);
//        long lightId = getLight.getLong("highLight", -1);
//
//        if (songs.get(pos).getID() == lightId) {
//            holder.songView.setTextColor(Color.MAGENTA);
//        }

//        if (selectedPos == pos) {
//            holder.songView.setTextColor(Color.BLUE);
//        }


        //get title and artist strings, embedded pictures
        holder.songView.setText(currSong.getTitle());
        holder.artistView.setText(currSong.getArtist());

//        Glide.with(mContext).load(function.GetBitMapByte(currSong.getData()))
//                .override(57, 63)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT).skipMemoryCache(true)
//                .error(R.drawable.noteicon)
//                .into(holder.coverimg);

        //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
        ImageLoader.getInstance().displayImage(function.getAlbumArtUri(currSong.getAlbumid()).toString(),
                holder.coverimg, new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.noteicon)
                        .resetViewBeforeLoading(true).build());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                Intent play = new Intent(Constants.TO_SERVICE);
                play.setAction(Constants.SV_PLAYONE);
                play.putExtra(Constants.POSITION, pos);
                if (isResult) {
                    play.putExtra(Constants.TYPE_NAME, Constants.SEARCH_TYPE);
                } else if (isGenres) {
                    play.putExtra(Constants.TYPE_NAME, Constants.GENRES_TYPE);
                } else {
                    play.putExtra(Constants.TYPE_NAME, Constants.SONG_TYPE);
                }
                c.sendBroadcast(play);

                if (!isResult && !isGenres) {
                    notifyItemChanged(selectedPos);
                    selectedPos = holder.getLayoutPosition();

//                    SharedPreferences sharedLight = PreferenceManager.getDefaultSharedPreferences(mContext);
//                    SharedPreferences.Editor editorLight = sharedLight.edit();
//                    editorLight.putLong("highLight", songs.get(selectedPos).getID());
//                    editorLight.apply();

                    notifyItemChanged(selectedPos);
                }

                RcFunction.AddRecent(mContext, Constants.RECENT_SONG_ID, songs.get(pos).getID());
            }
        });

        if (animate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                setAnimation(holder.itemView, pos);
            else {
                if (pos > 10)
                    setAnimation(holder.itemView, pos);
            }
        }

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp(mContext, holder.btn, pos);
            }
        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateListSong(ArrayList<Song> update) {
        this.songs = update;
    }

    public ArrayList<Song> GetListSong() {
        return this.songs;
    }

    private void createPopUp(Context context, View view, final int curpos) {


        //creating a popup menu for song in songlist
        PopupMenu popup = new PopupMenu(context, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.popup_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_to_playlist:
                        AddToPlaylistDialog.newInstance(songs.get(curpos)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                        break;

                    case R.id.go_to_album:
                        NavigationHelper.navigateToSongAlbum((AppCompatActivity) mContext, songs.get(curpos).getAlbumid()
                                , songs.get(curpos).getAlbum(), null);
                        break;

                    case R.id.go_to_artist:
                        NavigationHelper.navigateToSongArtist((AppCompatActivity) mContext, songs.get(curpos).getArtist(), null);
                        break;

                    case R.id.share:
                        ToolFunction.shareTrack(mContext, songs.get(curpos).getData());
                        break;

                    case R.id.delete:
                        long[] deleteIds = {songs.get(curpos).getID()};
                        ToolFunction.showDeleteDialog(mContext, songs.get(curpos).getTitle(), deleteIds, RecyclerSongAdapter.this, curpos, false);
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }

    private void createPopUp2(Context context, View view, final int curpos) {


        //creating a popup menu for song in songlist
        PopupMenu popup = new PopupMenu(context, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.popup_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_to_playlist:
                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View customview = inflater.inflate(R.layout.alert_layout, null);
                        lv = customview.findViewById(R.id.listPlaylist);
                        Namelist = new ArrayList<>();
                        final DatabaseHandler db = new DatabaseHandler(mContext);
                        Namelist = db.GetAllPlaylistName();
                        SongAdapter adapter = new SongAdapter(mContext, Namelist);
                        lv.setAdapter(adapter);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                                long idsong = songs.get(curpos).getID();
                                String idpl = db.getIdByName(Namelist.get(pos));
                                if (!db.CheckSongAdded(Long.toString(idsong), idpl)) {
                                    db.addSongToPlaylist(Long.toString(idsong), idpl);
                                    Toast.makeText(mContext, "Song added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Song already Exist !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                        alertDialog.setTitle("Add To Playlist");
                        alertDialog.setMessage("Select Playlist Name");

                        alertDialog.setView(customview);

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

                        AlertDialog alert = alertDialog.create();
                        alert.show();

                        break;

                    case R.id.go_to_album:
                        NavigationHelper.navigateToSongAlbum((AppCompatActivity) mContext, songs.get(curpos).getAlbumid()
                                , songs.get(curpos).getAlbum(), null);
                        break;

                    case R.id.go_to_artist:
                        NavigationHelper.navigateToSongArtist((AppCompatActivity) mContext, songs.get(curpos).getArtist(), null);
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();


        //creating a popup menu for playlist song
//        PopupMenu popup2 = new PopupMenu(context, view);
//        //inflating menu from xml resource
//        popup2.inflate(R.menu.popup_menu_in_playlist);
//        //adding click listener
//        popup2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                switch (item.getItemId()) {
//
//                    case R.id.action_in_pl_1:
//                        long idsong = songs.get(curpos).getID();
//
//                        Intent remove = new Intent("ToPlaylist");
//                        remove.setAction("Remove");
//                        remove.putExtra("songid", idsong);
//                        mContext.sendBroadcast(remove);
//
//                        break;
//
//                    case R.id.action_in_pl_2:
//                        break;
//                }
//                return false;
//            }
//        });
//        //displaying the popup
//        popup2.show();
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = songs.get(i).getID();
        }

        return ret;
    }

    @Override
    public void updateDataSet(ArrayList<Song> arraylist) {
        this.songs = arraylist;
        this.songIDs = getSongIds();
    }

    @Override
    public void removeSongAt(int i) {
        songs.remove(i);
        updateDataSet(songs);
    }
}
