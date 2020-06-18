package dominando.android.ex20_provider.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import dominando.android.ex20_provider.models.Hotel;
import dominando.android.ex20_provider.R;

public class HotelDetalheFragment extends Fragment {

    public static final String EXTRA_HOTEL = "hotel";
    public static final String TAG_DETALHE = "tagDetalhe";
    TextView nome;
    TextView endereco;
    RatingBar stars;
    Hotel h;
    ShareActionProvider shareActionProvider;

    // Por restrição da plataforma todos os Fragmentos
    // deverão ter apenas um construtor sem parametros,
    // se fazendo necessario o uso de Factory Methods
    public static HotelDetalheFragment novaInstancia(Hotel hotel){
        Bundle parametro = new Bundle();
        parametro.putSerializable(EXTRA_HOTEL, hotel);
        HotelDetalheFragment fragment = new HotelDetalheFragment();
        fragment.setArguments(parametro);
        return fragment;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.acao_editar){
            Activity act = getActivity();
            if (act instanceof AoEditarHotel){
                AoEditarHotel editarHotel = (AoEditarHotel)act;
                editarHotel.aoEditarHotel(h);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        h = (Hotel)getArguments().getSerializable(EXTRA_HOTEL);
        setHasOptionsMenu(true);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View layout = inflater.inflate(R.layout.fragment_detail_hotel, container, false);
        nome = layout.findViewById(R.id.txtNome);
        endereco = layout.findViewById(R.id.txtEndereco);
        stars = layout.findViewById(R.id.rtbEstrelas);

        if(h != null){
            nome.setText(h.nome);
            endereco.setText(h.endereco);
            stars.setRating(h.estrelas);
        }
        return layout;
    }
    //Implementando o SharedProvider
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_hotel_detalhe, menu);
        MenuItem shareItem = menu.findItem(R.id.acao_compartilhar);
        //Pegando o SharedProvider do MenuItem
        shareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);
        //Setando o valores dentro da string
        String texto = getString(R.string.texto_compartilhar, h.nome, h.estrelas);

        Intent i =new Intent(Intent.ACTION_SEND);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, texto);
        shareActionProvider.setShareIntent(i);
    }
    public interface AoEditarHotel{
        void aoEditarHotel(Hotel hotel);
    }
}
