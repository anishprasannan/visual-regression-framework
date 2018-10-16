package nz.co.companyname.testing.webdriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class contains methods for maintaining the webdriver pool. */
public class WebDriverPool {
  static Map<String, WebDriver> webDriverPool = new HashMap<String, WebDriver>();

  private static final Logger LOG = LoggerFactory.getLogger(WebDriverPool.class);

  public static void addWebDriverToPool(String key, WebDriver driver) {
    webDriverPool.put(key, driver);
  }

  public static WebDriver getWebDriverFor(String key) {
    return webDriverPool.get(key);
  }

  public static Boolean isWebDriverInPoolAndAvailable(String key) {
    try {
      return webDriverPool.containsKey(key) && !getWebDriverFor(key).getCurrentUrl().isEmpty();
    } catch (WebDriverException e) {
      e.printStackTrace();
      webDriverPool.remove(key);
      return false;
    }
  }

  public static void removeDriverFromPool(String key) {
    webDriverPool.get(key).quit();
    webDriverPool.remove(key);
  }

  public static void flushWebDriverPool() {
    List<WebDriver> driverList = new ArrayList<WebDriver>(webDriverPool.values());
    for (WebDriver driver : driverList) {
      try {
        driver.quit();
        LOG.info(">>>>Closing browser");
      } catch (WebDriverException e) {
        e.printStackTrace();
      }
    }
    webDriverPool.clear();
  }
}
