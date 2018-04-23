package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import samsung.com.myplayer2.Model.Artist;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.lastfmapi.LastFmClient;
import samsung.com.myplayer2.lastfmapi.callbacks.ArtistInfoListener;
import samsung.com.myplayer2.lastfmapi.models.ArtistQuery;
import samsung.com.myplayer2.lastfmapi.models.LastfmArtist;

/**
 * Created by 450G4 on 3/26/2018.
 */

public class RecyclerArtistAdapter extends RecyclerView.Adapter<RecyclerArtistAdapter.MyRecyclerArtistHolder> {

    private ArrayList<Artist> artists;
    private ArtistClickListener mClickListener;
    Context mContext;

    public RecyclerArtistAdapter(Context context, ArrayList<Artist> artistList) {
        this.mContext = context;
        this.artists = artistList;
    }

    public class MyRecyclerArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView artistName;
        ImageView artistImg;
        LinearLayout footer;

        public MyRecyclerArtistHolder(View artistLay) {
            super(artistLay);

            artistName = (TextView) artistLay.findViewById(R.id.artist_name);
            artistImg = (ImageView) artistLay.findViewById(R.id.artist_img);
            footer = artistLay.findViewById(R.id.footer);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onArtistClick(view, getAdapterPosition());
        }
    }

    @Override
    public MyRecyclerArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View artistView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist, parent, false);
        return new MyRecyclerArtistHolder(artistView);
    }

    @Override
    public void onBindViewHolder(final MyRecyclerArtistHolder holder, int position) {
        Artist curArtist = artists.get(position);

        holder.artistName.setText(curArtist.getName());

        LastFmClient.getInstance(mContext).getArtistInfo(new ArtistQuery(curArtist.getName()), new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(LastfmArtist artist) {
                if (artist != null && artist.mArtwork != null) {

                    ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
                    ImageLoader.getInstance().displayImage(artist.mArtwork.get(2).mUrl, holder.artistImg,
                            new DisplayImageOptions.Builder().cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .showImageOnFail(R.drawable.album_none)
                                    .resetViewBeforeLoading(true)
                                    .displayer(new FadeInBitmapDisplayer(400))
                                    .build(), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    if (loadedImage != null) {
                                        new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                                            @Override
                                            public void onGenerated(Palette palette) {
                                                int color = palette.getVibrantColor(Color.parseColor("#66000000"));
                                                holder.footer.setBackgroundColor(color);
                                                Palette.Swatch swatch = palette.getVibrantSwatch();
                                                int textColor;
                                                if (swatch != null) {
                                                    textColor = getOpaqueColor(swatch.getTitleTextColor());
                                                } else textColor = Color.parseColor("#ffffff");

                                                holder.artistName.setTextColor(textColor);
                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    holder.footer.setBackgroundColor(0);
                                    if (mContext != null) {
                                        holder.artistName.setTextColor(1);
                                    }

                                }
                            });

                }
            }

            @Override
            public void artistInfoFailed() {

            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    // allows clicks events to be caught
    public void setArtistClickListener(ArtistClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ArtistClickListener {
        void onArtistClick(View view, int position);
    }

    public static int getOpaqueColor(@ColorInt int paramInt) {
        return 0xFF000000 | paramInt;
    }
}
