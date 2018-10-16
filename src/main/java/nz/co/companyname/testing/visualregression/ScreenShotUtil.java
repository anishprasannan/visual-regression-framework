package nz.co.companyname.testing.visualregression;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods for taking screen shots. A hack is introduced, as Safari and IE
 * always returns a full page screenshot, and causes java heap space issues, as the number of tests
 * increases. Please remove the hack if your tests don't run into heap space issues.
 */
public class ScreenShotUtil {

  //Hack for Safari and IE
  private static Map<String, File> safariScreenShot = new HashMap<String, File>();
  private static Map<String, File> explorerScreenShot = new HashMap<String, File>();
  private static final Logger LOG = LoggerFactory.getLogger(ScreenShotUtil.class);
  //End of hack

  public static File takeScreenShot(WebDriver driver) {

    //Hack for Safari and IE
    if (((RemoteWebDriver) driver)
        .getCapabilities()
        .getBrowserName()
        .toLowerCase()
        .contains("safari")) {
      LOG.info("Checking if safari screenshot available for current Url.");
      if (!safariScreenShot.containsKey(driver.getCurrentUrl())) {
        LOG.info("Safari not on same page, hence capturing new screenshot.");
        safariScreenShot.clear();
        safariScreenShot.put(
            driver.getCurrentUrl(), ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
      } else {
        LOG.info("Safari still on same page, hence returning previous screenshot.");
      }

      return safariScreenShot.get(driver.getCurrentUrl());
    }

    if (((RemoteWebDriver) driver)
        .getCapabilities()
        .getBrowserName()
        .toLowerCase()
        .contains("internet explorer")) {
      ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)");
      LOG.info("Checking if IE screenshot available for current Url.");
      if (!explorerScreenShot.containsKey(driver.getCurrentUrl())) {
        LOG.info("IE not on same page, hence capturing new screenshot.");
        explorerScreenShot.clear();
        explorerScreenShot.put(
            driver.getCurrentUrl(), ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
      } else {
        LOG.info("IE still on same page, hence returning previous screenshot.");
      }

      return explorerScreenShot.get(driver.getCurrentUrl());
    }
    //End of hack
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
  }
}
