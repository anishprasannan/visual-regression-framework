package nz.co.companyname.testing.visualregression;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.awt.*;

/**
 * This class contains methods for baselining and performing layout tests.
 *
 * @see #baselineOrPerformTest(By, String)
 */
public class LayoutUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LayoutUtil.class);

    private final WebDriver driver;

    /** Used to set baseline mode on or off. */
    private final String IS_BASELINE = "isBaseline";

    private final String BASELINE_PATH = System.getProperty("baseline.output.dir", "");

    public LayoutUtil(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Baselines or tests depending on IS_BASELINE.
     *
     * @param by By type parameter for selecting the component
     * @param elementTitle String type parameter identifying the component
     * @return true for baseline mode. true or false in case of comparison
     * @see #IS_BASELINE
     */
    public boolean baselineOrPerformTest(By by, String elementTitle) {

        HashMap<String, Point[]> elementMap = new HashMap<String, Point[]>();
        WebElement element = driver.findElement(by);
        File outputFile = new File(BASELINE_PATH + "layout.ser");
        if (outputFile.exists()) {
            elementMap = syncOutputFileToElementMap(outputFile);
        }
        else {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Boolean.getBoolean(IS_BASELINE)) {
            try {
                //Get reactangle topleft and bottom right
                Point topLeft= new Point();
                topLeft.x=element.getLocation().getX();
                topLeft.y=element.getLocation().getY();
                Point bottomRight = new Point();
                bottomRight.x=topLeft.x+element.getSize().getWidth();
                bottomRight.y=topLeft.y+element.getSize().getHeight();
                Point[] elementCordinates = {topLeft, bottomRight};
                //Store in Hashmap
                elementMap.put(elementTitle, elementCordinates);
                //Write Hashmap
                syncElementMapToOutputFile(elementMap, outputFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            try {
                //Get reactangle topleft and bottom right
                Point topLeft= new Point();
                topLeft.x=element.getLocation().getX();
                topLeft.y=element.getLocation().getY();
                Point bottomRight = new Point();
                bottomRight.x=topLeft.x+element.getSize().getWidth();
                bottomRight.y=topLeft.y+element.getSize().getHeight();
                //Get Hashmap values
                Point[] baselineValues = elementMap.get(elementTitle);
                if (baselineValues[0].getX() == topLeft.getX() && baselineValues[0].getY() == topLeft.getY()
                    && baselineValues[1].getX() == bottomRight.getX() && baselineValues[1].getY() == bottomRight.getY()) {
                    return true;
                }
                else {
                    return false;
                }
                //Compare
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }

    private HashMap<String, Point[]> syncOutputFileToElementMap(File fileToSync) {
        try {
            ObjectInputStream readFile = new ObjectInputStream(new FileInputStream(fileToSync));
            HashMap<String, Point[]> elementMap = (HashMap<String, Point[]>) readFile.readObject();
            readFile.close();
            return elementMap;
        }
        catch (Exception e) {
            e.printStackTrace();
            LOG.error("Sync Failed: File and element hash map out of sync");
        }
        return null;
    }

    private void syncElementMapToOutputFile(HashMap<String, Point[]> elementMap, File outputFile) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(outputFile));
            outputStream.writeObject(elementMap);
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            LOG.error("Sync Failed: File and element hash map out of sync");
        }
    }
}
