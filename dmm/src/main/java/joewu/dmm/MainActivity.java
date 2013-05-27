package joewu.dmm;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import com.fima.cardsui.views.CardUI;
import com.fima.cardsui.objects.CardStack;

public class MainActivity extends Activity {

    private CardUI cardsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardsView = (CardUI) findViewById(R.id.main_cards_view);
        cardsView.setSwipeable(false);

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
//        CardStack pastStack = new CardStack();
//        pastStack.setTitle("PAST EVENTS");
//        cardsView.addStack(pastStack);
        cardsView.addCard(new CountdownCard("#99cc00", "Flight to China", "Apr 23, 2013", "AC025 @ 11:15am", true, false));
        cardsView.addCardToLastStack(new CountdownCard("#ffbb33", "Flight to Vancouver", "May 4, 2013", "AC026 @ 3:40pm, delayed till 19:00pm", true, false));
        cardsView.addCardToLastStack(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));
//        cardsView.addCard(new CountdownCard("#33b5e5", "Google I/O 2013", "May 15, 2013", "", true, false));

        cardsView.addCard(new CountdownCard("#ffbb33", "Work Term Ends", "Aug 30, 2013", "", true, false));

        cardsView.addCard(new CountdownCard("#ff4444", "Study Permit Expiry", "Dec 30, 2013",
                "Remember to apply for renewal 90 days before the expiry. Consult advisors at SFU International if necessary.",
                true, false));

        cardsView.addCard(new CountdownCard("#aa66cc", "Passport", "Nov 15, 2020", "That's still quite a while from now; but the Chinese Consulate needs to be contacted two years before the expiry in maximum, so that they could perform a background check in China beforehand, which also accelerate the entire process. Ask somebody for his or her related experience, that could help a lot.", true, false));

        cardsView.addCard(new CountdownCard("#99cc00", "App Release", "Aug 27, 2050", "", true, false));
        cardsView.refresh();
    }
    
}
