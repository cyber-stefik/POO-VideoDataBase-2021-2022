package entities;
import action.Action;
import database.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static java.lang.Integer.valueOf;


public final class Movie extends Video {
    private final int duration;
    private Integer views;
    private ArrayList<Double> ratings;
    private Double grade;
    private Integer isFavorite;
    private Integer popularity;

    public Movie(final String title, final int year, final ArrayList<String> cast,
                 final ArrayList<String> genres, final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
        this.views = 0;
        this.grade = 0.0;
        ratings = new ArrayList<>();
        isFavorite = 0;
        popularity = 0;
    }

    public int getDuration() {
        return duration;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(final Integer views) {
        this.views = views;
    }

    /**
     */
    public void getFinalGrade() {
        double sum = 0;
        for (Double rating : ratings) {
            sum += rating;
        }
        this.grade = sum / ratings.size();
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return a specific movie
     */
    public static Movie getMovie(final Database database,
                                 final Action action) {
        ArrayList<Movie> movies = database.getMoviesData();
        for (Movie movie : movies) {
            if (Objects.equals(movie.getTitle(), action.getTitle())) {
                return movie;
            }
        }
        return null;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(final Integer popularity) {
        this.popularity = popularity;
    }

    public ArrayList<Double> getRatings() {
        return ratings;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(final Double grade) {
        this.grade = grade;
    }

    public void setRatings(final ArrayList<Double> ratings) {
        this.ratings = ratings;
    }

    public Integer getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(final Integer isFavorite) {
        this.isFavorite = isFavorite;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of strings which contain movie names
     */
    public static ArrayList<String> favoriteMovie(final Database database,
                                                  final Action action) {
        // importedLollies you naughty, naughty
        ArrayList<Movie> movies = database.getMoviesData();
        ArrayList<User> users = database.getUsersData();
        // setting the number of favorite occurrences
        mySetIsFavorites(movies, users);
        // sorting by the number of favorite occurrences
        Comparator<Movie> comparator;
        comparator = Comparator.comparing(Movie::getIsFavorite);
        // 2nd criteria is title
        comparator = comparator.thenComparing(Video::getTitle);
        movies.sort(comparator);
        // reversing the list if the sort type is descending
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(movies);
        }
        ArrayList<String> titles = new ArrayList<>();
        for (Movie movie : movies) {
            Integer year = movie.getYear();
            // verifying filters
            if (action.getFilters().get(0).get(0) != null) {
                Integer actionYear = Integer.valueOf(action.getFilters().get(0).get(0));
                ArrayList<String> genres = movie.getGenres();
                if (movie.getIsFavorite() != 0
                        && year.equals(actionYear)
                        && genres.contains(action.getFilters().get(1).get(0))) {
                    titles.add(movie.getTitle());
                }
            }
        }
        return titles;
    }

    /**
     * @param movies arraylist of movies
     * @param users arraylist of users
     * This function calculates the occurrences of a movie in users favorites
     * list
     */
    public static void mySetIsFavorites(final ArrayList<Movie> movies,
                                  final ArrayList<User> users) {
        for (Movie movie : movies) {
            int favorites = 0;
            for (User user : users) {
                if (user.getFavoriteVideos().contains(movie.getTitle())) {
                    favorites++;
                }
            }
            movie.setIsFavorite(favorites);
        }
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of movie names, sorted by the duration asc/desc
     */
    public static ArrayList<String> longestMovie(final Database database,
                                                 final Action action) {
        ArrayList<Movie> movies = database.getMoviesData();
        ArrayList<String> titles = new ArrayList<>();
        String sortType = action.getSortType();
        Comparator<Movie> comparator;
        // sorting by duration
        comparator = Comparator.comparing(Movie::getDuration);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        movies.sort(comparator);
        // if sort type is desc, reverse the arraylist
        if (!sortType.equals("asc")) {
            Collections.reverse(movies);
        }
        for (Movie movie : movies) {
            ArrayList<String> genres = movie.getGenres();
            Integer year = movie.getYear();
            // verifying filters
            if (action.getFilters().get(0).get(0) != null
                && action.getFilters().get(1).get(0) != null) {
                Integer actionYear = Integer.valueOf(action.getFilters().get(0).get(0));
                if (movie.getDuration() != 0
                        && genres.contains(action.getFilters().get(1).get(0))
                        && year.equals(actionYear)) {
                    // adding the title
                    titles.add(movie.getTitle());
                }
            }
        }
        return titles;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of movie names, sorted by rating
     */
    public static ArrayList<String> ratingMovies(final Database database,
                                                 final Action action) {
        ArrayList<Movie> movies = database.getMoviesData();
        ArrayList<String> titles = new ArrayList<>();
        Comparator<Movie> comparator;
        // sorting by grades
        comparator = Comparator.comparing(Movie::getGrade);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        movies.sort(comparator);
        // reverse the arraylist so the first one is the highest ranked
        Collections.reverse(movies);
        for (Movie movie : movies) {
            ArrayList<String> genres = movie.getGenres();
            Integer year = movie.getYear();
            Integer actionYear = valueOf(action.getFilters().get(0).get(0));
            // verifying filters
            if (movie.getGrade() != 0
                    && genres.contains(action.getFilters().get(1).get(0))
                    && year.equals(actionYear)) {
                titles.add(movie.getTitle());
            }
        }
        return titles;
    }

    /**
     * @param movies arraylist of movies
     * @param users arraylist of users
     * This function sets views for a movie from every user
     */
    public static void mySetViews(final ArrayList<Movie> movies,
                                  final ArrayList<User> users) {
        for (Movie movie : movies) {
            int totalViews = 0;
            for (User user : users) {
                if (user.getHistory().get(movie.getTitle()) != null) {
                    totalViews += user.getHistory().get(movie.getTitle());
                }
            }
            movie.setViews(totalViews);
        }
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of strings which contains movie names, sorted by
     *         the most viewed asc/desc
     */
    public static ArrayList<String> mostViewed(final Database database,
                                               final Action action) {
        ArrayList<Movie> movies = database.getMoviesData();
        ArrayList<User> users = database.getUsersData();
        ArrayList<String> titles = new ArrayList<>();
        // using mySetViews to set the views for every movie
        mySetViews(movies, users);
        Comparator<Movie> comparator;
        // sorting by the number of views
        comparator = Comparator.comparing(Movie::getViews);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        movies.sort(comparator);
        // reverse if sort type is desc so the first is the most viewed
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(movies);
        }
        for (Movie movie : movies) {
            Integer year = movie.getYear();
            // verifying filters
            if (action.getFilters().get(0).get(0) != null) {
                Integer actionYear = Integer.valueOf(action.getFilters().get(0).get(0));
                ArrayList<String> genres = movie.getGenres();
                if (movie.getViews() != 0
                        && year.equals(actionYear)
                        && genres.contains(action.getFilters().get(1).get(0))) {
                    titles.add(movie.getTitle());
                }
            }
        }
        return titles;
    }

    @Override
    public String toString() {
        return "Movie{"
                + "duration="
                + duration
                + ", views=" + views
                + ", ratings=" + ratings
                + ", grade=" + grade
                + ", isFavorite=" + isFavorite
                + ", popularity=" + popularity
                + '}';
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, views, grade);
    }


}
