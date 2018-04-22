package samsung.com.myplayer2.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.LyricsExtractor;
import samsung.com.myplayer2.Class.LyricsLoader;
import samsung.com.myplayer2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LyricsFragment extends Fragment {


    public LyricsFragment() {
        // Required empty public constructor
    }

    private String lyrics = null;
    Toolbar toolbar;
    String song_title;
    String song_path;
    String artist_name;
    View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            song_title = getArguments().getString(Constants.SONG_TITLE);
            song_path = getArguments().getString(Constants.SONG_PATH);
            artist_name = getArguments().getString(Constants.ARTIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_lyrics,container,false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        setupToolbar();

        loadLyrics();

        return rootView;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(song_title);
    }

    private void loadLyrics() {

        final View lyricsView = rootView.findViewById(R.id.lyrics);
        final TextView poweredbyTextView = (TextView) lyricsView.findViewById(R.id.lyrics_makeitpersonal);
        poweredbyTextView.setVisibility(View.GONE);
        final TextView lyricsTextView = (TextView) lyricsView.findViewById(R.id.lyrics_text);
        lyricsTextView.setText(getString(R.string.lyrics_loading));
        String filename = song_path;
        if (filename != null && lyrics == null) {
            lyrics = LyricsExtractor.getLyrics(new File(filename));
        }

        if (lyrics != null) {
            lyricsTextView.setText(lyrics);
        } else {
            String artist = artist_name;
            if (artist != null) {
                int i = artist.lastIndexOf(" feat");
                if (i != -1) {
                    artist = artist.substring(0, i);
                }

                LyricsLoader.getInstance(this.getContext()).getLyrics(artist, song_title, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        lyrics = s;
                        if (s.equals("Sorry, We don't have lyrics for this song yet.\n")) {
                            lyricsTextView.setText(R.string.no_lyrics);
                        } else {
                            lyricsTextView.setText(s);
                            poweredbyTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        lyricsTextView.setText(R.string.no_lyrics);
                    }
                });

            } else {
                lyricsTextView.setText(R.string.no_lyrics);
            }
        }
    }
}
