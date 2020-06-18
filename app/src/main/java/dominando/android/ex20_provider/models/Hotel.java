package dominando.android.ex20_provider.models;

import java.io.Serializable;

public class Hotel implements Serializable {
    public String nome;
    public String endereco;
    public float estrelas;
    public long id;

    public Hotel(String nome, String endereco, float estrelas){
        this.nome = nome;
        this.endereco = endereco;
        this.estrelas = estrelas;
    }

    public Hotel(long id, String nome, String endereco, float estrelas){
        this.id =id;
        this.nome = nome;
        this.endereco = endereco;
        this.estrelas = estrelas;
    }

    @Override public String toString(){
        return String.valueOf(id) +" "+ nome ;
    }
}
