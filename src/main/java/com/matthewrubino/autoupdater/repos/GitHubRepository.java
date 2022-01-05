package com.matthewrubino.autoupdater.repos;

import com.matthewrubino.autoupdater.objects.Release;
import com.matthewrubino.autoupdater.objects.ReleaseAsset;
import com.matthewrubino.autoupdater.objects.Repository;
import com.matthewrubino.autoupdater.util.ObjectGenerator;

import java.util.List;

public class GitHubRepository extends Repository {
    private GitHubOwner owner;
    private String name;
    private String id;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOwner() {
        return owner.getName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Release getLatestRelease() {
        String uri = "https://api.github.com/repos/" + getOwner() + "/" + getName() + "/releases/latest";

        ObjectGenerator objectGenerator = new ObjectGenerator();
        return objectGenerator.generateFromApi(uri, GitHubRelease.class);
    }
}

class GitHubOwner {
    private String login;

    public String getName() {
        return login;
    }
}

class GitHubRelease extends Release {
    private String tag_name;
    private String id;
    private List<GitHubReleaseAsset> assets;

    public String getName() {
        return tag_name;
    }

    public String getId() {
        return id;
    }

    public List<GitHubReleaseAsset> getAssets() {
        return assets;
    }
}

class GitHubReleaseAsset extends ReleaseAsset {
    private String name;
    private String browser_download_url;

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return browser_download_url;
    }
}
