package joewu.dmm;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import com.fima.cardsui.views.CardUI;

public class MainActivity extends Activity {

    private CardUI cardsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardsView = (CardUI) findViewById(R.id.main_cards_view);
        cardsView.setSwipeable(true);

        addSampleCards();
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

    private void addSampleCards() {
        cardsView.addCard(new CountdownCard("#ff4444", "Study Permit Expiry", "Dec 30, 2013",
                "Remember to apply for renewal 90 days before the expiry. Consult advisors at SFU International if necessary.",
                true, false));

        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
        cardsView.refresh();
    }
    
}
