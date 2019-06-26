package io.zino.restquery.config;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ConfigUtil {
    private static final Gson GSON = new Gson();

    public static ConfigDO getConfig(String path) {
        try {
            String rawConfig = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8)
                    .stream().collect(Collectors.joining());

            return GSON.fromJson(rawConfig, ConfigDO.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
