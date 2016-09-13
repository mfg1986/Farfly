package com.farfly;



import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import android.content.Intent;
import android.app.AlertDialog.Builder;
import com.farfly.LocationHelper.LocationResult;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.Calendar;
import com.farfly.Soporte.Consulta;

public class MapaLugaresActivity extends FragmentActivity {

    private GoogleMap mMap;
    static Soporte soporte;
    private ImageButton btn_crear_lugar,btn_eliminar_lugares;
    private LocationControl locationControlTask;
    private boolean hasLocation = false;
    LocationHelper locHelper;
    private Location currentLocation;
    private boolean waiting=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Si archivo de estados no esta vacio, es decir, que la barra progreso de búsqueda de ubicación actual estaba ejecutandose
        if(savedInstanceState!=null) {
            restoreProgress(savedInstanceState);//Restauramos la actividad de búsqueda de ubicación
        }

        //Instanciamos la clase de soporte de la aplicación
        soporte=new Soporte(getBaseContext(),this);
        //Instanciamos la clase Idioma
        Idioma idioma = new Idioma(getBaseContext());
        //Cargamos el idioma que este guardado en las preferencias compartidas
        idioma.cargarLocale();

        // Comprobamos si tenemos internet
        InternetConexion ic = new InternetConexion(getApplicationContext());
        boolean flag_internet = ic.estaConectadoInternet();
        //Si no tenemos internet
        if(!flag_internet){//Mostramos el aviso de que no se cargaran los mapas
            DialogoInfo dialog_info=soporte.nuevoDialogo(5);
            dialog_info.show(getSupportFragmentManager(), getString(R.string.titulo_sin_internet));
        }
        //Comprobamos si debemos mostrar el asistente de uso(unicamente se muestra la primera vez que se entra en la actividad)
       if(soporte.leerPreferencia("asistente_mapa")){
           AsistenteDeUso();
           //Desactivamos el asistente
           soporte.guardarPreferencia("asistente_mapa",false);
       }
        //Cargamos el contenido del layout
        setContentView(R.layout.mapa);
        //Referenciamos los botones de "crear lugar"(icono +) y "eliminar lugares"(icono x)
        btn_crear_lugar=(ImageButton)findViewById(R.id.btn_crear_lugar);
        btn_eliminar_lugares=(ImageButton)findViewById(R.id.btn_eliminar_lugares);
    }


/**********************METODOS PARA CARGAR EL MAPA Y ACTUALIZAR LAS UBICACIONES***********************/
/****************************************************************************************************/
    @Override
    protected void onResume() {
        super.onResume();
        //Obtenemos y configuramos el mapa
        setUpMapIfNeeded();
        //Actualizamos las posicion guardadas
        ActualizarLocalizacionesMapa();

    }

//Metodo para obtener el mapa en caso necesario
    private void setUpMapIfNeeded() {
        //Si no se cargo previamente el mapa los cargamos desde el SupportMapFragment
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Si el mapa se cargo pasamos a utilizarlo
            if (mMap != null) {
                //Configuramos los elementos necesarios del mapa
                setUpMap();
            }
        }
    }
