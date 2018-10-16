package nz.co.companyname.webdriver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import nz.co.companyname.testing.visualregression.ScreenshotTaker;
import nz.co.companyname.testing.webdriver.AccessBuilder;
import nz.co.companyname.testing.webdriver.WebDriverPool;
import nz.co.companyname.testing.webdriver.WebDriverRule;
import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements webdriver for the vanilla tests. Creating, maintaining, and destroying them
 * as needed.
 *
 * @see #login()
 * @see #navigateToPage(String)
 * @see #setScreenDimensions(WebDriver, int, int)
 * @see #getWebDriver()
 */
public class WebDriverFixtureVanilla {

  private static final Logger LOG = LoggerFactory.getLogger(WebDriverFixtureVanilla.class);

  @ClassRule public static WebDriverRule driverRule = new WebDriverRule();

  private WebDriver driver;

  private ScreenshotTaker screenshotTaker;

  @Rule
  public TestWatcher testWatcher =
      new TestWatcher() {
        @Override
        protected void starting(Description description) {
          LOG.info(
              ">> Running test  - "
                  + description.getClassName()
                  + " : "
                  + description.getMethodName());
        }

        @Override
        protected void succeeded(Description description) {
          LOG.info(
              ">> Finished test - "
                  + description.getClassName()
                  + " : "
                  + description.getMethodName());
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
        }
      };

  @AfterClass
  public static void afterClass() {
    WebDriverPool.removeDriverFromPool(Thread.currentThread().getName());
  }

  public WebDriverFixtureVanilla() {
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
    driver = driverRule.getDriverForThread(Thread.currentThread().getName());
  }
}
