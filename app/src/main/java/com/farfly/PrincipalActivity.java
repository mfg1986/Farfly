package com.farfly;

import android.app.Dialog;
import android.content.Intent;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class PrincipalActivity extends FragmentActivity {

    private Idioma idioma;
    private static Soporte soporte;
    static Button btnMapas,btnListaLugares;
    static ImageButton btnOpciones;
    private static  Dialog dialog_salir=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Instanciamos la clase Idioma
        idioma=new Idioma(getBaseContext());
        //Cargamos el idioma que trae el dispositivo por defecto
        idioma.cargarLocale();
        //Instanciamos la clase de soporte de la aplicación
        soporte=new Soporte(getBaseContext(),this);
        //Cargamos el layout de la actividad
        setContentView(R.layout.principal);

       //Leemos el estado de la app para saber si es la primera vez que se ejecuta
        String appstar = checkAppStart();
        //Si es la primera vez que se ejecuta la aplicacion lanzaremos la configuración de idioma y activaremos todos los asistentes de uso
        if(appstar.equals("FIRST_TIME")){
            //Inicializamos todos los flags de la lista de preferencias
            configuracionArranqueAplicacion();
            //Lanzamos la configuracion de indicandole que estamos en modo de configuración
            MenuIdioma(true);
        }

        //Obtenemos la referencia de los elementos del layout
        btnMapas=(Button)findViewById(R.id.btnMapas);
        btnListaLugares=(Button)findViewById(R.id.btnListaLugares);
        btnOpciones=(ImageButton)findViewById(R.id.btnOpciones);

        //Comportamiento del boton "Mapas" que lanza la Actividad MapaLugares
        btnMapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llamamos a la actividad MapsActivity
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MapaLugaresActivity.class);
                startActivity(intent);
            }
        });

        //Comportamiento del boton "Lista Lugares" que lanza la Actividad MapaLugares
        btnListaLugares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llamamos a la actividad ListaLugaresActivity
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ListaLugaresActivity.class);
                startActivity(intent);
            }
        });

        //Comportamiento del boton Opciones que lanza el menu
        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuOpciones();

            }
        });

    }

