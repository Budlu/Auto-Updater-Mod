package com.matthewrubino.autoupdater.util;

import com.google.gson.Gson;
import com.matthewrubino.autoupdater.AutoUpdater;
import com.matthewrubino.autoupdater.objects.Mod;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileManager {
    private final String MODID = AutoUpdater.MODID;
    private final String reposFile = "repos.txt";
    private final String cacheFile = "cache.txt";

    public boolean updateMod(Mod mod) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(mod.getDownloadUrl())).build();

        try {
            File file = new File("mods/" + mod.getRepositoryId() + ".jar");
            HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(file.toPath(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));

            if (response.statusCode() == 302) {
                Optional<String> redirectUrl = response.headers().firstValue("location");
                if (redirectUrl.isPresent()) {
                    mod.setDownloadUrl(redirectUrl.get());
                    return updateMod(mod);
                }
            }

            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> readReposFile() {
        createModFolder();
        File file = new File("mods/" + MODID + "/" + reposFile);

        try {
            if (!file.createNewFile()) {
                LinkedList<String> repos = new LinkedList<String>();
                Scanner scanner = new Scanner(file);

                while (scanner.hasNext()) {
                    String repo = scanner.nextLine().trim();
                    repos.add(repo);
                }

                scanner.close();
                return repos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new LinkedList<String>();
    }

    public Map<String, String> readReposCache() {
        createModFolder();
        File file = new File("mods/" + MODID + "/" + cacheFile);

        try {
            if (!file.createNewFile()) {
                Gson gson = new Gson();
                FileReader reader = new FileReader(file);

                AutoUpdaterCache cache = gson.fromJson(reader, AutoUpdaterCache.class);

                reader.close();
                Map<String, String> repos = cache.getRepos();
                if (repos != null) return repos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<String, String>();
    }

    public void setReposCache(Map<String, String> repos) {
        createModFolder();
        File file = new File("mods/" + MODID + "/" + cacheFile);
        AutoUpdaterCache cache = new AutoUpdaterCache(repos);

        try {
            if (!file.createNewFile()) {
                Gson gson = new Gson();
                FileWriter writer = new FileWriter(file);

                gson.toJson(cache, writer);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean createModFolder() {
        File directory = new File("mods/" + MODID);
        if (!directory.exists()) {
            return directory.mkdir();
        }
        return false;
    }
}

class AutoUpdaterCache {
    private Map<String, String> repos;

    public AutoUpdaterCache(Map<String, String> repos) {
        this.repos = repos;
    }

    public Map<String, String> getRepos() {
        return repos;
    }
}
