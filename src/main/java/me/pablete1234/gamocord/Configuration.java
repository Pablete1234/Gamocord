package me.pablete1234.gamocord;

import net.dv8tion.jda.api.entities.Activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG_FILE = "config.properties";

    private static final String LINK_PREFIX = "link.",
            CHANNEL_ID = ".channel-id",
            SERVER_ID = ".server-id",
            API_KEY = ".api-key";

    private final Properties props = new Properties();

    private final String discordToken;
    private final Activity activity;
    private final String prefixes;
    private final Map<String, GamocosmServer> links = new HashMap<>();

    public Configuration() throws IOException {
        loadProps();

        this.discordToken = props.getProperty("discord-token");
        this.activity = Activity.of(
                Activity.ActivityType.valueOf(props.getProperty("activity.type")),
                props.getProperty("activity.text"));

        this.prefixes = props.getProperty("prefixes");

        rebuildLinks();
    }

    public void loadProps() throws IOException {
        props.load(new FileInputStream(CONFIG_FILE));
    }

    public void rebuildLinks() {
        links.clear();
        for (Object keyObj : props.keySet()) {
            String key = (String) keyObj;
            // Only use channel-id keys, to avoid adding same server multiple times
            if (!key.startsWith(LINK_PREFIX) || !key.endsWith(CHANNEL_ID)) continue;

            links.put(props.getProperty(key), new GamocosmServer(
                    key.replace(LINK_PREFIX, "").replace(CHANNEL_ID, ""),
                    props.getProperty(key.replace(CHANNEL_ID, SERVER_ID)),
                    props.getProperty(key.replace(CHANNEL_ID, API_KEY))));
        }
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getPrefixes() {
        return prefixes;
    }

    public GamocosmServer getAPI(String channelId) {
        return links.get(channelId);
    }

    public Map<String, GamocosmServer> getServers() {
        return links;
    }

    public void saveProps() throws IOException {
        links.forEach((channel, server) ->
                props.put(LINK_PREFIX + server.getName() +  CHANNEL_ID, channel));
        props.store(new FileOutputStream(CONFIG_FILE), null);
    }
}
