package ar.edu.utn.frba.myapplication.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieSummaryInList {

    /*
    {
      "poster_path": "/z6BP8yLwck8mN9dtdYKkZ4XGa3D.jpg",
      "adult": false,
      "overview": "A big screen remake of John Sturges' classic western The Magnificent Seven, itself a remake of Akira Kurosawa's Seven Samurai. Seven gun men in the old west gradually come together to help a poor village against savage thieves.",
      "release_date": "2016-09-22",
      "genre_ids": [
        37,
        28
      ],
      "id": 333484,
      "original_title": "The Magnificent Seven",
      "original_language": "en",
      "title": "The Magnificent Seven",
      "backdrop_path": "/g54J9MnNLe7WJYVIvdWTeTIygAH.jpg",
      "popularity": 3.716729,
      "vote_count": 35,
      "video": false,
      "vote_average": 4.24
    }
    */

    private int id;
    private String originalTitle;
    private String posterPath;
    private Date releaseDate;

    public MovieSummaryInList(int id, String originalTitle, String posterPath, Date releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getFormattedDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(getReleaseDate());
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
