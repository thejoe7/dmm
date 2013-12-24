package joewu.dmm.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import joewu.dmm.adapters.ListWidgetAdapter;

/**
 * Created by joewu on 12/23/13.
 */
public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListWidgetAdapter(this.getApplicationContext(), intent);
    }
}
