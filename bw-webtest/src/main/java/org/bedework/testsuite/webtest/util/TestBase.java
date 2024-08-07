/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
 */
package org.bedework.testsuite.webtest.util;

import org.bedework.util.misc.Util;

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
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author johnsa
 *
 */
public class TestBase {
  /** Browser drivers */
  public enum DriverType {
    HTMLUNIT, FIREFOX, IE, CHROME;
  }

  private static DriverType dType;

  private WebDriver driver;
  private Actions actions;

  private static Properties props;
  private static Util.PropertyFetcher pfetcher;
  private static final Object lock = new Object();

  public static final String overridePropfileSysProperty =
          "org.bedework.testsuite.webtest.overrides";

  // Property names
  /** Driver type */
  public static final String propDriverType = "driverType";

  /** Logout string - found in the URL */
  public static final String propLogoutText = "logoutText";

  public static final String propBedeworkLogo = "bedeworkLogo";
  public static final String propBedeworkLogoThumb = "bedeworkLogoThumb";

  /** Public client strings for testing */

  public static final String propPublicHost = "publicHost";
  public static final String propPublicHome = "publicHome";
  public static final String propPublicFooter = "publicFooter";

  /** Personal client strings for testing */
  public static final String propPersonalFooter =
          "personalFooter";
  public static final String propUserManageCalTitle =
          "userManageCalTitle";

  /** Submissions client strings for testing */
  public static final String propSubmissionsFooter =
          "submissionsFooter";

  public String getProperty(final String name) {
    if (props == null) {
      synchronized (lock) {
        if (props == null) {
          final var newProps = new Properties();
          try (final InputStream stream =
            getClass().getResourceAsStream("/webtest.properties")) {
            newProps.load(stream);
          } catch (final IOException e) {
            throw new RuntimeException(e);
          }

          final var overrides =
                  System.getProperty(overridePropfileSysProperty);

          if (overrides == null) {
            props = newProps;
          } else {
            final var overrideProps = new Properties(newProps);

            try (final InputStream stream =
                         new FileInputStream(overrides)) {
              overrideProps.load(stream);
            } catch (final IOException e) {
              throw new RuntimeException(e);
            }

            props = overrideProps;
          }

          // Use Util.propertyReplace to allow property
          // references in the file(s).
          pfetcher = new Util.PropertiesPropertyFetcher(props);
        }
      }
    }

    return Util.propertyReplace(props.getProperty(name),
                                pfetcher);
  }


  /** Type to be used for tests
   *
   * @param val driver type
   */
  public static void setDriverType(final DriverType val) {
    dType = val;
  }

  /**
   * @return current driver type
   */
  public static DriverType getDriverType() {
    return dType;
  }

  /**
   * Get a driver of the current type
   *
   * @return driver
   */
  public WebDriver getWebDriver() {
    if (driver != null) {
      return driver;
    }

    setDriverType(DriverType.valueOf(
            getProperty(propDriverType)));

    switch(getDriverType()) {
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

    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

    return driver;
  }

  public Actions getActions() {
    if (actions == null) {
      actions = new Actions(getWebDriver());
    }
    return actions;
  }

  /** Close the driver - and the browser.
   *
   */
  public void closeDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  protected WebElement sendLoginGetFooter(
          final String user,
          final String password) {
    // Log in to the client
    final WebElement element = driver.findElement(By.name("j_username"));
    element.sendKeys(user);
    final var pwElement = driver.findElement(By.name("j_password"));
    pwElement.sendKeys(password);
    pwElement.submit();

    // Verify that we are logged in
    return driver.findElement(By.id("footer"));
  }

  /**
   * Login to the web client
   */
  public void login(final String client,
                    final String user,
                    final String password) {
    try {
      final WebDriver driver = getWebDriver();

      if (client.equals("personal")) {
        driver.get("http://localhost:8080/ucal");
      }
      if (client.equals("submissions")) {
        driver.get("http://localhost:8080/eventsubmit");
      }

      // Log in to the client
      WebElement element = driver.findElement(By.name("j_username"));
      element.sendKeys(user);
      element = driver.findElement(By.name("j_password"));
      element.sendKeys(password);
      element.submit();

      // Verify that we are logged in
      element = driver.findElement(By.id("footer"));
      if (client.equals("personal")) {
        assertEquals(element.getText(),
                     getProperty(propPersonalFooter));
      }
      if (client.equals("submissions")) {
        assertThat("Submissions footer not found.",
                   element.getText(),
                   containsString(getProperty(propSubmissionsFooter)));
      }

      // Output the footer text:
      System.out.println("Logged into " + client + " client as user \"" + user + "\"");
    } catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeException(t);
    }
  }

