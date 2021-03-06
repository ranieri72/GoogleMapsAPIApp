package com.example.mtz_5555_transp.mymapapplication.Util;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mtz-5555-transp on 15/08/17.
 */

public class RotaHttp {

    public static final String driving = "driving";
    public static final String walking = "walking";
    public static final String bicycling = "bicycling";
    public static final String transit = "transit";

    public static List<LatLng> carregarRota(LatLng orig, LatLng dest, String mode) {
        List<LatLng> posicoes = null;
        try {
            posicoes = new ArrayList<>();

            String urlRota = String.format(Locale.US,
                    "http://maps.googleapis.com/maps/api/directions/json?" +
                            "origin=%f,%f&destination=%f,%f&" +
                            "sensor=true&mode=%s",
                    orig.latitude, orig.longitude,
                    dest.latitude, dest.longitude,
                    mode);

            URL url = new URL(urlRota);
            String result = bytesParaString(url.openConnection().getInputStream());

            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonRoute = jsonObject.getJSONArray("routes").getJSONObject(0);
            JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);

            JSONArray steps = leg.getJSONArray("steps");
            final int numSteps = steps.length();
            JSONObject step;

            for (int i = 0; i < numSteps; i++) {
                step = steps.getJSONObject(i);
                String pontos = step.getJSONObject("polyline").getString("points");
                posicoes.addAll(PolyUtil.decode(pontos));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return posicoes;
    }

    public static String urlStaticMap(LatLng orig, LatLng dest) {
        return String.format(Locale.US,
                "http://maps.googleapis.com/maps/api/staticmap?" +
                        "size=1000x1000&" +
                        "markers=color:blue%%7Clabel:O%%7C%f,%f&" +
                        "markers=color:red%%7Clabel:D%%7C%f,%f",
                orig.latitude, orig.longitude,
                dest.latitude, dest.longitude);
    }

    private static String bytesParaString(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bufferzao = new ByteArrayOutputStream();
        int byteslidos;
        while ((byteslidos = is.read(buffer)) != -1) {
            bufferzao.write(buffer, 0, byteslidos);
        }
        return new String(bufferzao.toByteArray(), "UTF-8");
    }
}
