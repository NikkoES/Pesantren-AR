
package io.github.nikkoes.pesantrenar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Pesantren implements Serializable {

    @Expose
    private String alamat;
    @SerializedName("created_at")
    private String createdAt;
    @Expose
    private String fasilitas;
    @Expose
    private String foto;
    @SerializedName("id_pesantren")
    private String idPesantren;
    @Expose
    private String kurikulum;
    @Expose
    private String latitude;
    @Expose
    private String longitude;
    @SerializedName("nama_pesantren")
    private String namaPesantren;
    @SerializedName("no_telp")
    private String noTelp;
    @Expose
    private String ormas;
    @Expose
    private String pimpinan;

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getIdPesantren() {
        return idPesantren;
    }

    public void setIdPesantren(String idPesantren) {
        this.idPesantren = idPesantren;
    }

    public String getKurikulum() {
        return kurikulum;
    }

    public void setKurikulum(String kurikulum) {
        this.kurikulum = kurikulum;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNamaPesantren() {
        return namaPesantren;
    }

    public void setNamaPesantren(String namaPesantren) {
        this.namaPesantren = namaPesantren;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getOrmas() {
        return ormas;
    }

    public void setOrmas(String ormas) {
        this.ormas = ormas;
    }

    public String getPimpinan() {
        return pimpinan;
    }

    public void setPimpinan(String pimpinan) {
        this.pimpinan = pimpinan;
    }

}