/*****************************************************************************************************/
/********************************MENUS: OPCIONES E IDIOMA*********************************************/
/*****************************************************************************************************/
    public void MenuOpciones(){
        //Obtenemos el inflador de layout
        LayoutInflater inflater = getLayoutInflater();
        //Creamos un constructor de menu opciones para esta actividad
        Builder menu_opciones= new Builder(PrincipalActivity.this);
        //Convertimos en vista el xml del menu opciones con el inflador de layout
        View view = inflater.inflate(R.layout.menu_opciones, (ViewGroup)findViewById(R.id.menu_opciones));
        //Asociamos la vista obtenida al constructor del menu opciones
        menu_opciones.setView(view);
        //Creamos el menu de opciones a través del constructor
        final Dialog dialog= menu_opciones.create();

        //Obtenemos todas las opcions del menu de su layout
        Button btn_acerca_app=(Button)view.findViewById(R.id.btn_acerca_app);
        Button btn_acerca_des=(Button)view.findViewById(R.id.btn_acerca_des);
        Button btn_asistente=(Button)view.findViewById(R.id.btn_asistente);
        Button btn_ajustes=(Button)view.findViewById(R.id.btn_ajustes);
        //Comportamiento si se selecciona la opcion "Acerca app"
        btn_acerca_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un dialogo que será de tipo 1-->Que será información de la aplicación
                DialogoInfo dialog_info = soporte.nuevoDialogo(1);
                //Mostramos el dialogo
                dialog_info.show(getSupportFragmentManager(), getString(R.string.opcion_acerca_app));
                //Cerramos el menu de opciones
                dialog.cancel();
            }
        });
        //Comportamiento si se selecciona la opcion "Contacto"
        btn_acerca_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un dialogo que será de tipo 2-->Que será informacion del desarrollados y como ponerse en contacto con el
                DialogoInfo dialog_info = soporte.nuevoDialogo(2);
                //Mostramos el dialogo
                dialog_info.show(getSupportFragmentManager(), getString(R.string.opcion_acerca_desarrollador));
                //Cerramos el menu de opciones
                dialog.cancel();
            }
        });
        //Comportamiento si se selecciona la opcion "Mostrar Asistente"
        btn_asistente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mostramos el asistente de uso para el usuario
                MostrarAsistente();
                //Cerramos el menu de opciones
                dialog.cancel();
            }
        });
        //Comportamiento si se selecciona la opcion "Idioma"
        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un dialogo que será de tipo 3-->Que será un aviso indicando que los campos que introdujo el usuario no seran traducidos
                DialogoInfo dialog_info = soporte.nuevoDialogo(3);
                //Mostramos el aviso
                dialog_info.show(getSupportFragmentManager(), getString(R.string.ajustes));
                //Cerramos el menu de opciones
                dialog.cancel();
                //Lanzamos el menu de idioma indicandole que no estamos en modo de configuración
                MenuIdioma(false);

            }
        });
    //Mostramos el menú de opciones
        dialog.show();
    }
    public void MenuIdioma(final boolean flag_configuracion){
        //Obtenemos el inflador de layout
        LayoutInflater inflater = getLayoutInflater();
        //Creamos un constructor de menu idioma para esta actividad
        Builder menu_idioma= new Builder(PrincipalActivity.this);
        //Convertimos en vista el xml del menu idioma con el inflador de layout
        View view = inflater.inflate(R.layout.menu_idioma, (ViewGroup) findViewById(R.id.menu_idioma));
        //Asociamos la vista obtenida al constructor del menu idioma
        menu_idioma.setView(view);
        //Creamos el menu de idioma a través del constructor
        final Dialog dialog_idioma= menu_idioma.create();

        //Obtenemos los botones "español" e "ingles" del layout del menu de idioma
        Button btn_español=(Button)view.findViewById(R.id.btn_español);
        Button btn_ingles=(Button)view.findViewById(R.id.btn_ingles);

        //Comportamiento si se selecciona el idioma "español"
        btn_español.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Guardamos el idioma en las preferencias compartidas de idioma-->Español
                  idioma.salvarLocale("es");
                //Cerramos el menu de idioma
                  dialog_idioma.cancel();
                //Reiniciamos la actividad para que cargue el idioma seleccionado
                  Reiniciar();
                //Si estamos configurando el idioma tras la instalacion mostramos toast informativo:"Idioma configurado correctamente"
                 if(flag_configuracion) {
                     soporte.CrearToast(getString(R.string.idioma_configurado_ok_es));
                 }else{//Si cambiamos el idioma en cualquier otro momento mostramos el toast informativo:"Idioma cambiado correctamente"
                     soporte.CrearToast(getString(R.string.cambio_idioma_ok_es));
                 }
            }
        });
        //Comportamiento si se selecciona el idioma "ingles"
        btn_ingles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Guardamos el idioma en las preferencias compartidas de idioma-->Ingles
                idioma.salvarLocale("en");
                //Cerramos el menu de idioma
                dialog_idioma.cancel();
                //Reiniciamos la actividad para que cargue el idioma seleccionado
                Reiniciar();
                //Si estamos configurando el idioma tras la instalacion mostramos toast informativo: "Language configured successfully"
                if(flag_configuracion) {
                    soporte.CrearToast(getString(R.string.idioma_configurado_ok_en));
                }else{//Si cambiamos el idioma en cualquier otro momento mostramos el toast informativo:"Language changed successfully"
                    soporte.CrearToast(getString(R.string.cambio_idioma_ok_en));
                }

            }
        });
        //Mostramos el menu de idioma
        dialog_idioma.show();
    }




/*****************************************************************************************************/
/************************METODO PARA REINICIAR LA ACTIVIDAD TRAS CAMBIAR EL IDIOMA********************/
/*****************************************************************************************************/
    public void Reiniciar(){
        //Creamos el intent indicandole el paquete al que pertenece nuestra actividad
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        //Cerramos todas la actividades que pudieran estar abiertas
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Terminamos la actividad
        finish();
        //Volvemos a arrancar la actividad
        startActivity(intent);
    }



