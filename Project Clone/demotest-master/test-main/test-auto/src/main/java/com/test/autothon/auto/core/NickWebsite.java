package com.test.autothon.auto.core;

import com.test.autothon.common.ReadPropertiesFile;
import com.test.autothon.ui.core.UIOperations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NickWebsite extends UIOperations {

    private final static Logger logger = LogManager.getLogger(NickWebsite.class);
    private Map<String, String> linkAndToolTip = new HashMap<>();


    public void setDeatilsFromNickWebSite() {
        String locator = ReadPropertiesFile.getPropertyValue("nick.all.image");
        List<WebElement> webElement = getElement(locator);
        int noOfItems = webElement.size();
        logger.info("Total no of items: " + noOfItems);
        String urlLinks = "";
        String toolTipText = "";
        for (WebElement ele : webElement) {
            urlLinks = ele.findElement(By.tagName("a")).getAttribute("href");
            toolTipText = ele.getAttribute("textContent");
            linkAndToolTip.put(urlLinks, toolTipText);
            logger.info("Adding [" + urlLinks + "] and [" + toolTipText + "] to the map");
        }
    }

    public Map<String, String> getDetailsFromNickWebsite() {
        return linkAndToolTip;
    }

    public void checkLinksAreNotBroken() {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        for (String url : linkAndToolTip.keySet()) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    isLinkBroken(url);
                }
            });

        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void checkToolTipValueIsNotBalnk() {

        for (String url : linkAndToolTip.keySet()) {
            String ttText = linkAndToolTip.get(url);
            if (ttText.isEmpty() || ttText == "")
                logger.error("Tool tip is blank for url " + url);
        }
    }

    public void openUrlInNewTabAndCheckTitle() {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        for (String url : linkAndToolTip.keySet()) {
            String ttText = linkAndToolTip.get(url);
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    assertNickTitleAndUrl(url, ttText);
                }
            });

        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void assertNickTitleAndUrl(String url, String ttText) {
        launchURL(url);
        takeScreenShot();
        String title = getPageTitle();
        logger.info("Title: " + title + "\nToolTip: " + ttText);

        if (title.toLowerCase().contains(ttText.toLowerCase()))
            logger.info("Title and Tool tip text matches");
        else
            logger.error("Mismatch in title and tooltip");

    }
}

