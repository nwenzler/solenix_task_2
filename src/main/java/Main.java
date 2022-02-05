import org.json.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Main {

    final static String issuesAPI = "https://api.github.com/repos/planetlabs/staccato/issues";

    /*
     * Sends a GET request to the specified URL and parses response into a JSONArray.
     */
    public static JSONArray getJSONArray(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        StringBuilder content = new StringBuilder();

        // Try with resource to automatically close the scanner
        try (Scanner scanner = new Scanner(url.openStream())) {
            while (scanner.hasNext()) {
                content.append(scanner.nextLine());
            }
        }

        return new JSONArray(content.toString());
    }

    /*
     * Extracts necessary infos from JSONArray (issue names and character count of first comment).
     * Note: The character count of a first comment includes special characters used for markdown formatting.
     * Note: GitHub counts pull requests as issues, this method does not.
     */
    public static Map<String, String> parseIssues(JSONArray jsonArray) throws IOException {

        String issueTitle;
        String issueBody;
        Map<String, String> issuesOutput = new HashMap<>();

        int jsonArrayLength = jsonArray.length();
        JSONObject currentJSONObject;
        for (int i = 0; i < jsonArrayLength; i++) {
            currentJSONObject = jsonArray.getJSONObject(i);
            // Filter out pull requests (GitHub Issues API counts PRs as issues)
            if (!currentJSONObject.has("pull_request")) {
                issueTitle = (String) currentJSONObject.get("title");
                issueBody = (String) currentJSONObject.get("body");
                issuesOutput.put(issueTitle, String.valueOf(issueBody.length()));
            }
        }
        return issuesOutput;
    }

    /*
     * Prints the necessary infos (issue names and character count of first comment).
     * Note: the formatting tries to follow the task specification as closely as possible.
     */
    public static void printIssues(Map<String, String> issuesOutput) {
        for (Map.Entry<String, String> entry : issuesOutput.entrySet()) {
            System.out.println(entry.getKey() + "\t\t" + entry.getValue() + " characters");
        }
    }

    public static void main(String[] args) {
        try {
            JSONArray jsonArray = getJSONArray(issuesAPI);
            printIssues(parseIssues(jsonArray));
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}

