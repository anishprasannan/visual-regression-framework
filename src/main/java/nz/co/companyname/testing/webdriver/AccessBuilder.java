package nz.co.companyname.testing.webdriver;

import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class contains methods for navigating web pages in your tests. */
public class AccessBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(AccessBuilder.class);

  private static final String BASE_URL = System.getProperty("base.url", "");

  private final String url;

  private final WebDriver driver;

  public AccessBuilder(WebDriver driver) {
    this.driver = driver;
    this.url = makeUrl();
  }

  private String makeUrl() {
    try {
      URIBuilder builder = new URIBuilder(BASE_URL);
      return builder.build().toString();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Please check URL ", e);
    }
  }

  /** Used in login() method of WebDriverFixture classes for navigating to BASE_URL */
  public void navigateToURL() {
    //TODO Change the logic as needed, for your implementation
    LOG.info("Navigating to URL : " + url);
    if (driver.getCurrentUrl().contentEquals(url)) {
      LOG.info("Already at URL : " + url);
    } else {
      goTo(url);
      LOG.info("At URL : " + url);
    }
  }

  /**
   * Used in navigateToPage() method of WebDriverFixture classes for navigating to a page in
   * BASE_URL
   */
  public void navigateToPage(String page) {
    //TODO Change the logic as needed, for your implementation
    String pageURL = url + "#!/" + page;
    LOG.info("Navigating to page : " + pageURL);
    if (driver.getCurrentUrl().contentEquals(pageURL)) {
      LOG.info("Already at page : " + pageURL);
    } else {
      goTo(pageURL);
      LOG.info("At page : " + pageURL);
    }
  }

  private void goTo(String url) {
    LOG.info("Calling driver.get " + url);
    driver.get(url);
  }
}
