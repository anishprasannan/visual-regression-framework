package nz.co.companyname.visualtests.specification;

import nz.co.companyname.testing.visualregression.VisualUtil;
import nz.co.companyname.webdriver.WebDriverFixtureConcordion;
import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;

/** This is the corresponding Test class for the RegressionSet2.html file in resources. */
@RunWith(ConcordionRunner.class)
@FullOGNL
public class RegressionSet2Test extends WebDriverFixtureConcordion {

  public boolean isInputRendered() {
    return new VisualUtil(getWebDriver()).baselineOrPerformTest(By.cssSelector("#input1"), "input");
  }
}
