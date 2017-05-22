package ar.edu.utn.frba.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ar.edu.utn.frba.myapplication.R;
import ar.edu.utn.frba.myapplication.api.ImdbApi;
import ar.edu.utn.frba.myapplication.model.MovieListResponse;
import ar.edu.utn.frba.myapplication.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PremiumFragment extends Fragment {


    public static PremiumFragment newInstance() {
        return new PremiumFragment();
    }

    @BindView(R.id.movie_list)
    RecyclerView mMovieList;

    ImdbApi mApiService;
    ProgressDialog mLoadingDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiService = Util.createNetworkClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mLoadingDialog = new ProgressDialog(getContext());
        mLoadingDialog.setMessage(getResources().getString(R.string.placeholder_loading));

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMovieList.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onStart() {
        super.onStart();

        mLoadingDialog.show();

        Call<MovieListResponse> response = mApiService.getUpcomingMovies();
        response.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                if(response.isSuccessful()){
                    MovieListResponse movieListResponse = response.body();

                    MovieListAdapter adapter = new MovieListAdapter(movieListResponse.getResults());
                    mMovieList.setAdapter(adapter);
                }
                mLoadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.connection_error, Toast.LENGTH_SHORT).show();
                mLoadingDialog.dismiss();
            }
        });
    }


}
