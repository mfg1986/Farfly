package com.farfly;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.farfly.LugaresProvider.Lugares;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Soporte {
    private Context contexto;
    private String uri_foto_cargar;
    private ArrayList<String> customList;
    private Activity activity;
    private Bitmap imagen;
    private Consulta consulta;
    private ImagenSupport imagenSuport;


//Constructor de la clase
    public Soporte(Context contexto,Activity activity){
        this.contexto=contexto;
        this.activity=activity;
        this.imagenSuport=new ImagenSupport(contexto);

    }


/**********************METODOS PARA GESTIONAR LAS PREFERENCIAS COMPARTIDAS****************************/
/****************************************************************************************************/
//Metodo para leer las preferencias compartidas
    public boolean leerPreferencia(String flag){
        SharedPreferences prefs =contexto.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        return prefs.getBoolean(flag, false);
    }
//Método para guardar las preferencias compartidas
    public void guardarPreferencia(String flag,boolean valor){
        SharedPreferences prefs =contexto.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(flag, valor);
        editor.commit();

    }


/****METODO PARA OBTENER LA CLASE QUE DA SOPORTE A LAS IMÁGENES Y LA URI DE LA FOTO A CARGAR*********/
/****************************************************************************************************/
//Metodo para obtener la clase de soporte para imagenes
    public ImagenSupport getImagenSuport() {
        return imagenSuport;
    }
//Métodos getter and setter de la variable uri_foto_cargar
    public String getUri_foto_cargar() {
        return uri_foto_cargar;
    }
    public void setUri_foto_cargar(String uri_foto_cargar) {
        this.uri_foto_cargar = uri_foto_cargar;
    }


/**********************METODOS PARA DAR ESTILO A LOS TEXTOS Y CREAR TOAST PERSONALIZADOS**************/
/****************************************************************************************************/
//Método para establecer el estilo de los textos
    public void setEstilo(TextView texto,Button boton, Typeface letra,int size, int color){
        if(texto!=null) {//Para dar estilo a textos
            texto.setTypeface(letra);//Tipo de letra
            texto.setTextSize(size);//Tamaño
            texto.setTextColor(color);//Color

        }
        if (boton != null) {//Para dar estilo a botones
            boton.setTypeface(letra);//Tipo de letra
            boton.setTextSize(size);//Tamaño
            boton.setTextColor(color);//Color
        }


    }
    //Método para crear Toast personalizados
    public void CrearToast(String texto){
        //Obtenemos el inflador de layout
        LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Convertimos en vista el xml del layout de toast con el inflador de layout
        View layout = inflater.inflate(R.layout.toast,(ViewGroup)activity.findViewById(R.id.toast_layout));

        //Obtenemos el el TextView
        TextView text = (TextView) layout.findViewById(R.id.texto_toast);
        text.setText(texto);//Le ponemos el texto

        Toast toast = new Toast(contexto); // Creamos el toast
        toast.setGravity(Gravity.CENTER, 0,0);//Posicionamos el Toast en pantalla
        toast.setDuration(Toast.LENGTH_LONG);//Establecemos la duracion
        toast.setView(layout); //Enlazamos con la vista
        toast.show(); //Mostramos el toast

    }


/**********METODOS PARA GESTIONAR EL SPINNER: EL ICONO Y EL TEXTO DEL TIPO DE LUGAR********************/
/****************************************************************************************************/
//Método para añadir elementos al spinner
    public void AñadirItemsSpinner(String desde,Spinner spinner_tipo) {
       //Creamos un array de string
        ArrayList<String>  customList = new ArrayList<>();
        //Cambiamos el primer elemento si estamos en la actividad Editar-->"Seleccione Categoria.." y en el resto-->"Todos"
        if(desde.equals("editar")){customList.add(contexto.getString(R.string.seleccione));}
        else{customList.add(contexto.getString(R.string.todos));}
        //Añadimos todos los tipos que debe tener el spinner
        customList.add(contexto.getString(R.string.aeropuerto));
        customList.add(contexto.getString(R.string.pub));
        customList.add(contexto.getString(R.string.cafeteria));
        customList.add(contexto.getString(R.string.cajero));
        customList.add(contexto.getString(R.string.comida_rapida));
        customList.add(contexto.getString(R.string.discoteca));
        customList.add(contexto.getString(R.string.tren));
        customList.add(contexto.getString(R.string.gimnasio));
        customList.add(contexto.getString(R.string.hospital));
        customList.add(contexto.getString(R.string.hotel));
        customList.add(contexto.getString(R.string.monumento));
        customList.add(contexto.getString(R.string.otro));
        customList.add(contexto.getString(R.string.parque));
        customList.add(contexto.getString(R.string.piscina));
        customList.add(contexto.getString(R.string.playa));
        customList.add(contexto.getString(R.string.puerto));
        customList.add(contexto.getString(R.string.restaurante));
        customList.add(contexto.getString(R.string.supermercado));
        customList.add(contexto.getString(R.string.taxi));
        customList.add(contexto.getString(R.string.tienda));
        //Creamos el adaptador del spinner indicandole el layout de cada item, la lista de tipos que acabamos de crear y la actividad
        ArrayAdapter<String> myAdapter = new SpinnerAdapter(contexto, R.layout.item_spinner, customList,activity);
        //Asociamos el adaptador al spinner pasado como argumento
        spinner_tipo.setAdapter(myAdapter);
        //Guardamos la lista de posibles tipos de lugares
        setListaTipo(customList);

    }
//Método para guardar la lista de posibles tipos de lugares
    public void setListaTipo(ArrayList<String> customList){this.customList=customList;}
//Método para obtener la lista de posibles tipos de lugares
    public ArrayList<String> getListaTipo() {
        return this.customList;
    }
//Método para elegir que logo poner en función del tipo o categoria de lugar
    public int SelectorDeLogo(String categoria, String tamanyo){
        int logo=-1;
        //Si el tamaño de logo deseado es el normal
        if(tamanyo.equals("normal")) {
            if (categoria.equals(contexto.getString(R.string.seleccione)) || categoria.equals(contexto.getString(R.string.todos))) {
                logo = R.drawable.vacio;
            }
            if (categoria.equals(contexto.getString(R.string.restaurante))) {
                logo = R.drawable.restaurante;
            }
            if (categoria.equals(contexto.getString(R.string.monumento))) {
                logo = R.drawable.monumento;
            }
            if (categoria.equals(contexto.getString(R.string.comida_rapida))) {
                logo = R.drawable.comida_rapida;
            }
            if (categoria.equals(contexto.getString(R.string.cafeteria))) {
                logo = R.drawable.cafeteria;
            }
            if (categoria.equals(contexto.getString(R.string.tienda))) {
                logo = R.drawable.tienda;
            }
            if (categoria.equals(contexto.getString(R.string.supermercado))) {
                logo = R.drawable.supermercado;
            }
            if (categoria.equals(contexto.getString(R.string.hospital))) {
                logo = R.drawable.hospital;
            }
            if (categoria.equals(contexto.getString(R.string.hotel))) {
                logo = R.drawable.hotel;
            }
            if (categoria.equals(contexto.getString(R.string.taxi))) {
                logo = R.drawable.taxi;
            }
            if (categoria.equals(contexto.getString(R.string.tren))) {
                logo = R.drawable.tren;
            }
            if (categoria.equals(contexto.getString(R.string.puerto))) {
                logo = R.drawable.puerto;
            }
            if (categoria.equals(contexto.getString(R.string.aeropuerto))) {
                logo = R.drawable.avion;
            }
            if (categoria.equals(contexto.getString(R.string.cajero))) {
                logo = R.drawable.cajero;
            }
            if (categoria.equals(contexto.getString(R.string.discoteca))) {
                logo = R.drawable.discoteca;
            }
            if (categoria.equals(contexto.getString(R.string.pub))) {
                logo = R.drawable.pub;
            }
            if (categoria.equals(contexto.getString(R.string.piscina))) {
                logo = R.drawable.piscina;
            }
            if (categoria.equals(contexto.getString(R.string.playa))) {
                logo = R.drawable.playa;
            }
            if (categoria.equals(contexto.getString(R.string.parque))) {
                logo = R.drawable.parque;
            }
            if (categoria.equals(contexto.getString(R.string.gimnasio))) {
                logo = R.drawable.gimnasio;
            }
            if (categoria.equals(contexto.getString(R.string.otro))) {
                logo = R.drawable.otros;
            }
        }
        //Si el tamaño de logo deseado es el pequeño
        if(tamanyo.equals("small")){
            if (categoria.equals(contexto.getString(R.string.seleccione)) || categoria.equals(contexto.getString(R.string.todos))) {
                logo = R.drawable.vacio_small;
            }
            if (categoria.equals(contexto.getString(R.string.restaurante))) {
                logo = R.drawable.restaurante_small;
            }
            if (categoria.equals(contexto.getString(R.string.monumento))) {
                logo = R.drawable.monumento_small;
            }
            if (categoria.equals(contexto.getString(R.string.comida_rapida))) {
                logo = R.drawable.comida_rapida_small;
            }
            if (categoria.equals(contexto.getString(R.string.cafeteria))) {
                logo = R.drawable.cafeteria_small;
            }
            if (categoria.equals(contexto.getString(R.string.tienda))) {
                logo = R.drawable.tienda_small;
            }
            if (categoria.equals(contexto.getString(R.string.supermercado))) {
                logo = R.drawable.supermercado_small;
            }
            if (categoria.equals(contexto.getString(R.string.hospital))) {
                logo = R.drawable.hospital_small;
            }
            if (categoria.equals(contexto.getString(R.string.hotel))) {
                logo = R.drawable.hotel_small;
            }
            if (categoria.equals(contexto.getString(R.string.taxi))) {
                logo = R.drawable.taxi_small;
            }
            if (categoria.equals(contexto.getString(R.string.tren))) {
                logo = R.drawable.tren_small;
            }
            if (categoria.equals(contexto.getString(R.string.puerto))) {
                logo = R.drawable.puerto_small;
            }
            if (categoria.equals(contexto.getString(R.string.aeropuerto))) {
                logo = R.drawable.avion_small;
            }
            if (categoria.equals(contexto.getString(R.string.cajero))) {
                logo = R.drawable.cajero_small;
            }
            if (categoria.equals(contexto.getString(R.string.discoteca))) {
                logo = R.drawable.discoteca_small;
            }
            if (categoria.equals(contexto.getString(R.string.pub))) {
                logo = R.drawable.pub_small;
            }
            if (categoria.equals(contexto.getString(R.string.piscina))) {
                logo = R.drawable.piscina_small;
            }
            if (categoria.equals(contexto.getString(R.string.playa))) {
                logo = R.drawable.playa_small;
            }
            if (categoria.equals(contexto.getString(R.string.parque))) {
                logo = R.drawable.parque_small;
            }
            if (categoria.equals(contexto.getString(R.string.gimnasio))) {
                logo = R.drawable.gimnasio_small;
            }
            if (categoria.equals(contexto.getString(R.string.otro))) {
                logo = R.drawable.otros_small;
            }
        }
        return logo;

    }
//Método para traducir el spinner de tipo en caso de cambio de idioma
    public int buscarTraducción(String texto){
        int tipo_id=-1;
        if(texto.equals("Aeropuerto") ||texto.equals("Airport") ){tipo_id=R.string.aeropuerto;}
        if(texto.equals("Bar de Copas")||texto.equals("Pub")){tipo_id=R.string.pub;}
        if(texto.equals("Cafetería")||texto.equals("Coffee Shop")){tipo_id=R.string.cafeteria;}
        if(texto.equals("Cajero")||texto.equals("Cashier")){tipo_id=R.string.cajero;}
        if(texto.equals("Comida Rápida")||texto.equals("Fast Food")){tipo_id=R.string.comida_rapida;}
        if(texto.equals("Discoteca")||texto.equals("Discotheque")){tipo_id=R.string.discoteca;}
        if(texto.equals("Estación de Tren")||texto.equals("Train Station")){tipo_id=R.string.tren;}
        if(texto.equals("Gimnasio")||texto.equals("Gym")){tipo_id=R.string.gimnasio;}
        if(texto.equals("Hospital")){tipo_id=R.string.hospital;}
        if(texto.equals("Hotel")){tipo_id=R.string.hotel;}
        if(texto.equals("Monumento")||texto.equals("Monument")){tipo_id=R.string.monumento;}
        if(texto.equals("Otro")||texto.equals("Other")){tipo_id=R.string.otro;}
        if(texto.equals("Parque")||texto.equals("Park")){tipo_id=R.string.parque;}
        if(texto.equals("Piscina")||texto.equals("Swimming Pool")){tipo_id=R.string.piscina;}
        if(texto.equals("Playa")||texto.equals("Beach")){tipo_id=R.string.playa;}
        if(texto.equals("Puerto")||texto.equals("Port")){tipo_id=R.string.puerto;}
        if(texto.equals("Restaurante")||texto.equals("Restaurant")){tipo_id=R.string.restaurante;}
        if(texto.equals("Supermercado")||texto.equals("Supermarket")){tipo_id=R.string.supermercado;}
        if(texto.equals("Taxi")){tipo_id=R.string.taxi;}
        if(texto.equals("Tienda")||texto.equals("Shop")){tipo_id=R.string.tienda;}

        return tipo_id;
    }



/**********************METODOS PARA MOSTRAR UN LUGAR YA SEA PARA EDITARLO O PARA MOSTRARLO************/
/****************************************************************************************************/
//Método para mostrar un lugar determinado
   /* public boolean MostrarLugar(String accion,int id, Layout layout){
        boolean cargar_lugar;
        //Realizamos una consulta indicando que es de un lugar concreto y pasandole el id de dicho lugar
        consulta=ConsultaBD("uno", id);
        if(consulta!=null){//Si la consulta se realizo correctamente
            //Obtenemos el lugar
            Lugar lugar = consulta.getLugar();
            //Cargamos el lugar pasandole, el lugar obtenido de la consulta, la acción y el layout pasados como argumentos
            cargar_lugar=CargarLugar(accion, lugar,layout);
        }else{

            cargar_lugar=false;
        }
        //Si la carga del lugar es correcta devolverá un true en caso contrario devolverá un false
        return cargar_lugar;

    }*/

public boolean MostrarLugar(String accion,int id, Layout layout){
    boolean cargar_lugar;
    //Realizamos una consulta indicando que es de un lugar concreto y pasandole el id de dicho lugar
    consulta=ConsultaBD("uno", id);
    if(consulta!=null){//Si la consulta se realizo correctamente
        //Obtenemos el lugar
        Lugar lugar = consulta.getLugar();
       //Terminamos de completar la clase lugar obteniendo la imagen
        lugar=ObtenerImagen(lugar);
        //Cargamos los valores de lugar en el layout
        CargarValoresLayout(accion,lugar,layout);
        //Activamos el flag que indica que la carga del lugar se llevo a cabo con éxito
        cargar_lugar=true;
    }else{
        //Desactivamos el flag que indica que la carga del lugar se llevo a cabo con éxito
        cargar_lugar=false;
    }
    //Si la carga del lugar es correcta devolverá un true en caso contrario devolverá un false
    return cargar_lugar;

}
//Método para realizar consultas al proveedor de contenido
    public Consulta ConsultaBD(String seleccion,int id){
        ArrayList<Lugar> lugares=new ArrayList<>();
        Lugar lugar;
        Cursor cursor=null;
        //Creamos una matriz de String con todas las variables que deseamos obtener en la consulta
        String[]campos_obtener=new String[]{Lugares._ID,Lugares.COL_NOMBRE, Lugares.COL_TIPO, Lugares.COL_LOGO, Lugares.COL_FOTO, Lugares.COL_LATITUD,Lugares.COL_LONGITUD, Lugares.COL_VALORACION,Lugares.COL_DESCRIPCION};
        Uri lugares_uri;
        //Si la seleccion pasada como argumento es "uno" queremos consultar un lugar concreto por lo que creamos la uri para realizar la consulta a traves de su id tambien pasado como argumento
        if(seleccion.equals("uno")){lugares_uri=Uri.parse("content://com.farfly/Lugares/"+String.valueOf(id));}
        else{lugares_uri=LugaresProvider.CONTENT_URI;}//Si la selección pasada es "todos" directamente la uri a consultar sera la del proveedor de contenido
        //Creamos un ContentResolver para realizar la consulta
        ContentResolver cr=contexto.getContentResolver();

        try {
            //Realizamos la consulta al proveedor de contenido a través del ContentResolver
            cursor= cr.query(lugares_uri,campos_obtener,null,null,null);
            if (cursor.getCount() > 0) {//Si la consulta se realizo correctamente
                //Ponemos el cursor en posicion
                cursor.moveToFirst();
                    do {//Recorremos todos los registros obtenidos en la consulta
                         //Obtenemos la informacion del lugar del cursor con el indice de la columna
                        id=cursor.getInt(cursor.getColumnIndex("_id"));
                        String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                        String tipo = contexto.getString(buscarTraducción(cursor.getString(cursor.getColumnIndex("tipo"))));
                        String uri_foto_cargar = cursor.getString(cursor.getColumnIndex("foto"));
                        setUri_foto_cargar(uri_foto_cargar);
                        double latitud = cursor.getDouble(cursor.getColumnIndex("latitud"));
                        double longitud = cursor.getDouble(cursor.getColumnIndex("longitud"));
                        float valoracion=cursor.getFloat(cursor.getColumnIndex("valoracion"));
                        String descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
                        //Creamos un objeto lugar con todos los elementos de la consulta
                        lugar = new Lugar(id, nombre, tipo, SelectorDeLogo(tipo,"small"),SelectorDeLogo(tipo,"normal"), uri_foto_cargar, latitud, longitud, valoracion,descripcion);
                        //Vamos añadiendo el lugar a la lista de lugares
                        lugares.add(lugar);

                    }while(cursor.moveToNext());
                //Creamos un objeto consulta
                consulta=new Consulta();
                if(seleccion.equals("uno")){//Si la consulta se realizo para un lugar concreto
                    consulta.setLugar(lugares.get(0));//Colocamos dicho lugar en la variable lugar del objeto consulta
                    consulta.setLugares(null);//Ponemos a null la variable lista de lugares de consulta
                }else {//Si la consulta se realizo para todos los lugares
                    consulta.setLugar(null);//El objeto lugar lo ponemos a null en consulta
                    consulta.setLugares(lugares);//Guardamos la lista de lugares obtenidos  en la variable consulta
                }
            }else{//Si la consulta no se realizo correctamente
                consulta=null;
            }
        }catch (Exception e) {
            e.printStackTrace();
            consulta=null;
        }finally {//Para terminar
            //Cerramos el cursor de la consulta
            if(cursor!=null){cursor.close();}
        }
        //Devolveremos un objeto consulta si no hubo problemas o null en caso contrario
        return consulta;
    }
//Método para Cargar Lugar-->Termina de realizar obtener
    /*public boolean CargarLugar(String accion, Lugar lugar, Layout layout){
        //Terminamos de cargar todos los datos del lugar-->Imagen
        boolean cargar_lugar;
        try {
            //Obtenemos la uri en string de la foto a partir del lugar pasado como argumento
            String foto_consulta=lugar.getFoto();
            if(foto_consulta!=null){//Si es distinto de null
                //Si la imagen es distinta de la imagen que aparece por defecto debemos cargarla
                if(!foto_consulta.equals("Imagen vacia")){
                    // Limpiamos el espacio de memoria de la imagen a utilizar para evitar errores
                    if(imagen!=null){
                        imagen.recycle();
                        imagen=null;
                    }
                    //Obtenemos la imagen decodificando la Uri que obtuvimos del objeto lugar pasado como argumento
                    imagen = imagenSuport.decodeUri(Uri.parse(foto_consulta));
                }
                //Si la imagen guardada es la que tenemos por defecto indicamos que la imagen sera null
                if(foto_consulta.equals("Imagen vacia")){
                    imagen=null;
                }
            }else{imagen=null;}
            //Guardamos la imagen obtenido en el objeto lugar
            lugar.setImagen(imagen);
            //Ponemos los valores extraidos del cursor en el layout que pasamos como argumento
            CargarValoresLayout(accion,lugar,layout);
            cargar_lugar = true;//Activamos el flag que indica se cargo el lugar
        }catch ( FileNotFoundException|NullPointerException e){
            e.printStackTrace();
            cargar_lugar=false;//Si se produce algún error mantenemos desactivado el flag de carga
        }
        //Devolvemos un true si se cargo el lugar correctamente y un false en caso contrario
        return cargar_lugar;

    }*/


        public Lugar ObtenerImagen(Lugar lugar){
        //Terminamos de cargar todos los datos del lugar-->Imagen

        try {
            //Obtenemos la uri en string de la foto a partir del lugar pasado como argumento
            String foto_consulta=lugar.getFoto();
            if(foto_consulta!=null){//Si es distinto de null
                //Si la imagen es distinta de la imagen que aparece por defecto debemos cargarla
                if(!foto_consulta.equals("Imagen vacia")){
                    // Limpiamos el espacio de memoria de la imagen a utilizar para evitar errores
                    if(imagen!=null){
                        imagen.recycle();
                        imagen=null;
                    }
                    //Obtenemos la imagen decodificando la Uri que obtuvimos del objeto lugar pasado como argumento
                    imagen = imagenSuport.decodeUri(Uri.parse(foto_consulta));
                }
                //Si la imagen guardada es la que tenemos por defecto indicamos que la imagen sera null
                if(foto_consulta.equals("Imagen vacia")){
                    imagen=null;
                }
            }else{imagen=null;}
            //Guardamos la imagen obtenido en el objeto lugar
            lugar.setImagen(imagen);

        }catch ( FileNotFoundException|NullPointerException e){
            e.printStackTrace();
        }
        //Devolvemos el lugar con el campo imagen con la fotografia cargada o null si no hay imagen o hubo un error
        return lugar;
    }
//Método para asignar todos los valores de lugar a las vistas del layout, en funcion de si se esta mostrando o editando el lugar
    public void CargarValoresLayout(String accion,Lugar lugar,Layout layout){
        //Leemos el idioma de la app
        String language= Idioma.leePreferencias("idioma", contexto);
        //Ponemos la latitud y la longitud en el layout
        layout.getLatitud().setText(String.valueOf(lugar.getLatitud()));
        layout.getLongitud().setText(String.valueOf(lugar.getLongitud()));
        //Ponemos la valoracion del lugar
        layout.getValoracion().setRating(lugar.getValoracion());
        //Si estamos mostrando un lugar-->El layout pasado como argumento tendra ciertas variables de tipo TextView
        if(accion.equals("mostrar")){
            //Ponemos el nombre en el textview del layout
            layout.getNombre_mostrar().setText(lugar.getNombre());
            //Ponemos el tipo en el textview del layout
            layout.getTipo_mostrar().setText(lugar.getTipo());
            //Le damos estilo
           // layout.getTipo_mostrar().setTypeface(Typeface.createFromAsset(contexto.getAssets(),"fonts/Animated.ttf"));
           //Ponemos el logo del tipo de lugar a mostrar
            layout.getIcono().setImageResource(lugar.getLogoSmall());

          if(lugar.getImagen()!=null){//Si lugar tiene guardada una imagen
              layout.getFoto_mostrar().setImageBitmap(lugar.getImagen());//Ponemos dicha imagen en el layout

          }else{//Si la imagen guardada en lugar es null
              //Si el idioma es español-->Ponemos la imagen vacía con texto en español en el layout
              if(language.equals("es")){layout.getFoto_mostrar().setImageResource(R.drawable.cartel_sin_foto);}
              //Si el idioma es inglés-->Ponemos la imagen vacía con texto en inglés en el layout
              if(language.equals("en")){layout.getFoto_mostrar().setImageResource(R.drawable.cartel_sin_foto_ingles);}

          }
          //Ponemos la descripcion en el textview del layout
          layout.getDescripcion_mostrar().setText(lugar.getDescripcion());
        }
        //Si estamos editando un lugar-->El layout pasado como argumento tendra ciertas variables de tipo EditText
        if(accion.equals("editar")){
            //Colocamos en el spinner el tipo correspondiente al lugar a editar
            layout.getCustomList().add(0,lugar.getTipo());
            layout.getCustomList().remove(1);
            //Ponemos en el editText el nombre del lugar a editar
            layout.getNombre_editar().setText(lugar.getNombre());

            if(lugar.getImagen()!=null) {//Si lugar tiene guardada una imagen
                layout.getFoto_editar().setImageBitmap(lugar.getImagen());//Ponemos dicha imagen en el layout
            }else{//Si la imagen guardada en lugar es null
                //Si el idioma es español-->Ponemos la imagen vacía con texto en español en el layout
                if(language.equals("es")){layout.getFoto_editar().setImageResource(R.drawable.cartel_sin_foto);}
                //Si el idioma es inglés-->Ponemos la imagen vacía con texto en inglés en el layout
                if(language.equals("en")){layout.getFoto_editar().setImageResource(R.drawable.cartel_sin_foto_ingles);}
            }
            //Ponemos la descripcion en el edittext del layout
            layout.getDescripcion_editar().setText(lugar.getDescripcion());
        }

    }
//Clase interna para gestionar la consulta de un lugar de la base de datos o todos los lugares de la base de datos
    public class Consulta{
        private ArrayList<Lugar> lugares;//Variable para cuando se realiza una consulta de todos los lugares
        private Lugar lugar;//Variable para cuando se realiza la consulta de un lugar
        //Constructor de la clase
        public Consulta(){}
        //Getters and Setters de todas la variables de la clase
        public ArrayList<Lugar> getLugares() {
            return lugares;
        }
        public void setLugares(ArrayList<Lugar> lugares) {
            this.lugares = lugares;
        }
        public Lugar getLugar() {
            return lugar;
        }
        public void setLugar(Lugar lugar) {
            this.lugar = lugar;
        }
    }



/**********************METODOS PARA ELIMINAR UNO O TODOS LOS LUGARES GUARDADOS*************************/
/****************************************************************************************************/
//Método para eliminar uno o todos los lugares de la base de datos
    public void EliminarLugares(String seleccion,String desde,int id){
        Uri lugares_uri;
        //Si la seleccion pasada como argumento es "uno" queremos consultar un lugar concreto por lo que creamos la uri para realizar la consulta a traves de su id tambien pasado como argumento
        if(seleccion.equals("todos")){lugares_uri=LugaresProvider.CONTENT_URI;}
        else{lugares_uri=Uri.parse("content://com.farfly/Lugares/"+String.valueOf(id));}//Si la selección pasada es "todos" directamente la uri a consultar sera la del proveedor de contenido
        //Realizamos una consulta de todos los lugares para ver si hay lugares guardados
        consulta=ConsultaBD("todos", -1);
        if(consulta==null){//Si no hay lugares guardados en la base de datos
            CrearToast(contexto.getString(R.string.no_hay_lugares));//Avisamos al usuario

        } else {//Si hay lugares guardados en la base de datos
           //Creamos un ContentResolver para contactar con el proveedor de contenido
            ContentResolver cr = contexto.getContentResolver();
            //Eliminamos el lugar o todos los lugares en funcion de la seleccion pasada como argumento
            int cont = cr.delete(lugares_uri, null, null);
            if (cont < 1) {//Si hubo errores al eliminar el/los lugares
                if (desde.equals("lista")) {//Si intentamos borrar el lugar desde EditarLugarActivity
                    CrearToast(contexto.getString(R.string.error_eliminar_lugar));
                }
                if (desde.equals("mapa")) {//Si intentamos borrar todos los lugares desde MapaActivity
                    CrearToast(contexto.getString(R.string.error_eliminar_lugares));
                }
            }else {//Si no hubo errores y se elimino correctamente
                if (desde.equals("lista")) {//Si vinimos de ListaLugaresActivity solo se pudo borrar un lugar
                    CrearToast(contexto.getString(R.string.lugar_eliminado));//Indicamos al usuario que se borro el lugar
                    CrearToast(contexto.getString(R.string.nuevos_lugares));//Le guiamos por si quiere crear lugares nuevos
                }
                if (desde.equals("mapa")) {//Si vinimos de MapaLugaresActivity se podrán borrar uno o todos los lugares
                    if (seleccion.equals("todos")){CrearToast(contexto.getString(R.string.lugares_eliminados));}//Si borramos todos los lugares
                    if (seleccion.equals("uno")){CrearToast(contexto.getString(R.string.lugar_eliminado));}//Si borramos un lugar

                }
            }
        }


    }


/**********************METODO PARA CREAR CLASES DIALOGOINFO DE DISTINTOS TIPOS************************/
/****************************************************************************************************/
//Metodo para crear clases de tipo DialogoInfo pasandole como argumento la variable entera opción
    public DialogoInfo nuevoDialogo(int opcion) {
        DialogoInfo dialogo = new DialogoInfo();
        Bundle args = new Bundle();
        args.putInt("opcion", opcion);
        dialogo.setArguments(args);
        return dialogo;
    }



/**********************METODO PARA CREAR EL ASISTENTE DE LISTA LUGARES ACTIVITY***********************/
/****************************************************************************************************/
//Método para crear el asistente correspondiente a la actividad
    public void AsistenteListar(){
        //Obtenemos el inflador de layout
        LayoutInflater inflater =activity.getLayoutInflater();
        //Creamos un constructor del asistente
        AlertDialog.Builder asistente_listar= new AlertDialog.Builder(activity);
        //Convertimos en vista el xml del asistente con el inflador de layout
        View view = inflater.inflate(R.layout.asistente_listar_lugares, (ViewGroup)activity.findViewById(R.id.asistente_listar_lugares));
       //Obtenemos la referencia del botón aceptar del layout del asistente
        MiButton btn_aceptar_asistente=(MiButton)view.findViewById(R.id.btn_asistente_listar_lugares);
        //Hacemos visible el boton de "Aceptar" que pudo quedar invisible si usamos el asistente desde el menu de opciones de la actividad Principal
        btn_aceptar_asistente.setVisibility(View.VISIBLE);
        //Asociamos la vista obtenida al constructor del asistente
        asistente_listar.setView(view);
        //Creamos el dialogo del asistente través del constructor
        final Dialog dialog= asistente_listar.create();


        //Obtenemos los botones el botón de aceptar del layout del menu de idioma
       // Button btn_aceptar_asistente=(Button)view.findViewById(R.id.btn_asistente_listar_lugares);


        //Si presionamos el boton de aceptar abrimos el asistente de la actividad ListarLugar
        btn_aceptar_asistente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();//Cerramos el dialogo
            }
        });
        //Mostramos el asistente
        dialog.show();

    }

}
