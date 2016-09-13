package com.farfly;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;



public class MostrarLugarActivity extends FragmentActivity {

    private static Soporte soporte;
    private static Layout layout;
    private String  desde;

/**********METODOS PARA CREAR LA ACTIVIDAD Y LANZAR LA ACTIVIDAD "EDITAR" SI SE PULSA EL BOTON********/
/****************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Instanciamos la clase Idioma
        Idioma idioma = new Idioma(getBaseContext());
        //Cargamos el idioma leyéndolo de las preferencias compartidas
        idioma.cargarLocale();
        //Instanciamos la clase de soporte de la aplicación
        soporte=new Soporte(getBaseContext(),this);
        //Cargamos el layout
        setContentView(R.layout.mostrarlugar);

        //Comprobamos si debemos mostrar el asistente de uso(unicamente se muestra la primera vez que se entra en la actividad)
        if(soporte.leerPreferencia("asistente_mostrar")){
            //Mostramos el asistente-->Explicación de mostrar lugares
            DialogoInfo dialog_crear=soporte.nuevoDialogo(9);
            dialog_crear.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_mostrar));
            //Desactivamos el asistente
            soporte.guardarPreferencia("asistente_mostrar",false);
        }


        //Referencias a los elementos del layout
        TextView nombre = (TextView) findViewById(R.id.mostrar_nombre);
        ImageView icono = (ImageView) findViewById(R.id.mostrar_icono);
        TextView tipo = (TextView) findViewById(R.id.mostrar_tipo);
        ImageView foto = (ImageView) findViewById(R.id.mostrar_foto);
        TextView latitud = (TextView) findViewById(R.id.mostrar_latitud);
        TextView longitud = (TextView) findViewById(R.id.mostrar_longitud);
        RatingBar valoracion = (RatingBar) findViewById(R.id.ratingbar);
        TextView descripcion = (TextView) findViewById(R.id.mostrar_descripcion);
        Button btnEditar = (Button) findViewById(R.id.btnEditar);
        //Instanciamos la clase Layout en modo mostrar
        layout=new Layout(nombre, tipo, icono, foto, latitud, longitud, valoracion, descripcion);
        //Obtenemos desde donde se lanzo la actividad MostrarLugar del intent
        desde =this.getIntent().getExtras().getString("desde");
        //Comportamiento de los botones
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lanzamos la actividad para editar el lugar
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditarLugarActivity.class);
                Bundle parametros_enviar = new Bundle();
                parametros_enviar.putString("accion", "editar");
                parametros_enviar.putString("desde",desde);
                parametros_enviar.putInt("_id", getIntent().getExtras().getInt("_id"));
                intent.putExtras(parametros_enviar);
                startActivity(intent);

            }
        });

    }


/**********************************METODOS PARA MOSTRAR EL LUGAR**************************************/
/****************************************************************************************************/
    @Override
    protected void onResume(){
        /*Obtenemos el flag "lugar_eliminado" para saber:
                -Pulsamos el marker y mostramos un lugar determinado en cuyo caso lugar_eliminado=false y deberemos mostrar dicho lugar
                -Si se volvio de editar un lugar guardando los cambios en cuyo caso lugar_eliminidao=false y deberemos mostrar el lugar editado
                -Si se volvio de editar un lugar y dicho lugar se elimino en cuyo caso lugar_eliminado=true y no deberemos mostrar
        */

        //Obtenemos el flag lugar eliminado de las preferencias compartidas
        boolean lugar_eliminado= soporte.leerPreferencia("lugar_eliminado");

        boolean cargar_lugar=false;
        if(!lugar_eliminado) {//Si el lugar fue editado y no eliminado o si solo pulsamos el marker para ver un lugar concreto
            //Obtenemos el id del Lugar del Intent que lanzo la activity
             int id=this.getIntent().getExtras().getInt("_id");

            //Cargamos todos los datos del lugar en el layout si es correcta la carga la variable cargar_lugar=true
            cargar_lugar = soporte.MostrarLugar("mostrar",id,layout);

        }
        if(!cargar_lugar){//Si el lugar no se cargo adecuadamente o no hizo falta cargarlo porque se elimino en ambos casos cargar_lugar==false
            //Esta bandera se activa cuando volviamos de eliminar un lugar ahora la desactivamos
            soporte.guardarPreferencia("lugar_eliminado",false);
            //Activamos la bandera por si la eliminacion se llevo a cabo desde la lista, para que esta se actualice
            soporte.guardarPreferencia("lugar_eliminado_lista",true);
            finish();//Finalizamos la activity
        }
        super.onResume();

    }


/**********************METODOS PARA CERRAR LA ACTIVIDAD AL DARLE "ATRAS"******************************/
/****************************************************************************************************/
//Método para cerrar la actividad al darle al botón de "Atrás"
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Si le damos al boton de "Back" cerramos la actividad
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;

    }

}
