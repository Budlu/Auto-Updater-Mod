package com.matthewrubino.autoupdater;

import com.matthewrubino.autoupdater.objects.Mod;
import com.matthewrubino.autoupdater.objects.Release;
import com.matthewrubino.autoupdater.objects.ReleaseAsset;
import com.matthewrubino.autoupdater.objects.Repository;
import com.matthewrubino.autoupdater.util.FileManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class AutoUpdater implements ModInitializer {
	public static final String MODID = "autoupdater";
	public static final Logger LOGGER = LogManager.getLogger(AutoUpdater.MODID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loading repositories configuration...");

		FileManager fm = new FileManager();
		List<String> repos = fm.readReposFile();
		Map<String, String> reposCache = fm.readReposCache();

		LOGGER.info("Checking for updates...");

		for (String url: repos) {
			tryUpdateRepository(url, reposCache);
		}

		LOGGER.info("Updating repositories cache...");

		fm.setReposCache(reposCache);
	}

	private void tryUpdateRepository(String url, Map<String, String> reposCache) {
		LOGGER.info("Checking repository " + url);

		Repository repository = RepositoryFactory.getRepository(url);
		if (repository == null) return;

		String repositoryName = repository.getName();
		String repositoryId = repository.getId();
		if (repositoryName == null || repositoryId == null) return;

		LOGGER.info(repositoryName + " is a valid repository with id " + repositoryId);

		Release release = repository.getLatestRelease();
		if (release == null) return;

		String releaseName = release.getName();
		String releaseId = release.getId();
		if (releaseName == null || releaseId == null) return;

		LOGGER.info("Found a valid release " + releaseName + " with id " + releaseId);

		String currentReleaseId = reposCache.getOrDefault(repositoryId, null);
		if (releaseId.equals(currentReleaseId)) return;
		reposCache.put(repositoryId, releaseId);

		List<? extends ReleaseAsset> assets = release.getAssets();
		if (assets == null || assets.size() < 1) return;

		ReleaseAsset asset = assets.get(0);
		if (asset == null) return;

		String assetName = asset.getName();
		String downloadUrl = asset.getDownloadUrl();
		if (assetName == null || downloadUrl == null) return;

		LOGGER.info("Found asset with name: " + assetName);
		LOGGER.info("Download at: " + downloadUrl);

		// Mod name should be repository ID for consistency in naming
		Mod mod = new Mod(assetName, repositoryId, downloadUrl);
		FileManager fm = new FileManager();
		if (fm.updateMod(mod)) {
			LOGGER.info("Successfully updated " + mod.getName());
		} else {
			LOGGER.error("Failed to update " + mod.getName());
		}
	}
}
