package com.farfly;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

public class Idioma {
    Context contexto;


    public Idioma(Context contexto){
        this.contexto=contexto;
    }

    public void cargarLocale() {
        //Indicamos el nombre de la variable de las preferencias compartidas que vamos a leer
        String PrefIdioma = "idioma";
        //Llamamos a la función para leer las preferencias y obtener el idioma que esta configurado
        String idioma = leePreferencias (PrefIdioma,contexto);
        //Cambiamos el idioma poniendo el que leimos de las preferencias compartidas
        cambiarLocale(idioma);
    }
    public void salvarLocale(String locale) {
        //Indicamos la clave de la variable que vamos a cambiar en las preferencias compartidas
        String PrefIdioma = "idioma";
        //Guardamos el idioma introducido como variable locale
        salvaPreferencias(PrefIdioma, locale, contexto);

    }
    public void cambiarLocale(String locale) {
        //Si esta vacio, queda el idioma por defecto del dispositivo y si no entramos en el if
        if (!locale.equals("")) {
            //Configuramos el idioma leido desde SharedPreferences como idioma de la app
            Locale miLocale = new Locale(locale);
            Locale.setDefault(miLocale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = miLocale;
            contexto.getResources().updateConfiguration(config, contexto.getResources().getDisplayMetrics());
        }
    }
/*****************************PREFERENCIASSS*****************************/
    /**************************************************************************/
    public static void salvaPreferencias(String key, String value, Context context) {
        //Obtenemos las preferencias compartidas por defecto
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //LLamaos al editor de preferencias
        SharedPreferences.Editor editor = prefs.edit();
        //Introducimos la clave de la variable a modificar con su valor modificado
        editor.putString(key, value);
        //Ejecutamos el editor
        editor.commit();
    }
    public static String leePreferencias(String key, Context context) {
        //Obtnemos las preferencias compartidas por defecto
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //Devolvemos la variable introducida como key
        return preferences.getString(key, "");
    }

}





