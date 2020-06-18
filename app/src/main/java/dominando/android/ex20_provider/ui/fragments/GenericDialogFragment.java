package dominando.android.ex20_provider.ui.fragments;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class GenericDialogFragment extends DialogFragment implements OnClickListener {
    private static final String EXTRA_ID ="id";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_TITULO = "title";
    private static final String EXTRA_BOTOES = "butons";
    private static final String DIALOG_TAG = "Simple Dialog";
    private int dialogId;

    public static GenericDialogFragment novoDialog(int id, int title, int message,int[] buttonTexts){
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, id);
        bundle.putInt(EXTRA_TITULO,title);
        bundle.putInt(EXTRA_MESSAGE, message);
        bundle.putIntArray(EXTRA_BOTOES, buttonTexts);

        GenericDialogFragment dialog = new GenericDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState){
        dialogId = getArguments().getInt(EXTRA_ID);
        int titulo = getArguments().getInt(EXTRA_TITULO);
        int mensagem = getArguments().getInt(EXTRA_MESSAGE);
        int[] botoes = getArguments().getIntArray(EXTRA_BOTOES);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensagem);
        if(botoes == null || botoes.length == 0){
            alertDialogBuilder.setPositiveButton("OK", this);
            return alertDialogBuilder.create();
        }

        switch (botoes.length){
            case 3:
                alertDialogBuilder.setNeutralButton(botoes[2], this);
            case 2:
                alertDialogBuilder.setNegativeButton(botoes[1], this);
            case 1:
                alertDialogBuilder.setPositiveButton(botoes[0], this);
        }
        return alertDialogBuilder.create();
    }

    @Override public void onClick(DialogInterface dialog, int which){
        Activity activity = getActivity();
        if(activity instanceof AoClicarNoDialog){
            AoClicarNoDialog listener = (AoClicarNoDialog)activity;
            listener.aoClicar(dialogId, which);
        }
    }
    public void abrir(FragmentManager supportFragmentManager){
        Fragment dialogFragment = supportFragmentManager.findFragmentByTag(DIALOG_TAG);
        if(dialogFragment == null){
            show(supportFragmentManager, DIALOG_TAG);
        }
    }

    public interface AoClicarNoDialog{
        void aoClicar(int id, int botao);
    }
}
