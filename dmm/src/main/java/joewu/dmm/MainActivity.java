package joewu.dmm;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onMenuItemSelected(int id, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                // Do something for create
                return true;
            case R.id.action_settings:
                showSettingsActivity();
                return true;
            default:
                return super.onMenuItemSelected(id, item);
        }
    }

    private void showSettingsActivity() {
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(intent);
    }
    
}
