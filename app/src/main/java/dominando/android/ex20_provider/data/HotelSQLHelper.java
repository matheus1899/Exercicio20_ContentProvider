package dominando.android.ex20_provider.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HotelSQLHelper extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "dbhotel.db";
    private static final int VERSAO_BANCO = 1;

    public static final String TABELA_HOTEL = "hotel";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_NOME = "nome";
    public static final String COLUNA_ENDERCO = "endereco";
    public static final String COLUNA_ESTRELAS = "estrelas";

    public HotelSQLHelper(Context context){
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    //Será chamado caso o Banco de Dados não exista;
    @Override public void onCreate(SQLiteDatabase sqliteDB){
        String sql = "CREATE TABLE "
                + TABELA_HOTEL+" ("
                + COLUNA_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUNA_NOME + " TEXT, "
                + COLUNA_ENDERCO + " TEXT, "
                + COLUNA_ESTRELAS + " REAL)";

        sqliteDB.execSQL(sql);
    }
    //INTEGER - 8, 16, 32, 64 bits
    //REAL - Ponto flutuante 64 bits
    //TEXT - UTF-8, UTF-16BE, UTF-16LE
    //BLOB  - Array de Bytes

    //Será chamado caso o banco atualize
    //OBS: A alteração é em relação aos campos e
    //tabelas do banco e não tem a ver com dados
    //Ou seja, alterar os dados não altera a
    // estrutura do banco
    @Override public void onUpgrade(SQLiteDatabase sqliteDB, int oldVersion, int newVersion){

    }
}
