package cn.leap.app;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mcxiaoke.popupmenu.PopupMenuCompat;

import cn.leap.app.fragments.HomeFragment;

/**
 * app main
 * Created by longjianlin on 14-8-23.
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private long waitTime = 2000;  //sign out wait time
    private long touchTime = 0;    //sign out touch time

    public MainActivity() {
        super(R.string.main_title);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment(this, getSlidingMenu())).commit();

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

        //add activity
        LeapApplication.getInstance().addActivity(this);
    }


    /**
     * create menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.more) {
            View view = findViewById(id);
            showPopupMenu(view);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * show PopupMenu
     *
     * @param view
     */
    private void showPopupMenu(View view) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "showPopupMenu()");
        }
        final PopupMenuCompat.OnMenuItemClickListener onMenuItemClickListener =
                new PopupMenuCompat.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        Toast.makeText(MainActivity.this, TAG, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                };
//        final PopupMenuCompat.OnDismissListener onDismissListener =
//                new PopupMenuCompat.OnDismissListener() {
//                    @Override
//                    public void onDismiss(PopupMenuCompat PopupMenu) {
//
//                    }
//                };
        PopupMenuCompat popupMenu = new PopupMenuCompat(this, view);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
//        popupMenu.setOnDismissListener(onDismissListener);
        popupMenu.inflate(R.menu.menu);
        popupMenu.show();
    }

    /**
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控返回键
            long currentTime = System.currentTimeMillis();
            if (getSlidingMenu().isMenuShowing()) {
                toggle();
            } else {
                if ((currentTime - touchTime) >= waitTime) {
                    Toast.makeText(MainActivity.this, R.string.sign_out, Toast.LENGTH_SHORT).show();
                    touchTime = currentTime;
                } else {
                    //sign out
                    LeapApplication.getInstance().signOut();
                }
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
