package com.matthewrubino.autoupdater;

import com.matthewrubino.autoupdater.objects.Repository;
import com.matthewrubino.autoupdater.repos.GitHubRepository;
import com.matthewrubino.autoupdater.util.ObjectGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepositoryFactory {

    public static Repository getRepository(String url) {
        if (url.contains("github.com")) {
            Pattern p = Pattern.compile("^(https?://)?github.com/(?<owner>.*?)/(?<repo>.*?)(.git)?/?$");
            Matcher m = p.matcher(url);

            if (m.matches()) {
                String owner = m.group("owner");
                String repo = m.group("repo");
                String repoUri = "https://api.github.com/repos/" + owner + "/" + repo;

                ObjectGenerator objectGenerator = new ObjectGenerator();
                return objectGenerator.generateFromApi(repoUri, GitHubRepository.class);
            } else {
                return null;
            }
        }

        return null;
    }

}
