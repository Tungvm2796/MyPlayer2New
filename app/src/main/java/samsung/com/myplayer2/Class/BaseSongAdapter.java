package samsung.com.myplayer2.Class;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import samsung.com.myplayer2.Model.Song;

public class BaseSongAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class MyRecyclerSongHolder extends RecyclerView.ViewHolder {

        public MyRecyclerSongHolder(View songLay) {
            super(songLay);
        }

    }

    public void removeSongAt(int i) {
    }

    public void updateDataSet(ArrayList<Song> arraylist) {
    }

    public void updatePlaylist(int pos) {
    }

    ;
}
