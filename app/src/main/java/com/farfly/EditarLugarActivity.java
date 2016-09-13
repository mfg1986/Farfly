package com.farfly;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.FileNotFoundException;


public class EditarLugarActivity extends FragmentActivity {

    final static int RESULTADO_GALERIA = 1,RESULTADO_CAMERA=2;
    private static boolean  foto_modificada=false,foto_nueva=false;
    private static boolean[] datos_ok= new boolean[]{false,false};
    private Bitmap imagen_galeria;
    private static LinearLayout layout_botones;
    private static Layout layout;
    private static String tipo_elegido="Seleccione Categoria...";
    private static TextView latitud, longitud;
    private static EditText nombre, descripcion;
    private static ImageButton foto;
    private static RatingBar valoracion;
    private static Button btnEliminar;
    private static String uri_foto_crear,action,desde;
    private static Soporte soporte;


/*****METODO CREA LA ACTIVIDAD, LEE LOS PARAMETROS Y DECIDE SI DEBE EDITARSE O CREARSE UN LUGAR*****/
/***************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Instanciamos la clase de soporte de la aplicación
        soporte=new Soporte(getBaseContext(),this);
        //Instanciamos la clase Idioma
        Idioma idioma = new Idioma(getBaseContext());
        //Cargamos el idioma leyéndolo de las preferencias compartidas
        idioma.cargarLocale();
        //Cargamos el layout
        setContentView(R.layout.editarlugar);
        //Comprobamos si debemos mostrar el asistente de uso(unicamente se muestra la primera vez que se entra en la actividad)
        if(soporte.leerPreferencia("asistente_editar")){
            // Mostramos el asistente-->Explicacion de como editar lugares
            DialogoInfo dialog_crear=soporte.nuevoDialogo(10);
            dialog_crear.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_editar));
            //Desactivamos el asistente
            soporte.guardarPreferencia("asistente_editar",false);
        }

        //Obtenemos la referencia a los elementos del layot
        layout_botones=(LinearLayout)findViewById(R.id.layout_botones);
        TextView cabecera = (TextView) findViewById(R.id.cabecera_editar_lugar);
        nombre = (EditText) findViewById(R.id.editar_nombre);
        foto = (ImageButton) findViewById(R.id.editar_foto);
        latitud = (TextView) findViewById(R.id.editar_latitud);
        longitud = (TextView) findViewById(R.id.editar_longitud);
        valoracion=(RatingBar)findViewById(R.id.ratingbar);
        descripcion = (EditText) findViewById(R.id.editar_descripcion);

        //Leemos el idioma de la aplicación de las preferencias compartidas
        String language= Idioma.leePreferencias("idioma", getBaseContext());
        //Si esta en inglés-->Mostramos en la foto la imagen vacía con el texto en inglés
        if(language.equals("en")){foto.setImageResource(R.drawable.cartel_sin_foto_ingles);}
        //Si esta en español-->Mostramos en la foto la imagen vacía con el texto en español
        if(language.equals("es")){foto.setImageResource(R.drawable.cartel_sin_foto);}

        //Leemos el Intent y el tipo de accion a realizar
        Bundle parametros = this.getIntent().getExtras();
        action = parametros.getString("accion");
        desde=parametros.getString("desde");

        //Definimos el adaptador del Spinner tipo
        Spinner spinner_tipo = (Spinner) findViewById(R.id.spinner_personalizado);
        //Añadimos las categorias al spinner y enlazamos con su adaptador
        soporte.AñadirItemsSpinner("editar",spinner_tipo);

        //Creamos un objeto con los elementos del layout de la actividad
        layout=new Layout(nombre,soporte.getListaTipo(),foto,latitud,longitud,valoracion,descripcion);

        //Definimos el comportamiento del spinner
        spinner_tipo.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Guardamos el tipo elegido en el spinner del filtro
                tipo_elegido = String.valueOf(parent.getItemAtPosition(pos));

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        //Definimos el comportamiento del boton de la imagen
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Activamos el flag que indica que vamos a poner una foto nueva
                foto_nueva = true;
                //Lanzamos el menu de foto
                MenuFoto();

            }
        });

        //Si llamaos a la actividad EditarLugar para crear un lugar nuevo
        if ( action.equals("crear") && parametros.containsKey("latitud") && parametros.containsKey("longitud")) {
            //Ponemos la cabecera de Crear Lugares
            cabecera.setText(getString(R.string.cabecera_crear_lugares));
            //Leemos la latitud y la longitud que nos envio la actividad de Mapas
            double lat = parametros.getDouble("latitud");
            double lng= parametros.getDouble("longitud");

            //Colocamos en el Layout la latitud y la longitud que seran fijos
            latitud.setText(String.valueOf(lat));
            longitud.setText(String.valueOf(lng));
            //Creamos el nuevo lugar
            CrearLugar();
        }
        //Si llamamos a la actividad EditarLugar para editar un lugar ya existente
        if(action.equals("editar")&& parametros.containsKey("_id")){
            //Ponemos la cabecera de Editar Lugares
            cabecera.setText(getString(R.string.cabecera_editar_lugares));
            //Obtenemos el _id del lugar a editar
            int id=parametros.getInt("_id");
            //Cargamos los datos del lugar que queremos editar
            EditarLugar(id);

        }

    }

/********************METODO PARA CREAR UN LUGAR NUEVO***********************************************/
/***************************************************************************************************/
    public void CrearLugar() {
        //Obtenemos la referencia al boton crear/editar
        Button btnCrear = (Button) findViewById(R.id.btnCrear_Guardar);
        btnEliminar=(Button)findViewById(R.id.btnEliminar);

        //Le ponemos el texto de "CREAR" y el estilo al boton
        btnCrear.setText(R.string.btnCrear);

        //Eliminamos el boton Eliminar
        layout_botones.removeView(btnEliminar);

        //Establecemos el listener para que Guarde los datos introducidos por el usuario
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comprobamos que se introducen todos los datos del lugar
                datos_ok = DatosIntroducidos();

                //Si el usuario no selecciono un tipo de lugar o categoria le avisamos que debe introducirlo
                if (!datos_ok[0]) {
                    AvisoIntroducirCategoria();
                }

                //Si el usuario no relleno todos los datos, le avisamos y le damos a oportunidad de continuar o no
                if (!datos_ok[1] && datos_ok[0]) {
                    AvisoDatosIncompletos();
                }

                if (datos_ok[0] && datos_ok[1]) {//Si el usuario relleno todos los datos correctamente
                    datos_ok[0] = false;//Reseteamos el flag de introducir categoria
                    datos_ok[1] = false;//Reseteamos el flag de introducir el resto de datos
                    Guardar();//Guardamos el lugar
                    finish();//Cerramos la actividad
                }

            }
        });

    }

