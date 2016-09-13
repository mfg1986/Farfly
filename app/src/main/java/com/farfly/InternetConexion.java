package com.farfly;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class InternetConexion {
    private Context contexto;

    public InternetConexion(Context contexto){
        this.contexto = contexto;
    }

    //Chequea los posibles proveedores de internet
    public boolean estaConectadoInternet(){
        //Obtenemos el administrados de conectividad
        ConnectivityManager conexion = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Si hay administrador de conectividad
        if (conexion != null){
            //Creamos una matriz con la información de todas la redes obtenidas a través del administrador de conectividad
            NetworkInfo[] info = conexion.getAllNetworkInfo();
            if (info != null)//Si hay alguna red obtenida
                for (NetworkInfo anInfo : info)//Recorremos todas las redes y vemos si el dispositivo esta conectado a alguna de ellas
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        //Si encontramos alguna red en estado conectado devolvemos true
                        return true;
                    }

        }
        //Devolvemos false en caso de que el dispositivo no disponga de conexión a internet y true en caso contrario
        return false;
    }


}

