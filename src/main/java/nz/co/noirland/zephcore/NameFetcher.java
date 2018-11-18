package nz.co.noirland.zephcore;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * <a href='https://gist.github.com/evilmidget38/a5c971d2f2b2c3b3fb37'>Created by evilmidget38</a>
 */
public class NameFetcher {
    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final JSONParser jsonParser = new JSONParser();

    public static String getName(UUID uuid) throws Exception {
        return getNames(Arrays.asList(uuid)).get(uuid);
    }

    public static Map<UUID, String> getNames(Collection<UUID> uuids) throws Exception {
        Map<UUID, String> ret = new HashMap<UUID, String>();
        for (UUID uuid: uuids) {
            HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL+uuid.toString().replace("-", "")).openConnection();
            JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            String error = (String) response.get("error");
            String errorMessage = (String) response.get("errorMessage");
            if (error != null && error.length() > 0) {
                throw new IllegalStateException(errorMessage);
            }
            String name = (String) response.get("name");
            if (name == null) {
                continue;
            }
            ret.put(uuid, name);
        }
        return ret;
    }
}