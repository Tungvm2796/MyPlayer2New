package samsung.com.myplayer2.Class;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import samsung.com.myplayer2.Activities.MainActivity;
import samsung.com.myplayer2.Fragments.AlbumSongFragment;
import samsung.com.myplayer2.Fragments.ArtistSongFragment;
import samsung.com.myplayer2.Fragments.SearchFragment;
import samsung.com.myplayer2.R;

public class NavigationHelper {

    public static void navigateToSongAlbum(Activity context, long albumID, String albumName) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        transaction.setCustomAnimations(R.anim.activity_fade_in,
                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
        fragment = AlbumSongFragment.getFragment(albumID, albumName);

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();
    }

    public static void navigateToSongArtist(Activity context, String artist_name) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        transaction.setCustomAnimations(R.anim.activity_fade_in,
                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
        fragment = ArtistSongFragment.getFragment(artist_name);

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();
    }

    public static void navigateToSearch(Activity context) {
        //final Intent intent = new Intent(context, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //intent.setAction(Constants.NAVIGATE_SEARCH);
        //context.startActivity(intent);
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment = new SearchFragment();

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();
    }

    public static void goToLyrics(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_LYRICS);
        context.startActivity(intent);
    }

    public static void goToArtist(Context context, String artist) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST, artist);
        context.startActivity(intent);
    }

    public static void goToAlbum(Context context, long albumId, String albumName) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ALBUM);
        intent.putExtra(Constants.ALBUM_ID, albumId);
        intent.putExtra(Constants.ALBUM, albumName);
        context.startActivity(intent);
    }
}
