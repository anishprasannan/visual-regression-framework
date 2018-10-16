package nz.co.companyname.testing.webdriver;

public enum WebDriverProperties {
  BROWSER_NAME("webdriver.browser.name", ""),
  BROWSER_PROPS("webdriver.browser.props", "");

  private final String propertyName;

  private final String defaultValue;

  WebDriverProperties(String propertyName, String defaultValue) {
    this.propertyName = propertyName;
    this.defaultValue = defaultValue;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getDefaultValue() {
    return defaultValue;
  }
}
