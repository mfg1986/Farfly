package com.farfly;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;



public class DialogoInfo extends DialogFragment {
    private int opcion;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obtenemos los argumentos pasados cuando creamos la clase que este caso sera el entero opción que nos indicará
        //que aviso es el elegido para mostrar
        opcion = getArguments().getInt("opcion");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Obtenemos el inflador de xml de la actividad
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Creamos el constructor del aviso
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        int layout_cargar=-1;
        //Elegimos el layout que queremos cargar en función de la opción elegida al instanciar la clase
        if(opcion == 1){layout_cargar=R.layout.dialog_acerca_app;}//Diálogo acerca app
        if(opcion==2){layout_cargar=R.layout.dialog_acerca_des;}//Diálogo contacto
        if(opcion==3){layout_cargar=R.layout.cambiar_idioma;}//Diálogo aviso al cambiar de idioma
        if(opcion==4){layout_cargar=R.layout.aviso_falta_categoria;}//Diálogo aviso cuando no se introduce categoría
        if(opcion==5){layout_cargar=R.layout.aviso_sin_internet;}//Diálogo aviso sin internet cuandos e accede al mapa
        if(opcion==6){layout_cargar=R.layout.asistente_crear_lugar;}//Diálogo asistente para crear lugares en el mapa
        if(opcion==7){layout_cargar=R.layout.asistente_eliminar_lugares;}//Diálogo asistente para eliminar lugares en el mapa
        if(opcion==8){layout_cargar=R.layout.asistente_mostrar_lugar_mapa;}//Diálogo asistente para mostrar lugares en el mapa
        if(opcion==9){layout_cargar=R.layout.asistente_mostrar_lugar;}//Diálogo asistente para cuando estemos mostrando un lugar
        if(opcion==10){layout_cargar=R.layout.asistente_editar_lugar;}//Diálogo asistente para editar un lugar
        if(opcion==11){layout_cargar=R.layout.asistente_listar_lugares;}//Diálogo asistente para listar lugares en el mapa

        //Convertimos en vista el xml del layout elegido mediante la variable opción con el inflador de layout
        View view = inflater.inflate(layout_cargar, null);
        if(opcion==11){//Si la opción es la 11 vamos a cargar el asistente de la actividad ListaLugaresActivity
            //En este caso vamos a cargar un layout que posee un botón de aceptar que en este caso no nos va a hacer falta
            //Por ello obtenemos el botón del layout y lo ocultamos
            MiButton btn=(MiButton)view.findViewById(R.id.btn_asistente_listar_lugares);
            btn.setVisibility(View.GONE);
        }
        //Asociamos la vista al constructor del diálogo y le ponemos un boton aceptar que cerrará el diálogo
        builder.setView(view).setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cerramos el dialogo
                dialog.cancel();

            }
        });

        //Creamos el diálogo a través del constructor
        Dialog dialog=builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Cuando el diálogo se muestre queremos que tenga un aspecto personalizado por lo que creamos un listener que saltará cuando el diálogo se muestre
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                //Obtenemos el botón de aceptar del diálogo creado
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                //Le ponemos un fondo azul al botón que cambiará de color cuando lo pulsemos
                positiveButton.setBackgroundResource(R.drawable.btn_dialog_estados);

                //Instanciamos la clase de Soporte de la App
                 final Soporte soporte = new Soporte(getActivity().getBaseContext(),getActivity());
                //Instanciamos la letra personalizada que vamos a utilizar
                final Typeface letra = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Animated.ttf");
                //Le damos estilo a la letra del botón aceptar: letra personalizada, 20 de tamaño, color blanco
                soporte.setEstilo(null, positiveButton, letra, 20, getActivity().getResources().getColor(R.color.blanco));


            }
        });
        //Devolvemos el dialogo que hemos creado
        return dialog;
    }




}