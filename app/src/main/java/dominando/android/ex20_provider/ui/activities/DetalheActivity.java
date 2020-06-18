package dominando.android.ex20_provider.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import dominando.android.ex20_provider.data.HotelSQLHelper;
import dominando.android.ex20_provider.models.Hotel;
import dominando.android.ex20_provider.providers.HotelProvider;
import dominando.android.ex20_provider.ui.fragments.HotelDetalheFragment;
import dominando.android.ex20_provider.ui.fragments.HotelDialogFragment;
import dominando.android.ex20_provider.R;

public class DetalheActivity extends AppCompatActivity implements
        HotelDetalheFragment.AoEditarHotel,
        HotelDialogFragment.AoSalvarHotel {
    public static final String EXTRA_HOTEL = "hotel";

    @Override protected void onCreate(Bundle savedinstanceState){
        super.onCreate(savedinstanceState);
        setContentView(R.layout.activity_detalhe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        Hotel hotel = (Hotel)i.getSerializableExtra(EXTRA_HOTEL);
        exibirHotelFragment(hotel);
    }

    private void exibirHotelFragment(Hotel hotel){
        HotelDetalheFragment fragment = HotelDetalheFragment.novaInstancia(hotel);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout,fragment, HotelDetalheFragment.TAG_DETALHE);
        ft.commit();
    }

    @Override public void aoEditarHotel(Hotel hotel) {
        HotelDialogFragment editNameDialog = HotelDialogFragment.novaInstancia(hotel);
        editNameDialog.abrir(getSupportFragmentManager());
    }

    @Override public void salvouHotel(Hotel hotel) {
        ContentValues cv = new ContentValues();
        cv.put(HotelSQLHelper.COLUNA_ID, hotel.id);
        cv.put(HotelSQLHelper.COLUNA_ENDERCO, hotel.endereco);
        cv.put(HotelSQLHelper.COLUNA_ESTRELAS, hotel.estrelas);
        cv.put(HotelSQLHelper.COLUNA_NOME, hotel.nome);
        getContentResolver().insert(HotelProvider.CONTENT_URI, cv);
        exibirHotelFragment(hotel);
        // Com isso, quando voltarmos para a outra Activity(HotelActivity)
        // podemos requisitar que a lista seja atualizada pois o metodo
        // o(nActivityResult(int, int, Intent) será chamado
        setResult(RESULT_OK);
    }
}
