package com.example.pawsome;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Favourites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favourites extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Favourites() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Favourites.
     */
    // TODO: Rename and change types and number of parameters
    public static Favourites newInstance(String param1, String param2) {
        Favourites fragment = new Favourites();
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

    private BreedAdapter breedAdapter;
    private PaginationScrollListener paginationScrollListener;
    private LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
    private  final Retrofit retrofit=new Retrofit.Builder().baseUrl("https://api.thedogapi.com/").
            addConverterFactory(GsonConverterFactory.create())
            .build();
    private final MyAPI myAPI=retrofit.create(MyAPI.class);
    private int CurrentPage=1;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private boolean isLastPage=false;
    private boolean isLoading=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_favourites, container, false);
        progressBar=view.findViewById(R.id.progressBar);
        recyclerView=view.findViewById(R.id.recyclerView2);
        breedAdapter=new BreedAdapter(getContext());
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());

        ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull  RecyclerView recyclerView, @NonNull  RecyclerView.ViewHolder viewHolder, @NonNull  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull  RecyclerView.ViewHolder viewHolder, int direction) {
                if(breedAdapter.getItemCount()!=0) {
                    Call<ResponseBody> call=myAPI.DeleteFav(breedAdapter.getItem(viewHolder.getAdapterPosition()).getId(),"36ff4fb4-8ebb-4a04-b98f-82408a950684");
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.v("Res",""+response.code());
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                    breedAdapter.remove(breedAdapter.getItem(viewHolder.getAdapterPosition()));
                    //breedAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                }

            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);


        paginationScrollListener=new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                CurrentPage+=1;
                isLoading=true;
                LoadNextPage();

            }

            @Override
            public int getTotalPageCount() {
                return 20;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        };

        recyclerView.addOnScrollListener(paginationScrollListener);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(breedAdapter);
        LoadFirstPage();
        return view;
    }
    public void LoadFirstPage(){
        Call<List<Animal>> call=myAPI.GetFav(sharedPreferences.getString("UserId",""),CurrentPage,20,"36ff4fb4-8ebb-4a04-b98f-82408a950684");
        call.enqueue(new Callback<List<Animal>>() {
            @Override
            public void onResponse(Call<List<Animal>> call, Response<List<Animal>> response) {
                if(response.code()!=200) return;
                isLoading=false;
                progressBar.setVisibility(View.GONE);
                breedAdapter.addAll(response.body());
                Log.v("Tag1",""+breedAdapter.getItemCount()+response.body().size());
            }

            @Override
            public void onFailure(Call<List<Animal>> call, Throwable t) {

            }
        });
    }
    public void LoadNextPage(){
        Call<List<Animal>> call=myAPI.GetFav(sharedPreferences.getString("UserId",""),CurrentPage,20,"36ff4fb4-8ebb-4a04-b98f-82408a950684");
        call.enqueue(new Callback<List<Animal>>() {
            @Override
            public void onResponse(Call<List<Animal>> call, Response<List<Animal>> response) {
                if(response.code()!=200) return;
                breedAdapter.removeLoadingFooter();
                isLoading=false;


                breedAdapter.addAll(response.body());
                if(CurrentPage!=8)
                    breedAdapter.addLoadingFooter();
                else isLastPage=true;
                Log.v("Tag",""+breedAdapter.getItemCount()+response.body().size());
            }

            @Override
            public void onFailure(Call<List<Animal>> call, Throwable t) {

            }
        });
    }
}