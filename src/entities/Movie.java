package entities;
import action.Action;
import database.Database;
import utils.Utils;

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
        for (Double rating : this.ratings) {
            sum += rating;
        }
        if (this.ratings.size() != 0) {
            this.setGrade(sum  / this.ratings.size());
        }
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



    public static ArrayList<Movie> filterMovies(final Database database,
                                                final Action action) {
        ArrayList<Movie> movies = new ArrayList<>(database.getMoviesData());
        // magic number
        int magicNumber = 0;
        if (action.getFilters().get(magicNumber).get(0) != null) {
            for (String year : action.getFilters().get(magicNumber)) {
                // if an actor does not contain an award, remove it from the
                // arraylist
                movies.removeIf(movie ->
                        !(movie.getYear() == Integer.valueOf(year)));
            }
        }
        magicNumber = 1;
        if (action.getFilters().get(magicNumber).get(0) != null) {
            for (String genre : action.getFilters().get(magicNumber)) {
                // if an actor does not contain a filter word, remove it from
                // the arraylist
                movies.removeIf(movie ->
                                !movie.getGenres().contains(genre));
            }
        }
        return movies;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of strings which contain movie names
     */
    public static ArrayList<String> favoriteMovie(final Database database,
                                                  final Action action) {
        // importedLollies you naughty, naughty
        ArrayList<Movie> movies = filterMovies(database, action);
        ArrayList<User> users = new ArrayList<>(database.getUsersData());
        // setting the number of favorite occurrences
        mySetIsFavorites(movies, users);
        movies.removeIf(movie ->
                movie.getIsFavorite() == 0);
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
        int i = 0;
        for (Movie movie : movies) {
            if (i == action.getNumber()) {
                break;
            } else {
                i++;
                titles.add(movie.getTitle());
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
        ArrayList<Movie> movies = filterMovies(database, action);
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
        int i = 0;
        for (Movie movie : movies) {
            if (i == action.getNumber()) {
                break;
            } else {
                titles.add(movie.getTitle());
                i++;
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
        ArrayList<Movie> movies = new ArrayList<>(database.getMoviesData());
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
        ArrayList<Movie> movies = filterMovies(database, action);
        ArrayList<User> users = new ArrayList<>(database.getUsersData());
        ArrayList<String> titles = new ArrayList<>();
        // using mySetViews to set the views for every movie
        mySetViews(movies, users);
        movies.removeIf(movie ->
                        movie.getViews() == 0);
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
        int i = 0;
        for (Movie movie : movies) {
            if (i == action.getNumber()) {
                break;
            } else {
                i++;
                titles.add(movie.getTitle());
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


}
