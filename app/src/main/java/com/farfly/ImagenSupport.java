package com.farfly;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class ImagenSupport {
    private Context contexto;
    private Bitmap imagen;

    public ImagenSupport(Context contexto){
        this.contexto=contexto;
    }
    //Metodo para manejo eficiente de imganes-->Escalamos la imagen para que no ocupe tanto y no nos de un error tipo OutOfMemory
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public Bitmap decodeUri(Uri uri)throws FileNotFoundException {
        try {
            if (imagen != null) {
               imagen.recycle();
            }
            //Obtenemos la imagen a través de la decodificacion de su Uri
            InputStream stream=contexto.getContentResolver().openInputStream(uri);
            imagen= BitmapFactory.decodeStream(stream);
            stream.close();

            //Obtenemos el tamaño de la pantalla del dispositivo
            WindowManager wm = (WindowManager) contexto.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width_screen = size.x;

            //Devolvemos la imagen escalada en funcion del ancho de pantalla
            return scaleImage(width_screen,imagen);

        } catch (OutOfMemoryError|Exception e) {
            //En caso de problemos devolvemos un null
            e.printStackTrace();
            return null;

        }
    }
    //Función para escalar la imagen en funcion de la anchura de la pantalla
    private Bitmap scaleImage(int anchura, Bitmap imagen){

        //Tomamos las dimensiones actuales de la imagen y diseñamos la caja en la que vamos a enmarcarla tomando como referencia la anchura de la pantalla
        int width = imagen.getWidth();
        int height = imagen.getHeight();
        //Pasamos a pixeles la densidad de la anchura de pantalla que es nuestra referencia
        int bounding = dpToPx(anchura);


        //Determinamos cuanto debemos escarlar la imagen
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        //Creamos la matriz para el escalado y añadimos el factor de escala
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        //Creamos una imagen nueva a partir de la original pero escalada y la devolvemos
        return Bitmap.createBitmap(imagen, 0, 0, width, height, matrix, true);


    }
    //Función para transformar densidad de pantalla en píxeles
    private int dpToPx(int dp){
        float density = contexto.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }


}