//Metodo que sirve para configurar el estado de la camara, los marcadores del mapa y los listeners una vez que se obtuvieron los mapas
    private void setUpMap() {
        //Centramos el mapa en Espanya
        CameraUpdate centrar= CameraUpdateFactory.newLatLngZoom(new LatLng(40.41, -3.69), 5F);
        mMap.animateCamera(centrar);

        //Creamos el listener para cuando pulsemos en lugares vacios-->Crearemos lugar
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //Llamamos la actividad EditarLugarActivity en modo "Crear"
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditarLugarActivity.class);
                Bundle parametros = new Bundle();
                parametros.putString("accion", "crear");
                parametros.putDouble("latitud", point.latitude);
                parametros.putDouble("longitud", point.longitude);
                intent.putExtras(parametros);
                startActivity(intent);
            }
        });

        //Creamos un listener para cuando pulsemos sobre marcadores que ya existen-->Mostraremos el Lugar
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Llamamos la actividad EditarLugarActivity en modo "Mostrar"
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MostrarLugarActivity.class);
                Bundle parametros = new Bundle();
                String marcador = marker.getTitle();
                int id_enviar = Integer.valueOf(marcador.substring(0, marcador.indexOf(".")));
                parametros.putInt("_id", id_enviar);
                parametros.putString("desde", "mapa");
                intent.putExtras(parametros);
                startActivity(intent);
                return false;
            }
        });

        //Comportamiento del boton crear lugar ubicado en la esquina superior derecha de la pantalla y designado con el icono +
        //Este boton crea un nuevo lugar con las coordenadas actuales del dispositivo
        btn_crear_lugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locHelper = new LocationHelper(getApplicationContext(), MapaLugaresActivity.this);
                //Comprobamos que hay proveedores de ubicación activos
                boolean flag_proveedores = locHelper.canGetLocation(locationResult);
                if (flag_proveedores) {//Si tenemos proveedores de ubicación
                    waiting = true;//activamos el flag que indica que estamos esperando posicion
                    //Actualizamos las posiciones y recuperamos la ultima obtenida
                    locHelper.getLocation();
                    //Creamos una nueva tarea asincrona que mostrara la barra de progreso
                    locationControlTask = new LocationControl();
                    //Ejecutamos la tarea asincrona que se encarga de mostrar la barra de progreso, comprobar que se obtuvo la ubicación y mandarla a EditarLugarActivity para crear el lugar.
                    locationControlTask.execute(MapaLugaresActivity.this);

                } else {//Si no tenemos ningun proveedor de servicio activado
                    locHelper.AvisoGPSDesactivado();//Preguntamos al usuario si quiere activar alguno
                }


            }

        });
        //Comportamiento del boton eliminar lugares ubicado en la esquina superior derecha de la pantalla y designado con el icono x
        //Este boton elimina todos los lugares de la base de datos
        btn_eliminar_lugares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Realizamos una consulta de todos los lugares
                Consulta consulta = soporte.ConsultaBD("todos", -1);
                if(consulta==null){//Si la base de datos no tiene lugares guardados avisamos al usuario
                    soporte.CrearToast(getString(R.string.no_hay_lugares));
                }else{AvisoEliminarLugares();}//Si hay lugares en la base de datos los eliminamos
            }
        });
    }

//Método para actualizar las localizaciones en el mapa
    public void ActualizarLocalizacionesMapa(){
        //Limpiamos el mapa de marcadores
        mMap.clear();
        //Realizamos una consulta de todos los lugares guardados en la base de datos
        Consulta consulta=soporte.ConsultaBD("todos", -1);
        if(consulta!=null){//Si la consulta es correcta
            ArrayList<Lugar> lugares=consulta.getLugares();//Obtenemos la lista de los lugares
            for (int i = 0; i < lugares.size(); i++) {//Recorremos la lista
                //Obtenemos uno a uno los lugares
                Lugar lugar = lugares.get(i);
                //Creamos un marcador para el lugar con la posicion determinada por la latitud y longitud en el mapa
                MarkerOptions marcador = new MarkerOptions();
                marcador.position(new LatLng(lugar.getLatitud(), lugar.getLongitud()));
               //Le ponemos en el titulo el nombre del lugar y su posición en la base de datos
                marcador.title(lugar.getId() + "." + lugar.getNombre());
                try {
                    //Le asignamos el icono correspondiente al tipo de sitio
                    marcador.icon(BitmapDescriptorFactory.fromResource(lugar.getLogo()));
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }

                //Anyadimos el marcador al mapa
                mMap.addMarker(marcador);

            }
        }
    }



