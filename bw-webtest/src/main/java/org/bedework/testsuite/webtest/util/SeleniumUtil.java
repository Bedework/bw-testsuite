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

import org.bedework.testsuite.webtest.util.TestDefs.DriverType;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author johnsa
 *
 */
public class SeleniumUtil {
  private static DriverType dType = DriverType.FIREFOX;
  private static WebDriver driver;
  private static WebDriverWait wait;

  /** Type to be used for tests
   *
   * @param val
   */
  public static void setDriverType(final DriverType val) {
    if (dType != val) {
      driver = null;
    }

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
  public static WebDriver getWebDriver() {
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

    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    System.out.println("Returning driver.");

    return driver;
  }

  /** Close the driver - and the browser.
   *
   */
  public static void closeDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  public static WebDriverWait getWebDriverWait() {
    if (wait != null) {
      return wait;
    }
    if (driver == null) {
      return null;
    }

    wait = new WebDriverWait(driver, Duration.ofSeconds(30));

    System.out.println("Returning driver wait.");

    return wait;
  }

  /**
   * Login to the admin web client
   */
  public static void login(final String client, final String user, final String password) {
    try {
      final WebDriver driver = getWebDriver();

      if (client.equals("admin")) {
        driver.get("http://localhost:8080/caladmin");
      }
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
      if (client.equals("admin")) {
        assertEquals(element.getText(),TestDefs.adminFooter);
      }
      if (client.equals("personal")) {
        assertEquals(element.getText(),TestDefs.personalFooter);
      }
      if (client.equals("submissions")) {
        assertThat("Submissions footer not found.",
                   element.getText(),
                   containsString(TestDefs.submissionsFooter));
      }

      // Output the footer text:
      System.out.println("Logged into " + client + " client as user \"" + user + "\"");
    } catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeException(t);
    }
  }

  public static void logout() {
    final WebElement element = getWebDriver().findElement(By.xpath("//a[contains(@href,TestDefs.logoutText)]"));
    assertNotNull(element);
    element.click();
    findById("loginBox");
  }

  public static boolean checkboxValue(final String id) {
    final WebElement checkbox = driver.findElement(By.id(id));
    return checkbox.isSelected();
  }

  /**
   *
   * @param name of checkbox
   * @param value to set if needed
   * @return true if changed (submmit needed)
   */
  public static boolean setCheckboxValueIfNeeded(final String name,
                                                 final boolean value) {
    final WebElement checkbox = driver.findElement(By.name(name));
    if (checkbox.isSelected() == value) {
      return false;
    }

    checkbox.click();
    return true;
  }

  public static void clickByName(final String name) {
    driver.findElement(By.name(name)).click();
  }

  public static WebElement findById(final String id) {
    return driver.findElement(By.id(id));
  }

  public static void setTextById(final String id,
                                 final String val) {
    findById(id).sendKeys(val);
  }

  public static boolean tableHasElementText(final String id,
                                            final String val) {
    final var table = driver.findElement(By.id(id));
    final List<WebElement> cells =
            driver.findElements(By.tagName("td"));

    for (final var cell: cells) {
      if (cell.getText().equals(val)) {
        return true;
      }
    }
    return false;
  }

  public static void gotoAdminPage(final String hrefSegment) {
    driver.findElement(By.xpath("//a[contains(@href,'" +
                                        hrefSegment +
                                        "')]")).click();
    SeleniumUtil.checkPage("admin");
  }

  public static void checkPage(final String client) {
    final WebElement e = driver.findElement(By.id("footer"));
    if (client.equals("admin")) {
      assertThat("Footer must contain correct text: ",
                 e.getText(),
                 containsString(TestDefs.adminFooter));
      return;
    }

    if (client.equals("public")) {
      assertThat("Footer must contain correct text: ",
                 e.getText(),
                 containsString(TestDefs.publicFooter));
      return;
    }
  }

  public static ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
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