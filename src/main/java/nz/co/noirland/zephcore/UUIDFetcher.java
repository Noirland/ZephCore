package nz.co.noirland.zephcore;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * <a href='https://gist.github.com/evilmidget38/df8dcd7855937e9d1e1f'>Created by evilmidget38</a>
 */
public class UUIDFetcher {
    private static final int MAX_SEARCH = 100;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/page/";
    private static final String AGENT = "minecraft";
    private static final JSONParser jsonParser = new JSONParser();
    private static final Map<String, UUID> uuidCache = new HashMap<String, UUID>();

    public static Map<String, UUID> getUUIDs(List<String> names) {
        Map<String, UUID> ret = new HashMap<String, UUID>();
        List<String> uncached = new ArrayList<String>();
        for(String name : names) {
            if(uuidCache.containsKey(name)) {
                ret.put(name, uuidCache.get(name));
            }else{
                uncached.add(name);
            }
        }
        if(!uncached.isEmpty()) {
            try {
                Map<String, UUID> freshUUIDs = getFreshUUIDs(uncached);
                ret.putAll(freshUUIDs);
                uuidCache.putAll(freshUUIDs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static Map<String, UUID> getFreshUUIDs(List<String> names) throws Exception {
        Map<String, UUID> uuidMap = new HashMap<String, UUID>();
        String body = buildBody(names);
        for (int i = 1; i < MAX_SEARCH; i++) {
            HttpURLConnection connection = createConnection(i);
            writeBody(connection, body);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            JSONArray array = (JSONArray) jsonObject.get("profiles");
            Number count = (Number) jsonObject.get("size");
            if (count.intValue() == 0) {
                break;
            }
            for (Object profile : array) {
                JSONObject jsonProfile = (JSONObject) profile;
                String id = (String) jsonProfile.get("id");
                String name = (String) jsonProfile.get("name");
                UUID uuid = UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" +id.substring(20, 32));
                uuidMap.put(name, uuid);
            }
        }
        return uuidMap;
    }

    private static void writeBody(HttpURLConnection connection, String body) throws Exception {
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.write(body.getBytes());
        writer.flush();
        writer.close();
    }

    private static HttpURLConnection createConnection(int page) throws Exception {
        URL url = new URL(PROFILE_URL+page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
    @SuppressWarnings("unchecked")
    private static String buildBody(List<String> names) {
        List<JSONObject> lookups = new ArrayList<JSONObject>();
        for (String name : names) {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            obj.put("agent", AGENT);
            lookups.add(obj);
        }
        return JSONValue.toJSONString(lookups);
    }
}