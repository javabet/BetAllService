package com.wisp.game.core.utils;

import org.springframework.util.ResourceUtils;

import java.util.Collection;
import java.util.LinkedHashSet;

public class FilePathUtils {


    public FilePathUtils() {

    }

    public Collection<String> searchLocationsForFile(String location) {
        return searchLocationsForFile( location,null );
    }

    public Collection<String> searchLocationsForFile(String location,String customConfigPath) {
        Collection<String> locations = new LinkedHashSet<>();
        String _location = shaping(location);
        if (customConfigPath != null) {
            String prefix = ResourceUtils.FILE_URL_PREFIX + customConfigPath;
            if (!customConfigPath.endsWith("/")) {
                locations.add(prefix + "/" + _location);
            } else {
                locations.add(prefix + _location);
            }
        } else {
            locations.add(ResourceUtils.FILE_URL_PREFIX + "./config/" + _location);
        }
        locations.add(ResourceUtils.FILE_URL_PREFIX + "./" + _location);
        return locations;
    }


    public Collection<String> searchLocationsForClasspath(String location) {
        return searchLocationsForClasspath( location,null );
    }

    public Collection<String> searchLocationsForClasspath(String location,String customConfigPath) {
        Collection<String> locations = new LinkedHashSet<>();
        String _location = shaping(location);
        if (customConfigPath != null) {
            String prefix = ResourceUtils.CLASSPATH_URL_PREFIX + customConfigPath;
            if (!customConfigPath.endsWith("/")) {
                locations.add(prefix + "/" + _location);
            } else {
                locations.add(prefix + _location);
            }
        } else {
            locations.add(ResourceUtils.CLASSPATH_URL_PREFIX + "/Config/" + _location);
        }

        locations.add(ResourceUtils.CLASSPATH_URL_PREFIX + "/" + _location);
        return locations;
    }

    private String shaping(String location) {
        if (location.startsWith("./")) {
            return location.substring(2);
        }
        if (location.startsWith("/")) {
            return location.substring(1);
        }
        return location;
    }

}
