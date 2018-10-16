package nz.co.companyname.testing.visualregression;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 * This class contains methods for setting failed screenshot for output files. Inspired by <a
 * href="https://github.com/concordion/concordion-extensions-demo/blob/master/src/test/java/org/concordion/ext/selenium/SeleniumScreenshotTaker.java">
 * Concordion Example</a>
 */
public class ScreenshotTaker {

  private static final String OUTPUT_PATH = System.getProperty("test.output.dir");

  private final WebDriver driver;

  public ScreenshotTaker(WebDriver driver) {
    WebDriver baseDriver = driver;
    while (baseDriver instanceof EventFiringWebDriver) {
      baseDriver = ((EventFiringWebDriver) baseDriver).getWrappedDriver();
    }
    this.driver = baseDriver;
  }

  /**
   * Checks for a custom screenshot else writes screenshot of current screen to the output file.
   *
   * @param fileName String type parameter for the name under which the test result will be stored
   * @throws IOException
   */
  public void writeScreenshotTo(String fileName) throws IOException {
    File output = new File(OUTPUT_PATH + fileName + ".png");
    if (FailedTestImage.isCustomScreenShotAvailable(driver.getWindowHandle())) {
      BufferedImage screenshot = FailedTestImage.getFailScreenShot(driver.getWindowHandle());
      ImageIO.write(screenshot, "png", output);
    } else {
      BufferedImage screenshot;
      try {
        screenshot = ImageIO.read(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
        ImageIO.write(screenshot, "png", output);
      } catch (ClassCastException e) {
        e.printStackTrace();
      }
    }
  }
}
