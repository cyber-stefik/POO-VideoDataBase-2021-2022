package action;

import database.Database;
import entities.User;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;

public final class Recommend {
    private Recommend() { }

    /**
     * @param database database which contains info about every entity
     * @param jsonArray the jsonArray where I'll write the result
     * @param action the action to be executed
     * @param writer function to write in the jsonArray
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void recommend(final Database database,
                                 final JSONArray jsonArray,
                                 final Action action,
                                 final Writer writer) throws IOException {
        String msg = null;
        // switch to get every type of recommendation
        switch (action.getType()) {
            case "standard":
                if (User.standardRecommendation(database, action) == null) {
                    msg = "StandardRecommendation cannot be applied!";
                } else {
                    msg = "StandardRecommendation result: "
                            + User.standardRecommendation(database, action);
                }
                break;
            case "search":
                if (User.searchRecommendation(database, action).size() == 0
                    || User.searchRecommendation(database, action) == null) {
                    msg = "SearchRecommendation cannot be applied!";
                } else {
                    msg = "SearchRecommendation result: "
                            + User.searchRecommendation(database, action);
                }
                break;
            case "popular":
                if (User.popularRecommendation(database, action) == null) {
                    msg = "PopularRecommendation cannot be applied!";
                } else {
                    msg = "PopularRecommendation result: "
                            + User.popularRecommendation(database, action);
                }
                break;
            case "best_unseen":
                if (User.bestunseenRecommendation(database, action) == null) {
                    msg = "BestRatedUnseenRecommendation cannot be applied!";
                } else {
                    msg = "BestRatedUnseenRecommendation result: "
                            + User.bestunseenRecommendation(database, action);
                }
                break;
            case "favorite":
                if (User.favoriteRecommendation(database, action) == null) {
                    msg = "FavoriteRecommendation cannot be applied!";
                } else {
                    msg = "FavoriteRecommendation result: "
                            + User.favoriteRecommendation(database, action);
                }
            default:
                break;

        }
        // writing in the jsonArray the result of execution
        jsonArray.add(jsonArray.size(),
                writer.writeFile(action.getActionId(), "", msg));
    }
}
