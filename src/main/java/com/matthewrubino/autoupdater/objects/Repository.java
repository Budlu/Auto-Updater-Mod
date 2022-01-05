package com.matthewrubino.autoupdater.objects;

abstract public class Repository {
    abstract public String getName();
    abstract public String getOwner();
    abstract public String getId();
    abstract public Release getLatestRelease();
}
