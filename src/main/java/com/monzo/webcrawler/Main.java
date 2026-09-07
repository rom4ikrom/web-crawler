package com.monzo.webcrawler;

import com.monzo.webcrawler.application.WebCrawlerApplication;
import com.monzo.webcrawler.bootstrap.ApplicationContext;
import com.monzo.webcrawler.bootstrap.DependencyOverrides;

public class Main {

    public static void main(String[] args) {
        DependencyOverrides dependencyOverrides = DependencyOverrides.none();
        ApplicationContext applicationContext = new ApplicationContext(dependencyOverrides);
        new WebCrawlerApplication(applicationContext).start();
    }

}
