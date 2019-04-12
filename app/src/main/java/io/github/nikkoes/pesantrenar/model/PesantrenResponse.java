package io.github.nikkoes.pesantrenar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PesantrenResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<Pesantren> data;

    public String getStatus() {
        return status;
    }

    public List<Pesantren> getData() {
        return data;
    }
}
