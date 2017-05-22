package ar.edu.utn.frba.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ar.edu.utn.frba.myapplication.model.MovieSummaryInList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.movie_image)
    ImageView mMovieImage;

    @BindView(R.id.movie_title)
    TextView mMovieTitle;

    @BindView(R.id.movie_release_date)
    TextView mMovieReleaseDate;

    MovieSummaryInList movie;

    public MovieItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(MovieSummaryInList movieSummaryInList){

        movie = movieSummaryInList;

        mMovieTitle.setText(movieSummaryInList.getOriginalTitle());
        mMovieReleaseDate.setText(mMovieReleaseDate.getResources().getString(R.string.placeholder_release_date, movieSummaryInList.getFormattedDate()));
        Picasso
                .with(mMovieImage.getContext())
                .load("http://image.tmdb.org/t/p/w185/" + movieSummaryInList.getPosterPath())
                .into(mMovieImage);
    }

    @OnClick(R.id.movie_item)
    public void itemSelected(){
        Toast.makeText(mMovieTitle.getContext(), movie.getOriginalTitle(), Toast.LENGTH_SHORT).show();
    }

}