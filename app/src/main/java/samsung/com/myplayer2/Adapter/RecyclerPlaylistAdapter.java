package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.PlaylistFunction;
import samsung.com.myplayer2.Model.Playlist;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/18/2018.
 */

public class RecyclerPlaylistAdapter extends RecyclerView.Adapter<RecyclerPlaylistAdapter.MyRecyclerPlaylistHolder> {

    private ArrayList<Playlist> playList;
    private PlaylistClickListener mClickListener;
    Context mContext;
    private int lastPosition = -1;
    boolean animate;

    public RecyclerPlaylistAdapter(Context context, ArrayList<Playlist> PList, boolean anim) {
        this.mContext = context;
        this.playList = PList;
        this.animate = anim;
    }

    public class MyRecyclerPlaylistHolder extends RecyclerView.ViewHolder {
        TextView ListName;
        TextView SongCount;
        public ImageView ListImg;
        ImageButton btnEdit;

        public MyRecyclerPlaylistHolder(View ListLay) {
            super(ListLay);

            ListName = (TextView) ListLay.findViewById(R.id.playlist_name);
            SongCount = ListLay.findViewById(R.id.pl_song_count);
            ListImg = (ImageView) ListLay.findViewById(R.id.playlist_img);
            btnEdit = ListLay.findViewById(R.id.btn_edit_pl);
//            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            if (mClickListener != null)
//                mClickListener.onPlaylistClick(view, getAdapterPosition());
//        }
//
//        @Override
//        public boolean onLongClick(View view) {
//            if (mClickListener != null)
//                mClickListener.onPlaylistLongClick(view, getAdapterPosition());
//            return false;
//        }

        //implements View.OnClickListener, View.OnLongClickListener
    }

    @Override
    public MyRecyclerPlaylistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View PlayListView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist, parent, false);
        return new MyRecyclerPlaylistHolder(PlayListView);
    }

    @Override
    public void onBindViewHolder(final MyRecyclerPlaylistHolder holder, int position) {
        final int pos = position;
        Playlist curPlayList = playList.get(position);

        holder.ListName.setText(curPlayList.getName());
        holder.SongCount.setText(Integer.toString(curPlayList.getSongCount()) + " songs");
//        TypedArray images = mContext.getResources().obtainTypedArray(R.array.array_drawables);
//        int choice = (int) (Math.random() * images.length());
//        GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(holder.ListImg);
//        Glide.with(mContext).load(images.getResourceId(choice, R.drawable.pic5)).into(target);
//        images.recycle();

        String art = getArtUri(curPlayList.getListid());
        if (art.equals("")) {
            Glide.with(mContext).load(R.drawable.noteicon)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .skipMemoryCache(true)
                    .into(holder.ListImg);
        } else {
            ImageLoader.getInstance().displayImage(art, holder.ListImg
                    , new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnLoading(R.drawable.noteicon)
                            .showImageOnFail(R.drawable.noteicon)
                            .resetViewBeforeLoading(true).build());
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp(mContext, view, pos);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)
                    mClickListener.onPlaylistClick(holder, pos);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mClickListener != null)
                    mClickListener.onPlaylistLongClick(holder, pos);
                return false;
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

        // It is important that each shared element in the source screen has a unique transition name.
        // For example, we can't just give all the images in our grid the transition name "kittenImage"
        // because then we would have conflicting transition names.
        // By appending "_image" to the position, we can support having multiple shared elements in each
        // grid cell in the future.
        ViewCompat.setTransitionName(holder.ListImg, String.valueOf(position) + "_playlist_image");
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
        return playList.size();
    }

    // allows clicks events to be caught
    public void setPlaylistClickListener(PlaylistClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface PlaylistClickListener {
        void onPlaylistClick(MyRecyclerPlaylistHolder holder, int position);

        void onPlaylistLongClick(MyRecyclerPlaylistHolder holder, int position);
    }

    private void createPopUp(final Context context, final View view, final int curpos) {


        //creating a popup menu for song in songlist
        PopupMenu popup = new PopupMenu(context, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.menu_edit_playlist);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit_playlist:
                        break;

                    case R.id.action_delete_playlist:
                        AlertDialog.Builder aat = new AlertDialog.Builder(context);
                        aat.setTitle("Delete ?")
                                .setMessage("Are you sure to delete " + playList.get(curpos).getName() + " ?")
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
                                                PlaylistFunction.deletePlaylists(context, playList.get(curpos).getListid());

                                                Context c = view.getContext();
                                                Intent pl = new Intent(Constants.TO_PLAYLIST);
                                                pl.setAction(Constants.RELOAD_PLAYLIST);
                                                c.sendBroadcast(pl);

                                                //RenewListSongOfPlaylist(pos);
                                                Toast.makeText(context, "Playlist deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                        AlertDialog art = aat.create();
                        art.show();
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }

    private String getSubString(String entry) {
        return entry.substring(0, entry.indexOf(Constants.MYPLAYER_PL_CR));
    }

    private String getArtUri(long id) {
        ArrayList<Song> pl = new ArrayList<>();
        PlaylistFunction.getSongsInPlaylist(mContext, id, pl);

        if (pl.size() > 0) {
            return Function.getAlbumArtUri(pl.get(0).getAlbumid()).toString();
        } else {
            return "";
        }
    }

    public void updateDataSet(ArrayList<Playlist> arraylist) {
        this.playList.clear();
        this.playList.addAll(arraylist);
        notifyDataSetChanged();
    }
}
