package io.github.nikkoes.pesantrenar.ar;

import android.location.Location;

import com.beyondar.android.plugin.BeyondarObjectPlugin;
import com.beyondar.android.plugin.GeoObjectPlugin;
import com.beyondar.android.util.math.Distance;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;

import java.util.Iterator;

/**
 * Created by Amal Krishnan on 27-03-2017.
 */

public class ArObject extends BeyondarObject {

    private String namaPesantren;
    private String alamatPesantren;
    private double jarakPesantren;
    private String imagePesantren;
    private String idPesantren;

    public ArObject(String namaPesantren, String alamatPesantren, double jarakPesantren, String imagePesantren, String idPesantren) {
        this.namaPesantren = namaPesantren;
        this.alamatPesantren = alamatPesantren;
        this.jarakPesantren = jarakPesantren;
        this.imagePesantren = imagePesantren;
        this.idPesantren = idPesantren;
    }

    public String getNamaPesantren() {
        return namaPesantren;
    }

    public String getAlamatPesantren() {
        return alamatPesantren;
    }

    public double getJarakPesantren() {
        return jarakPesantren;
    }

    public String getImagePesantren() {
        return imagePesantren;
    }

    public String getIdPesantren() {
        return idPesantren;
    }
}
