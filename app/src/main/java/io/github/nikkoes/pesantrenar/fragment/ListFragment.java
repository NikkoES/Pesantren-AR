package io.github.nikkoes.pesantrenar.fragment;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.nikkoes.pesantrenar.R;
import io.github.nikkoes.pesantrenar.activity.DetailPesantrenActivity;
import io.github.nikkoes.pesantrenar.adapter.recyclerview.PesantrenAdapter;
import io.github.nikkoes.pesantrenar.model.Pesantren;
import io.github.nikkoes.pesantrenar.model.PesantrenResponse;
import io.github.nikkoes.pesantrenar.utils.DialogUtils;

import static io.github.nikkoes.pesantrenar.data.Constant.PESANTREN;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    Unbinder unbinder;

    PesantrenAdapter adapter;

    public static ListFragment newInstance(double latitude, double longitude) {

        Bundle args = new Bundle();
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        adapter = new PesantrenAdapter(getContext(), getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.hasFixedSize();

        adapter.setOnItemClickListener(new PesantrenAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent i = new Intent(getActivity(), DetailPesantrenActivity.class);
                i.putExtra("pesantren", adapter.getItem(position));
                i.putExtra("my_location", new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude")));
                startActivity(i);
            }
        });

        loadItems();
    }

    private void loadItems() {
        AndroidNetworking.get(PESANTREN)
                .build()
                .getAsObject(PesantrenResponse.class, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof PesantrenResponse) {
                            List<Pesantren> list = ((PesantrenResponse) response).getData();

                            adapter.swap(list);

                            Log.e("LIST", "" + adapter.getListItem().size());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        DialogUtils.closeDialog();
                        Toast.makeText(getActivity(), "Kesalahan teknis, silahkan cek koneksi internet anda !", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
