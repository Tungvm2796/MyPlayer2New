package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import samsung.com.myplayer2.Fragments.AlbumFragment;
import samsung.com.myplayer2.Fragments.ArtistFragment;
import samsung.com.myplayer2.Fragments.OnlineFragment;
import samsung.com.myplayer2.Fragments.PlaylistFragment;
import samsung.com.myplayer2.Fragments.SongListFragment;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 1/24/2018.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter{
    Context mContext;
    public FragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position){

            case 0:

                frag = new SongListFragment();

                break;

            case 1:

                frag = new AlbumFragment();

                break;

            case 2:

                frag = new ArtistFragment();

                break;

            case 3:

                frag = new PlaylistFragment();

                break;

            case 4:

                frag = new OnlineFragment();

                break;

        }
        return frag;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position){
        String title = "";
        switch (position){
            case 0:
                title = mContext.getResources().getString(R.string.frag1);
                break;
            case 1:
                title = mContext.getResources().getString(R.string.frag2);
                break;
            case 2:
                title = mContext.getResources().getString(R.string.frag3);
                break;
            case 3:
                title = mContext.getResources().getString(R.string.frag4);
                break;
            case 4:
                title = mContext.getResources().getString(R.string.frag5);
                break;
        }
        return title;
    }

    
}
