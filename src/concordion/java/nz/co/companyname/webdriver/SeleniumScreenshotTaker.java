package nz.co.companyname.webdriver;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import nz.co.companyname.testing.visualregression.FailedTestImage;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.ext.ScreenshotUnavailableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 * This class contains methods for setting failed screenshot for concordion output file.
 *
 * @see <a
 *     href="https://github.com/concordion/concordion-extensions-demo/blob/master/src/test/java/org/concordion/ext/selenium/SeleniumScreenshotTaker.java">
 *     Concordion Example</a>
 */
public class SeleniumScreenshotTaker implements ScreenshotTaker {
  private final WebDriver driver;

  public SeleniumScreenshotTaker(WebDriver driver) {
    WebDriver baseDriver = driver;
    while (baseDriver instanceof EventFiringWebDriver) {
      baseDriver = ((EventFiringWebDriver) baseDriver).getWrappedDriver();
    }
    this.driver = baseDriver;
  }

  /**
   * Checks for a custom screenshot else writes screenshot of current screen to the outputStream.
   *
   * @param outputStream
   * @return width
   * @throws IOException
   */
  @Override
  public int writeScreenshotTo(OutputStream outputStream) throws IOException {
    int width = 0;
    if (outputStream != null) {
      if (FailedTestImage.isCustomScreenShotAvailable(driver.getWindowHandle())) {
        BufferedImage screenshot = FailedTestImage.getFailScreenShot(driver.getWindowHandle());
        ImageIO.write(screenshot, "png", outputStream);
        width = screenshot.getWidth();
      } else {
        byte[] screenshot;
        try {
          screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (ClassCastException e) {
          throw new ScreenshotUnavailableException("driver does not implement TakesScreenshot");
        }
        outputStream.write(screenshot);
        width =
            ((Long) ((JavascriptExecutor) driver).executeScript("return document.body.clientWidth"))
                .intValue();
      }
    }
    return width;
  }

  @Override
  public String getFileExtension() {
    return "png";
  }
}
