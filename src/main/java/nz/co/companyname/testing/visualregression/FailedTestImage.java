package nz.co.companyname.testing.visualregression;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/** This class contains methods for handling custom screen shots. */
public class FailedTestImage {
  static Map<String, BufferedImage> failScreenShotMap = new HashMap<String, BufferedImage>();

  public static void setFailScreenShot(String key, BufferedImage failScreenShot) {
    failScreenShotMap.put(key, failScreenShot);
  }

  public static Boolean isCustomScreenShotAvailable(String key) {
    return failScreenShotMap.containsKey(key);
  }

  public static BufferedImage getFailScreenShot(String key) {
    return failScreenShotMap.remove(key);
  }
}
