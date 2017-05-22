package ar.edu.utn.frba.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ar.edu.utn.frba.myapplication.model.MovieSummaryInList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieItemViewHolder>{

    MovieSummaryInList[] items;

    public MovieListAdapter(MovieSummaryInList[] items) {
        this.items = items;
    }

    @Override
    public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(MovieItemViewHolder holder, int position) {
        holder.bind(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

}