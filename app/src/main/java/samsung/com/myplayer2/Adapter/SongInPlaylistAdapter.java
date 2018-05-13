package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.ItemTouchHelperAdapter;
import samsung.com.myplayer2.Class.ItemTouchHelperViewHolder;
import samsung.com.myplayer2.Class.PlaylistFunction;
import samsung.com.myplayer2.Class.ToolFunction;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;

public class SongInPlaylistAdapter extends RecyclerView.Adapter<SongInPlaylistAdapter.MyRecyclerSongHolder> implements ItemTouchHelperAdapter {

    private ArrayList<Song> songs;
    AppCompatActivity mContext;
    ArrayList<String> Namelist;
    ListView lv;
    Function function = new Function();
    private static String listId = "";
    private int lastPosition = -1;
    boolean animate;
    private long playlistId;
    private long[] songIDs;

    public SongInPlaylistAdapter(AppCompatActivity context, ArrayList<Song> data, boolean anim) {
        this.mContext = context;
        this.songs = data;
        this.animate = anim;
        this.songIDs = getSongIds();
    }

    public SongInPlaylistAdapter() {
    }

    public class MyRecyclerSongHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
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

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public SongInPlaylistAdapter.MyRecyclerSongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //map to song layout
        View songView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song, parent, false);
        return new SongInPlaylistAdapter.MyRecyclerSongHolder(songView);
    }

    @Override
    public void onBindViewHolder(final SongInPlaylistAdapter.MyRecyclerSongHolder holder, int position) {
        final int pos = position;

        //get song using position
        final Song currSong = songs.get(position);

        //get title and artist strings, embedded pictures
        holder.songView.setText(currSong.getTitle());
        holder.artistView.setText(currSong.getArtist());

        Glide.with(mContext).load(function.GetBitMapByte(currSong.getData()))
                .override(57, 63)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).skipMemoryCache(true)
                .error(R.drawable.noteicon)
                .into(holder.coverimg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                Intent play = new Intent(Constants.TO_SERVICE);
                play.setAction(Constants.SV_PLAYONE);
                play.putExtra(Constants.POSITION, pos);
                play.putExtra(Constants.TYPE_NAME, Constants.PLAYLIST_TYPE);
                c.sendBroadcast(play);
                //Toast.makeText(mContext, Integer.toString(pos), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp(holder.btn, pos);
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
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private void createPopUp(View view, final int curpos) {
        //creating a popup menu
        PopupMenu popup2 = new PopupMenu(mContext, view);
        //inflating menu from xml resource
        popup2.inflate(R.menu.popup_menu_in_playlist);
        //adding click listener
        popup2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_remove_from_playlist:
                        PlaylistFunction.removeFromPlaylist(mContext, songs.get(curpos).getID(), playlistId);
                        removeSongAt(curpos);
                        notifyItemRemoved(curpos);
                        notifyDataSetChanged();

                        Intent playlist = new Intent(Constants.TO_PLAYLIST_SONG);
                        playlist.setAction(Constants.RELOAD_PLAYLIST_SONG);
                        mContext.sendBroadcast(playlist);

                        break;

                    case R.id.action_share:
                        ToolFunction.shareTrack(mContext, songs.get(curpos).getData());
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup2.show();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < songs.size() && toPosition < songs.size()) {
//            if (fromPosition < toPosition) {
//                for (int i = fromPosition; i < toPosition; i++) {
//                    Collections.swap(songs, i, i + 1);
//                }
//            } else {
//                for (int i = fromPosition; i > toPosition; i--) {
//                    Collections.swap(songs, i, i - 1);
//                }
//            }
            Song song = getSongAt(fromPosition);
            removeSongAt(fromPosition);
            addSongTo(toPosition, song);
            notifyItemMoved(fromPosition, toPosition);
        }

        return true;
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {

        MediaStore.Audio.Playlists.Members.moveItem(mContext.getContentResolver(),
                playlistId, fromPosition, toPosition);
    }

    @Override
    public void afterItemMoved() {

        notifyDataSetChanged();

        Intent change = new Intent(Constants.TO_PLAYLIST_SONG);
        change.setAction(Constants.RELOAD_PLAYLIST_SONG);
        mContext.sendBroadcast(change);
    }

    @Override
    public void onItemDismiss(int position) {
        removeSongAt(position);
        notifyItemRemoved(position);
    }

    public void setListId(String id) {
        listId = id;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = songs.get(i).getID();
        }

        return ret;
    }

    public void updateDataSet(ArrayList<Song> arraylist) {
        this.songs = arraylist;
        this.songIDs = getSongIds();
    }

    public void removeSongAt(int i) {
        songs.remove(i);
        updateDataSet(songs);
    }

    public Song getSongAt(int i) {
        return songs.get(i);
    }

    public void addSongTo(int i, Song song) {
        songs.add(i, song);
    }
}
