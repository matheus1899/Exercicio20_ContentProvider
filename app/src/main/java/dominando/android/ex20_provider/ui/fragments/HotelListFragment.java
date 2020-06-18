package dominando.android.ex20_provider.ui.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import java.util.ArrayList;
import java.util.List;
import dominando.android.ex20_provider.models.Hotel;
import dominando.android.ex20_provider.providers.HotelProvider;
import dominando.android.ex20_provider.ui.adapters.HotelCursorAdapter;
import dominando.android.ex20_provider.R;
import static dominando.android.ex20_provider.data.HotelSQLHelper.COLUNA_ENDERCO;
import static dominando.android.ex20_provider.data.HotelSQLHelper.COLUNA_ESTRELAS;
import static dominando.android.ex20_provider.data.HotelSQLHelper.COLUNA_ID;
import static dominando.android.ex20_provider.data.HotelSQLHelper.COLUNA_NOME;

public class HotelListFragment extends Fragment implements
        ActionMode.Callback,
        AdapterView.OnItemLongClickListener,
        LoaderCallbacks<Cursor> {

    HotelCursorAdapter adapter;
    String mTextoBusca;
    ListView listView;
    ActionMode actionMode;

    @Override public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Ao girarmos a tela do smartphone
        // não perdemos os dados inseridos
        //-------------------------------------------------------
        // Importante lembrar que a View
        // será recriada de qualquer forma
        setRetainInstance(true);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        adapter = new HotelCursorAdapter(getActivity(), null);
        View v = inflater.inflate(R.layout.fragment_list_hoteis, parent, false);
        listView = v.findViewById(R.id.listHoteis);
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(listView, view, position, id);
            }
        });
        listView.setAdapter(adapter);
        setRetainInstance(true);
        return v;
    }
    public void limparBusca() {
        buscar(null);
    }
    public void buscar(String s){
        try{
            if(s == null || s.length() <= 0){
                if(s == null && mTextoBusca == null){
                    Log.e("TAG", "buscar: s && mTextoBusca are null");
                    return;
                }
                getLoaderManager().restartLoader(0, null, this);
                return;
            }
            mTextoBusca = TextUtils.isEmpty(s) ? null : s;
            Bundle b = new Bundle();
            b.putString("TEXTO_PESQUISA", mTextoBusca);
            getLoaderManager().restartLoader(0, b, this);
        }catch (NullPointerException ex){
            Log.e("TAG", "buscar: "+ex.getMessage());
            getLoaderManager().restartLoader(0, null, this);
        }
    }
    public void onListItemClick(ListView l, View v, int position, long id){
        Log.i("PASSOU", "onListItemClick: HotelListFragment");

        if(actionMode == null){
            Activity act = getActivity();
            if(act instanceof AoClicarNoHotel){
                Cursor cursor = (Cursor)l.getItemAtPosition(position);
                Hotel hotel = hotelFromCursor(cursor);
                AoClicarNoHotel listener = (AoClicarNoHotel)act;
                listener.clicouNoHotel(hotel);
            }
        }else{

            int itensMarcados = atualizarItensMarcados(listView, position);
            if(itensMarcados == 0) {
                actionMode.finish();
            }
        }
    }
    //
    @Override public boolean onCreateActionMode(ActionMode actionMode, Menu menu){
        //Substitui o menu padrão pelo menu de exclusão
        getActivity().getMenuInflater().inflate(R.menu.menu_delete_list, menu);
        Log.w("PASSOU", "onCreateActionMode: HotelListFragment");
        return true;
    }
    @Override public boolean onPrepareActionMode(ActionMode actionMode, Menu menu){
        return false;
    }
    @Override public void onDestroyActionMode(ActionMode action) {
        actionMode = null;
        for (int i = 0; i < listView.getCount(); i++) {
            listView.setItemChecked(i, false);
        }
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }
    //
    @Override public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem){
        if(menuItem.getItemId() == R.id.acao_delete){
            SparseBooleanArray selecionados = listView.getCheckedItemPositions();
            List<Hotel> excluidos = new ArrayList<Hotel>();

            for(int i = selecionados.size()-1; i>=0; i--){
                if(selecionados.valueAt(i)){
                    Cursor cursor = (Cursor)listView.getItemAtPosition(selecionados.keyAt(i));
                    Hotel h = hotelFromCursor(cursor);
                    Uri u = HotelProvider.CONTENT_URI.buildUpon().appendPath(String.valueOf(h.id)).build();
                    getContext().getContentResolver().delete(u, null, null);
                    excluidos.add(h);
                }
            }
            limparBusca();
            actionMode.finish();

            Activity activity = getActivity();
            if(activity instanceof AoExcluirHoteis){
                AoExcluirHoteis excluirHoteis = (AoExcluirHoteis)activity;
                excluirHoteis.exclusaoCompleta(excluidos);
            }
            return true;
        }
        return false;
    }
    @Override public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id){
        boolean consumed = (actionMode == null);
        if(consumed){
            AppCompatActivity activity = (AppCompatActivity)getActivity();
            actionMode = activity.startActionMode(this);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setItemChecked(position, true);
            atualizarItensMarcados(listView, position);
        }
        return consumed;
    }
    //
    @Override public Loader onCreateLoader(int id, Bundle args){
        CursorLoader cursor = null;
        try {
            if (args.containsKey("TEXTO_PESQUISA")) {
                String search = args.getString("TEXTO_PESQUISA");
                if (search.length() <= 0) {
                    cursor = getCursorLoader(HotelProvider.CONTENT_URI);
                } else {
                    cursor = getCursorLoader(HotelProvider.CONTENT_URI, COLUNA_NOME + "= ?", new String[]{search});
                }
            } else {
                cursor = getCursorLoader(HotelProvider.CONTENT_URI);
            }
        }
        catch(NullPointerException ex){
            Log.e("TAG", "onCreateLoader: "+ex.getMessage() );
            cursor = getCursorLoader(HotelProvider.CONTENT_URI);
        }
        catch(Exception ex){
            Log.e("TAG", "onCreateLoader: "+ex.getMessage() );
            cursor = getCursorLoader(HotelProvider.CONTENT_URI);
        }finally{
            return cursor;
        }
    }
    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        adapter.swapCursor(data);
    }
    @Override public void onLoaderReset(Loader<Cursor> cursor){
        adapter.swapCursor(null);
    }
    //
    private CursorLoader getCursorLoader(Uri uri, String selection, String[] selectionArgs){
        return new CursorLoader(getActivity(),
                uri,
                null,
                selection,
                selectionArgs,
                null);
    }
    private CursorLoader getCursorLoader(Uri uri){
        return new CursorLoader(getActivity(),
                uri,
                null,
                null,
                null,
                null);
    }
    public static Hotel hotelFromCursor(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndex(COLUNA_ID));
        String nome = cursor.getString(cursor.getColumnIndex(COLUNA_NOME));
        String endereco = cursor.getString(cursor.getColumnIndex(COLUNA_ENDERCO));
        float estrelas =cursor.getFloat(cursor.getColumnIndex(COLUNA_ESTRELAS));
        return new Hotel(id,nome,endereco,estrelas);
    }
    private int atualizarItensMarcados(ListView list, int position){
        SparseBooleanArray selecionados = list.getCheckedItemPositions();
        list.setItemChecked(position, list.isItemChecked(position));
        int checkedCount = 0;
        for (int i = selecionados.size()-1; i>=0; i--){
            if(selecionados.valueAt(i)){
                checkedCount++;
            }
        }
        String s = getResources().getQuantityString(R.plurals.numeros_selecionados, checkedCount, checkedCount);
        actionMode.setTitle(s);
        return checkedCount;
    }
    public interface AoExcluirHoteis{
        void exclusaoCompleta(List<Hotel> hoteis);
    }
    public interface AoClicarNoHotel{
        void clicouNoHotel(Hotel hotel);
    }
}