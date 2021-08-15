package com.example.pawsome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NameAdapter extends ArrayAdapter<Favs> {
    public NameAdapter(@NonNull Context context, @NonNull List<Favs> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=convertView;
        if(view==null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.name,parent,false);
        }
        Favs current=getItem(position);
        ((TextView)view.findViewById(R.id.Data)).setText(current.getSub_id());
        ((TextView)view.findViewById(R.id.Query)).setText(current.getImage_id());
        return view ;
    }
}
