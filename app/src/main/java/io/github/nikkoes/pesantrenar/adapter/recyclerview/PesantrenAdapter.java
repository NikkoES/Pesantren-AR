package io.github.nikkoes.pesantrenar.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nikkoes.pesantrenar.R;
import io.github.nikkoes.pesantrenar.model.Pesantren;
import io.github.nikkoes.pesantrenar.utils.Algorithm;

import static io.github.nikkoes.pesantrenar.data.Constant.IMAGE_URL;

public class PesantrenAdapter extends RecyclerView.Adapter<PesantrenAdapter.MyViewHolder> {

    List<Pesantren> list;
    Context context;

    double latitude, longitude;

    OnItemClickListener mOnItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_pesantren)
        ImageView imagePesantren;
        @BindView(R.id.txt_nama_pesantren)
        TextView txtNamaPesantren;
        @BindView(R.id.txt_alamat)
        TextView txtAlamat;
        @BindView(R.id.txt_jarak)
        TextView txtJarak;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public PesantrenAdapter(Context context, double latitude, double longitude) {
        this.list = new ArrayList<>();
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pesantren, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Pesantren item = list.get(position);
        Picasso.get().load(IMAGE_URL + item.getFoto()).placeholder(R.drawable.ic_pesantren).into(holder.imagePesantren);
        holder.txtNamaPesantren.setText(item.getNamaPesantren());
        holder.txtAlamat.setText(item.getAlamat());
        holder.txtJarak.setText(Algorithm.calculateHarversine(latitude, longitude, Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude())) + " KM");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(Pesantren item) {
        list.add(item);
        notifyItemInserted(list.size() + 1);
    }

    public void addAll(List<Pesantren> listItem) {
        for (Pesantren item : listItem) {
            add(item);
        }
    }

    public void removeAll() {
        list.clear();
        notifyDataSetChanged();
    }

    public void swap(List<Pesantren> datas) {
        if (datas == null || datas.size() == 0)
            return;
        if (list != null && list.size() > 0)
            list.clear();
        list.addAll(datas);
        notifyDataSetChanged();

    }

    public Pesantren getItem(int pos) {
        return list.get(pos);
    }

    public String showHourMinute(String hourMinute) {
        String time = "";
        time = hourMinute.substring(0, 5);
        return time;
    }

    public void setFilter(List<Pesantren> list) {
        list = new ArrayList<>();
        list.addAll(list);
        notifyDataSetChanged();
    }

    public List<Pesantren> getListItem() {
        return list;
    }

}
