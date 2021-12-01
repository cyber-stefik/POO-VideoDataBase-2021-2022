package entities;
import action.Action;
import database.Database;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public final class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final ArrayList<String> favoriteVideos;
    private final ArrayList<String> ratedVideos = new ArrayList<>();

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history,
                final ArrayList<String> favoriteVideos) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.favoriteVideos = favoriteVideos;
        this.history = history;
    }

    public User(final User user) {
        this.username = user.getUsername();
        this.subscriptionType = user.getSubscriptionType();
        this.history = user.getHistory();
        this.favoriteVideos = user.getFavoriteVideos();
    }

    /**
     * @param user the user who wants to add a movie to his favorite videos
     * @param videoTitle the video to be added in the favorite videos list
     * @return a string which is the result of the action
     */
    public String addFavorite(final User user, final String videoTitle) {
        String msg;
        // if the user hasn't watched the movie yet
        if (!user.history.containsKey(videoTitle)) {
            msg = "error -> " + videoTitle + " is not seen";
            // if the user has already added the video in his favorite videos
            // list
        } else if (user.favoriteVideos.contains(videoTitle)) {
            msg = "error -> " + videoTitle + " is already in favourite list";
        } else {
            // add the video
            user.favoriteVideos.add(videoTitle);
            msg = "success -> " + videoTitle + " was added as favourite";
        }
        return msg;
    }

    /**
     * @param user the user who wants to watch a movie
     * @param movie movie to be watched
     * @return a string which is the result of the action
     */
    public String watchMovie(final User user, final Movie movie) {
        String msg;
        int views;
        // if the movie hasn't been watched yet
        if (!user.history.containsKey(movie.getTitle())) {
            views = 0;
            views++;
            user.history.put(movie.getTitle(), views);
            movie.setViews(views);
            msg = "success -> " + movie.getTitle()
                    + " was viewed with total views of " + views;
        } else {
            // update the views of the movie, if it has been already seen
            views = user.history.get(movie.getTitle());
            views++;
            user.history.put(movie.getTitle(), views);
            movie.setViews(views);
            msg = "success -> " + movie.getTitle()
                    + " was viewed with total views of "
                    + movie.getViews();
        }
        return msg;
    }

    /**
     * @param user the user who wants to watch a serial
     * @param serial the serial to be watched
     * @return a string which is the result of the action
     */
    public String watchSerial(final User user, final Serial serial) {
        String msg;
        int views;
        // if the serial hasn't been watched yet
        if (!user.history.containsKey(serial.getTitle())) {
            views = 0;
            views++;
            user.history.put(serial.getTitle(), views);
            serial.setViews(views);
            msg = "success -> " + serial.getTitle() + " was viewed with total views of " + views;
        // else updates the total views
        } else {
            views = user.history.get(serial.getTitle());
            views++;
            user.history.put(serial.getTitle(), views);
            serial.setViews(views);
            msg = "success -> " + serial.getTitle() + " was viewed with total views of "
                    + serial.getViews();
        }
        return msg;
    }

    /**
     * @param user the user who wants to rate a movie
     * @param action the action to be applied
     * @param movie the movie to be rated
     * @return a string which is the result of the action
     */
    public String rateMovie(final User user, final Action action,
                            final Movie movie) {
        String msg;
        String videoTitle = action.getTitle();
        double grade = action.getGrade();
        // if the user has watched the movie and hasn't rated it yet
        if (user.history.containsKey(videoTitle)
            && !user.ratedVideos.contains(videoTitle)) {
                if (!ratedVideos.contains(videoTitle)) {
                    movie.getRatings().add(action.getGrade());
                    user.ratedVideos.add(videoTitle);
                }
                movie.getFinalGrade();
                msg = "success -> " + videoTitle + " was rated with " + grade + " by "
                        + user.getUsername();
        // if the user has already rated the movie
        } else if (user.ratedVideos.contains(videoTitle)) {
            msg = "error -> " + videoTitle + " has been already rated";
        // if the user hasn't seen the movie yet
        } else {
            msg = "error -> " + videoTitle + " is not seen";
        }
        return msg;
    }

    /**
     * @param user the user who wants to rate a serial
     * @param action the action to be applied
     * @param serial the serial to be rated
     * @return a string which is the result of the action
     */
    public String rateSerial(final User user, final Action action,
                             final Serial serial) {
        String msg;
        String videoTitle = action.getTitle();
        double grade;
        // if the user has watched the movie and hasn't rated it yet
        if (user.history.containsKey(videoTitle)) {
            // setting the grade for the serial
            serial.mySetGrade(action);
            // getting the grade to add to serial
            grade = action.getGrade();
            // adding the serial to ratedVideos
            user.ratedVideos.add(serial.getTitle());
            int index = action.getSeasonNumber() - 1;
            serial.getSeasons().get(index).getRatings().add(grade);
            msg = "success -> " + videoTitle + " was rated with " + grade + " by "
                    + user.getUsername();
        // if the serial has already been rated
        } else if (user.ratedVideos.contains(videoTitle)) {
            msg = "error -> " + videoTitle + " has been already rated";
        // if the serial has not been seen yeet
        } else {
            msg = "error -> " + videoTitle + " is not seen";
        }
        return msg;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist with the most active users
     */
    public static ArrayList<String> ratingNumbers(final Database database,
                                                  final Action action) {
        ArrayList<User> users = database.getUsersData();
        ArrayList<String> toReturn = new ArrayList<>();
        String sortType = action.getSortType();
        // sorting by how many ratings a user has given
        users.sort(Comparator.comparingDouble(u -> u.ratedVideos.size()));
        // if sort type is desc, reverse the arraylist
        if (!sortType.equals("asc")) {
            Collections.reverse(users);
        }
        // adding to an arraylist of strings
        for (User user : users) {
            if (user.ratedVideos.size() != 0) {
                toReturn.add(user.getUsername());
            }
        }
        return toReturn;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return a string which is the name of the first unseen movie/serial
     *         in the database
     */
    public static String standardRecommendation(final Database database,
                                                final Action action) {
        ArrayList<User> users = database.getUsersData();
        ArrayList<Movie> movies = database.getMoviesData();
        ArrayList<Serial> serials = database.getSerialsData();
        User userGood = null;
        // getting the user and use the copy constructor
        for (User user : users) {
            if (user.getUsername().equals(action.getUsername())) {
                userGood = new User(user);
            }
        }
        // if the user doesn't exit, return null
        if (userGood == null) {
            return null;
        }
        // iterating through the list of movies and returning the first
        // movie which is not seen
        for (Movie movie : movies) {
            if (!userGood.history.containsKey(movie.getTitle())) {
                return movie.getTitle();
            }
        }
        // if there's no movie to be recommended, iterate through serials
        for (Serial serial : serials) {
            if (!userGood.history.containsKey(serial.getTitle())) {
                return serial.getTitle();
            }
        }
        return null;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist with the movies/serials from a specific genre
     */
    public static ArrayList<String> searchRecommendation(final Database database,
                                                         final Action action) {
        ArrayList<User> users = database.getUsersData();
        ArrayList<Movie> movies = database.getMoviesData();
        ArrayList<Serial> serials = database.getSerialsData();
        User userGood = null;
        for (User user : users) {
            if (user.getUsername().equals(action.getUsername())) {
                userGood = new User(user);
            }
        }
        assert userGood != null;
        if (!userGood.getSubscriptionType().equals("PREMIUM")) {
            return null;
        }
        // sorting movies by the movie grade
        Comparator<Movie> comparator;
        comparator = Comparator.comparing(Movie::getGrade);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        movies.sort(comparator);

        // sorting serial by the serial grade
        Comparator<Serial> comparator2;
        comparator2 = Comparator.comparing(Serial::getGrade);
        // 2nd criteria is the title
        comparator2 = comparator2.thenComparing(Video::getTitle);
        serials.sort(comparator2);
        ArrayList<String> titles = new ArrayList<>();
        // iterate through the movies
        for (Movie movie : movies) {
            // check the genre and if the user has already seen the movie
            if (movie.getGenres().contains(action.getGenre())
                && !userGood.getHistory().containsKey(movie.getTitle())) {
                titles.add(movie.getTitle());
            }
        }
        // iterate through the serials
        for (Serial serial : serials) {
            // check the genre and if the user has already seen the serial
            if (serial.getGenres().contains(action.getGenre())
                && !userGood.getHistory().containsKey(serial.getTitle())) {
                titles.add(serial.getTitle());
            }
        }
        return titles;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return the most popular movie/serial from a certain genre
     */
    public static String popularRecommendation(final Database database,
                                               final Action action) {
        ArrayList<User> users = database.getUsersData();
        ArrayList<Movie> movies = database.getMoviesData();
        User userGood = null;
        HashMap<String, Integer> genres = new HashMap<>();
        for (User user : users) {
            if (user.getUsername().equals(action.getUsername())) {
                userGood = new User(user);
                break;
            }
        }
        assert userGood != null;
        if (!userGood.getSubscriptionType().equals("PREMIUM")) {
            return null;
        }
        // setting the views for movies
        Movie.mySetViews(movies, users);
        for (Movie movie : movies) {
            // calculating the views for every genre
            // and add the genre as a key and views as a value in a hashmap
            for (String genre : movie.getGenres()) {
                // if the genre has been already added, update the value
                // in the hashmap
                if (genres.containsKey(genre)) {
                    Integer aux = genres.get(genre);
                    aux += movie.getViews();
                    genres.replace(genre, aux);
                // if the genre isn't in the hashmap and the movie views
                // exist, put the genre in the hashmap as key and views as value
                } else if (movie.getViews() != null) {
                    genres.put(genre, movie.getViews());
                }
            }
        }
        ArrayList<Serial> serials = database.getSerialsData();
        Serial.mySerialSetViews(serials, users);
        for (Serial serial : serials) {
            // calculating the views for every genre
            // and add the genre as a key and views as a value in a hashmap
            for (String genre : serial.getGenres()) {
                // if the genre has been already added, update the value
                // in the hashmap
                if (genres.containsKey(genre)) {
                    Integer aux = genres.get(genre);
                    aux += serial.getViews();
                    genres.replace(genre, aux);
                // if the genre isn't in the hashmap and the movie views
                // exist, put the genre in the hashmap as key and views as value
                } else if (serial.getViews() != null) {
                    genres.put(genre, serial.getViews());
                }
            }
        }
        // get all genres of all existing movies and serials
        ArrayList<String> allGenres = new ArrayList<>();
        for (Movie movie : movies) {
            for (String genre : movie.getGenres()) {
                if (!allGenres.contains(genre)) {
                    allGenres.add(genre);
                }
            }
        }
        for (Serial serial : serials) {
            for (String genre : serial.getGenres()) {
                if (!allGenres.contains(genre)) {
                    allGenres.add(genre);
                }
            }
        }
        // sort the hashmap by value
        Map<String, Integer> sortHash = genres.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        // create an arraylist of HashMap.Entry
        ArrayList<HashMap.Entry<String, Integer>> map =
                new ArrayList<>(sortHash.entrySet());
        // reverse the arraylist so the most watched genre is the first one
        Collections.reverse(map);
        ArrayList<String> titles = new ArrayList<>();
        // add every title
        for (HashMap.Entry<String, Integer> title : map) {
            titles.add(title.getKey());
        }

        for (Movie movie : movies) {
            for (String title : titles) {
                // if the user has seen a movie of a certain genre
                if (userGood.getHistory().containsKey(movie.getTitle())) {
                    break;
                } else if (!movie.getGenres().contains(title)) {
                    return movie.getTitle();
                }
            }
        }

        for (Serial serial : serials) {
            for (String title : titles) {
                // if the user has seen a serial of a certain genre
                if (userGood.getHistory().containsKey(serial.getTitle())) {
                    break;
                } else if (!serial.getGenres().contains(title)) {
                    return serial.getTitle();
                }
            }
        }
        return null;
    }
    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return a string which is the highest unseen graded movie/serial
     */
    public static String bestunseenRecommendation(final Database database,
                                                  final Action action) {
        ArrayList<User> users = database.getUsersData();
        ArrayList<Movie> movies = database.getMoviesData();
        ArrayList<Serial> serials = database.getSerialsData();
        User userGood = null;
        for (User user : users) {
            if (user.getUsername().equals(action.getUsername())) {
                userGood = new User(user);
                break;
            }
        }
        Comparator<Movie> comparator;
        // sorting the movies by grade
        comparator = Comparator.comparing(Movie::getGrade);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        movies.sort(comparator);
        // reverse the arraylist so the highest graded movie is the first
        Collections.reverse(movies);
        Comparator<Serial> comparator2;
        comparator2 = Comparator.comparing(Serial::getGrade);
        comparator2 = comparator2.thenComparing(Serial::getTitle);
        serials.sort(comparator2);
        Collections.reverse(serials);
        // iterating through the movies to find an unseen one
        for (Movie movie : movies) {
            // if the user hasn't seen the movie, return it
            assert userGood != null;
            if (!userGood.history.containsKey(movie.getTitle())) {
                return movie.getTitle();
            }
        }
        // iterating through the serials to find an unseen one
        for (Serial serial : serials) {
            // if the user hasn't seen the serial, return it
            assert userGood != null;
            if (!userGood.history.containsKey(serial.getTitle())) {
                return serial.getTitle();
            }
        }
        return null;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return a string which has the most favorite occurrences in users
     *         favorite videos
     */
    public static String favoriteRecommendation(final Database database,
                                                final Action action) {
        ArrayList<User> users = database.getUsersData();
        ArrayList<Movie> movies = database.getMoviesData();
        User userGood = null;
        for (User user : users) {
            if (user.getUsername().equals(action.getUsername())) {
                userGood = new User(user);
                break;
            }
        }

        assert userGood != null;
        if (!userGood.getSubscriptionType().equals("PREMIUM")) {
            return null;
        }
        // setting the favorite occurrences for movies
        Movie.mySetIsFavorites(movies, users);
        Comparator<Movie> comparator;
        // sort by grades
        comparator = Comparator.comparing(Movie::getIsFavorite);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        movies.sort(comparator);
        // reverse the arraylist so the first movie is the highest graded
        Collections.reverse(movies);
        for (Movie movie : movies) {
            // if the user hasn't seen the movie yet, return it
            if (!userGood.getHistory().containsKey(movie.getTitle())) {
                return movie.getTitle();
            }
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public ArrayList<String> getFavoriteVideos() {
        return favoriteVideos;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return the user with the name action.getUsername()
     */
    public static User getUser(final Database database, final Action action) {
        ArrayList<User> users = database.getUsersData();
        for (User user : users) {
            if (Objects.equals(user.getUsername(), action.getUsername())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "UserInputData{" + "username='"
                + username + '\'' + ", subscriptionType='"
                + subscriptionType + '\'' + ", history="
                + history + ", favoriteMovies="
                + favoriteVideos
                + "ratedVideos=" + ratedVideos + '}';
    }
}
