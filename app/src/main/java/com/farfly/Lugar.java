package com.farfly;

import android.graphics.Bitmap;

public class Lugar {

    private int id,logo, logo_small;
    private String nombre,tipo,foto,descripcion;
    private double latitud,longitud;
    private Bitmap imagen;
    private float valoracion;

//Constructor 1 de la clase
    public Lugar(String nombre){
        this.nombre=nombre;
    }
//Constructor 2 de la clase
    public Lugar(int elid,String elnombre,String eltipo,int ellogo_small,int ellogo,String lafoto, double lalatitud,double lalongitud,float lavaloracion, String ladescripcion) {
        this.id=elid;
        this.nombre=elnombre;
        this.tipo=eltipo;
        this.logo_small=ellogo_small;
        this.logo=ellogo;
        this.foto=lafoto;
        this.latitud=lalatitud;
        this.longitud=lalongitud;
        this.valoracion=lavaloracion;
        this.descripcion=ladescripcion;
    }


/*******************GETTER Y SETTERS NECESARIOS DE LAS VARIABLES DE LA CLASE*************************/
/***************************************************************************************************/
    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getLatitud() {
        return this.latitud;
    }

    public void setLatitud(double lalatitud) {
        this.latitud = lalatitud;
    }

    public double getLongitud() {
        return this.longitud;
    }

    public void setLongitud(double lalongitud) {
        this.longitud = lalongitud;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int elid) {
        this.id = elid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String elnombre) {
        this.nombre = elnombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String ladescripcion) {
        this.descripcion = ladescripcion;
    }

    public int getLogo(){
        return this.logo;
    }

    public int getLogoSmall(){
        return this.logo_small;
    }

    public String getFoto() {
        return foto;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {

        if(this.imagen!=null){
            this.imagen.recycle();
            this.imagen=null;
        }
        this.imagen = imagen;
    }
}
