package com.farfly;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LocationHelper
{
    LocationManager locationManager;
    private LocationResult locationResult;
    boolean gpsEnabled = false;
    boolean networkEnabled = false;
    private Activity activity;
    private Context contexto;

//Constructor de la clase
    public LocationHelper(Context contexto,Activity activity){
        this.contexto=contexto;
        this.activity=activity;
    }

//Metodo para indicar si es posible obtener la localización actual o no
    public boolean canGetLocation(LocationResult result) {
        locationResult = result;
        boolean cangetlocationenable=false;
        if (locationManager == null){//Si no tenemos controlador de servicio de ubicación
           //Obtenemos el controlador del servicio de ubicacion
            locationManager = (LocationManager) contexto.getSystemService(Context.LOCATION_SERVICE);
        }

        try {  //Obtenemos el flag que indica que el proveedor de GPS esta activo
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            cangetlocationenable=true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {   //Obtenemos el flag que indica que el proveedor de red esta activo
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            cangetlocationenable=true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Si no tenemos proveedores de ubicación activos
        if (!gpsEnabled && !networkEnabled) {
            cangetlocationenable=false;
        }
        return cangetlocationenable;//Devolvemos true en caso de que algun proveedor este activo y false en de que los dos esten desactivados
    }
//Método para obtener la localización actual del dispositivo
    public boolean getLocation() {

        //Si tenemos el proveedor de GPS activo activamos las actualizaciones de posicion pasandole un listener
        if(gpsEnabled){locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);}
        //Si tenemos el proveedor de Red activo activamos las actualizaciones de posicion pasandole un listener
        if(networkEnabled){locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);}

        //Obtenemos la útima posición
        GetLastLocation();
        return true;
    }
//Listener para el proveedor de ubicacion de Red
    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location)
        {
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}
    };
//Listener para el proveedor de ubicación de GPS
    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location)
        {
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}

    };
//Metodo para obtener la última localización válida del dispositivo
    private void GetLastLocation(){

        //Borramos las actualizaciones de los listener-->Paramos los listeners de los proveedores de ubicación
        locationManager.removeUpdates(locationListenerGps);
        locationManager.removeUpdates(locationListenerNetwork);

        Location gpsLocation = null;
        Location networkLocation = null;

        //Si el GPS esta activo obtenemos la ubicación actual utilizando su proveedor
        if(gpsEnabled){gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);}
        //Si Los servicios de ubicación en red estan activos obtenemos la ubicación actual utilizando su proveedor
        if(networkEnabled){networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);}

        //Si la ubicaciones proporcionadas por los dos proveedores son válidas cogemos la última que obtuvimos como resultado y salimos
        if(gpsLocation != null && networkLocation != null)
        {
            if(gpsLocation.getTime() > networkLocation.getTime()){locationResult.gotLocation(gpsLocation);}
            else{locationResult.gotLocation(networkLocation);}
            return;
        }
        //Si la ubicacion proporcionada por el GPS es valida y la de la red no, tomamos la del GPS como resultado y salimos
        if(gpsLocation != null ){locationResult.gotLocation(gpsLocation); return;}
        //Si la ubicacion proporcionada por la red es valida y la del GPS no, tomamos la del GPS como resultado y salimos
        if(networkLocation != null ){locationResult.gotLocation(networkLocation);return;}

        //En cualquier otro caso el resultado es null
        locationResult.gotLocation(null);
    }
//Metodo para borrar las actualizaciones y parar los listeners de los proveedores de ubicación
    public void stopLocationUpdates(){
       //Eliminamos la actualizaciones de los dos listeners de los proveedores de ubicación
        locationManager.removeUpdates(locationListenerGps);
        locationManager.removeUpdates(locationListenerNetwork);
    }
//Definimos una clase de tipo abstracta con un único metodo que sera coger la localizacion
    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
//Método para avisarnos de que no disponemos de proveedores de ubicación activados y nos permite activar uno
    public void AvisoGPSDesactivado(){
        //Obtenemos el inflador de layout
        LayoutInflater inflater = activity.getLayoutInflater();
        //Creamos un constructor de aviso opciones para esta actividad
        AlertDialog.Builder aviso= new AlertDialog.Builder(activity);
        //Convertimos en vista el xml del aviso opciones con el inflador de layout
        View view = inflater.inflate(R.layout.aviso_gps, (ViewGroup)activity.findViewById(R.id.aviso_gps));
        //Asociamos la vista obtenida al constructor del aviso
        aviso.setView(view);
        //Creamos el aviso a través del constructor
        final Dialog dialog= aviso.create();

        //Obtenemos las referencias a los botones de "Aceptar" y "Cancelar" del aviso
        Button btn_aceptarGPS=(Button)view.findViewById(R.id.btn_aceptarGPS);
        Button btn_cancelarGPS=(Button)view.findViewById(R.id.btn_cancelarGPS);

        //Si presionamos el boton de aceptar abrimos los ajustes de servicios de ubicacion del dispositivo
        btn_aceptarGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialogIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                contexto.startActivity(dialogIntent);
                dialog.cancel();
            }
        });
        //Si presionamos el botón de cancelar cerramos el dialogo
        btn_cancelarGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });
        //Mostramos el aviso
        dialog.show();


    }
}
