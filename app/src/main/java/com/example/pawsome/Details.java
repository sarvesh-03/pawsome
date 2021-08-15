package com.example.pawsome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Details extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Details() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Details.
     */
    // TODO: Rename and change types and number of parameters
    public static Details newInstance(String param1, String param2) {
        Details fragment = new Details();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private ImageView imageView;
    private Animal1 animal;
    private ImageView favIm;
    private ImageView share;
    private SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_details, container, false);
        share=view.findViewById(R.id.share);
        animal=(Animal1)getArguments().getSerializable("animal");
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        imageView=(ImageView)view.findViewById(R.id.imageView2);
        if(animal.getImage()!=null){
            if(animal.getImage().get("url").toString()!=null){
                Log.v("Tag",animal.getImage().get("url").toString());
                Picasso.get().load(animal.getImage().get("url").getAsString()).centerCrop().resize(400,400).into((ImageView)view.findViewById(R.id.imageView2));
            }
        }
        favIm =view.findViewById(R.id.Favourite);
        favIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFav(animal);
            }
        });
        ArrayList<Favs> data= new ArrayList<Favs>();
        data.add(new Favs("Name:",animal.getName()));
        data.add(new Favs("Id:",animal.getId()));
        data.add(new Favs("Temperament:",animal.getTemperament()));
        data.add(new Favs("Life Span:",animal.getLife_span()));
        data.add(new Favs("Bred For:",animal.getBred_for()));
        data.add(new Favs("Breed Group:",animal.getBreed_group()));
        data.add(new Favs("Origin:",animal.getOrigin()));
        data.add(new Favs("Weight(imp):",animal.getWeight().get("imperial").getAsString()));
        data.add(new Favs("Weight(mtrc):",animal.getWeight().get("metric").getAsString()));
        data.add(new Favs("Height(imp):",animal.getHeight().get("imperial").getAsString()));
        data.add(new Favs("Height(mtrc):",animal.getHeight().get("metric").getAsString()));
        NameAdapter nameAdapter=new NameAdapter(getContext(),data);
        ((ListView)view.findViewById(R.id.listview)).setAdapter(nameAdapter);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareImage();
                share.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }
    private  final Retrofit retrofit=new Retrofit.Builder().baseUrl("https://api.thedogapi.com/").
            addConverterFactory(GsonConverterFactory.create())
            .build();
    private final MyAPI myAPI=retrofit.create(MyAPI.class);
    public void AddFav(Animal1 animal){
        Fav fav=new Fav();
        if(animal.getImage()!=null)
            if(animal.getImage().get("id")!=null)
            fav.setImage_id(animal.getImage().get("id").getAsString());
        fav.setSub_id(sharedPreferences.getString("UserId",""));
        Call<ResponseBody> call=myAPI.postVote(fav,"36ff4fb4-8ebb-4a04-b98f-82408a950684");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                favIm.setVisibility(View.INVISIBLE);
                Log.v("Suc",""+response.code());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void ShareImage() {
        if (imageView.getDrawable() != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            String bitmapPath = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "SuperHero", null);
            Intent intent = new Intent(Intent.ACTION_SEND);
            Uri bitmapUri = Uri.parse(bitmapPath);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            intent.putExtra(Intent.EXTRA_TEXT, "NAME" + ":" + animal.getName());
            startActivity(Intent.createChooser(intent, "Share Image"));
        }
    }
}