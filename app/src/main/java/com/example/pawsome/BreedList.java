package com.example.pawsome;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.SearchView;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BreedList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BreedList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BreedList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BreedList.
     */
    // TODO: Rename and change types and number of parameters

    public static BreedList newInstance(String param1, String param2) {
        BreedList fragment = new BreedList();
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

    @Override
    public void onAttach(@NonNull  Context context) {
        super.onAttach(context);
        breedListListener=(BreedListListener)context;
    }

    public interface BreedListListener{
        void InputSent(Animal a);
    }
    private SharedPreferences sharedPreferences;
    private BreedListListener breedListListener;
    private ProgressBar progressBar;
    private boolean isLastPage=false;
    private boolean isLoading=false;
    private int CurrentPage=1;
    private RecyclerView recyclerView;
    private BreedAdapter breedAdapter;
    private BreedAdapter searchAdapter;
    private  LinearLayoutManager linearLayoutManager;
    private  final Retrofit retrofit=new Retrofit.Builder().baseUrl("https://api.thedogapi.com/").
            addConverterFactory(GsonConverterFactory.create())
            .build();
    private final MyAPI myAPI=retrofit.create(MyAPI.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        CurrentPage=1;
        View view=inflater.inflate(R.layout.fragment_breed_list, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        progressBar=view.findViewById(R.id.Progress);
        searchAdapter=new BreedAdapter(getContext());
        breedAdapter=new BreedAdapter(getContext());
        setHasOptionsMenu(true);
        linearLayoutManager=new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        PaginationScrollListener paginationScrollListener = new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
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
        recyclerView.addOnItemTouchListener(new RecycleViewListener(getContext(), recyclerView, new RecycleViewListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                breedListListener.InputSent(((BreedAdapter)recyclerView.getAdapter()).getItem(position));
               // Log.v("+",view.getVisibility()+" "+R.id.Favourite);
            }

            @Override
            public void onLongClick(View view, int position) {
                //AddFav(((BreedAdapter)recyclerView.getAdapter()).getItem(position));
                //breedListListener.InputSent(((BreedAdapter)recyclerView.getAdapter()).getItem(position));
            }
        }));




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadFirstPage();
    }

    public void LoadFirstPage(){
        Log.v("1",""+CurrentPage);
        Call<List<Animal>> call=myAPI.getAnimals(1,20,"36ff4fb4-8ebb-4a04-b98f-82408a950684");
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
        Log.v("2",""+CurrentPage);
        CurrentPage+=1;
        Call<List<Animal>> call=myAPI.getAnimals(CurrentPage,20,"36ff4fb4-8ebb-4a04-b98f-82408a950684");
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

    private int iterator;
    public void searchDog(String s){
        CurrentPage=1;
        if(searchAdapter.getItemCount()!=0)
            searchAdapter.clear();
         progressBar.setVisibility(View.VISIBLE);
         Call<List<Animal>> call=myAPI.getDog(1,20,s,"36ff4fb4-8ebb-4a04-b98f-82408a950684");
         Log.v("Tag",s);
         call.enqueue(new Callback<List<Animal>>() {
             @Override
             public void onResponse(Call<List<Animal>> call, Response<List<Animal>> response) {
                 if(response.code()!=200) return;

                 if(response.body()!=null) {
                     Log.v("Tag","Response");
                     progressBar.setVisibility(View.GONE);
                     searchAdapter.addAll(response.body());
                     iterator=0;
                     while (iterator<searchAdapter.getItemCount()){

                         Animal animal=searchAdapter.getItem(iterator);
                         iterator++;
                         if(animal.getReference_image_id()!=null){
                             Call<Image> c=myAPI.getImageUrl(animal.getReference_image_id(),"36ff4fb4-8ebb-4a04-b98f-82408a950684");
                             c.enqueue(new Callback<Image>() {
                                 @Override
                                 public void onResponse(Call<Image> call, Response<Image> response) {
                                     if(response.code()==200){
                                         if(!response.body().getUrl().equals("")){
                                             animal.SetJsonObj();
                                             animal.getImage().addProperty("url",response.body().getUrl());
                                             searchAdapter.notifyDataSetChanged();
                                             Log.v("Hello",response.body().getUrl());
                                         }

                                     }

                                     Log.v("Str",""+response.code());
                                 }

                                 @Override
                                 public void onFailure(Call<Image> call, Throwable t) {

                                 }
                             });
                         }
                     }
                     if(searchAdapter.getItemCount()!=0)
                     recyclerView.setAdapter(searchAdapter);
                 }
                 Log.v("Tag","Res");
             }

             @Override
             public void onFailure(Call<List<Animal>> call, Throwable t) {

             }
         });
    }
    public void AddFav(Animal animal){
        Fav fav=new Fav();
        if(animal.getImage()!=null)
        fav.setImage_id(animal.getImage().get("id").getAsString());
        fav.setSub_id(sharedPreferences.getString("UserId",""));
        Call<ResponseBody> call=myAPI.postVote(fav,"36ff4fb4-8ebb-4a04-b98f-82408a950684");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    Log.v("Suc",""+response.code());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.searchview,menu);
        MenuItem menuItem= menu.findItem(R.id.app_bar_search);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.equals(""))
                    searchDog(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setAdapter(breedAdapter);
                return false;
            }
        });
    }


}