/*******************AVISO AL USUARIO DE QUE SE VAN A ELIMINAR TODOS LOS LUGARES GUARDADOS************/
/****************************************************************************************************/
//Método para avisar al usuario de que va a eliminar todos los lugares
    public void AvisoEliminarLugares() {
        //Obtenemos el inflador de layout
        LayoutInflater inflater = getLayoutInflater();
        //Creamos un constructor de aviso  para esta actividad
        Builder aviso_eliminar_lugares= new Builder(MapaLugaresActivity.this);
        //Convertimos en vista el xml del aviso con el inflador de layout
        View view = inflater.inflate(R.layout.aviso_eliminar_lugares, (ViewGroup)findViewById(R.id.aviso_eliminar_lugares));
        //Asociamos la vista obtenida al constructor del aviso
        aviso_eliminar_lugares.setView(view);
        //Creamos el aviso de eliminar lugares a través del constructor
        final Dialog dialog= aviso_eliminar_lugares.create();

       //Obtenemos la referencia a los botones de "Aceptar" y "Cancelar"
        Button btn_aceptar_eliminar=(Button)view.findViewById(R.id.btn_aceptar_eliminar_lugares);
        Button btn_cancelar_eliminar=(Button)view.findViewById(R.id.btn_cancelar_eliminar_lugares);

        //Comportamiento del botón aceptar-->Eliminar los lugares
        btn_aceptar_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();//Cerramos el aviso
                soporte.EliminarLugares("todos", "mapa", -1);//Eliminamos todos los lugares
                ActualizarLocalizacionesMapa();//Actualizamos el mapa
            }
        });
        //Comportamiento del botón cancelar-->Cerrar el aviso
        btn_cancelar_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        //Mostramos el aviso
        dialog.show();
    }

/*******************TAREA ASINCRONA ENCARGADA DE GESTIONAR LA OBTENCIÓN LA UBICACIÓN ACTUAL**********/
/****************************************************************************************************/
//Tarea Asincrona encargada de gestionar la obtención de la ubicación cuando creamos un lugar a partir de este método
    private class LocationControl extends AsyncTask<Context, Void, Void>{
        private ProgressDialog dialog_progress;
        //Metodo para obtener la barra de progreso
        public ProgressDialog get_dialog_progress() {return dialog_progress;}

        private boolean flag_error=false;

        //Antes de lanzar la tarea de control configuramos el dialogprogres y lo mostramos
        protected void onPreExecute() {
                //Creamos una barra de progreso para la actividad MapaLugares
                this.dialog_progress= new ProgressDialog(MapaLugaresActivity.this);
                //Indicamos que la barra de progreso puede ser cancelada
                this.dialog_progress.setCancelable(true);
                //Mostramos la barra de progreso
                this.dialog_progress.show();
                //Le asociamos el layout que debe mostrar
                this.dialog_progress.setContentView(R.layout.custom_progressdialog);
               //Listener de cancelación de la barra de progreso
                dialog_progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //Desactivamos el flag que indica que estamos esperando ubicación
                        waiting=false;
                        //Paramos la tarea asincrona
                        locationControlTask.cancel(true);
                    }
                });
        }
        //Comportamiento de la tarea asincrona
        protected Void doInBackground(Context... params){

            //Esperamos aproximadamente 10 segundos para ver si somos capaces de obtener la localizacion a través de la red o del gps y en caso de no obtenerla paramos la tarea de búsqueda

            //Obtenemos una referencia temporal
            Long t = Calendar.getInstance().getTimeInMillis();

            //mientras no hayamos conseguido la localización y el tiempo transcurrido desde que tomamos la referencia no sea superior a 15000 ms=15s
            while (!hasLocation && Calendar.getInstance().getTimeInMillis() - t < 15000) {
                try {
                    //Le decimos a la tarea que se espere 1s
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Si el tiempo se agoto y no obtuvimos la posición
            if(Calendar.getInstance().getTimeInMillis() - t >=15000 && !hasLocation){
                flag_error=true;//Activamos el flag de error
            }
            return null;
        }

        //Si termino de ejecutarse la tarea bien porque se consiguio la posición o porque se agoto el tiempo
        protected void onPostExecute(final Void unused)
        {   //Si el dialogo de progreso se esta mostrando
            if (this.dialog_progress.isShowing()) {//Cerramos el dialogo de progreso
                    this.dialog_progress.dismiss();
                    this.dialog_progress = null;
            }

            if (currentLocation != null)//Si obtuvimos la posicion actual
            {   //Lanzamos la EditarActivity para crear el lugar con la posición actual
                useLocation();
                //Paramos la actualizaciones de ubicacion de los listeners
                locHelper.stopLocationUpdates();
                //Paramos la tarea asincrona
                locationControlTask.cancel(true);
            } else {//Si no obtuvimos la posicion y se agoto el tiempo

                if (flag_error) {
                    //Mostramos un mensaje informativo al usuario de que no se pudo obtener la posicion
                    soporte.CrearToast(getString(R.string.error_ubicacion));
                    //Desactivamos el flag de error
                    flag_error = false;
                    //Paramos la actualizaciones de ubicacion de los listeners
                    locHelper.stopLocationUpdates();
                    //Paramos la tarea
                    locationControlTask.cancel(true);

                }

            }
            //Desactivamos el flag que indica que estamos esperando la ubicación
            waiting=false;
        }


    }

    //Instanciamos un objeto de la clase abstracta LocationResult que implementa el LocationHelper y sobreescribimos su metodo gotLocation para darle el uso especifico que necesitamos en la actividad
    public LocationResult locationResult = new LocationResult(){
        @Override
        public void gotLocation(final Location location){
            if(location!=null){//Si la localización obtenida es válida
                currentLocation = new Location(location);//Creamos la localización actual
                hasLocation = true;//Activamos el flag de localización obtenida
            }else{
                hasLocation=false;//Si la localización obtenida no es válida desactivamos el flag de localizacion obtenida
            }
        }
    };

    //Método que utilizar la locaclización actual obtenida para llamar a EditarLugarActivity y crear un lugar con dicha ubicación
    public void useLocation(){
        //Llamamos la actividad EditarLugarActivity en modo "Crear" con la locaclización actual obtenida
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), EditarLugarActivity.class);
        Bundle parametros = new Bundle();
        parametros.putString("accion", "crear");
        parametros.putDouble("latitud", currentLocation.getLatitude());
        parametros.putDouble("longitud", currentLocation.getLongitude());
        intent.putExtras(parametros);
        startActivity(intent);
    }

