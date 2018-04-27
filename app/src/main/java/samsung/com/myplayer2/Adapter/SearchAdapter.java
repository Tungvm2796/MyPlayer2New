package samsung.com.myplayer2.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.Handler.DatabaseHandler;
import samsung.com.myplayer2.Model.Album;
import samsung.com.myplayer2.Model.Artist;
import samsung.com.myplayer2.Model.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.lastfmapi.LastFmClient;
import samsung.com.myplayer2.lastfmapi.callbacks.ArtistInfoListener;
import samsung.com.myplayer2.lastfmapi.models.ArtistQuery;
import samsung.com.myplayer2.lastfmapi.models.LastfmArtist;

public class SearchAdapter  extends RecyclerView.Adapter<SearchAdapter.ItemHolder>{

    private Activity mContext;
    private List searchResults = Collections.emptyList();
    ArrayList<String> Namelist;
    ListView lv;
    Function function = new Function();

    public SearchAdapter(Activity context) {
        this.mContext = context;

    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof Song)
            return 0;
        if (searchResults.get(position) instanceof Album)
            return 1;
        if (searchResults.get(position) instanceof Artist)
            return 2;
        if (searchResults.get(position) instanceof String)
            return 3;
        return 4;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0:
                View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song, null);
                ItemHolder ml0 = new ItemHolder(v0);
                return ml0;
            case 1:
                View v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_search, null);
                ItemHolder ml1 = new ItemHolder(v1);
                return ml1;
            case 2:
                View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_search, null);
                ItemHolder ml2 = new ItemHolder(v2);
                return ml2;
            case 3:
                View v10 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_section_header, null);
                ItemHolder ml3 = new ItemHolder(v10);
                return ml3;
            default:
                View v3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song, null);
                ItemHolder ml4 = new ItemHolder(v3);
                return ml4;
        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        switch (getItemViewType(i)) {
            case 0:
                final int pos = i;
                Song song = (Song) searchResults.get(i);
                itemHolder.title.setText(song.getTitle());
                itemHolder.songartist.setText(song.getArtist());
                ImageLoader.getInstance().displayImage(function.getAlbumArtUri(song.getAlbumid()).toString(), itemHolder.albumArt,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisk(true)
                                .showImageOnFail(R.drawable.noteicon)
                                .resetViewBeforeLoading(true)
                                .displayer(new FadeInBitmapDisplayer(400))
                                .build());

//                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Context c = view.getContext();
//                        Intent play = new Intent("ToService");
//                        play.setAction("SvPlayOne");
//                        play.putExtra("pos", pos);
//                        play.putExtra(Constants.TYPE_NAME, Constants.SEARCH_TYPE);
//                        c.sendBroadcast(play);
//                    }
//                });

                itemHolder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createPopUp(mContext, itemHolder.btn, pos);
                    }
                });

                break;
            case 1:
                Album album = (Album) searchResults.get(i);
                itemHolder.albumtitle.setText(album.getAlbumName());
                itemHolder.albumartist.setText(album.getArtistName());
                ImageLoader.getInstance().displayImage(function.getAlbumArtUri(album.getId()).toString(), itemHolder.albumArt,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisk(true)
                                .showImageOnFail(R.drawable.album_none)
                                .resetViewBeforeLoading(true)
                                .displayer(new FadeInBitmapDisplayer(400))
                                .build());
                break;
            case 2:
                Artist artist = (Artist) searchResults.get(i);
                itemHolder.artisttitle.setText(artist.getName());
                String albumNmber = function.makeLabel(mContext, R.plurals.Nalbums, artist.getAlbumCount());
                String songCount = function.makeLabel(mContext, R.plurals.Nsongs, artist.getSongCount());
                itemHolder.albumsongcount.setText(function.makeCombinedString(mContext, albumNmber, songCount));
                LastFmClient.getInstance(mContext).getArtistInfo(new ArtistQuery(artist.getName()), new ArtistInfoListener() {
                    @Override
                    public void artistInfoSucess(LastfmArtist artist) {
                        if (artist != null && itemHolder.artistImage != null) {
                            ImageLoader.getInstance().displayImage(artist.mArtwork.get(1).mUrl, itemHolder.artistImage,
                                    new DisplayImageOptions.Builder().cacheInMemory(true)
                                            .cacheOnDisk(true)
                                            .showImageOnFail(R.drawable.album_none)
                                            .resetViewBeforeLoading(true)
                                            .displayer(new FadeInBitmapDisplayer(400))
                                            .build());
                        }
                    }

                    @Override
                    public void artistInfoFailed() {

                    }
                });
                break;
            case 3:
                itemHolder.sectionHeader.setText((String) searchResults.get(i));
            case 4:
                break;
        }
    }

    @Override
    public void onViewRecycled(ItemHolder itemHolder) {

    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }


    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, songartist, albumtitle, artisttitle, albumartist, albumsongcount, sectionHeader;
        protected ImageView albumArt, artistImage, songImg;
        protected Button btn;

        public ItemHolder(View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.song_title);
            this.songartist = (TextView) view.findViewById(R.id.song_artist);
            this.albumsongcount = (TextView) view.findViewById(R.id.count);
            this.artisttitle = (TextView) view.findViewById(R.id.artist_name);
            this.albumtitle = (TextView) view.findViewById(R.id.album_name);
            this.albumartist = (TextView) view.findViewById(R.id.artist);
            this.albumArt = (ImageView) view.findViewById(R.id.album_coverImg);
            this.artistImage = (ImageView) view.findViewById(R.id.artist_coverImg);
            this.songImg = (ImageView) view.findViewById(R.id.coverImg);
            this.btn = (Button) view.findViewById(R.id.menuSong);

            this.sectionHeader = (TextView) view.findViewById(R.id.section_header);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (getItemViewType()) {
                case 0:
                    Context c = v.getContext();
                    Intent play = new Intent("ToService");
                    play.setAction("SvPlayOne");
                    play.putExtra("pos", getAdapterPosition());
                    play.putExtra(Constants.TYPE_NAME, Constants.SEARCH_TYPE);
                    c.sendBroadcast(play);

                    break;
                case 1:
                    NavigationHelper.goToAlbum(mContext, ((Album) searchResults.get(getAdapterPosition())).getId()
                            ,((Album) searchResults.get(getAdapterPosition())).getAlbumName());
                    break;
                case 2:
                    NavigationHelper.goToArtist(mContext, ((Artist) searchResults.get(getAdapterPosition())).getName());
                    break;
                case 3:
                    break;
                case 4:
                    break;
            }
        }

    }

    private void createPopUp(Context context, View view, final int curpos) {


        //creating a popup menu for song in songlist
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(context, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.popup_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action1:

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
                                long idsong = ((Song) searchResults.get(curpos)).getID();
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

                    case R.id.action2:

                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }
}
