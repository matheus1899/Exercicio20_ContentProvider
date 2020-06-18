package dominando.android.ex20_provider.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.cursoradapter.widget.CursorAdapter;
import dominando.android.ex20_provider.R;
import static dominando.android.ex20_provider.data.HotelSQLHelper.COLUNA_ESTRELAS;
import static dominando.android.ex20_provider.data.HotelSQLHelper.COLUNA_NOME;

public class HotelCursorAdapter extends CursorAdapter {

    public HotelCursorAdapter(Context ctx, Cursor cursor){
        super(ctx,cursor,0);
    }
    @Override public void bindView(View view, Context context, Cursor cursor){
        TextView txtMessage = view.findViewById(R.id.txt_item);
        RatingBar ratingBar = view.findViewById(R.id.rtb_item);
        String nome = cursor.getString(cursor.getColumnIndex(COLUNA_NOME));
        ratingBar.setRating(cursor.getFloat(cursor.getColumnIndex(COLUNA_ESTRELAS)));
        txtMessage.setText(nome);
        Log.e("TAG", "bindView");
    }

    @Override public View newView(Context ctx, Cursor cursor, ViewGroup parent){
        Log.e("TAG", "newView");
        return LayoutInflater.from(ctx).inflate(R.layout.item_hotel, null);
    }

    @Override public View getView(int pos, View convertView, ViewGroup parent){
        View v = super.getView(pos,convertView, parent);
        ListView l = (ListView)parent;
        int color = l.isItemChecked(pos) ?
                Color.argb(0xFF, 0x31, 0xB6, 0xE7) :
                Color.TRANSPARENT;
        v.setBackgroundColor(color);
        Log.e("TAG", "getView");
        return v;
    }
}
