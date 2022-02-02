import org.json.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class Main {
    // TODO error handling

    private final static String issuesAPI = "https://api.github.com/repos/planetlabs/staccato/issues";

    /*
    Sends a GET request to the specified URL and parses response into a JSONArray
     */
    public static JSONArray getJSONArray(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responsecode = conn.getResponseCode();

        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        }else{
            String content = "";
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                content += scanner.nextLine();
            }
            scanner.close();
            JSONArray jsonArray = new JSONArray(content);
            return jsonArray;
        }
    }

    /*
    Extracts necessary infos from JSONArray (issue names and character count of first comment)
    Note: The character count of a first comment includes special characters used for markdown formatting.
    Note: GitHub counts pull requests as issues, this method does not.
     */
    public static void parseIssues(String issuesAPI) throws IOException {

        JSONArray jsonArray = getJSONArray(issuesAPI);

        String issueTitle;
        String issueBody;
        // TODO replace with mapping?
        for (int i = 0; i < jsonArray.length(); i++) {

            // Filter out pull requests (GitHub Issues API counts PRs as issues)
            if (!jsonArray.getJSONObject(i).has("pull_request")){
                issueTitle = (String) jsonArray.getJSONObject(i).get("title");
                issueBody = (String) jsonArray.getJSONObject(i).get("body");
                System.out.println(issueTitle + " " + issueBody.length() + " characters");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        parseIssues(issuesAPI);
    }
}

