package com.farfly;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;
import com.farfly.Soporte.Consulta;
import java.util.ArrayList;

public class ListaLugaresActivity extends ListActivity {

    private static LugaresAdapter adapter;
    private static Soporte soporte;
    private static String tipo_elegido;

/**********METODOS PARA CREAR LA ACTIVIDAD Y LANZAR EL ASISTENTE SI ES NECESARIO*********************/
/****************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Cargamos el idioma
        Idioma idioma = new Idioma(getBaseContext());
        idioma.cargarLocale();
        //Creamos la clase de soporte de la aplicación
        soporte=new Soporte(getBaseContext(),this);
        //Cargamos el layout
        setContentView(R.layout.listalugares);
        //Inicializamos el filtro
        tipo_elegido=getString(R.string.todos);

        //Comprobamos si debemos mostrar el asistente de listar lugares,e.d, si es la primera vez que el usuario entra le mostramos el asistente
        if(soporte.leerPreferencia("asistente_listar")) {
            soporte.AsistenteListar();//Mostramos el asistente
            soporte.guardarPreferencia("asistente_listar",false);//Desactivamos el asistente
        }

        //Definimos el adaptador del Spinner tipo
        Spinner spinner_tipo = (Spinner) findViewById(R.id.spinner_personalizado);
        //Añadimos las categorias al spinner y enlazamos con su adaptador
        soporte.AñadirItemsSpinner("listar", spinner_tipo);
        //Definimos el comportamiento del spinner
        spinner_tipo.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Obtenemos el tipo elegido en el filtro
                tipo_elegido = String.valueOf(parent.getItemAtPosition(pos));

                if (adapter != null) { //Si ya se creo el adaptador de la lista  aplicamos el filtro
                    adapter.getFilter().filter(tipo_elegido, new Filter.FilterListener() {
                        public void onFilterComplete(int count) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }


/**********************METODOS PARA MOSTRAR LOS LUGARES EN LA LISTA SEGÚN EL FILTRO*******************/
/****************************************************************************************************/
    @Override
    protected void onResume() {
        //Leemos si accedimos a eliminar el lugar desde ListaLugaresActivity
        boolean lugar_eliminado_lista=soporte.leerPreferencia("lugar_eliminado_lista");
        //Realizamos una consulta de los lugares que debemos listar
        Consulta consulta =soporte.ConsultaBD("todos", -1);

        if(consulta!=null) {//Si la lista no esta vacia
            //Cogemos todos los lugares de la consulta realizada
            ArrayList<Lugar> lugares=consulta.getLugares();
            //Creamos nuestro adaptador con la lista de lugares
            adapter = new LugaresAdapter(ListaLugaresActivity.this, lugares,this);
            //Aplicamos el filtro a dicha lista
            adapter.getFilter().filter(tipo_elegido, new Filter.FilterListener() {
                public void onFilterComplete(int count) {

                }
            });
            //Enlazamos el adapatador a nuestra listview
            setListAdapter(adapter);

        }else{//Si la lista esta vacia y volvimos de eliminar un lugar
            if(lugar_eliminado_lista){
                //Limpiamos la bandera de lugar_eliminado_lista de las preferencias que activamos en la actividad MostrarLugarActivity
                soporte.guardarPreferencia("lugar_eliminado_lista",false);

                //Limpiamos la lista que debera mostrarse el texto de cuando esta vacia
                setListAdapter(null);

            }
        }
        super.onResume();
    }


/**********************METODO PARA MOSTRAR EL LUGAR SELECCIONADO EN LA LISTA**************************/
/****************************************************************************************************/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //Obtenemos el lugar que se selecciono en la lista
        Lugar lugar= (Lugar) l.getItemAtPosition(position);
        if(!lugar.getNombre().equals("")) {//Si el lugar es un lugar valido seguno el filtro
            int id_lugar = lugar.getId();//Obtenemos el id del lugar
            //LLamamos a MostrarLugar con el id del lugar e indicandole que lo llamamos desde la lista de lugares
            Intent intent = new Intent();
            intent.setClass(this, MostrarLugarActivity.class);
            intent.putExtra("_id", id_lugar);
            intent.putExtra("desde","lista");
            startActivity(intent);
        }else{//Si el lugar esta vacio, es decir, que no hay lugares disponibles del tipo que indica el filtro, mostramos un aviso.
            soporte.CrearToast(getString(R.string.otra_categoria));

        }

    }


/**********************METODOS PARA CERRAR LA ACTIVIDAD AL DARLE "ATRAS"******************************/
/****************************************************************************************************/
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        //Si le damos al boton de "Back" cerramos la actividad
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;

    }

}

