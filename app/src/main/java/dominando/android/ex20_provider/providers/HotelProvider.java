package dominando.android.ex20_provider.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import dominando.android.ex20_provider.data.HotelSQLHelper;
import static dominando.android.ex20_provider.data.HotelSQLHelper.COLUNA_ID;
import static dominando.android.ex20_provider.data.HotelSQLHelper.COLUNA_NOME;
import static dominando.android.ex20_provider.data.HotelSQLHelper.TABELA_HOTEL;

public class HotelProvider extends ContentProvider {
    private static final String AUTHORITY="dominando.android.ex22_provider.providers";
    private static final int TIPO_GERAL = 1;
    private static final int TIPO_HOTEL_ESPECIFICO = 2;
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY).buildUpon()
            .appendPath(TABELA_HOTEL).build();

    private HotelSQLHelper mHelper;
    private static final UriMatcher sUriMatcher;

    static{
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TABELA_HOTEL, TIPO_GERAL);
        sUriMatcher.addURI(AUTHORITY,TABELA_HOTEL+"/#",TIPO_HOTEL_ESPECIFICO);
    }
    @Override public boolean onCreate(){
        mHelper = new HotelSQLHelper(getContext());
        return true;
    }
    @Override public String getType(Uri uri){
        return null;
    }
    //
    @Override public Uri insert(Uri uri, ContentValues cv){
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Uri res;

        switch (uriType){
            case TIPO_GERAL:
                long id = db.insertWithOnConflict(
                        TABELA_HOTEL,
                        null,
                        cv,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if(id > -1){
                    res = ContentUris.withAppendedId(CONTENT_URI, id);
                }else{
                    throw new SQLiteException("Erro ao inserir: "+uri);
                }
                break;
            default:
                throw new IllegalArgumentException("URI não suportada: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return res;
    }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs){
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        int linhasDeletadas;

        switch (uriType){
            case TIPO_GERAL:
                linhasDeletadas = db.delete(
                        TABELA_HOTEL,
                        COLUNA_ID +"= ?",
                        selectionArgs);
                break;
            case TIPO_HOTEL_ESPECIFICO:
                linhasDeletadas = db.delete(
                        TABELA_HOTEL,
                        COLUNA_ID+"= ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new IllegalArgumentException("URI desconhecida: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return linhasDeletadas;
    }
    @Override public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs){
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int linhasAlteradas;

        switch (uriType){
            case TIPO_HOTEL_ESPECIFICO:
                linhasAlteradas = db.update(
                        TABELA_HOTEL,
                        values,
                        COLUNA_ID+"= ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new IllegalArgumentException("URI desconhecida: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return linhasAlteradas;
    }
    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor c;
        switch (uriType){
            case TIPO_GERAL:
                c = db.query(
                        TABELA_HOTEL,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TIPO_HOTEL_ESPECIFICO:
                c = db.query(
                        TABELA_HOTEL,
                        projection,
                        COLUNA_ID + "= ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI desconhecida: "+uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
}
