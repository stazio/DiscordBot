import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

public class YTSearch {

    private static String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&key=AIzaSyCzjqcg1ydoDTLfAICZRwS9PwktYh0tk64";
    public static Map<String, String> search(String name) {
        JSONObject object = execute(URL + "&q=" + name);
        JSONArray array =  object.getJSONArray("items");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < array.length(); i++ ) {
            JSONObject obs = array.getJSONObject(i);
            if (obs.has("id") && obs.getJSONObject("id").has("videoId")) {
                String id = obs.getJSONObject("id").getString("videoId");
                String videoName = obs.getJSONObject("snippet").getString("title");
                String channelName = obs.getJSONObject("snippet").getString("channelTitle");
                map.put(id, videoName + " - " + channelName);
            }
        }

        return map;
    }

    @SneakyThrows
    public  static JSONObject execute(String urlQuery) {
        Document doc = Jsoup.connect(urlQuery).timeout(10 * 1000).ignoreContentType(true).get();
        return (JSONObject) new JSONTokener(doc.body().text()).nextValue();
    }

}
