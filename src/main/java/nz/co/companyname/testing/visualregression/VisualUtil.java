package nz.co.companyname.testing.visualregression;

import static nz.co.companyname.testing.visualregression.ScreenShotUtil.takeScreenShot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods for baselining and performing tests.
 *
 * @see #baselineOrPerformTest(By, String)
 * @see #baselineOrPerformTest(By, String, float)
 * @see #TOLERANCE_FACTOR
 * @see #NEGLIGENCE_PERCENTAGE
 * @see #MASKING_COLOUR
 */
public class VisualUtil {

  private static final Logger LOG = LoggerFactory.getLogger(VisualUtil.class);

  private final WebDriver driver;

  /** Used to set baseline mode on or off. */
  private final String IS_BASELINE = "isBaseline";

  private final String BASELINE_PATH = System.getProperty("baseline.output.dir", "");

  /**
   * Set an acceptable level of difference between the same pixel positions on the baseline image
   * and the image we are comparing.
   */
  private final int TOLERANCE_FACTOR = 0;

  /**
   * Set an acceptable level of difference between the baseline image and the image we are
   * comparing.
   */
  private final float NEGLIGENCE_PERCENTAGE = 0;

  /** Mask off areas in the baseline image, that needs to be omitted from testing. */
  private final Color MASKING_COLOUR = Color.MAGENTA;

  public VisualUtil(WebDriver driver) {
    this.driver = driver;
  }

