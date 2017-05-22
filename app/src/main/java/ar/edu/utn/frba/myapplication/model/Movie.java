package ar.edu.utn.frba.myapplication.model;

public class Movie{

    /*
          "genres": [
            {
              "id": 37,
              "name": "Western"
            },
            {
              "id": 28,
              "name": "Action"
            }
          ],
          "overview": "A big screen remake of John Sturges' classic western The Magnificent Seven, itself a remake of Akira Kurosawa's Seven Samurai. Seven gun men in the old west gradually come together to help a poor village against savage thieves.",
          "poster_path": "/z6BP8yLwck8mN9dtdYKkZ4XGa3D.jpg",
          "release_date": "2016-09-22",
          "title": "The Magnificent Seven"
        }
    */

    private Genre[] genres;
    private String overview;
    private String posterPath;
    private String releaseDate;
    private String title;

    public Movie(Genre[] genres, String overview, String posterPath, String releaseDate, String title) {
        this.genres = genres;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.title = title;
    }

    public Genre[] getGenres() {
        return genres;
    }

    public void setGenres(Genre[] genres) {
        this.genres = genres;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
