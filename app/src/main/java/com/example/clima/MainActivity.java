package  com.example.clima;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.AsyncTask;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clima.Modelo.Clima;
import  com.example.clima.Peticiones.peticion;


import com.google.android.gms.location.*;

import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import android.os.Looper;

import android.location.Location;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import androidx.legacy.content.*;
import java.text.DecimalFormat;
public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_INTERNET = 123;
    private Button button;
    private static final int REQUEST_CODE = 1;
    private double latitud;
    private double longitud;
    private boolean control = false;

    private Clima c = null;


    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         Context con = this.getApplicationContext();

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCoordenada();

                // crear e iniciar la tarea en segundo plano
                ObtenerUbicacionTask tarea = new ObtenerUbicacionTask();
                tarea.execute();





            }
        });


    }













        // estamos actualizando las coordenadas
    public void ObtenerCoordendasActual(View view){


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        } else {
                        getCoordenada();




        }
    }





    @Override  //obtenemos el permiso del usuario y obtenemos las coordenadas
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            //
            if (requestCode == REQUEST_CODE && grantResults.length > 0) {  //estamos obteniendo un un 1 como respuesta y si estamos consiguiendo un permiso ya que le arreglo de permisos no esta vacio
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // comparamos el primer permiso pedido con la constante de permiso otorgado
                    getCoordenada();
                } else {
                    Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
                }
            }
    }





    private void getCoordenada() {

        try {

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                control = true;

            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                       latitud = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                       longitud = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                       control = true;

                    }

                }


            }, Looper.myLooper());


        }catch (Exception ex){
            System.out.println("Error es :" + ex);
            control = true;


        }

    }

    private void actualizarUbicacion() {

        peticion p = new peticion();
        p.setLatitud(latitud);
        p.setLongitud(longitud);
        p.setContexto(this.getApplicationContext());
        p.start();
        try {
            p.join();
           c = p.getClima();
            if(c== null){
                Toast.makeText(this, "No hay conexiíon a internet", Toast.LENGTH_SHORT).show();
                return;

            }


                Cargar(c);



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private class ObtenerUbicacionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while (!control) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            control = false;
            // notificar a la actividad principal que la ubicación ha sido actualizada
            actualizarUbicacion();

        }
    }



    private void Cargar(Clima c){

        String cadena = "https://openweathermap.org/img/wn/"+c.getWeather().get(c.getWeather().size()-1).getIcon()+".png";
        System.out.println(cadena);
        DecimalFormat formato = new DecimalFormat("0.00");

        ImageView i = (ImageView) findViewById(R.id.imagen);
        Picasso.get().load(cadena).into(i);

        TextView maximo = (TextView) findViewById(R.id.maximo);
        maximo.setText("temperatura maxima "+formato.format(c.getMain().getTemp_max()-273.15)+"");
        TextView temp = (TextView)  findViewById(R.id.temperatura);
        temp.setText("temperatura "+formato.format(c.getMain().getTemp()-273.15)+"");
        TextView minimo = (TextView)  findViewById(R.id.minimo);
        minimo.setText("temperatura minima "+formato.format(c.getMain().getTemp_min()-273.15));



        TextView pais = (TextView)  findViewById(R.id.pais);
        pais.setText(c.getSys().getCountry());
        TextView lugar = (TextView) findViewById(R.id.lugar);
        lugar.setText(c.getName());

        TextView viento = (TextView)  findViewById(R.id.viento);
        viento.setText("Viento de "+formato.format(c.getWind().getSpeed()*3.6)+ "Km/h");
        TextView humedad = (TextView)  findViewById(R.id.humedad);
        humedad.setText("Humedad de "+ c.getMain().getHumidity()+"%");


    }


   }




