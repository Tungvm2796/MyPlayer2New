package samsung.com.myplayer2.Activities;

import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import samsung.com.myplayer2.Class.NavigationHelper;
import samsung.com.myplayer2.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_search:
                NavigationHelper.navigateToSearch(BaseActivity.this);
                return true;
            case R.id.action_equalizer:
                Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                if ((intent.resolveActivity(getPackageManager()) != null)) {
                    startActivityForResult(intent, RESULT_OK);
                } else {
                    // No equalizer found
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
