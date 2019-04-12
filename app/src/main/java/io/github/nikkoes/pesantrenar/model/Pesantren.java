package io.github.nikkoes.pesantrenar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pesantren implements Serializable {

    @SerializedName("id_pesantren")
    @Expose
    private String idPesantren;
    @SerializedName("nama_pesantren")
    @Expose
    private String namaPesantren;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("kurikulum")
    @Expose
    private String kurikulum;
    @SerializedName("fasilitas")
    @Expose
    private String fasilitas;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("id_ormas")
    @Expose
    private String idOrmas;

    public String getIdPesantren() {
        return idPesantren;
    }

    public String getNamaPesantren() {
        return namaPesantren;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getKurikulum() {
        return kurikulum;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public String getFoto() {
        return foto;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getIdOrmas() {
        return idOrmas;
    }
}
