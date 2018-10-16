package nz.co.companyname.testing.webdriver;

import org.junit.rules.ExternalResource;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class contains methods for creating webdriver for a thread. */
public class WebDriverRule extends ExternalResource {

  private static final Logger LOG = LoggerFactory.getLogger(WebDriverRule.class);

  public WebDriver getDriverForThread(String threadName) {
    if (WebDriverPool.isWebDriverInPoolAndAvailable(threadName)) {
      return WebDriverPool.getWebDriverFor(threadName);
    } else {
      return createDriver(threadName);
    }
  }

  private WebDriver createDriver(String threadName) {
    String requestedBrowser =
        System.getProperty(
            WebDriverProperties.BROWSER_NAME.getPropertyName(),
            WebDriverProperties.BROWSER_NAME.getDefaultValue());
    String browserProps =
        System.getProperty(
            WebDriverProperties.BROWSER_PROPS.getPropertyName(),
            WebDriverProperties.BROWSER_PROPS.getDefaultValue());

    WebDriver driver = null;

    if (requestedBrowser.equals("firefox")) {
      LOG.info("Creating firefox driver");
      FirefoxOptions options = new FirefoxOptions();
      options.addArguments(browserProps);
      driver = new FirefoxDriver(options);
      driver.manage().window().setSize(new Dimension(1050, 768));
    } else if (requestedBrowser.equals("chrome")) {
      //TODO: Add chrome browser setup here
    } else {
      throw new RuntimeException("Invalid browser name requested [" + requestedBrowser + "]");
    }

    WebDriverPool.addWebDriverToPool(threadName, driver);
    return driver;
  }
}
