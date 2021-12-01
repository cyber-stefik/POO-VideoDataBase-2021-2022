package action;

import database.Database;
import entities.Movie;
import entities.Serial;
import entities.User;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.Objects;

public final class Command {
    private Command() {
    }

    /**
     * @param database database which contains info about every entity
     * @param jsonArray the jsonArray where I'll write the result
     * @param action the action to be executed
     * @param writer function to write in the jsonArray
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void command(final Database database, final JSONArray jsonArray,
                               final Action action, final Writer writer)
            throws IOException {
        User user = User.getUser(database, action);
        String msg = null;
        // switch to get every type of command action
        switch (action.getType()) {
            // calling static functions to solve the action
            case "favorite" -> {
                assert user != null;
                msg = user.addFavorite(user, action.getTitle());
            }
            case "view" -> {
                assert user != null;
                if (Movie.getMovie(database, action) != null) {
                    msg = user.watchMovie(user,
                            Objects.requireNonNull(Movie.getMovie(database,
                                    action)));
                } else {
                    msg = user.watchSerial(user,
                            Objects.requireNonNull(Serial.getSerial(database,
                                    action)));
                }
            }
            case "rating" -> {
                Movie movie = Movie.getMovie(database, action);
                if (movie != null) {
                    assert user != null;
                    msg = user.rateMovie(user, action, movie);

                } else {
                    Serial serial = Serial.getSerial(database, action);
                    if (serial != null) {
                        assert user != null;
                        msg = user.rateSerial(user, action, serial);
                    }
                }
            }
            default -> {
            }
        }
        // writing in the jsonArray the result of execution
        jsonArray.add(jsonArray.size(),
                writer.writeFile(action.getActionId(), "", msg));
    }
}
