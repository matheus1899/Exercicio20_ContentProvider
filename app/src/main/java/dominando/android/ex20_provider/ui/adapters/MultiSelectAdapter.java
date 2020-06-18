package dominando.android.ex20_provider.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MultiSelectAdapter<T> extends ArrayAdapter<T> {
    public MultiSelectAdapter(Context context, int textViewResourceId, List<T> objects){
        super(context, textViewResourceId, objects);
    }




    @Override public View getView(int position, View convertView, ViewGroup parent){
        View v = super.getView(position, convertView, parent);
        ListView listView = (ListView)parent;

        int color = listView.isItemChecked(position) ? Color.RED: Color.TRANSPARENT;

        v.setBackgroundColor(color);
        return v;
    }
}
