package nz.co.noirland.zephcore;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * <a href='https://gist.github.com/evilmidget38/26d70114b834f71fb3b4'>Created by evilmidget38</a>
 */
public class UUIDFetcher {
    private static final double PROFILES_PER_REQUEST = 100;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private static final JSONParser jsonParser = new JSONParser();
    private static final Map<String, UUID> uuidCache = new HashMap<String, UUID>();

    protected static void updatePlayer(String name, UUID uuid) {
        uuidCache.put(name, uuid);
    }

    public static UUID getUUID(String name) {
        return getUUIDs(new ArrayList<String>(Arrays.asList(name))).get(name);
    }

    public static Map<String, UUID> getUUIDs(Collection<String> names) {
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
        int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
        for (int i = 0; i < requests; i++) {
            HttpURLConnection connection = createConnection();
            String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
            writeBody(connection, body);
            JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            for (Object profile : array) {
                JSONObject jsonProfile = (JSONObject) profile;
                String id = (String) jsonProfile.get("id");
                String name = (String) jsonProfile.get("name");
                UUID uuid = parseUUID(id);
                uuidMap.put(getNameWithNonCase(names, name), uuid);
            }
        }
        for(String name : names) {
            if(!uuidMap.containsKey(name)) {
                uuidMap.put(name, null);
            }
        }
        return uuidMap;
    }

    /**
     * Returns the entry in names that is a case non-sensitive version of capsName.
     * Used in implementation as Mojang returns case-corrected versions of names. This is not
     * helpful when converting names in a database.
     * @param names The original list of names
     * @param capsName The case-corrected name returned
     * @return The original name
     */
    private static String getNameWithNonCase(Collection<String> names, String capsName) {
        for(String name : names) {
            if(name.equalsIgnoreCase(capsName)) return name;
        }
        return capsName;
    }

    private static void writeBody(HttpURLConnection connection, String body) throws Exception {
        OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL(PROFILE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private static UUID parseUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" +id.substring(20, 32));
    }
}