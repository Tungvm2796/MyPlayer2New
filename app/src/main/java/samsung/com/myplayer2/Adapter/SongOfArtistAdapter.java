package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import samsung.com.myplayer2.Class.BaseSongAdapter;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.Class.ToolFunction;
import samsung.com.myplayer2.Dialogs.AddToPlaylistDialog;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;

public class SongOfArtistAdapter extends BaseSongAdapter<SongOfArtistAdapter.MyRecyclerSongHolder> {

    private ArrayList<Song> songs;
    AppCompatActivity mContext;
    ArrayList<String> Namelist;
    ListView lv;
    Function function = new Function();
    boolean animate;
    private int lastPosition = -1;
    private long[] songIDs;

    public SongOfArtistAdapter(AppCompatActivity context, ArrayList<Song> data, boolean anim) {
        this.mContext = context;
        this.songs = data;
        this.animate = anim;
        this.songIDs = getSongIds();
    }

    public SongOfArtistAdapter() {
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

    public SongOfArtistAdapter.MyRecyclerSongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //map to song layout
        View songView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song, parent, false);
        return new SongOfArtistAdapter.MyRecyclerSongHolder(songView);
    }

    @Override
    public void onBindViewHolder(final SongOfArtistAdapter.MyRecyclerSongHolder holder, int position) {
        //get song using position
        final Song currSong = songs.get(position);
        final int pos = position;

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
                play.putExtra(Constants.TYPE_NAME, Constants.ARTIST_TYPE);
                c.sendBroadcast(play);
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
                        ToolFunction.showDeleteDialog(mContext, songs.get(curpos).getTitle(), deleteIds, SongOfArtistAdapter.this, curpos, false);
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
        popup.getMenu().findItem(R.id.go_to_artist).setVisible(false);
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
