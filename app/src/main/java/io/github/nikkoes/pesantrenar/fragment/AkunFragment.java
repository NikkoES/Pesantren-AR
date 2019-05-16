package io.github.nikkoes.pesantrenar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.nikkoes.pesantrenar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AkunFragment extends Fragment {


    Unbinder unbinder;

    public AkunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_pengaturan, R.id.btn_bantuan, R.id.btn_tentang, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pengaturan:
                Toast.makeText(getActivity(), "Sedang dalam pengembangan !", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_bantuan:
                Toast.makeText(getActivity(), "Sedang dalam pengembangan !", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_tentang:
                Toast.makeText(getActivity(), "Sedang dalam pengembangan !", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_logout:
                getActivity().finish();
                break;
        }
    }
}