/********************METODO PARA EDITAR UN LUGAR EXISTENTE******************************************/
    /***************************************************************************************************/
    public void EditarLugar(int id){

        //Obtenemos la referencia al boton crear/editar
        Button btnGuardar = (Button) findViewById(R.id.btnCrear_Guardar);
        btnEliminar=(Button)findViewById(R.id.btnEliminar);

        //Le ponemos el texto "GUARDAR" y el estilo a los botones
        btnGuardar.setText(R.string.btnGuardar);

        //Cargamos en los campos del layout los valores del lugar que vamos a editar
        boolean cargar_lugar=soporte.MostrarLugar("editar",id,layout);

        if(!cargar_lugar){//Si hubo problemas al cargar los datos se lo indicamos al usuario
            soporte.CrearToast(getString(R.string.error_cargar));
        }
        //Comportamiento del boton de Guardar Lugar editado
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Procedemos a guardar los cambios
                datos_ok = DatosIntroducidos();
                if (!datos_ok[0]) {//Si no se introdujo la categoria o tipo del lugar
                    AvisoIntroducirCategoria();//Lanzamos un aviso

                }
                if (!datos_ok[1] && datos_ok[0]) {//Si el usuario no relleno todos los datos, le avisamos y le damos a oportunidad de continuar o no
                    AvisoDatosIncompletos();
                }
                if (datos_ok[0] && datos_ok[1]) {//Si el usuario relleno todos los datos correctamente
                    datos_ok[0] = false;
                    datos_ok[1] = false;
                    Guardar();//Guardamos el lugar
                    finish();
                }//Cerramos la actividad
            }
        });


        //Comportamiento del boton Eliminar Lugar
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lanzamos el aviso de que se va a eliminar el lugar
                AvisoEliminarLugar();
            }
        });

    }


