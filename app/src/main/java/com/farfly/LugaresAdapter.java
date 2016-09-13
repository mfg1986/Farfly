package com.farfly;



import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.ArrayList;


public class LugaresAdapter extends BaseAdapter implements Filterable {
    //Declaramos las variables
    private ArrayList<Lugar> lugares;//Lista de lugares
    private ArrayList<Lugar> lugaresFilterList;
    private LugaresFilter lugaresFilter;
    private LayoutInflater mInflater;//El inflador de xml a layout
    private Context contexto;
    private Soporte soporte;

//Constructor de la clase
    public LugaresAdapter(Context context, ArrayList<Lugar> loslugares,Activity activity){
        this.contexto=context;
        this.soporte=new Soporte(context,activity);
        this.mInflater=LayoutInflater.from(context);
        this.lugares=loslugares;
        this.lugaresFilterList=loslugares;
    }
//Metodo que devuelve el numero de elementos de la lista
    public int getCount(){
        return lugares.size();
    }
//Método que devuelve el objeto Lugar correspondiente a una determinada posición en la lista
    public Lugar getItem(int position){
        return lugares.get(position);
    }
//Método que devuelve la posición del elemento en la lista
    public long getItemId(int position){
        return position;
    }
//Metodo que devuelve la vista correspondiente a un lugar de la lista
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder holder;
        //Obtenemos la letra personalizada
        Typeface letra = Typeface.createFromAsset(contexto.getAssets(),"fonts/Animated.ttf");
        //Si no convertimos el lugar en una vista de la lista anteriormente
        if (convertView == null) {
            //Convertimos en vista el xml correspondiente a un elemento de la lista
            convertView = mInflater.inflate(R.layout.listalugares_item,null);
            //Instanciamos la clase interna
            holder = new ViewHolder();
            //Asignamos los elementos de la vista de un item de la lista a las variables de la clase interna
            holder.hNombre_titulo=(TextView)convertView.findViewById(R.id.til_nombre);
            holder.hNombre = (TextView) convertView.findViewById(R.id.idNombre);
            holder.hValoracion_titulo=(TextView)convertView.findViewById(R.id.til_valoracion);
            holder.hValoracion=(RatingBar) convertView.findViewById(R.id.ratingbar_small);
            holder.hImage = (ImageView) convertView.findViewById(R.id.idLogo);
            //Asociamos a la vista de item la clase interna
            convertView.setTag(holder);
        } else {//Si el lugar en concreto ya fue convertido en vista
            //Obtenemos la vista de item asociada a la clase interna
            holder = (ViewHolder) convertView.getTag();
        }
        //Obtenemos el lugar
        Lugar lugar = getItem(position);
        if(lugar.getNombre().equals("")){//Si aplicando el filtro no hay lugares de ese tipo

            //Modificamos la vista para que muestre un mensaje de "Lo siento.No se encontraron lugares" y un icono.
           //Cambiamos el texto del titulo de nombre para mostrar el mensaje
            holder.hNombre_titulo.setText(contexto.getString(R.string.no_lugar));
            //Cambiamos el estilo para mostrar el mensaje
            soporte.setEstilo(holder.hNombre_titulo, null, letra, 20, contexto.getResources().getColor(R.color.gris));
            holder.hNombre_titulo.setPadding(10, 10, 0, 0);
            //Hacemos invisible el campo nombre
            holder.hNombre.setVisibility(View.GONE);
            //Hacemos invisible el titulo de valoración
            holder.hValoracion_titulo.setVisibility(View.GONE);
            //Hacemos invisible el campo de valoración
            holder.hValoracion.setVisibility(View.GONE);
            //Ponemos el icono de no se encontraron lugares
            holder.hImage.setImageResource(R.drawable.sinlugar);
        }else {//Si aplicando el filtro existen lugares de ese tipo

            //Recuperamos el formato de vista original por si el usuario utilizo el filtro sin resultados y le aplicamos los valores obtenidos del lugar correspondiente
           //Ponemos el texto del titulo de nombre
            holder.hNombre_titulo.setText(contexto.getString(R.string.nombre));
            //Le devolvemos el estilo original
            soporte.setEstilo(holder.hNombre_titulo, null, letra, 20, contexto.getResources().getColor(R.color.azul2));
            holder.hNombre_titulo.setPadding(0,0,0,0);

            //Hacemos visible el campo nombre
             holder.hNombre.setVisibility(View.VISIBLE);
            //Ponemos el nombre del lugar
             holder.hNombre.setText(lugar.getNombre());

            //Hacemos visible el titulo de valoración
            holder.hValoracion_titulo.setVisibility(View.VISIBLE);
            //Hacemos visible el campo de valoración
            holder.hValoracion.setVisibility(View.VISIBLE);
            //Ponemos la valoración correspondiente al lugar
            holder.hValoracion.setRating(lugar.getValoracion());
            //Ponemos el icono del tipo de lugar
            holder.hImage.setImageResource(lugar.getLogo());
        }
        return convertView;

    }
    //Clase interna para dar agilidad al proceso de carga de los elementos de la lista
    class ViewHolder{
        TextView hNombre_titulo,hValoracion_titulo;
        TextView hNombre;
        RatingBar hValoracion;
        ImageView hImage;
    }