/*****************************************************************************************************/
/************************MÉTODO PARA SABER SI ESTAMOS EN MODO DE CONFIGURACIÓN************************/
/*****************************************************************************************************/
    public String checkAppStart() {
        PackageInfo pInfo;
        //Obtenemos la preferencias compartidas por defecto de la app
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Inicializamos la variable appStart como si fuera una ejecución "NORMAL"
        String appStart ="NORMAL";
        try {
            //Obtenemos la información del paquete de nuestra app
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
           //Obtenemos la ultima versión del código de las preferencias compartidas por defecto, y si no esta guardado le indicamos que queremos que nos devuelva -1
            int lastVersionCode = sharedPreferences.getInt("last_app_version", -1);
            //Si la version anterior es -1 significa que es la primera vez que ejecutamos la app
            if (lastVersionCode == -1) {
                appStart="FIRST_TIME";//Indicamos en la variable appStart que es la primera vez que se ejecuta el código, es una ejecución "FIRST_TIME" o estamos en modo configuración
            } else {//Si la version anterior es distinta de -1
                appStart="NORMAL";//Indicamos en la variable appStart que no es la primera vez que se ejecuta el código, es una ejecución "NORMAL"
            }
            //Obtenemos la version actual del código
            int currentVersionCode = pInfo.versionCode;
            //Guardamos la versión actual en las preferencias compartidas por defecto
            sharedPreferences.edit().putInt("last_app_version", currentVersionCode).commit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //Devolvemos el estado de ejecución del código que será "FIRST_TIME"(modo configuración) la primera vez que se ejecutó o "NORMAL" en el resto de casos.
        return appStart;
    }
    public void configuracionArranqueAplicacion(){

        //Todos los asistentes activados
        soporte.guardarPreferencia("asistente_mapa",true);
        soporte.guardarPreferencia("asistente_listar", true);
        soporte.guardarPreferencia("asistente_mostrar", true);
        soporte.guardarPreferencia("asistente_editar", true);

        //Ningun lugar ha sido eliminado
        soporte.guardarPreferencia("lugar_eliminado", false);
        soporte.guardarPreferencia("lugar_eliminado_lista", false);

    }



/*****************************************************************************************************/
/************************METODO PARA MOSTRAR EL ASISTENTE DE USO**************************************/
/*****************************************************************************************************/
    public void MostrarAsistente(){
        //Explicacion de como editar lugar-->Sexto en mostrarse
        DialogoInfo dialog_editar=soporte.nuevoDialogo(10);
        dialog_editar.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_editar));
        //Explicacion de como mostrar lugar-->Quinto en mostrarse
        DialogoInfo dialog_mostrar=soporte.nuevoDialogo(9);
        dialog_mostrar.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_mostrar));
        //Explicacion de como buscar lugar en lista-->Cuarto en mostrarse
        DialogoInfo dialog_listar=soporte.nuevoDialogo(11);
        dialog_listar.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_mostrar));
        //Explicacion de como mostrar lugares desde el mapa-->Tercero en mostrarse
        DialogoInfo dialog_mostrar_mapa=soporte.nuevoDialogo(8);
        dialog_mostrar_mapa.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_mostrar_mapa));
        //Explicacion de como eliminar lugares desde el mapa-->Segundo en mostrarse
        DialogoInfo dialog_eliminar_mapa=soporte.nuevoDialogo(7);
        dialog_eliminar_mapa.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_eliminar));
        // Explicacion de como crear lugares en el mapa-->Primero en mostrarse
        DialogoInfo dialog_crear_mapa=soporte.nuevoDialogo(6);
        dialog_crear_mapa.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_crear));

    }



/*****************************************************************************************************/
/****METODO PARA MOSTRAR UN AVISO DE CONFIRMACIÓN AL PULSAR LA TECLA ATRÁS PARA SALIR DE LA APP*******/
/*****************************************************************************************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Si le damos al boton de "Back"
        //Creamos un panel de dialogo con el titulo Salir, y que nos pregunta si estamos seguros de querer salir de la aplicacion
        //Obtenemos el inflador de layout
        LayoutInflater inflater = getLayoutInflater();
        //Creamos un constructor de aviso para esta actividad
        Builder aviso = new Builder(PrincipalActivity.this);
        //Convertimos en vista el xml del aviso con el inflador de layout
        View view = inflater.inflate(R.layout.salir, (ViewGroup) findViewById(R.id.salir));
        //Asociamos la vista obtenida al constructor del aviso
        aviso.setView(view);
        //Creamos el aviso a traves del constructor
        dialog_salir = aviso.create();
        //Obtenemos los botones aceptar y cancelar del layout del aviso
        Button btn_aceptar = (Button) view.findViewById(R.id.btn_aceptar_salir);
        Button btn_cancelar = (Button) view.findViewById(R.id.btn_cancelar_salir);

        //Comportamiento del botón acepta
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_salir.cancel();//Cerramos el dialogo
                finish();//Finalizamos la actividad

            }
        });

        //Comportamiento del botón cancelar
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_salir.cancel();//Cerramos el dialogo de aviso
            }
        });

        dialog_salir.show();//Mostramos el aviso

        // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
        return super.onKeyDown(keyCode, event);
    }
}

