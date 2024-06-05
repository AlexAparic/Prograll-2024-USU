package com.example.redsocial.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.redsocial.FiltersActivity;
import com.example.redsocial.PostActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.redsocial.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersFragment extends Fragment {

    View mView;
    CardView mCardViewDeporte;
    CardView mCardViewCocina;
    CardView mCardViewNoticias;
    CardView mCardViewMemes;

    public FiltersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filters, container, false);
        mCardViewDeporte = mView.findViewById(R.id.cardViewDeporte);
        mCardViewCocina = mView.findViewById(R.id.cardViewCocina);
        mCardViewNoticias = mView.findViewById(R.id.cardViewNoticias);
        mCardViewMemes = mView.findViewById(R.id.cardViewMemes);

        mCardViewDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Deporte");
            }
        });

        mCardViewCocina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Cocina");
            }
        });

        mCardViewNoticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Noticias");
            }
        });

        mCardViewMemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Memes");
            }
        });

        return mView;
    }

    private void goToFilterActivity(String category) {
        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}