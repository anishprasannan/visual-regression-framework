package nz.co.companyname.visualtests.specification;

import static org.junit.Assert.*;

import nz.co.companyname.testing.visualregression.LayoutUtil;
import nz.co.companyname.testing.visualregression.VisualUtil;
import nz.co.companyname.webdriver.WebDriverFixtureVanilla;
import org.junit.Test;
import org.openqa.selenium.*;

/** This is the Test class. */
public class SampleTest extends WebDriverFixtureVanilla {

  @Test
  public void isSpanRendered() {
    login();
    assertTrue(
        new LayoutUtil(getWebDriver()).baselineOrPerformTest(By.cssSelector("#span1"), "span"));
  }

  @Test
  public void isInputRendered() {
    login();
    assertTrue(
            new LayoutUtil(getWebDriver()).baselineOrPerformTest(By.cssSelector("#input1"), "input"));
  }
}