//Metodo para notificar cambios en los datos
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

//Metodo para obtener el filtro-->Devuelve la clase del filtro
    @Override
    public Filter getFilter() {
        if (lugaresFilter == null) {
            lugaresFilter = new LugaresFilter();
        }
        return lugaresFilter;
    }

//Clase interna correspondiente al filtro
    private class LugaresFilter extends Filter
    {
        //Metodo para filtrar la lista de lugares en función de su tipo
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Obtenemos el tipo por el que deseamos filtrar
            String Tipo= constraint.toString();

            FilterResults results = new FilterResults();
            ArrayList<Lugar> filterList = new ArrayList<>();
            boolean flag_lugar_filtrado=false;
            if (!Tipo.equals(contexto.getString(R.string.todos))) {//Si la seleccion del spinner es distinta de todos quiere decir que filtramos

                for (int i = 0; i < lugaresFilterList.size(); i++) {//Recorremos la lista de lugares a filtrar

                    if ( (lugaresFilterList.get(i).getTipo() ).equals(Tipo)) {//Aquellos lugares que coinciden con el tipo indicado en el spinner seran los que se muestren
                        flag_lugar_filtrado=true;//Flag para saber si al menos existe un lugar para mostrar despues del filtro
                        Lugar lugar = lugaresFilterList.get(i);//Cogemos el lugar cuyo tipo coincide con el filtro
                        filterList.add(lugar);//creamos la lista de lugares filtrados
                    }
                }
                if(!flag_lugar_filtrado){//Si no hay ningun lugar para mostrar despues de aplicar el filtro
                       Lugar nolugar=new Lugar("");//creamos un lugar que sera especial
                        filterList.add(nolugar);//Lo añadimos a la lista de filtrados
                        results.count = filterList.size();
                        results.values = filterList;
                }else {//Si la seleccion del spinner fue mostrar todos
                        results.count = filterList.size();//Guardamos el numero de lugares que pasaron el filtro
                        results.values = filterList;//Guardamos la lista filtrada

                }

            }
            else{//Si la seleccion del spinner fue mostrar todos
                results.count = lugaresFilterList.size();//Guardamos el numero de todos los lugares a mostrar
                results.values = lugaresFilterList;//Guardamos la lista sin filtrar

            }
            return results;//Devolvemos la lista filtrada
        }
        //Metodo para publicar los resultados del filtro
        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            lugares = (ArrayList<Lugar>)results.values;//Cambiamos el contenido de los lugares que se mostraran en la lista por la lista que nos devuelve el filtro
            notifyDataSetChanged();//Notificamos el cambio
        }
    }
}