  /**
   * Baselines or tests depending on IS_BASELINE.
   *
   * @param by By type parameter for selecting the component
   * @param elementTitle String type parameter identifying the component
   * @return true for baseline mode. true or false in case of comparison
   * @see #IS_BASELINE
   * @see #NEGLIGENCE_PERCENTAGE
   */
  public boolean baselineOrPerformTest(By by, String elementTitle) {
    ((JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoView(true);", driver.findElement(by));
    if (Boolean.getBoolean(IS_BASELINE)) {
      File outputFile = new File(BASELINE_PATH + elementTitle + ".png");
      try {
        ImageIO.write(
            processImageToGetOnlyElement(takeScreenShot(driver), driver.findElement(by)),
            "png",
            outputFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return true;
    } else {
      BufferedImage baseLineImage = null;
      File baseLineFile = new File(BASELINE_PATH + elementTitle + ".png");
      try {
        baseLineImage = ImageIO.read(baseLineFile);
      } catch (IOException e) {
        e.printStackTrace();
        LOG.error("Baseline file not found: " + BASELINE_PATH + elementTitle + ".png");
      }
      return compareImages(
          baseLineImage,
          processImageToGetOnlyElement(takeScreenShot(driver), driver.findElement(by)),
          NEGLIGENCE_PERCENTAGE);
    }
  }

  /**
   * Baselines or tests depending on IS_BASELINE.
   *
   * @param by By type parameter for selecting the component
   * @param elementTitle String type parameter identifying the component
   * @param negligencePercentage Float type parameter for setting a custom negligence for a test
   * @return true for baseline mode. true or false in case of comparison
   * @see #IS_BASELINE
   * @see #NEGLIGENCE_PERCENTAGE
   */
  public boolean baselineOrPerformTest(By by, String elementTitle, float negligencePercentage) {
    ((JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoView(true);", driver.findElement(by));
    if (Boolean.getBoolean(IS_BASELINE)) {
      File outputFile = new File(BASELINE_PATH + elementTitle + ".png");
      try {
        ImageIO.write(
            processImageToGetOnlyElement(takeScreenShot(driver), driver.findElement(by)),
            "png",
            outputFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return true;
    } else {
      BufferedImage baseLineImage = null;
      File baseLineFile = new File(BASELINE_PATH + elementTitle + ".png");
      try {
        baseLineImage = ImageIO.read(baseLineFile);
      } catch (IOException e) {
        e.printStackTrace();
        LOG.error("Baseline file not found: " + BASELINE_PATH + elementTitle + ".png");
      }
      return compareImages(
          baseLineImage,
          processImageToGetOnlyElement(takeScreenShot(driver), driver.findElement(by)),
          negligencePercentage);
    }
  }

  /**
   * Grabs the image of element from the image file.
   *
   * @param imageFileToProcess File type parameter that contains the image in which element is
   *     present
   * @param element WebElement type parameter that contains element
   * @return BufferedImage of element
   * @see #isStartPointCorrectionNeeded(BufferedImage)
   */
  private BufferedImage processImageToGetOnlyElement(File imageFileToProcess, WebElement element) {

    int startPointOnScreenY;

    BufferedImage imageToProcess, imageOfElement = null;

    try {
      imageToProcess = ImageIO.read(imageFileToProcess);
      Point startPoint = element.getLocation();

      try {
        startPointOnScreenY =
            startPoint.getY()
                - (int)
                        (long)
                            (Long)
                                ((JavascriptExecutor) driver)
                                    .executeScript("return window.pageYOffset;")
                    * isStartPointCorrectionNeeded(imageToProcess);
      } catch (ClassCastException e) {
        startPointOnScreenY =
            startPoint.getY()
                - (int)
                        (double)
                            (Double)
                                ((JavascriptExecutor) driver)
                                    .executeScript("return window.pageYOffset;")
                    * isStartPointCorrectionNeeded(imageToProcess);
      } catch (Exception e) {
        e.printStackTrace();
        LOG.error("Extraction of image of element has failed!");
        startPointOnScreenY = startPoint.getY();
      }

      imageOfElement =
          imageToProcess.getSubimage(
              startPoint.getX(),
              startPointOnScreenY,
              element.getSize().getWidth(),
              element.getSize().getHeight());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return imageOfElement;
  }

  /**
   * Correction is needed in situations where a full page screenshot is not returned.
   *
   * @param imageToProcess
   * @return
   */
  //Please add x correction similarly, if needed.
  private int isStartPointCorrectionNeeded(BufferedImage imageToProcess) {
    int correction;
    try {
      correction =
          imageToProcess.getHeight()
                  == (int)
                      (long)
                          (Long)
                              ((JavascriptExecutor) driver)
                                  .executeScript("return document.body.scrollHeight;")
              ? 0
              : 1;
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error("Setting of Correction flag has failed!");
      correction = 0;
    }
    return correction;
  }

  /**
   * Compare images with a level of negligence.
   *
   * @param trueImage BufferedImage type parameter which is usually the baselined image
   * @param imageToCompare BufferedImage type parameter which is usually the new image
   * @param negligencePercentage float type parameter that sets the percentage of negligence
   * @return true or false after comparison
   */
  private boolean compareImages(
      BufferedImage trueImage, BufferedImage imageToCompare, float negligencePercentage) {

    BufferedImage differenceImage = copyBufferedImage(imageToCompare);
    int pixelDifference = 0;
    int trueImageHeight = trueImage.getHeight(), trueImageWidth = trueImage.getWidth();

    if (trueImage.getHeight() == imageToCompare.getHeight()
        && trueImage.getWidth() == imageToCompare.getWidth()) {
      for (int y = 0; y < trueImageHeight; y += 1) {
        for (int x = 0; x < trueImageWidth; x += 1) {
          Color currentColor = new Color(imageToCompare.getRGB(x, y));
          Color trueColor = new Color(trueImage.getRGB(x, y));
          if (!(trueColor.getRed() == MASKING_COLOUR.getRed()
                  && trueColor.getGreen() == MASKING_COLOUR.getGreen()
                  && trueColor.getBlue() == MASKING_COLOUR.getBlue())
              && Math.sqrt(
                      (Math.pow(trueColor.getRed() - currentColor.getRed(), 2))
                          + (Math.pow(trueColor.getGreen() - currentColor.getGreen(), 2))
                          + (Math.pow(trueColor.getBlue() - currentColor.getBlue(), 2)))
                  > TOLERANCE_FACTOR) {
            Color newColor = Color.RED;
            differenceImage.setRGB(x, y, newColor.getRGB());
            pixelDifference++;
          }
        }
      }
    }

    //Image sizes of trueImage and imageToCompare did not match, which means they are not the same
    else {
      pixelDifference = trueImageHeight * trueImageWidth;
      Graphics differenceImageGraphics = differenceImage.getGraphics();
      String errorMessage =
          "Element sizes do not match. Baseline Element: "
              + trueImage.getHeight()
              + "x"
              + trueImage.getWidth()
              + ". Current Element: "
              + imageToCompare.getHeight()
              + "x"
              + imageToCompare.getWidth();
      String[] splitErrorMessages = errorMessage.split("\\.");
      try {
        differenceImageGraphics.setColor(Color.RED);
        differenceImageGraphics.drawString(
            splitErrorMessages[0], 0, differenceImageGraphics.getFontMetrics().getHeight());
        differenceImageGraphics.drawString(
            splitErrorMessages[1], 0, differenceImageGraphics.getFontMetrics().getHeight() * 2);
        differenceImageGraphics.drawString(
            splitErrorMessages[2], 0, differenceImageGraphics.getFontMetrics().getHeight() * 3);
      } catch (Exception e) {
        e.printStackTrace();
        LOG.error(errorMessage);
      }
      differenceImageGraphics.dispose();
    }

    float pixelDiffPercentage =
        (float) (((float) pixelDifference / (float) (trueImageHeight * trueImageWidth)) * 100.0);

    if (pixelDifference > 0 && pixelDiffPercentage > negligencePercentage) {
      BufferedImage testResult =
          new BufferedImage(
              trueImage.getWidth() + (imageToCompare.getWidth() * 2),
              Math.max(trueImage.getHeight(), imageToCompare.getHeight()),
              BufferedImage.TYPE_INT_ARGB);

      Graphics testResultGraphics = testResult.getGraphics();
      testResultGraphics.drawImage(trueImage, 0, 0, null);
      testResultGraphics.drawImage(imageToCompare, trueImage.getWidth(), 0, null);
      testResultGraphics.drawImage(
          differenceImage, trueImage.getWidth() + imageToCompare.getWidth(), 0, null);
      testResultGraphics.dispose();
      FailedTestImage.setFailScreenShot(driver.getWindowHandle(), testResult);
      return false;
    } else {
      return true;
    }
  }

  /**
   * Creates a copy of the original
   *
   * @param original
   * @return Copy of the original
   */
  private BufferedImage copyBufferedImage(BufferedImage original) {
    BufferedImage copy =
        new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
    Graphics copyGraphics = copy.getGraphics();
    copyGraphics.drawImage(original, 0, 0, null);
    copyGraphics.dispose();
    return copy;
  }
}
