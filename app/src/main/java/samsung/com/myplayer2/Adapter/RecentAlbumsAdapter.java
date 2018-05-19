package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.ToolFunction;
import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/12/2018.
 */

public class RecentAlbumsAdapter extends RecyclerView.Adapter<RecentAlbumsAdapter.RecentAlbumHolder> {

    private ArrayList<Album> albumList;
    private RecentAlbumClickListener mClickListener;
    Context mContext;
    Function function = new Function();

    public RecentAlbumsAdapter(Context context, ArrayList<Album> albumList) {
        this.mContext = context;
        this.albumList = albumList;
    }

    public class RecentAlbumHolder extends RecyclerView.ViewHolder {
        TextView albumName, albumArtist;
        public ImageView albumImg;
        LinearLayout footer;

        public RecentAlbumHolder(View albumLay) {
            super(albumLay);
            albumName = albumLay.findViewById(R.id.album_name);
            albumArtist = albumLay.findViewById(R.id.album_artist);
            albumImg = albumLay.findViewById(R.id.album_img);
            footer = albumLay.findViewById(R.id.footer);
        }
    }

    @Override
    public RecentAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumView;
        albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_album, parent, false);
        return new RecentAlbumHolder(albumView);
    }

    @Override
    public void onBindViewHolder(final RecentAlbumHolder holder, int position) {
        final int pos = position;
        Album curAlbum = albumList.get(position);

        holder.albumName.setText(curAlbum.getAlbumName());
        holder.albumArtist.setText(curAlbum.getArtistName());
//        Glide.with(mContext).load(function.BitmapToByte(curAlbum.getAlbumImg())).into(holder.albumImg);

        //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
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
                                    int textColor = ToolFunction.getBlackWhiteColor(swatch.getTitleTextColor());
                                    holder.albumName.setTextColor(textColor);
                                    holder.albumArtist.setTextColor(textColor);
                                } else {
                                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                    if (mutedSwatch != null) {
                                        int color = mutedSwatch.getRgb();
                                        holder.footer.setBackgroundColor(color);
                                        int textColor = ToolFunction.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
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

        // It is important that each shared element in the source screen has a unique transition name.
        // For example, we can't just give all the images in our grid the transition name "kittenImage"
        // because then we would have conflicting transition names.
        // By appending "_image" to the position, we can support having multiple shared elements in each
        // grid cell in the future.
        ViewCompat.setTransitionName(holder.albumImg, String.valueOf(position) + "_album_image");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)
                    mClickListener.onRecentAlbumClick(holder, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    // allows clicks events to be caught
    public void setRecentAlbumClickListener(RecentAlbumClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface RecentAlbumClickListener {
        void onRecentAlbumClick(RecentAlbumHolder view, int position);
    }

    public void updateListAlbum(ArrayList<Album> update) {
        this.albumList = update;
    }
}