/*****METODOS CONTROLAR EL ESTADO DE LA BARRA DE PROGRESO DE LA TAREA ASINCRONA AL OBTENER LA UBICACIÓN******/
/****************************************************************************************************/
//Método que se encarga de guardar el estado de la barra de progreso cuando buscamos la ubicación actual
    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        saveState.putBoolean("waiting",waiting);//Guardamos el estado de la barra de progreso
    }
    //Método que se encarga de restaurar la barra de progreso en caso de ser nesario
    private void restoreProgress(Bundle savedInstanceState) {
        waiting=savedInstanceState.getBoolean("waiting");//Obtenemos el estado de la barra de progreso
        if (waiting) {//Si la barra se estaba ejecutando
            if(locationControlTask!=null) {//Si la tarea no finalizo
                ProgressDialog refresher = locationControlTask.get_dialog_progress();//Obtenemos la barra de progreso
                refresher.dismiss();//la cerramos
            }

            //Volvemos a lanzar la buqueda de ubicación
            locHelper = new LocationHelper(getApplicationContext(),MapaLugaresActivity.this);
            if(locHelper.canGetLocation(locationResult)) {
                locHelper.getLocation();
                locationControlTask = new LocationControl();
                //Ejecutamos la tarea asincrona que se encarga de obtener la posicion actual via red o via gps
                locationControlTask.execute(MapaLugaresActivity.this);
            }

        }

    }

/**********************METODOS PARA MOSTRAR LOS ASISTENTES DE USO*************************************/
 /****************************************************************************************************/
    //Método para mostros los asistentes de uso
    public void AsistenteDeUso(){
        //Explicacion de como mostrar lugares desde el mapa-->Tercero en mostrarse
        DialogoInfo dialog_mostrar=soporte.nuevoDialogo(8);
        dialog_mostrar.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_mostrar_mapa));
        //Explicacion de como eliminar lugares desde el mapa-->Segundo en mostrarse
        DialogoInfo dialog_eliminar=soporte.nuevoDialogo(7);
        dialog_eliminar.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_eliminar));
        // Explicacion de como crear lugares en el mapa-->Primero en mostrarse
        DialogoInfo dialog_crear=soporte.nuevoDialogo(6);
        dialog_crear.show(getSupportFragmentManager(), getString(R.string.titulo_asistente_crear));
    }


/**********************METODOS PARA CERRAR LA ACTIVIDAD AL DARLE "ATRAS"******************************/
/****************************************************************************************************/
//Método para cerrar la actividad al darle al botón de "Atrás
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Si le damos al boton de "Back" y no se esta ejecutando la tarea asincrona, cerramos la actividad
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!waiting) {finish();}
        }
        return true;

    }

}