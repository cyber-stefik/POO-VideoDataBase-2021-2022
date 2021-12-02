package entities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import database.Database;
import action.Action;
import actor.ActorsAwards;
import utils.Utils;

public final class Actor {
    private String name;
    private final String careerDescription;
    private ArrayList<String> filmography;
    private final Map<ActorsAwards, Integer> awards;
    private Double rating;
    private Double avgRate;
    private Integer totalAwards;

    public Actor(final String name, final String careerDescription,
                 final ArrayList<String> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = awards;
        rating = 0.0;
        avgRate = 0.0;
        totalAwards = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public void setFilmography(final ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public Double getRatings() {
        return rating;
    }

    public void setRatings(final Double ratings) {
        this.rating = ratings;
    }

    public Double getAvgRate() {
        return avgRate;
    }

    /**
     * @param rate sum of rates of all the movies an actor has played in
     * @param noRoles number of movies an actor has played in
     */
    public void setAvgRate(final Double rate, final Integer noRoles) {
        this.avgRate = rate / noRoles;
    }

    public Integer getTotalAwards() {
        return totalAwards;
    }

    public void setTotalAwards(final Integer totalAwards) {
        this.totalAwards = totalAwards;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of Strings, which has the names of actors
     */
    public static ArrayList<String> average(final Database database, final Action action) {
        ArrayList<Movie> movies = new ArrayList<>(database.getMoviesData());
        ArrayList<Actor> actors = filterActors(database, action);
        double rate;
        int count;
        for (Actor actor : actors) {
            count = 0;
            rate = 0.0; // rate of movies in which an actor has played in
            for (Movie movie : movies) {
                // verifying if the movie cast contains the actor
                if (movie.getCast().contains(actor.getName())) {
                    if (movie.getGrade() != 0) {
                        // adding to rate
                        rate += movie.getGrade();
                        // counting the number of movies
                        count++;
                    }
                }
            }
            // doing the same for serials
            for (Serial serial : database.getSerialsData()) {
                if (serial.getCast().contains(actor.getName())) {
                    if (serial.getGrade() != 0) {
                        rate += serial.getFinalGrade();
                        count++;
                    }
                }
            }
            if (count != 0) {
                // setting the average rate of an actor
                actor.setAvgRate(rate, count);
            }
        }
        actors.removeIf(actor ->
                        actor.getAvgRate() == 0);
        // sorting by average rate
        Comparator<Actor> comparator = Comparator.comparing(Actor::getAvgRate);
        // 2nd criteria is the name
        comparator = comparator.thenComparing(Actor::getName);
        actors.sort(comparator);
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(actors);
        }
        ArrayList<String> actorsReturn = new ArrayList<>();
        int i = 0;
        // indexing through actors
        for (Actor actor : actors) {
            // stop if all actors wanted are added
            if (i == action.getNumber()) {
                break;
            } else if (actor.getAvgRate() != 0) {
                // adding to the list of names
                actorsReturn.add(actor.getName());
                i++;
            }
        }
        return actorsReturn;
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of actors which have the awards and the words in
     *         their description
     */
    public static ArrayList<Actor> filterActors(final Database database,
                                                final Action action) {
        ArrayList<Actor> actors = new ArrayList<>(database.getActorsData());
        // magic number
        int magicNumber = 3;
        if (action.getFilters().get(magicNumber) != null) {
            for (String award : action.getFilters().get(magicNumber)) {
                // if an actor does not contain an award, remove it from the
                // arraylist
                actors.removeIf(actor ->
                        !actor.getAwards().containsKey(Utils
                                .stringToAwards(award)));
            }
        }
        magicNumber = 2;
        if (action.getFilters().get(magicNumber) != null) {
            for (String word : action.getFilters().get(magicNumber)) {
                // if an actor does not contain a filter word, remove it from
                // the arraylist
                actors.removeIf(actor ->
                        !Utils.getWord(actor.getCareerDescription(), word));
            }
        }
        return actors;
    }

    /**
     * @param actor an actor, who will have his awards calculated
     */
    public static void setActorAwards(final Actor actor) {
        int sum = 0;
        // calculating the total number of awards
        if (actor.getAwards().containsKey(ActorsAwards.BEST_SCREENPLAY)) {
            sum += actor.getAwards().get(ActorsAwards.BEST_SCREENPLAY);
        }
        if (actor.getAwards().containsKey(ActorsAwards.BEST_SUPPORTING_ACTOR)) {
            sum += actor.getAwards().get(ActorsAwards.BEST_SUPPORTING_ACTOR);
        }
        if (actor.getAwards().containsKey(ActorsAwards.PEOPLE_CHOICE_AWARD)) {
            sum += actor.getAwards().get(ActorsAwards.PEOPLE_CHOICE_AWARD);
        }
        if (actor.getAwards().containsKey(ActorsAwards.BEST_DIRECTOR)) {
            sum += actor.getAwards().get(ActorsAwards.BEST_DIRECTOR);
        }
        if (actor.getAwards().containsKey(ActorsAwards.BEST_PERFORMANCE)) {
            sum += actor.getAwards().get(ActorsAwards.BEST_PERFORMANCE);
        }
        // setting the actor total awards
        actor.setTotalAwards(sum);
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of strings which contains actors names that have
     *         the filter awards
     */
    public static ArrayList<String> actorAwards(final Database database,
                                                final Action action) {
        ArrayList<Actor> actors = filterActors(database, action);
        // setting the awards number for every actor
        for (Actor actor : actors) {
            setActorAwards(actor);
        }
        if (actors.size() == 0) {
            return null;
        } else {
            ArrayList<String> titles = new ArrayList<>();
            // sorting the arraylist by the total awards of actors
            Comparator<Actor> comparator = Comparator.comparing(Actor::getTotalAwards);
            // 2nd criteria is the name
            comparator = comparator.thenComparing(Actor::getName);
            actors.sort(comparator);
            if (!action.getSortType().equals("asc")) {
                Collections.reverse(actors);
            }
            // adding every actor and return the arraylist of actor names
            for (Actor actor : actors) {
                titles.add(actor.getName());
            }
            return titles;
        }
    }

    /**
     * @param database database which contains info about every entity
     * @param action the action to be executed
     * @return an arraylist of strings which contains the names of actors
     *         that have in their description the filter words
     */
    public static ArrayList<String> filterDescription(final Database database,
                                                     final Action action) {
        ArrayList<Actor> actors = filterActors(database, action);
        // adding every actor name in an arraylist then return in
        ArrayList<String> names = new ArrayList<>();
        Comparator<Actor> comparator = Comparator.comparing(Actor::getName);
        actors.sort(comparator);
        if (!action.getSortType().equals("asc")) {
            Collections.reverse(actors);
        }
        for (Actor actor : actors) {
            names.add(actor.getName());
        }
        return names;
    }

    @Override
    public String toString() {
        return "Actor{"
                + "name='" + name + '\''
                + ", careerDescription='" + careerDescription + '\''
                + ", filmography=" + filmography
                + ", awards=" + awards
                + ", rating=" + rating
                + '}';
    }
}
