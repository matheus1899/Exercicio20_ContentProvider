package dominando.android.ex20_provider.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;
import dominando.android.ex20_provider.data.HotelSQLHelper;
import dominando.android.ex20_provider.providers.HotelProvider;
import dominando.android.ex20_provider.ui.fragments.GenericDialogFragment;
import dominando.android.ex20_provider.models.Hotel;
import dominando.android.ex20_provider.ui.fragments.HotelDetalheFragment;
import dominando.android.ex20_provider.ui.fragments.HotelDialogFragment;
import dominando.android.ex20_provider.ui.fragments.HotelListFragment;
import dominando.android.ex20_provider.R;

public class HotelActivity extends AppCompatActivity implements
        HotelListFragment.AoClicarNoHotel,
        HotelDialogFragment.AoSalvarHotel,
        GenericDialogFragment.AoClicarNoDialog,
        SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener,
        HotelDetalheFragment.AoEditarHotel,
        HotelListFragment.AoExcluirHoteis {

    //OnQueryTextListener    = Vai permitir tratar o campo de busca da ActionBar
    //OnActionExpandListener = É utilizada para sabermos qaundo uma ação da
    //                         Actionbar expandiu ou quando voltou ao normal
    //                         os seus dois metodo retornam um bool indicando
    //                         se a ação de expandir e de voltar ao normal deve ou
    //                         não ser executada
    private FragmentManager fm;
    private HotelListFragment listFragment;
    private long idSelecionado;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        fm = getSupportFragmentManager();
        listFragment = (HotelListFragment)fm.findFragmentById(R.id.fragmentList);
        Log.i("PASSOU", "onCreate: HotelActivity");
    }
    @Override public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_hotel, menu);
        //Metodos obsoletos...
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.hint_buscar));
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        return true;
    }
    //// Para chamar a partir de um Fragment
    //// O valor 1 é o requestCode
    //dialog.setTargetFragment(this, 1);
    //dialog.abrir(fragmentManager);
    //
    ////onClick da classe GenericDialogFragment ficaria assim...
    // @Override public void onClick(DialogInterface dialog, int which){
    //      Intent i = new Intent();
    //      i.putExtra("which", which);
    //      //Chamando o onActivityResult do targetFragment
    //      getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    //}
    //
    // @Override public void onActivityResult(int requestCode, int resultCode, Intent data){
    //      super.onActivityResult(requestode, resultCode, data);
    //      int which = data.getExtra("which", -1);
    //}
    @Override public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_info:
                GenericDialogFragment dialog =
                        GenericDialogFragment.novoDialog(0,
                        R.string.sobre_titulo,
                        R.string.sobre_mensagem,
                        new int[]{android.R.string.ok, R.string.sobre_botao_site});
                dialog.abrir(fm);
                break;
            case R.id.action_new:
                HotelDialogFragment hotelDialog = HotelDialogFragment.novaInstancia(null);
                hotelDialog.abrir(fm);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override public void aoClicar(int id, int botao){
        if(botao == DialogInterface.BUTTON_NEGATIVE){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://nglauber.blogspot.com"));
            startActivity(i);
        }
    }
    @Override public void salvouHotel(Hotel hotel){
        ContentValues cv = new ContentValues();
        //cv.put(HotelSQLHelper.COLUNA_ID, hotel.id);
        cv.put(HotelSQLHelper.COLUNA_ENDERCO, hotel.endereco);
        cv.put(HotelSQLHelper.COLUNA_ESTRELAS, hotel.estrelas);
        cv.put(HotelSQLHelper.COLUNA_NOME, hotel.nome);
        getContentResolver().insert(HotelProvider.CONTENT_URI, cv);
        listFragment.limparBusca();
        if(isTablet()){
            clicouNoHotel(hotel);
        }
    }
    public void clicouNoHotel(Hotel hotel){
        idSelecionado = hotel.id;
        if(isTablet()){
            HotelDetalheFragment fragment = HotelDetalheFragment.novaInstancia(hotel);

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.detail, fragment, HotelDetalheFragment.TAG_DETALHE);
            ft.commit();
        }
        else{
            Intent i = new Intent(this, DetalheActivity.class);
            i.putExtra(DetalheActivity.EXTRA_HOTEL, hotel);
            startActivityForResult(i,0);
        }
    }
    private boolean isTablet(){
        // verifica se o layout carregado
        // contém o layout de smartphone ou não

        //O Android carrega os layout da pasta large
        // quando a tela tem pelo menos 640 dp x 480 dp
        //return findViewById(R.id.detail) != null;

        // Outra maneira é criar um arquivo de valores
        // bools.xml na pasta values, adicionando a seguinte propriedade
        //<bool name="tablet">false</bool>
        //Depois crie um diretorio chamado values-large
        //copie o arquivo bools para a nova pasta
        // e altere o valor de "table" para true
        //use getResources().getBoolean(R.bool.table);
        return getResources().getBoolean(R.bool.tablet);
    }
    //Disparado quando clicamos no botão de Submit(Para efetuar a pesquisa).
    @Override public boolean onQueryTextSubmit(String string){
        return true;
    }
    //É chamado quando o texto é alterado
    @Override public boolean onQueryTextChange(String string){
        listFragment.buscar(string);
        return true;
    }
    @Override public boolean onMenuItemActionExpand(MenuItem item){
        return true; //Para expandir a View...
    }
    @Override public boolean onMenuItemActionCollapse(MenuItem item){
        listFragment.limparBusca();
        return true; // Para voltar ao normal
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            listFragment.limparBusca();
        }
    }
    @Override public void aoEditarHotel(Hotel hotel){
        HotelDialogFragment dialog = HotelDialogFragment.novaInstancia(hotel);
        dialog.abrir(getSupportFragmentManager());
    }
    @Override public void exclusaoCompleta(List<Hotel> excluidos){
        HotelDetalheFragment f = (HotelDetalheFragment)fm.findFragmentByTag(HotelDetalheFragment.TAG_DETALHE);
        if(f != null){
            boolean encontrou = false;
            for (Hotel h : excluidos){
                if (h.id == idSelecionado){
                    encontrou = true;
                    break;
                }
            }
            if(encontrou){
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(f).commit();
            }
        }
    }
}
