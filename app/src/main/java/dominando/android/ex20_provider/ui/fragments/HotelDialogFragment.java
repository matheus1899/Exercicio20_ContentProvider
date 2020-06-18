package dominando.android.ex20_provider.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import dominando.android.ex20_provider.models.Hotel;
import dominando.android.ex20_provider.R;

public class HotelDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private static final String DIALOG_TAG = "edtDialog";
    private static final String EXTRA_HOTEL = "hotel";

    private EditText edtNome;
    private EditText edtEndereco;
    private RatingBar rtbEstrelasHotel;
    private Hotel hotel;

    public static HotelDialogFragment novaInstancia(Hotel hotel){
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_HOTEL, hotel);
        HotelDialogFragment dialog = new HotelDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }
    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hotel = (Hotel)getArguments().getSerializable(EXTRA_HOTEL);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){

        View layout = inflater.inflate(R.layout.fragment_dialog_hotel, parent, false);
        edtNome = layout.findViewById(R.id.editTextNome);
        edtEndereco = layout.findViewById(R.id.editTextEndereco);
        rtbEstrelasHotel = layout.findViewById(R.id.ratingBarEstrelas);

        edtNome.requestFocus();
        edtEndereco.setOnEditorActionListener(this);

        if (hotel != null){
            edtNome.setText(hotel.nome);
            edtEndereco.setText(hotel.endereco);
            rtbEstrelasHotel.setRating(hotel.estrelas);
        }

        //Exibe o telhado ao abrir o Dialog
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(R.string.acao_novo);

        return layout;
    }

    //Tratando o botão de Ação do teclado...
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(EditorInfo.IME_ACTION_DONE == actionId){
            Activity activity = getActivity();
            //Activity implementa interface?
            if(activity instanceof AoSalvarHotel){
                if (hotel == null){
                    hotel = new Hotel(edtNome.getText().toString(),
                            edtEndereco.getText().toString(),
                            rtbEstrelasHotel.getRating());
                }else{
                    hotel.nome = edtNome.getText().toString();
                    hotel.endereco = edtEndereco.getText().toString();
                    hotel.estrelas = rtbEstrelasHotel.getRating();
                }

                AoSalvarHotel listener = (AoSalvarHotel) activity;
                listener.salvouHotel(hotel);

                //Fechando dialog
                dismiss();
                return true;
            }
        }
        return false;
    }

    public void abrir(FragmentManager fm){
        if(fm.findFragmentByTag(DIALOG_TAG) == null){
            show(fm, DIALOG_TAG);
        }
    }

    public interface AoSalvarHotel{
        void salvouHotel(Hotel hotel);
    }
}
