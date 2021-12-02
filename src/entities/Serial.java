package entities;

import action.Action;
import database.Database;
import entertainment.Season;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Comparator;
import java.util.Collections;

import static java.lang.Integer.valueOf;

public final class Serial extends Video {
    private final int numberOfSeasons;
    private final ArrayList<Season> seasons;
    private Integer views;
    private Double grade;
    private Double finalGrade;
    private Integer duration;
    private Integer isFavorite;

    public Serial(final String title, final ArrayList<String> cast,
                  final ArrayList<String> genres,
                  final int numberOfSeasons,
                  final ArrayList<Season> seasons,
                  final int year) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
        views = 0;
        grade = 0.0;
        finalGrade = 0.0;
        duration = 0;
        isFavorite = 0;
    }

    public int getNumberSeason() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return the serial with the name action.getTitle()
     */
    public static Serial getSerial(final Database database,
                                   final Action action) {
        ArrayList<Serial> serials = database.getSerialsData();
        for (Serial serial : serials) {
            if (Objects.equals(serial.getTitle(), action.getTitle())) {
                return serial;
            }
        }
        return null;
    }

    /**
     * @return a final grade for a serial
     */
    public Double myGetGrade() {
        double x;
        double y = 0.0;
        // iterating through the seasons of a serial
        for (Season season : seasons) {
            x = 0.0;
            for (Double rating : season.getRatings()) {
                // adding the rating of every season
                x += rating;
            }
            if (season.getRatings().size() != 0) {
                // y is the rating of every season
                y += x / season.getRatings().size();
            }
        }
        // returning the final grade
        this.finalGrade = y / this.numberOfSeasons;
        return this.finalGrade;
    }

    /**
     * @param action adds to this.grade the action.grade
     */
    public void mySetGrade(final Action action) {
        this.grade += action.getGrade();
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public double getGrade() {
        return this.grade;
    }

    /**
     * @return the final grade
     */
    public double getFinalGrade() {
        this.finalGrade = this.grade / this.getNumberSeason();
        return this.finalGrade;
    }

    /**
     * @param serial calculates and return a serial duration
     * @return the duration
     */
    public static Integer getSerialDuration(final Serial serial) {
            int sum = 0;
            for (Season season : serial.getSeasons()) {
                sum += season.getDuration();
            }
            serial.setDuration(sum);
            return serial.getDuration();
    }

    /**
     * @param serials arraylist of serials
     * @param users arraylist of users
     */
    public static void mySerialSetViews(final ArrayList<Serial> serials,
                                        final ArrayList<User> users) {
        setSerialViews(serials, users);
    }

    private static void setSerialViews(final ArrayList<Serial> serials,
                                       final ArrayList<User> users) {
        for (Serial serial : serials) {
            int totalViews = 0;
            for (User user : users) {
                if (user.getHistory().get(serial.getTitle()) != null) {
                    totalViews += user.getHistory().get(serial.getTitle());
                }
            }
            serial.setViews(totalViews);
        }
    }


    public Integer getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(final Integer isFavorite) {
        this.isFavorite = isFavorite;
    }

    public static ArrayList<Serial> filterSerial(final Database database,
                                                final Action action) {
        ArrayList<Serial> serials = new ArrayList<>(database.getSerialsData());
        // magic number
        int magicNumber = 0;
        if (action.getFilters().get(magicNumber).get(0) != null) {
            for (String year : action.getFilters().get(magicNumber)) {
                // if an actor does not contain an award, remove it from the
                // arraylist
                serials.removeIf(serial ->
                        serial.getYear() != Integer.valueOf(year));
            }
        }
        magicNumber = 1;
        if (action.getFilters().get(magicNumber).get(0) != null) {
            for (String genre : action.getFilters().get(magicNumber)) {
                // if an actor does not contain a filter word, remove it from
                // the arraylist
                serials.removeIf(serial ->
                        !serial.getGenres().contains(genre));
            }
        }
        return serials;
    }



    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of strings with the longest serials
     */
    public static ArrayList<String> longestSerial(final Database database,
                                                  final Action action) {
        ArrayList<Serial> serials = filterSerial(database, action);
        ArrayList<String> titles = new ArrayList<>();
        String sortType = action.getSortType();
        // setting the duration for every serial
        for (Serial serial : serials) {
            serial.setDuration(Serial.getSerialDuration(serial));
        }
        Comparator<Serial> comparator;
        // sorting by duration
        comparator = Comparator.comparing(Serial::getDuration);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        serials.sort(comparator);
        // if sort type is desc, reverse the arraylist
        if (!sortType.equals("asc")) {
            Collections.reverse(serials);
        }
        int i = 0;
        for (Serial serial : serials) {
            if (i == action.getNumber()) {
                break;
            } else {
                i++;
                titles.add(serial.getTitle());
            }
        }
        // iterate through the serials
        return titles;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of strings with favorite serials
     */
    public static ArrayList<String> favoriteSerial(final Database database,
                                                   final Action action) {
        ArrayList<Serial> serials = filterSerial(database, action);
        ArrayList<User> users = database.getUsersData();
        // calculating the number of favorite occurrences in ever user's
        // favorite videos
        mySetIsFavoriteSerial(serials, users);

        Comparator<Serial> comparator;
        // sort by number of favorite occurrences
        comparator = Comparator.comparing(Serial::getIsFavorite);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        serials.sort(comparator);

        // if sort type is desc, reverse the arraylist
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(serials);
        }

        ArrayList<String> titles = new ArrayList<>();
        int i = 0;
        for (Serial serial : serials) {
            if (i == action.getNumber()) {
                break;
            } else {
                i++;
                titles.add(serial.getTitle());
            }
        }
        // iterate through the serials

        return titles;
    }

    public static void mySetIsFavoriteSerial(ArrayList<Serial> serials,
                                  ArrayList<User> users) {
        for (Serial serial : serials) {
            int favorites = 0;
            for (User user : users) {
                if (user.getFavoriteVideos().contains(serial.getTitle())) {
                    favorites++;
                }
            }
            serial.setIsFavorite(favorites);
        }
        serials.removeIf(serial ->
                serial.getIsFavorite() == 0);
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist with the best rating serials
     */
    public static ArrayList<String> ratingSerials(final Database database,
                                                  final Action action) {
        ArrayList<Serial> serials = filterSerial(database, action);
        Comparator<Serial> comparator;
        // sort by grades
        for (Serial serial : serials) {
            serial.myGetGrade();
        }
        serials.removeIf(serial ->
                        serial.getGrade() == 0);
        comparator = Comparator.comparing(Serial::getGrade);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        serials.sort(comparator);

        // if sort type is desc, reverse the arraylist
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(serials);
        }
        ArrayList<String> titles = new ArrayList<>();
        // iterate through the serials
        int i = 0;
        for (Serial serial : serials) {
            if (i == action.getNumber()) {
                break;
            } else {
                titles.add(serial.getTitle());
                i++;
            }
        }
        return titles;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of strings with the mostviewed serials
     */
    public static ArrayList<String> mostViewedSerial(final Database database,
                                                     final Action action) {
        ArrayList<Serial> serials = filterSerial(database, action);
        ArrayList<User> users = database.getUsersData();
        // setting the views
        setSerialViews(serials, users);
        serials.removeIf(serial ->
                        serial.getViews() == 0);
        Comparator<Serial> comparator;
        // sort by the views
        comparator = Comparator.comparing(Serial::getViews);
        // 2nd criteria is the title
        comparator = comparator.thenComparing(Video::getTitle);
        serials.sort(comparator);
        // if sort type is desc, reverse the arraylist
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(serials);
        }
        ArrayList<String> titles = new ArrayList<>();
        int i = 0;
        // iterate through the serials
        for (Serial serial : serials) {
            if (i == action.getNumber()) {
                break;
            } else {
                titles.add(serial.getTitle());
            }
        }
        return titles;
    }

    @Override
    public String toString() {
        return "SerialInputData{" + " title= "
                + super.getTitle() + " " + " year= "
                + super.getYear() + " cast {"
                + super.getCast() + " }\n" + " genres {"
                + super.getGenres() + " }\n "
                + " numberSeason= " + numberOfSeasons
                + ", seasons=" + seasons + " grade="
                + grade + " \n";
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(final Integer views) {
        this.views = views;
    }


}
