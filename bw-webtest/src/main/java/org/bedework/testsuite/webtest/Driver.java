package org.bedework.testsuite.webtest;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Driver {
  /** Browser drivers */
  public enum DriverType {
    HTMLUNIT, FIREFOX, IE, CHROME
  }

  private final DriverType dType;

  private WebDriver driver;
  private Actions actions;
  private WebDriverWait wait5;

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

  public Driver scrollToCenter(final WebElement element) {
    ((JavascriptExecutor) driver).executeScript(
        """
            const rect = arguments[0].getBoundingClientRect();
            window.scrollBy({ top: rect.top + window.pageYOffset - (window.innerHeight / 2) + (rect.height / 2), 
                    behavior: 'smooth' });
            """, element);
    waitFor(element);
    return this;
  }

  public WebElement waitFor(final WebElement element) {
    return wait5.until(ExpectedConditions
                           .visibilityOf(element));
  }

  public WebElement waitByXpath(final String path) {
    return wait5.until(ExpectedConditions
                           .visibilityOfElementLocated(By.xpath(path)));
  }

  public void setTextById(final String id,
                          final String val) {
    waitFor(findById(id)).sendKeys(val);
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
    wait5 = new WebDriverWait(driver, Duration.ofSeconds(5));

    return driver;
  }

  private Actions getActions() {
    if (actions == null) {
      actions = new Actions(getWebDriver());
    }
    return actions;
  }
}
