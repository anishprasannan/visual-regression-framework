package nz.co.companyname.webdriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class contains methods for maintaining the webdriver pool. */
public class WebDriverPool {
  static Map<DesiredCapabilities, WebDriver> webDriverPool = new HashMap();

  private static final Logger LOG = LoggerFactory.getLogger(WebDriverPool.class);

  public static void addWebDriverToPool(DesiredCapabilities key, WebDriver driver) {
    webDriverPool.put(key, driver);
  }

  public static WebDriver getWebDriverFor(DesiredCapabilities key) {
    return webDriverPool.get(key);
  }

  public static Boolean isWebDriverInPoolAndAvailable(DesiredCapabilities key) {
    try {
      if (webDriverPool.containsKey(key)
          && !(((RemoteWebDriver) getWebDriverFor(key)).getSessionId() == null)) {
        return true;
      } else {
        webDriverPool.remove(key);
        return false;
      }
    } catch (WebDriverException e) {
      e.printStackTrace();
      webDriverPool.remove(key);
      return false;
    }
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
