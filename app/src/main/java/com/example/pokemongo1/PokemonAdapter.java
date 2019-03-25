package com.example.pokemongo1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder>{
    ArrayList<Pokemon> pokemons;
    Context context1,context2;

    public PokemonAdapter(ArrayList<Pokemon> pokemons, Context context1, Context context2) {
        this.pokemons = pokemons;
        this.context1 = context1;
        this.context2 = context2;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_row,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i) {
        viewHolder.txtName.setText(pokemons.get(i).getName());
        viewHolder.txtPower.setText(pokemons.get(i).getPower());
        viewHolder.imgHinh.setImageResource(pokemons.get(i).getImage());
    }

    @Override
    public int getItemCount() {
        return pokemons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName, txtPower;
        ImageView imgHinh;
        public ViewHolder( View itemView) {
            super(itemView);
            txtName = (TextView)itemView.findViewById(R.id.txtName);
            txtPower =(TextView) itemView.findViewById(R.id.txtPower);
            imgHinh =(ImageView) itemView.findViewById(R.id.imgHinh);
        }
    }

}
