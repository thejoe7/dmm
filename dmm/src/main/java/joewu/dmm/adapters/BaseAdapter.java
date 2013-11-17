package joewu.dmm.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joewu on 11/16/13.
 */
public class BaseAdapter<T> extends ArrayAdapter<T> {
    protected ArrayList<T> objects = new ArrayList<T>();
    protected Context context;

    public BaseAdapter(Context context, int cellViewResourceId, List<T> objects) {
        super(context, cellViewResourceId, objects);
        if (objects != null) this.objects.addAll(objects);
        this.context = context;
    }

    public void addObjects(List<T> objects) {
        boolean modified = false;
        for (int i = 0; i < objects.size(); ++i) {
            // filter duplicates
            if (!this.objects.contains(objects.get(i))) {
                this.objects.add(objects.get(i));
                modified = true;
            }
        }
        if (modified) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void add(T object) {
        // filter duplicates
        if (!this.objects.contains(object)) {
            this.objects.add(object);
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(T object) {
        this.objects.remove(object);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.objects.size();
    }

    @Override
    public T getItem(int position) {
        return this.objects.get(position);
    }

    @Override
    public void clear() {
        this.objects.clear();
    }
}
