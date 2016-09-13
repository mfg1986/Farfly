package com.farfly;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;


public class Layout {
    //Variables de la clase-->Son los componentes de los layouts de las Actividades "Editar Lugar" y "Mostrar Lugar"
    private TextView nombre_mostrar, tipo_mostrar,latitud,longitud,descripcion_mostrar;
    private EditText nombre_editar,descripcion_editar;
    private ImageView foto_mostrar,icono;
    private ImageButton foto_editar;
    private ArrayList<String>customList;
    private RatingBar valoracion;

//Constructor de la clase para cuando estamos en la actividad Mostrar Lugar
    public Layout(TextView nombre_mostrar,TextView tipo_mostrar,ImageView icono,ImageView foto_mostrar,TextView latitud,TextView longitud,RatingBar valoracion, TextView descripcion_mostrar){
        this.nombre_mostrar=nombre_mostrar;
        this.tipo_mostrar=tipo_mostrar;
        this.icono=icono;
        this.foto_mostrar=foto_mostrar;
        this.latitud=latitud;
        this.longitud=longitud;
        this.valoracion=valoracion;
        this.descripcion_mostrar=descripcion_mostrar;
    }

//Constructor de la clase para cuando estamos en la actividad Editar Lugar
    public Layout(EditText nombre_editar,ArrayList<String> customList,ImageButton foto_editar,TextView latitud,TextView longitud,RatingBar valoracion, EditText descripcion_editar){
        this.nombre_editar=nombre_editar;
        this.customList=customList;
        this.foto_editar=foto_editar;
        this.latitud=latitud;
        this.longitud=longitud;
        this.valoracion=valoracion;
        this.descripcion_editar=descripcion_editar;
    }

/*******************GETTER Y SETTERS NECESARIOS DE LAS VARIABLES DE LA CLASE*************************/
/***************************************************************************************************/
    public RatingBar getValoracion() {
        return valoracion;
    }

    public void setValoracion(RatingBar valoracion) {
        this.valoracion = valoracion;
    }

    public TextView getNombre_mostrar() {
        return nombre_mostrar;
    }

    public TextView getTipo_mostrar() {
        return tipo_mostrar;
    }

    public TextView getLatitud() {
        return latitud;
    }

    public void setLatitud(TextView latitud) {
        this.latitud = latitud;
    }

    public TextView getLongitud() {
        return longitud;
    }

    public void setLongitud(TextView longitud) {
        this.longitud = longitud;
    }

    public TextView getDescripcion_mostrar() {
        return descripcion_mostrar;
    }

    public EditText getNombre_editar() {
        return nombre_editar;
    }

    public EditText getDescripcion_editar() {
        return descripcion_editar;
    }

    public ImageView getFoto_mostrar() {
        return foto_mostrar;
    }

    public ImageView getIcono() {
        return icono;
    }

    public ImageButton getFoto_editar() {
        return foto_editar;
    }

    public ArrayList<String> getCustomList() {
        return customList;
    }

}
