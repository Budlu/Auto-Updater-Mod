package com.matthewrubino.autoupdater.objects;

import java.util.List;

abstract public class Release {
    abstract public String getName();
    abstract public String getId();
    abstract public List<? extends ReleaseAsset> getAssets();
}
