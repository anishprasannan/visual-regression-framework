package nz.co.companyname.webdriver;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.rules.ExternalResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class contains methods for creating webdriver, with capabilities. */
public class WebDriverRule extends ExternalResource {

  private static final Logger LOG = LoggerFactory.getLogger(WebDriverRule.class);
  private static final String REMOTE_BROWSERSTACK_URL =
      System.getProperty("remote.browserstack.url", "");

  public DesiredCapabilities createCapabilities(
      String os, String os_version, String browser, String browser_version, String resolution) {
    DesiredCapabilities caps;
    caps = new DesiredCapabilities();
    caps.setCapability("browser", browser);
    caps.setCapability("browser_version", browser_version);
    caps.setCapability("os", os);
    caps.setCapability("os_version", os_version);
    caps.setCapability("acceptSslCerts", "true");
    caps.setCapability("build", "JUnit-Parallel");
    caps.setCapability("resolution", resolution);
    caps.setCapability("browserstack.local", "true");
    caps.setCapability("browserstack.selenium_version", "3.7.1");

    return caps;
  }

  public WebDriver createDriverWithCaps(DesiredCapabilities caps) {
    if (WebDriverPool.isWebDriverInPoolAndAvailable(caps)) {
      LOG.info("Driver found in pool");
      return WebDriverPool.getWebDriverFor(caps);
    } else {
      LOG.info("Driver not found in pool, creating new driver and adding to pool");
      try {
        WebDriver driver = new RemoteWebDriver(new URL(REMOTE_BROWSERSTACK_URL), caps);
        driver.manage().window().maximize();
        WebDriverPool.addWebDriverToPool(caps, driver);
        return driver;
      } catch (MalformedURLException e) {
        e.printStackTrace();
        return null;
      }
    }
  }
}
