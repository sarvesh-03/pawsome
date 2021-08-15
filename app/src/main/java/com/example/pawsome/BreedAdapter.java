package com.example.pawsome;

import android.content.Context;
//import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BreedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Animal> AnimalList;
    private static final int ITEM=0;
    private static final int LOADING=1;
    private Context context;
    private boolean isLoadingAdded=false;

    public BreedAdapter(Context context) {
        this.context=context;
        AnimalList=new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder breedViewHolder=null;
        //View ItemView= LayoutInflater.from(parent.getContext())
          //      .inflate(R.layout.breed,parent,false);
        switch (viewType){
            case ITEM:
                breedViewHolder= new BreedViewHolder( LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.breed,parent,false));
                break;
            case LOADING:
                breedViewHolder=new LoadViewHolder( LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.breed_progress,parent,false));
                break;
        }


        return breedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Animal animal = AnimalList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                if(animal.getName()==null)
                    ((BreedViewHolder)holder).name.setVisibility((View.GONE));
                else
                ((BreedViewHolder)holder).name.setText(animal.getName());
                if(animal.getImage()!=null){
                    if(animal.getImage().get("url").toString()!=null){
                        Log.v("Tag",animal.getImage().get("url").toString());
                        Picasso.get().load(animal.getImage().get("url").getAsString()).centerCrop().resize(400,400).into(((BreedViewHolder) holder).imageView);
                    }
                }
                //Log.v("Tag","Item");
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return AnimalList==null?0:AnimalList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position==AnimalList.size()-1&& isLoadingAdded)? LOADING:ITEM;
    }

    public void add(Animal a){
        AnimalList.add(a);
        notifyItemInserted(AnimalList.size()-1);
    }

    public void addAll(List<Animal> a){
        for (Animal animal :a){
            add(animal);
        }
    }

    public void remove(Animal animal){
        int position=AnimalList.indexOf(animal);
        if(position>-1){
            AnimalList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear(){
        isLoadingAdded=false;
        while (getItemCount()>0){
            remove(getItem(0));
        }
    }

    public boolean isEmpty(){
        return  getItemCount()==0;
    }

    public void addLoadingFooter(){
        isLoadingAdded=true;
        add(new Animal());
    }

    public void removeLoadingFooter(){
        isLoadingAdded=false;

        int position =AnimalList.size()-1;
        Animal animal=getItem(position);

        if(animal!=null){
            AnimalList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Animal getItem(int position){
        return AnimalList.get(position);
    }

    public class BreedViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ImageView imageView;


        public BreedViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.Name);
            imageView=itemView.findViewById(R.id.dogimage);
        }
    }


    public class LoadViewHolder extends RecyclerView.ViewHolder{
        public LoadViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }





}