/***************METODOS PARA OBTENER LA FOTOGRAFIA DE LA GALERIA Y AÑADIRLA*************************/
/***************************************************************************************************/
//Método para mostrar el menú de foto cuando pulsamos el la fotografía
    public void MenuFoto(){
        //Obtenemos el inflador de layout
        LayoutInflater inflater = getLayoutInflater();
        //Creamos un constructor de menu de foto para esta actividad
        AlertDialog.Builder menu_foto = new AlertDialog.Builder(EditarLugarActivity.this);
        //Convertimos en vista el xml del menu foto con el inflador de layout
        View view = inflater.inflate(R.layout.menu_foto, (ViewGroup)findViewById(R.id.menu_foto));
        //Asociamos la vista obtenida al constructor del menu foto
        menu_foto.setView(view);
        //Creamos el menu de foto a través del constructor
        final Dialog dialog= menu_foto.create();
        //Obtenemos la referencia a las posibles opciones del menu
        Button btn_camera=(Button)view.findViewById(R.id.btn_camera);
        Button btn_galeria=(Button)view.findViewById(R.id.btn_galeria);
        //Comportamiento de la opcion "Ir a Cámara de fotos"-->Abrir la cámara de fotos
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();//Cerramos el menu
                AbrirCámara();//Lanzamos la cámara de fotos


            }
        });
        //Comportamiento de la opcion "Ir a galeria"-->Abrir la galeria
        btn_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();//Cerramos el menu
                AbrirGaleria();//Lanzamos el menu de proveedor de imagen para obtener la fotografía
            }
        });

        dialog.show();//Mostramos el menu
    }
//Método para abrir la cámara de fotos
    public void AbrirCámara() {
    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    startActivityForResult(intent, RESULTADO_CAMERA);
}
//Método para abrir menu para seleccionar el proveedor de imagen de la galería
    public void AbrirGaleria() {
        //LLamamos a los proveedores de imagen para obtener la fotografia
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.proveedor_fotografia)), RESULTADO_GALERIA);

    }
//Método que recoge el resultado tanto de la cámara de fotos como de obtener la imagen de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Verificamos que volvemos de la actividad lanzada por el Intent de RESULTADO_GALERIA y que el usuario no ha cancelado la operacion
        if ((requestCode == RESULTADO_GALERIA ||requestCode == RESULTADO_CAMERA )&& resultCode == RESULT_OK) {//Verificamos que volvemos de la actividad lanzada por el Intent de RESULTADO_GALERIA y que el usuario no ha cancelado la operacion
            //Activamos el flag que indica que se ha modificado la foto
            foto_modificada=true;

            //Cogemos la direccion de la foto obtenida de la Galeria o de la Camara y la parseamos a una URI
            uri_foto_crear = data.getDataString();
            Uri uriFotoCargar = Uri.parse(uri_foto_crear);


            try {
                //Limpiamos el espacio de memoria del bitmap para evitar un ErrorOutOfMemory
                if(imagen_galeria!=null){
                    imagen_galeria.recycle();
                    imagen_galeria=null;
                }
                //decodificamos la uri de la foto obtenida de la galeria y obtenemos un Bitmap
                imagen_galeria = soporte.getImagenSuport().decodeUri(uriFotoCargar);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                imagen_galeria=null;
            }
            //Ponemos el Bitmap obtenido (o en caso de problemas un null) en la foto
            foto.setImageBitmap(imagen_galeria);
        }
    }

