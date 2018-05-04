package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import samsung.com.myplayer2.R;

public class RecyclerGenresAdapter extends RecyclerView.Adapter<RecyclerGenresAdapter.MyRecyclerGenresHolder> {

    private ArrayList<String> genres;
    private GenresClickListener mClickListener;
    Context mContext;

    public RecyclerGenresAdapter(Context context, ArrayList<String> genresList) {
        this.mContext = context;
        this.genres = genresList;
    }

    public class MyRecyclerGenresHolder extends RecyclerView.ViewHolder {
        TextView genresName;
        public ImageView genresImg;

        public MyRecyclerGenresHolder(View genresLay) {
            super(genresLay);

            genresName = (TextView) genresLay.findViewById(R.id.genres_name);
            genresImg = genresLay.findViewById(R.id.genres_img);
            //itemView.setOnClickListener(this);

        }

//        @Override
//        public void onClick(View view) {
//            if (mClickListener != null)
//                mClickListener.onGenresClick(view, getAdapterPosition());
//        }
//        implements View.OnClickListener
    }

    @Override
    public MyRecyclerGenresHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View genresView = LayoutInflater.from(parent.getContext()).inflate(R.layout.genres, parent, false);
        return new MyRecyclerGenresHolder(genresView);
    }

    @Override
    public void onBindViewHolder(final MyRecyclerGenresHolder holder, int position) {
        final int pos = position;
        String curGenres = genres.get(position);

        holder.genresName.setText(curGenres);
        int resID = mContext.getResources().getIdentifier(curGenres.toLowerCase()+"_music", "drawable", mContext.getPackageName());
        Glide.with(mContext).load(resID)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).skipMemoryCache(true)
                .error(R.drawable.music_frame).into(holder.genresImg);

        // It is important that each shared element in the source screen has a unique transition name.
        // For example, we can't just give all the images in our grid the transition name "kittenImage"
        // because then we would have conflicting transition names.
        // By appending "_image" to the position, we can support having multiple shared elements in each
        // grid cell in the future.
        ViewCompat.setTransitionName(holder.genresImg, String.valueOf(position) + "_genres_image");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener!=null)
                    mClickListener.onGenresClick(holder, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    // allows clicks events to be caught
    public void setGenresClickListener(GenresClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface GenresClickListener {
        void onGenresClick(MyRecyclerGenresHolder view, int position);
    }
}
