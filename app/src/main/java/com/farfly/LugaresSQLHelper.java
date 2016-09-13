package com.farfly;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LugaresSQLHelper extends SQLiteOpenHelper {

    //Constructor de la Clase
    public LugaresSQLHelper(Context context,String nombre,SQLiteDatabase.CursorFactory factory,int version) {
        super(context,nombre,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Si la base de datos tiene permiso solo de lectura
        if(db.isReadOnly()){
            //Activamos el permiso de escritura
            db=getWritableDatabase();
        }
        //Creamos la sentencia SQL para la creación de nuestra base de datos
        String sqlCreate = "CREATE TABLE Lugares (_id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, tipo TEXT,logo INTEGER, foto TEXT,latitud DOUBLE,longitud DOUBLE,valoracion FLOAT,descripcion TEXT)";
        //Ejecutamos la sentencia SQL
        db.execSQL(sqlCreate);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}


