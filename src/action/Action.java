package action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import database.Database;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;


public final class Action {
    private final int actionId;
    /**
     * Type of action
     */
    private final String actionType;
    /**
     * Used for commands
     */
    private final String type;
    /**
     * Username of user
     */
    private final String username;
    /**
     * The type of object on which the actions will be performed
     */
    private final String objectType;
    /**
     * Sorting type: ascending or descending
     */
    private final String sortType;
    /**
     * The criterion according to which the sorting will be performed
     */
    private final String criteria;
    /**
     * Video title
     */
    private final String title;
    /**
     * Video genre
     */
    private final String genre;
    /**
     * Query limit
     */
    private final int number;
    /**
     * Grade for rating - aka value of the rating
     */
    private final double grade;
    /**
     * Season number
     */
    private final int seasonNumber;
    /**
     * Filters used for selecting videos
     */
    private List<List<String>> filters = new ArrayList<>();

    public Action(final int actionId, final String actionType,
                              final String type, final String username, final String genre) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.type = type;
        this.username = username;
        this.genre = genre;
        this.objectType = null;
        this.sortType = null;
        this.criteria = null;
        this.number = 0;
        this.title = null;
        this.grade = 0;
        this.seasonNumber = 0;
    }

    public Action(final int actionId, final String actionType, final String objectType,
                           final String genre, final String sortType, final String criteria,
                           final String year, final int number, final List<String> words,
                           final List<String> awards) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.objectType = objectType;
        this.sortType = sortType;
        this.criteria = criteria;
        this.number = number;
        this.filters.add(new ArrayList<>(Collections.singleton(year)));
        this.filters.add(new ArrayList<>(Collections.singleton(genre)));
        this.filters.add(words);
        this.filters.add(awards);
        this.title = null;
        this.type = null;
        this.username = null;
        this.genre = null;
        this.grade = 0;
        this.seasonNumber = 0;
    }

    public Action(final int actionId,
                  final String actionType,
                  final String type,
                  final String username,
                  final String objectType,
                  final String sortType,
                  final String criteria,
                  final String title,
                  final String genre,
                  final int number,
                  final double grade,
                  final int seasonNumber,
                  final List<List<String>> filters) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.type = type;
        this.grade = grade;
        this.username = username;
        this.title = title;
        this.seasonNumber = seasonNumber;
        this.genre = genre;
        this.objectType = objectType;
        this.sortType = sortType;
        this.criteria = criteria;
        this.number = number;
        this.filters = filters;
    }

    /**
     * @param database database which contains info about every entity
     * @param jsonArray the jsonArray where I'll write the result
     * @param writer function to write in the jsonArray
     * @param actions list of actions to be executed
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void choose(final Database database, final JSONArray jsonArray,
                              final Writer writer, final List<ActionInputData> actions)
                                throws IOException {
        ArrayList<Action> actions2 = new ArrayList<>();
        for (ActionInputData action1 : actions) {
            // populating actions2 with ActionInputData actions
            actions2.add(new Action(action1.getActionId(), action1.getActionType(),
                    action1.getType(), action1.getUsername(),
                    action1.getObjectType(), action1.getSortType(),
                    action1.getCriteria(), action1.getTitle(),
                    action1.getGenre(), action1.getNumber(),
                    action1.getGrade(), action1.getSeasonNumber(),
                    action1.getFilters()));
        }
        for (Action action : actions2) {
            // switch for every type of action
            switch (action.getActionType()) {
                // calling static functions to solve the action
                case "command" -> Command.command(database, jsonArray, action,
                        writer);
                case "query" -> Query.query(database, jsonArray, action,
                        writer);
                case "recommendation" -> Recommend.recommend(database,
                                        jsonArray, action, writer);
                default -> {
                }
            }
        }
    }

    public int getActionId() {
        return actionId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getSortType() {
        return sortType;
    }

    public String getCriteria() {
        return criteria;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getNumber() {
        return number;
    }

    public double getGrade() {
        return grade;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public List<List<String>> getFilters() {
        return filters;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, actionType, type, username, objectType,
                sortType, criteria, title, genre, number, grade,
                seasonNumber, filters);
    }

    @Override
    public String toString() {
        return "ActionInputData{"
                + "actionId=" + actionId
                + ", actionType='" + actionType + '\''
                + ", type='" + type + '\''
                + ", username='" + username + '\''
                + ", objectType='" + objectType + '\''
                + ", sortType='" + sortType + '\''
                + ", criteria='" + criteria + '\''
                + ", title='" + title + '\''
                + ", genre='" + genre + '\''
                + ", number=" + number
                + ", grade=" + grade
                + ", seasonNumber=" + seasonNumber
                + ", filters=" + filters
                + '}' + "\n";
    }
}
