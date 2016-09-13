package com.farfly;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.Toast;


public class LugaresProvider extends ContentProvider {


    //Definición del CONTENT_URI
    private static final String uri = "content://com.farfly/Lugares";
    public static Uri CONTENT_URI = Uri.parse(uri);

    //Campos necesarios para el UriMatcher
    private static final int LUGARES = 1;
    private static final int LUGARES_ID = 2;
    private static final UriMatcher uriMatcher;

    //Clase interna para declarar los componentes de columna
    public static final class Lugares implements BaseColumns {
        private Lugares() {
        }

        //Nombre de columnas
        public static final String COL_ID="_id";
        public static final String COL_NOMBRE = "nombre";
        public static final String COL_TIPO = "tipo";
        public static final String COL_LOGO = "logo";
        public static final String COL_FOTO = "foto";
        public static final String COL_LATITUD = "latitud";
        public static final String COL_LONGITUD = "longitud";
        public static final String COL_VALORACION="valoracion";
        public static final String COL_DESCRIPCION = "descripcion";
    }
    //Variables de la base de datos
    private LugaresSQLHelper dbh;
    private static final String DB_NAME = "LugaresDB";
    private static final int DB_VERSION = 1;
    private static final String TABLA_LUGARES = "Lugares";

    //Inicializamos el UriMatcher
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.farfly", "Lugares", LUGARES);
        uriMatcher.addURI("com.farfly", "Lugares/#", LUGARES_ID);

    }

    @Override
    public boolean onCreate() {
        //Instanciamos el Helper de la base de datos indicandole el nombre de la base de datos y la version
        dbh = new LugaresSQLHelper(getContext(), DB_NAME, null, DB_VERSION);
        return true;
    }
//Método para consultar uno o varios registros en la base de datos
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Cosntruimos el where en función de la uri que nos dan como argumento
        String where=ConstruirWhere(uri);
        //Instanciamos la base de datos
        SQLiteDatabase db = dbh.getWritableDatabase();
        //Devolvemos el cursor resultado de la consulta
        return db.query(TABLA_LUGARES, projection, where, selectionArgs, null, null, sortOrder);
    }

//Método para insertar uno o varios registros en la base de datos
    @Override
    public Uri insert(Uri uri, ContentValues values){
        Uri newUri=null;
        long regId;
        //Instanciamos la base de datos
        SQLiteDatabase db = dbh.getWritableDatabase();
        //Insertamos en la Tabla "Lugares" los valores pasados como argumento obteniendo id del registro en la base de datos
        regId = db.insert(TABLA_LUGARES, "", values);
        if (regId > 0) {//Si el registro se realizó correctamente
           //Creamos la Uri del nuevo registro que devemos devolver
            newUri = ContentUris.withAppendedId(CONTENT_URI, regId);
        }else{//Si el registro no se realizó correctamente
            //Mostramos un aviso
            Toast.makeText(getContext(),getContext().getString(R.string.error_insert),Toast.LENGTH_LONG).show();
        }
        //Devolvemos la uri del nuevo registro
        return  newUri;
    }
//Método para actualizar uno o varios registros de la base de datos
    @Override
    public int update(Uri uri, ContentValues values,String selection, String[] selectionArgs){

        //Cosntruimos el where en función de la uri que nos dan como argumento
        String where=ConstruirWhere(uri);
        //Instanciamos la base de datos
        SQLiteDatabase db=dbh.getWritableDatabase();
        //Actualizamos la base de datos en el lugar indicado por el where, con los valores pasados como argumento
        int cont=db.update(TABLA_LUGARES,values,where,selectionArgs);

        if(cont>0){//el lugar se ha modificado correctamente, devolvemos el numero de registros actualizados
            return cont;
        }else{//el lugar no se ha modificado correctamente, devolvemos -1
            return -1;
        }
    }
//Método para eliminar uno o varios registros de la base de datos
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        int cont;
        //Cosntruimos el where en función de la uri que nos dan como argumento
        String where=ConstruirWhere(uri);
        //Instanciamos la base de datos
        SQLiteDatabase db=dbh.getWritableDatabase();
        //Eliminamos de la base de datos el lugar/lugares
        cont=db.delete(TABLA_LUGARES,where,selectionArgs);
       //Devolvemos el número de registros eliminados
        return cont;
    }

//Método que devuelve el tipo MIME que se aplicará a los objetos de respuesta.
    @Override
    public String getType(Uri uri){
        int match=uriMatcher.match(uri);
        switch (match){
            //Para varios Lugares
            case LUGARES:
                return "vnd.android.cursor.dir/vnd.farfly.Lugares";
            //Para un lugar en concreto
            case LUGARES_ID:
                return "vnd.android.cursor.item/vnd.farfly.Lugares";
            default:
                return null;

        }
    }
//Método para construir el "WHERE" de la consulta
    public String ConstruirWhere(Uri uri){
        String where = null;
        //Si la Uri es del tipo 2 estamos accediendo a un Lugar concreto
        if (uriMatcher.match(uri) == LUGARES_ID) {
            where="_id="+uri.getLastPathSegment();
        }

        return where;
    }





}