  public void logout() {
    // Scroll to the top
    getActions().sendKeys(Keys.HOME).build().perform();
    final WebElement element = getWebDriver().
            findElement(By.xpath(
                    "//a[@id=\"bwLogoutButton\" and contains(@href, '" +
                        getProperty(propLogoutText) +
                        "')]"));
    assertNotNull(element);
    element.click();
    findById("loginBox");
  }

  public boolean checkboxValue(final String id) {
    final WebElement checkbox =
            getWebDriver().findElement(By.id(id));
    return checkbox.isSelected();
  }

  /**
   *
   * @param name of checkbox
   * @param value to set if needed
   * @return true if changed (submmit needed)
   */
  public boolean setCheckboxValueIfNeeded(final String name,
                                          final boolean value) {
    final WebElement checkbox = findByName(name);
    if (checkbox.isSelected() == value) {
      return false;
    }

    checkbox.click();
    return true;
  }

  public void clickById(final String id) {
    findById(id).click();
  }

  public void clickByName(final String name) {
    findByName(name).click();
  }

  public void clickByXpath(final String path) {
    findByXpath(path).click();
  }

  public void errorMustContain(final String reason,
                               final String matchValue) {
    assertThat(reason,
               findById("errors").getText(),
               containsString(matchValue));
  }

  public void errorMustNotContain(
          final String reason,
          final String matchValue) {
    assertThat(reason,
               findById("errors").getText(),
               not(containsString(matchValue)));
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

  public String getTextById(final String id) {
    return findById(id).getText();
  }

  public String getTextByTag(final String tag) {
    return findByTag(tag).getText();
  }

  public String getTextByXpath(final String path) {
    return findByXpath(path).getText();
  }

  public boolean presentById(final String id) {
    try {
      getWebDriver().findElement(By.id(id));
      return true;
    } catch (final NoSuchElementException ignored)
    {
      return false;
    }
  }

  public boolean presentByXpath(final String path) {
    try {
      getWebDriver().findElement(By.xpath(path));
      return true;
    } catch (final NoSuchElementException ignored)
    {
      return false;
    }
  }

  public void setTextById(final String id,
                          final String val) {
    findById(id).sendKeys(val);
  }

  public void setTextByName(final String name,
                            final String val) {
    findByName(name).sendKeys(val);
  }

  public boolean tableHasElementText(final String id,
                                            final String val) {
    final var table = findById(id);
    final List<WebElement> cells =
            getWebDriver().findElements(By.tagName("td"));

    for (final var cell: cells) {
      if (cell.getText().equals(val)) {
        return true;
      }
    }
    return false;
  }

  public void getPublicPage(final String href) {
    getWebDriver().get(href);
    checkPublicPage();
  }

  public void getPublicPageByXpath(final String xpath) {
    findByXpath(xpath).click();
    checkPublicPage();
  }

  public void checkPublicPage() {
    final WebElement e = findById("footer");

    assertThat("Footer must contain correct text: ",
               e.getText(),
               containsString(getProperty(propPublicFooter)));
  }

  public ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
    return new ExpectedCondition<WebElement>() {
      public WebElement apply(final WebDriver driver) {
        final WebElement toReturn = driver.findElement(locator);
        if (toReturn.isDisplayed()) {
          return toReturn;
        }
        return null;
      }
    };
  }
}
