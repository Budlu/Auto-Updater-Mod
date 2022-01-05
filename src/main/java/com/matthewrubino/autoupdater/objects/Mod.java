package com.matthewrubino.autoupdater.objects;

public class Mod {
    private final String name;
    private final String repositoryId;
    private String downloadUrl;

    public Mod(String name, String repositoryId, String downloadUrl) {
        this.name = name;
        this.repositoryId = repositoryId;
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
