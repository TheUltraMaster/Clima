package com.example.clima.Peticiones;
import android.content.Context;
import android.icu.text.SymbolTable;
import android.widget.Toast;

import com.example.clima.Modelo.Clima;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import  java.net.URL;
import java.util.Map;
import java.util.Scanner;

import retrofit2.http.Url;

public class peticion extends  Thread{
	public Context getContexto() {
		return contexto;
	}

	public void setContexto(Context contexto) {
		this.contexto = contexto;
	}

	Context contexto = null;

	public String getValor() {
		return valor;
	}

	String valor;

		public void  run(){
			valor = pedir();

		}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	private double latitud;

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	private double longitud;

	public Clima getClima() {
		return clima;
	}

	public void setClima(Clima clima) {
		this.clima = clima;
	}

	private Clima clima = null;


	private String pedir(){

		URL url;
		HttpURLConnection con;
		System.out.println(latitud);
		System.out.println(longitud);
	
		String UR = "https://api.openweathermap.org/data/2.5/weather?lat="+latitud+"&lon="+longitud+"&appid=8f7cfe967b97edc63f5e0b781dbee9c9";

		try{

			url = new URL(UR);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			if(con.getResponseCode() != 200){
				System.out.println(con.getResponseCode());
				return null;
			}else{	System.out.println(con.getResponseCode());
				StringBuilder datos = new StringBuilder();
				Scanner s = new Scanner(url.openStream());
				while(s.hasNext()){
					datos.append(s.nextLine());
				}
				s.close();
				con.disconnect();

				System.out.println(datos);

				Gson gson = new Gson();
				clima = gson.fromJson(datos.toString(), Clima.class);
				System.out.println("Coordenadas: " + clima.getCoord().getLon() + ", " + clima.getCoord().getLat() );

				return  datos.toString();

			}

		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}


	}







}
