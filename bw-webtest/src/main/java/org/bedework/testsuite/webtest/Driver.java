package org.bedework.testsuite.webtest;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;

public class Driver {
  /** Browser drivers */
  public enum DriverType {
    HTMLUNIT, FIREFOX, IE, CHROME
  }

  private final DriverType dType;

  private WebDriver driver;
  private Actions actions;

  public Driver(final DriverType dType) {
    this.dType = dType;
  }

  /** Close the driver - and the browser.
   *
   */
  public void close() {
    if (driver != null) {
      driver.close();
    }
  }

  public void scrollToTop() {
    getActions().sendKeys(Keys.HOME).build().perform();
  }

  protected void toIframeById(final String id) {
    final var iframe = findById(id);
    getWebDriver().switchTo().frame(iframe);
  }

  protected void toDefault() {
    getWebDriver().switchTo().defaultContent();
  }

  public void toHref(final String href) {
    getWebDriver().get(href);
  }

  public void clickByXpath(final String xpath) {
    findByXpath(xpath).click();
  }

  public WebElement findById(final String id) {
    return getWebDriver().findElement(By.id(id));
  }

  public WebElement findByName(final String val) {
    return getWebDriver().findElement(By.name(val));
  }

  public WebElement findByTag(final String val) {
    return getWebDriver().findElement(By.tagName(val));
  }

  public WebElement findByXpath(final String path) {
    return getWebDriver().findElement(By.xpath(path));
  }

  public WebElement findByAttribute(final String attr) {
    return getWebDriver().findElement(
        By.cssSelector("[" + attr + "]"));
  }

  public void setTextById(final String id,
                          final String val) {
    findById(id).sendKeys(val);
  }

  public boolean presentByXpath(final String path) {
    try {
      findByXpath(path);
      return true;
    } catch (final NoSuchElementException ignored) {
      return false;
    }
  }

  /**
   * Get a driver of the current type
   *
   * @return driver
   */
  private WebDriver getWebDriver() {
    if (driver != null) {
      return driver;
    }

    switch(dType) {
      case HTMLUNIT:
        driver = new HtmlUnitDriver();
        break;
      case FIREFOX:
        driver = new FirefoxDriver();
        break;
      case IE:
        driver = new InternetExplorerDriver();
        break;
      case CHROME:
        driver = new ChromeDriver();
        break;
    }

    driver.manage().timeouts().implicitlyWait(
        java.time.Duration.ofSeconds(10));

    return driver;
  }

  private Actions getActions() {
    if (actions == null) {
      actions = new Actions(getWebDriver());
    }
    return actions;
  }
}
