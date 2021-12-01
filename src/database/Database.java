package database;

import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import fileio.Input;
import java.util.ArrayList;
import entities.Actor;
import entities.Movie;
import entities.Serial;
import entities.User;
import action.Action;


public final class Database {
    private final ArrayList<Actor> actorsData = new ArrayList<>();
    private final ArrayList<User> usersData = new ArrayList<>();
    private final ArrayList<Action> commandsData = new ArrayList<>();
    private final ArrayList<Movie> moviesData = new ArrayList<>();
    private final ArrayList<Serial> serialsData = new ArrayList<>();

    public Database(final Input input) {
        for (ActorInputData actor : input.getActors()) {
            this.actorsData.add(new Actor(actor.getName(), actor.getCareerDescription(),
                                actor.getFilmography(), actor.getAwards()));
        }
        for (UserInputData user : input.getUsers()) {
            this.usersData.add(new User(user.getUsername(), user.getSubscriptionType(),
                                user.getHistory(), user.getFavoriteMovies()));
        }
        for (ActionInputData command : input.getCommands()) {
            this.commandsData.add(new Action(command.getActionId(), command.getActionType(),
                                            command.getType(), command.getUsername(),
                                            command.getObjectType(), command.getSortType(),
                                            command.getCriteria(), command.getTitle(),
                                            command.getGenre(), command.getNumber(),
                                            command.getGrade(), command.getSeasonNumber(),
                                            command.getFilters()));
        }
        for (MovieInputData movie : input.getMovies()) {
            this.moviesData.add(new Movie(movie.getTitle(), movie.getYear(), movie.getCast(),
                                            movie.getGenres(), movie.getDuration()));
        }
        for (SerialInputData serial : input.getSerials()) {
            this.serialsData.add(new Serial(serial.getTitle(), serial.getCast(),
                                serial.getGenres(), serial.getNumberSeason(),
                                serial.getSeasons(), serial.getYear()));
        }
    }

    public ArrayList<Actor> getActorsData() {
        return actorsData;
    }

    public ArrayList<User> getUsersData() {
        return usersData;
    }

    public ArrayList<Movie> getMoviesData() {
        return moviesData;
    }

    public ArrayList<Serial> getSerialsData() {
        return serialsData;
    }

    @Override
    public String toString() {
        return "Database{"
                + "actorsData=" + actorsData
                + ", usersData=" + usersData
                + ", commandsData=" + commandsData
                + ", moviesData=" + moviesData
                + ", serialsData=" + serialsData
                + '}';
    }
}