/*****************METODOS PARA GUARDAR LOS CAMBIOS TANTO PARA CUANDO SE CREA EL LUGAR COMO CUANDO SE EDITA***/
/***********************************************************************************************************/
    public  ContentValues LeerCampos(){
        String nombreBD,tipoBD,descripcionBD;
        int logoBD;
        double latitudBD, longitudBD;
        float valoracionBD;

        //Cogemos los valores introducidos por el usuario en el layout
        nombreBD = nombre.getEditableText().toString();
        tipoBD=tipo_elegido;
        logoBD=soporte.SelectorDeLogo(tipoBD,"normal");
        descripcionBD = descripcion.getEditableText().toString();
        latitudBD = Double.valueOf(latitud.getText().toString());
        longitudBD = Double.valueOf(longitud.getText().toString());
        valoracionBD=valoracion.getRating();

        //Creamos los campos a introducir
        ContentValues values = new ContentValues();
        values.put("nombre", nombreBD);
        values.put("tipo", tipoBD);
        values.put("logo",logoBD);

        if (foto_modificada) {//Si la foto fue modificada,e.d editamos la foto,entonces guardamos la foto nueva proveniente de la galeria
            values.put("foto", uri_foto_crear);
        }else{
            //Si no pulsamos en la foto y estamos en la opcion de editar, debemos guardar la misma que estaba
            if(action.equals("editar")){values.put("foto", soporte.getUri_foto_cargar());}
            //Si no pulsamos en la foto y estamos en la opcion de crear, debemos guardar la imagen vacia
            if(action.equals("crear")){values.put("foto","Imagen vacia");}
          }

        values.put("latitud", latitudBD);
        values.put("longitud", longitudBD);
        values.put("valoracion", valoracionBD);
        values.put("descripcion", descripcionBD);

        //Desactivamos el flag que indica que la foto fue modificada
        foto_modificada=false;
        return values;

    }
    public void Guardar(){
        //Leemos los campos
        ContentValues values=LeerCampos();
        ContentResolver cr=getContentResolver();
        //Si guardamos un lugar nuevo
        if(action.equals("crear")){
            //Accedemos al Lugar dentro del Proveedor de contenido
            Uri lugares_uri=LugaresProvider.CONTENT_URI;
            //Insertamos el nuevo registro de lugar en la base de datos
            cr.insert(lugares_uri,values);

        }
        //Si guardamos un lugar que ya existia
        if(action.equals("editar")){
            //Obtenemos el id del lugar a editar del intent
            int id=getIntent().getExtras().getInt("_id");
            //Accedemos al Lugar dentro del Proveedor de contenido
            Uri lugares_uri=Uri.parse("content://com.farfly/Lugares/"+String.valueOf(id));
            //Actualizamos registro de lugar en la base de datos
            int cont=cr.update(lugares_uri,values,null,null);
            if(cont!=1){
                soporte.CrearToast(getString(R.string.error_guardar));
            }

        }


    }


/**********************METODOS CORRESPONDIENTES A AVISOS**********************************************/
/****************************************************************************************************/
   public void AvisoEliminarLugar(){
       //Obtenemos el inflador de layout
        LayoutInflater inflater = getLayoutInflater();
       //Creamos un constructor del aviso para esta actividad
        Builder aviso= new Builder(EditarLugarActivity.this);
       //Convertimos en vista el xml del aviso con el inflador de layout
        View view = inflater.inflate(R.layout.aviso_eliminar_lugar, (ViewGroup)findViewById(R.id.aviso_eliminar_lugar));
       //Asociamos la vista obtenida al constructor del aviso
        aviso.setView(view);
       //Creamos el aviso eliminar lugar a través del constructor
        final Dialog dialog= aviso.create();
       //Obtenemos la referencia a los botones "aceptar" y "cancelar" del aviso
        Button btn_aceptar=(Button)view.findViewById(R.id.btn_aceptar_eliminar_lugar);
        Button btn_cancelar=(Button)view.findViewById(R.id.btn_cancelar_eliminar_lugar);
       //Comportamiento del botón "aceptar"
       btn_aceptar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.cancel();//Cerramos el dialogo
               int id = getIntent().getExtras().getInt("_id");//Obtenemos el id del lugar
               soporte.EliminarLugares("uno",desde, id);//Eliminamos el lugar de la base de datos
               soporte.guardarPreferencia("lugar_eliminado",true); //Indicamos que hemos eliminado el lugar en las preferencias compartidas
               finish();//Cerramos la actividad
           }
       });
       //Comportamiento del botón "cancelar"-->Cerrar el aviso
       btn_cancelar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.cancel();
           }
       });

        dialog.show();//Mostramos el aviso

    }
