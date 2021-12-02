package entities;

import java.util.ArrayList;

public abstract class Video {
    private final String title;
    private final int year;
    private final ArrayList<String> cast;
    private final ArrayList<String> genres;
    private double videoGrade = 0.0;
    private Integer videoIsFavorite = 0;

    public Video(final String title, final int year,
                     final ArrayList<String> cast, final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }

    public Video() {
        this.title = null;
        this.year = 0;
        this.cast = null;
        this.genres = null;
    }

    /**
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return
     */
    public int getYear() {
        return year;
    }

    /**
     * @return
     */
    public ArrayList<String> getCast() {
        return cast;
    }

    /**
     * @return
     */
    public ArrayList<String> getGenres() {
        return genres;
    }

    /**
     * @return
     */
    public double getVideoGrade() {
        return videoGrade;
    }

    /**
     * @param finalGrade
     */
    public void setVideoGrade(final double finalGrade) {
        this.videoGrade = finalGrade;
    }

    /**
     * @return
     */
    public Integer getVideoIsFavorite() {
        return videoIsFavorite;
    }

    /**
     * @param videoIsFavorite
     */
    public void setVideoIsFavorite(final Integer videoIsFavorite) {
        this.videoIsFavorite = videoIsFavorite;
    }
}
