
package io.github.nikkoes.pesantrenar.model;

import java.util.List;
import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class PesantrenResponse {

    @Expose
    private List<Pesantren> data;
    @Expose
    private String status;

    public List<Pesantren> getData() {
        return data;
    }

    public void setData(List<Pesantren> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