//Método para saber si el usuario introdujo todos los datos
    public boolean[] DatosIntroducidos(){
        String nombreBD,tipoBD,descripcionBD;
        float valoracionBD;
        //Obtnemos el nombre
        nombreBD = nombre.getEditableText().toString();
        //Obtenemos el tipo elegido en el spinner
        tipoBD=tipo_elegido;
        //Obtenemos la valoracion
        valoracionBD=valoracion.getRating();
        //Obtenemos la descripción
        descripcionBD = descripcion.getEditableText().toString();
        //La variable datos[0]-->Indica si se introdujo tipo en el spinner diferente a "Seleccione categoria..."
        datos_ok[0] = !tipoBD.equals(getString(R.string.seleccione));
        //La variable datos[1]-->Indica si nombre y descripcion son datos no vacios, si la valoracion es distinta de 0 y si la foto se modifico ya sea cuando creamos el lugar o cuando lo editamos
        datos_ok[1] = !(nombreBD.matches("") || descripcionBD.matches("") || (!foto_nueva && !foto_modificada) || valoracionBD==0 );
        //Devolvemos la variable datos_ok
        return  datos_ok;
    }
//Método para informar al usuario de que no introdujo todos los datos
   public void AvisoDatosIncompletos(){
       //Obtenemos el inflador de layout
        LayoutInflater inflater = getLayoutInflater();
       //Creamos un constructor de aviso  para esta actividad
        Builder aviso_datos_incompletos = new Builder(EditarLugarActivity.this);
       //Convertimos en vista el xml del aviso datos incompletos con el inflador de layout
        View view = inflater.inflate(R.layout.aviso_datos_incompletos, (ViewGroup)findViewById(R.id.aviso_datos_incompletos));
       //Asociamos la vista obtenida al constructor del aviso
        aviso_datos_incompletos.setView(view);
       //Creamos elaviso datos incompletos a través del constructor
       final Dialog dialog= aviso_datos_incompletos.create();

       //Obtenemos la referencia a los botones "aceptar" y "cancelar" del aviso
        Button btn_aceptar=(Button)view.findViewById(R.id.btn_aceptar_datos_incompletos);
        Button btn_cancelar=(Button)view.findViewById(R.id.btn_cancelar_datos_incompletos);
        //Comportamiento del botón "aceptar"
       btn_aceptar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.cancel();//Cerramos el aviso
               Guardar();//Guardamos el lugar
               finish();//Cerramos la actividad
           }
       });
       //Comportamiento del boton "cancelar"
       btn_cancelar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.cancel();//Cerramos el aviso
           }
       });

       dialog.show();//Mostramos el aviso
    }
//Metodo que indica al usuario que no introdujo el tipo de sitio
    public void AvisoIntroducirCategoria() {
        //Obtenemos el gestor de fragmentos
        FragmentManager fragmentManager = getSupportFragmentManager();
        //Creamos una clase DialogoInfo de tipo 4
        DialogoInfo dialog_info=soporte.nuevoDialogo(4);
        //Mostramos el aviso
        dialog_info.show(fragmentManager, getString(R.string.titulo_introducir_categoria));
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