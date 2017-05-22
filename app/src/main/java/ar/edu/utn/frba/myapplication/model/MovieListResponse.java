package ar.edu.utn.frba.myapplication.model;

public class MovieListResponse {

    private MovieSummaryInList[] results;

    public MovieListResponse(MovieSummaryInList[] results) {
        this.results = results;
    }

    public MovieSummaryInList[] getResults() {
        return results;
    }

    public void setResults(MovieSummaryInList[] results) {
        this.results = results;
    }
}
