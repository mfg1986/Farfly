package com.farfly;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<String> {

    private ArrayList<String>tipos;
    private Context context;
    private Soporte soporte;

//Constructor de la clase
    public SpinnerAdapter(Context context, int resourceId, ArrayList<String> objects,Activity activity) {
        super(context, resourceId, objects);
        this.tipos = objects;
        this.context = context;
        this.soporte= new Soporte(context,activity);

    }
//Metodo para obtener el spinner desplegado
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }
//Metodo para obtener la vista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Devolvemos la vista personalizada
        return getCustomView(position, parent);
    }

//Metodo para obtener la vista personalizada indicada en la posición que se pasa como argumento
    public View getCustomView(int position, ViewGroup parent) {
        //Convertimos en vista el xml del item de nuestro spinner
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_spinner, parent, false);

        //Obtenemos el tipo de sitio
        String categoria = tipos.get(position);
        //Obtenemos el elemento del layout que se corresponde con el tipo
        TextView categoria_layout = (TextView) row.findViewById(R.id.textview_spinner_custom);

        //Asignamos al elemento del layout el tipo
        categoria_layout.setText(categoria);

        //Ponemos el icono del tipo de sitio
        ImageView icono = (ImageView) row.findViewById(R.id.icon);
        //Obtenemos la orientacion de la pantalla
        int orientation= context.getResources().getConfiguration().orientation;
        if(orientation==1){//orientacion Portrait-->Icono tamaño normal
            icono.setImageResource(soporte.SelectorDeLogo(categoria, "normal"));
        }
        if(orientation==2){//Orientacion landscape-->Icono tamaño pequeño
            icono.setImageResource(soporte.SelectorDeLogo(categoria, "small"));
        }
        //Devolvemos la vista
        return row;
    }


}


