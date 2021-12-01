package action;

import database.Database;
import entities.Actor;
import entities.Movie;
import entities.Serial;
import entities.User;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;

public final class Query {
    private Query() { }

    /**
     * @param database database which contains info about every entity
     * @param jsonArray the jsonArray where I'll write the result
     * @param action the action to be executed
     * @param writer function to write in the jsonArray
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void query(final Database database, final JSONArray jsonArray,
                             final Action action, final Writer writer)
                                throws IOException {
        String msg = null;
        String criteria = action.getCriteria();
        // switch to get every type of query
        switch (action.getObjectType()) {
            case "actors":
                switch (criteria) {
                    case "average":
                        msg = "Query result: "
                                + Actor.average(database, action);
                        break;
                    case "awards":
                        if (Actor.actorAwards(database, action) != null) {
                            msg = "Query result: "
                                    + Actor.actorAwards(database, action);
                        } else {
                            msg = "Query result: []";
                        }
                        break;
                    case "filter_description":
                        msg = "Query result: "
                                + Actor.filterDescription(database, action);
                        break;
                    default:
                        break;
                }
                break;
            case "users":
                msg = "Query result: " + User.ratingNumbers(database, action);
                break;
            case "movies":
                switch (criteria) {
                    case "favorite":
                        msg = "Query result: "
                                + Movie.favoriteMovie(database, action);
                        break;
                    case "longest":
                            msg = "Query result: "
                                    + Movie.longestMovie(database, action);
                        break;
                    case "ratings":
                        msg = "Query result: " + Movie.ratingMovies(database,
                                                action);
                        break;
                    case "most_viewed":
                        if (Movie.mostViewed(database, action).size() == 0) {
                            msg = "Query result: []";
                        } else {
                            msg = "Query result: "
                                    + Movie.mostViewed(database, action);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "shows":
                switch (criteria) {
                    case "favorite":
                        msg = "Query result: "
                                + Serial.favoriteSerial(database, action);
                        break;
                    case "longest":
                        msg = "Query result: "
                                + Serial.longestSerial(database, action);
                        break;
                    case "ratings":
                        msg = "Query result: " + Serial.ratingSerials(database,
                                action);
                        break;
                    case "most_viewed":
                        if (Serial.mostViewedSerial(database, action).size() == 0) {
                            msg = "Query result: " + Serial.mostViewedSerial(
                                    database, action);
                        } else {
                            msg = "Query result: []";
                        }
                        break;
                    default:
                        break;
                }
            break;
            default:
                msg = "Query result: []";
                break;
            }
        // writing in the jsonArray the result of execution
        jsonArray.add(jsonArray.size(),
                writer.writeFile(action.getActionId(), "", msg));
    }

}
