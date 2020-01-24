package c.kevin.splash;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Movie  {
    private String title;
    private String image;
    private double rating;
    private int releaseYear;
    private ArrayList<String> genre;

    Movie(String title, String image, double rating, int releaseYear, ArrayList<String> genre) {
        this.title = title;
        this.image = image;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.genre=genre;
    }

    String getTitle() {
        return title;
    }
    String getImage() {
        return image;
    }
    double getRating() {
        return rating;
    }
    int getReleaseYear() {
        return releaseYear;
    }
    ArrayList<String> getGenre() {
        return genre;
    }


    @NonNull
    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", rating=" + rating +
                ", releaseYear=" + releaseYear +
                ", genre=" + genre +
                '}';
    }
}
