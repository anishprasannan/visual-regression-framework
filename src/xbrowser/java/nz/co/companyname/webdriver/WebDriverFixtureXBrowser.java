package nz.co.companyname.webdriver;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import nz.co.companyname.testing.visualregression.ScreenshotTaker;
import nz.co.companyname.testing.webdriver.AccessBuilder;
import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements webdriver for the xBrowser tests. Creating, maintaining, and destroying
 * them as needed.
 *
 * @see #login()
 * @see #navigateToPage(String)
 * @see #setScreenDimensions(WebDriver, int, int)
 * @see #getWebDriver()
 */
public class WebDriverFixtureXBrowser {

  private static final Logger LOG = LoggerFactory.getLogger(WebDriverFixtureXBrowser.class);

  /**
   * Extent Reporter used to create test results
   *
   * @see <a href="http://extentreports.com/docs/versions/2/java/">Extentreports v2</a>
   */
  private static ExtentReports extent = getExtentReporter();

  private ExtentTest test;

  private String testClassName;

  private String testMethodName;

  @ClassRule public static WebDriverRule driverRule = new WebDriverRule();

  private WebDriver driver;

  private ScreenshotTaker screenshotTaker;

  private DesiredCapabilities caps;

  @Rule
  public TestWatcher testWatcher =
      new TestWatcher() {
        @Override
        protected void starting(Description description) {
          //ExtentReporter
          testClassName = description.getClassName();
          testMethodName = description.getMethodName();
          test =
              extent.startTest(
                  testClassName.substring(testClassName.lastIndexOf(".") + 1)
                      + ":"
                      + testMethodName);
          //
          LOG.info(
              ">> Running test  - "
                  + description.getClassName()
                  + " : "
                  + description.getMethodName());
          //ExtentReporter
          test.log(LogStatus.INFO, "Running test : " + testMethodName + " in " + testClassName);
          //

        }

        @Override
        protected void succeeded(Description description) {
          LOG.info(
              ">> Finished test - "
                  + description.getClassName()
                  + " : "
                  + description.getMethodName());
          //ExtentReporter
          test.log(LogStatus.PASS, "Finished test : " + testMethodName + " in " + testClassName);
          //
        }

        @Override
        protected void failed(Throwable e, Description description) {
          LOG.info(
              ">> Failed test - "
                  + description.getClassName()
                  + " : "
                  + description.getMethodName());
          try {
            screenshotTaker.writeScreenshotTo(description.getMethodName());
          } catch (IOException e1) {
            e1.printStackTrace();
          }
          //ExtentReporter
          test.log(LogStatus.FAIL, "Failed test : " + testMethodName + " in " + testClassName);
          test.log(LogStatus.INFO, test.addScreenCapture(description.getMethodName() + ".png"));
          //
        }
      };

  @AfterClass
  public static void afterClass() {
    //ExtentTest

    extent.flush();
    WebDriverPool.flushWebDriverPool();
  }

  public WebDriverFixtureXBrowser() {
    LOG.info("Running tests (" + this.getClass().getSimpleName() + ")");

    try {
      LOG.info(
          "Running test on client computer: '" + InetAddress.getLocalHost().getHostName() + "'");
    } catch (UnknownHostException ignored) {
    }
  }

  @Before
  public void before() {
    createWebDriver();
    screenshotTaker = new ScreenshotTaker(getWebDriver());
  }

  /** Navigates to base.url Implement any common logic for tests during login */
  public void login() {
    new AccessBuilder(driver).navigateToURL();
    //TODO: Implement logic for login here.
  }

  /**
   * Navigates to base.url#!/pageName Implement any common logic for tests during page navigation
   *
   * @param pageName String type parameter identifying pageName
   */
  public void navigateToPage(String pageName) {
    new AccessBuilder(driver).navigateToPage(pageName);
    //TODO: Implement logic for navigating to page here.
  }

  /**
   * Sets width and height of test window.
   *
   * @param driver
   * @param width int type parameter corresponding to page width
   * @param height int type parameter corresponding to page height
   */
  public void setScreenDimensions(WebDriver driver, int width, int height) {
    driver.manage().window().setSize(new Dimension(width, height));
  }

  /**
   * Gets the web driver
   *
   * @return WebDriver
   */
  protected WebDriver getWebDriver() {
    return driver;
  }

  private void createWebDriver() {
    LOG.info("Creating driver for:" + Thread.currentThread().getName());
    driver = driverRule.createDriverWithCaps(caps);
  }

  protected void createRemoteEnv(
      String os, String os_version, String browser, String browser_version, String resolution) {
    caps = driverRule.createCapabilities(os, os_version, browser, browser_version, resolution);
  }

  //Extent Reports
  protected static ExtentReports getExtentReporter() {
    if (extent == null) {
      extent = new ExtentReports(System.getProperty("test.output.dir", "") + "Results.html", true);
    }
    return extent;
  }
}
