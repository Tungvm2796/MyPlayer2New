package samsung.com.myplayer2.Class;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.View;
import android.widget.Toast;

import samsung.com.myplayer2.Activities.MainActivity;
import samsung.com.myplayer2.Fragments.AlbumSongFragment;
import samsung.com.myplayer2.Fragments.ArtistSongFragment;
import samsung.com.myplayer2.Fragments.GenresSongFragment;
import samsung.com.myplayer2.Fragments.SearchFragment;
import samsung.com.myplayer2.R;

public class NavigationHelper {

    //Notice: API >= 21 (Android 5.0 Lollipop or higher) can use

    public static void navigateToSongAlbum(Activity context, long albumID, String albumName, View view) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

//        transaction.setCustomAnimations(R.anim.activity_fade_in,
//                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
        fragment = AlbumSongFragment.getFragment(albumID, albumName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new DetailsTransition());
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        if(view != null) {
            transaction.addSharedElement(view,  "transition_album_art");
        }
        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();
    }

    public static void navigateToSongArtist(Activity context, String artist_name, View view) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

//        transaction.setCustomAnimations(R.anim.activity_fade_in,
//                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
        fragment = ArtistSongFragment.getFragment(artist_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new DetailsTransition());
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        if(view != null) {
            transaction.addSharedElement(view,  "transition_artist_art");
        }
        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment); //replace() can show transition effect but not nice, so just use fade effect
        transaction.addToBackStack(null).commit();
    }

    public static void navigateToSongGenres(Activity context, String genres_name, View view) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

//        transaction.setCustomAnimations(R.anim.activity_fade_in,
//                R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
        fragment = GenresSongFragment.getFragment(genres_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new DetailsTransition());
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        if(view != null) {
            transaction.addSharedElement(view,  "transition_genres_art");
        }
        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment); //replace() can show transition effect but not nice, so just use fade effect
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

    public static void navigateToEqualizer(Activity context, int AudioSessionId) {
        try {
            // The google MusicFX apps need to be started using startActivityForResult
            context.startActivityForResult(createEffectsIntent(AudioSessionId), 666);
        } catch (final ActivityNotFoundException notFound) {
            Toast.makeText(context, "Equalizer not found", Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent createEffectsIntent(int AusioSessionId) {
        final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, AusioSessionId);
        return effects;
    }
}
