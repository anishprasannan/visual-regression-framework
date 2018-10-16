package nz.co.companyname.visualtests.specification;

import static org.junit.Assert.*;

import java.util.LinkedList;
import nz.co.companyname.testing.visualregression.VisualUtil;
import nz.co.companyname.webdriver.Parallelized;
import nz.co.companyname.webdriver.WebDriverFixtureXBrowser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;
import org.openqa.selenium.*;

/** This is the Test class. */
@RunWith(Parallelized.class)
public class RegressionTest extends WebDriverFixtureXBrowser {

  private String os;
  private String browserName;

  @Parameterized.Parameters(name = "env - {0}{2}")
  public static LinkedList<String[]> getEnvironments() throws Exception {
    LinkedList<String[]> env = new LinkedList<String[]>();

    //Do not repeat the same env
    env.add(new String[] {"Windows", "10", "chrome", "64.0", "1600x1200"});
    env.add(new String[] {"Windows", "10", "firefox", "57.0", "1600x1200"});
    env.add(new String[] {"OS X", "High Sierra", "Safari", "11.0", "1600x1200"});
    env.add(new String[] {"Windows", "10", "Edge", "16.0", "1600x1200"});
    env.add(new String[] {"Windows", "10", "IE", "11.0", "1600x1200"});
    //add more browsers here

    return env;
  }

  public RegressionTest(
      String os, String osVersion, String browserName, String browserVersion, String resolution) {
    this.os = os;
    this.browserName = browserName;
    createRemoteEnv(os, osVersion, browserName, browserVersion, resolution);
  }

  /**
   * Retry a failed test.
   *
   * @see <a href="https://www.swtestacademy.com/rerun-failed-test-junit/">Credits to ONUR
   *     BASKIRT</a>
   */
  public class Retry implements TestRule {
    private int retryCount;

    public Retry(int retryCount) {
      this.retryCount = retryCount;
    }

    public Statement apply(Statement base, Description description) {
      return statement(base, description);
    }

    private Statement statement(final Statement base, final Description description) {
      return new Statement() {
        @Override
        public void evaluate() throws Throwable {
          Throwable caughtThrowable = null;

          for (int i = 0; i < retryCount; i++) {
            try {
              base.evaluate();
              return;
            } catch (Throwable t) {
              caughtThrowable = t;
              System.err.println(description.getDisplayName() + ": run " + (i + 1) + " failed");
            }
          }
          System.err.println(
              description.getDisplayName() + ": giving up after " + retryCount + " failures");
          throw caughtThrowable;
        }
      };
    }
  }

  @Rule public Retry retry = new Retry(2);

  @Test
  public void isSpanRendered() {
    login();
    assertTrue(
        new VisualUtil(getWebDriver()).baselineOrPerformTest(By.cssSelector("#span1"), "span"));
  }
}
