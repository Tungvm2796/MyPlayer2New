package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/12/2018.
 */

public class RecyclerAlbumAdapter extends RecyclerView.Adapter<RecyclerAlbumAdapter.MyRecyclerAlbumHolder> {

    private ArrayList<Album> albumList;
    private AlbumClickListener mClickListener;
    Context mContext;
    Function function = new Function();
    boolean isGrid;

    public RecyclerAlbumAdapter(Context context, ArrayList<Album> albumList, boolean Grid) {
        this.mContext = context;
        this.albumList = albumList;
        this.isGrid = Grid;
    }

    public class MyRecyclerAlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView albumName, albumArtist;
        ImageView albumImg;
        LinearLayout footer;

        public MyRecyclerAlbumHolder(View albumLay) {
            super(albumLay);

            if (isGrid) {
                albumName = albumLay.findViewById(R.id.album_name);
                albumArtist = albumLay.findViewById(R.id.album_artist);
                albumImg = albumLay.findViewById(R.id.album_img);
                footer = albumLay.findViewById(R.id.footer);
            } else {
                albumName = albumLay.findViewById(R.id.album_name);
                albumArtist = albumLay.findViewById(R.id.album_artist);
                albumImg = albumLay.findViewById(R.id.album_coverImg);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onAlbumClick(view, getAdapterPosition());
        }
    }

    @Override
    public MyRecyclerAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumView;
        if (isGrid) {
            albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.albums, parent, false);
        } else {
            albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_search, parent, false);
        }
        return new MyRecyclerAlbumHolder(albumView);
    }

    @Override
    public void onBindViewHolder(final MyRecyclerAlbumHolder holder, int position) {
        Album curAlbum = albumList.get(position);

        holder.albumName.setText(curAlbum.getAlbumName());
        holder.albumArtist.setText(curAlbum.getArtistName());
//        Glide.with(mContext).load(function.BitmapToByte(curAlbum.getAlbumImg())).into(holder.albumImg);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
        if (isGrid) {
            ImageLoader.getInstance().displayImage(function.getAlbumArtUri(curAlbum.getId()).toString(),
                    holder.albumImg, new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnLoading(R.drawable.album_none)
                            .resetViewBeforeLoading(true).build(),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    if (swatch != null) {
                                        int color = swatch.getRgb();
                                        holder.footer.setBackgroundColor(color);
                                        int textColor = function.getBlackWhiteColor(swatch.getTitleTextColor());
                                        holder.albumName.setTextColor(textColor);
                                        holder.albumArtist.setTextColor(textColor);
                                    } else {
                                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                        if (mutedSwatch != null) {
                                            int color = mutedSwatch.getRgb();
                                            holder.footer.setBackgroundColor(color);
                                            int textColor = function.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
                                            holder.albumName.setTextColor(textColor);
                                            holder.albumArtist.setTextColor(textColor);
                                        }
                                    }


                                }
                            });
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.footer.setBackgroundColor(0);
                            if (mContext != null) {
                                holder.albumName.setTextColor(Color.BLACK);
                                holder.albumArtist.setTextColor(Color.LTGRAY);
                            }
                        }
                    }
            );
        } else {
            ImageLoader.getInstance().displayImage(function.getAlbumArtUri(curAlbum.getId()).toString(),
                    holder.albumImg, new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnLoading(R.drawable.album_none)
                            .resetViewBeforeLoading(true).build());
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    // allows clicks events to be caught
    public void setAlbumClickListener(AlbumClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface AlbumClickListener {
        void onAlbumClick(View view, int position);
    }

    public void updateListAlbum(ArrayList<Album> update) {
        this.albumList = update;
    }
}
