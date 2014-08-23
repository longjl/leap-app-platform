package cn.leap.app;

import android.content.IntentFilter;
import android.os.Bundle;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.leap.app.fragments.HomeFragment;
import cn.leap.app.fragments.LeftMenuFragment;

/**
 * Created by longjianlin on 14-8-23.
 */
public class MainActivity extends BaseActivity {

    public MainActivity() {
        super(R.string.main_title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment(this,getSlidingMenu())).commit();

        //open listener
        getSlidingMenu().setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

        //closed listener
        getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
    }
}
