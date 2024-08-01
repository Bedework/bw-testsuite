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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author johnsa
 *
 */
public class SeleniumUtil {
  private static DriverType dType;
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
  }

  public static void checkPage(final String client) {
    (new WebDriverWait(getWebDriver(), Duration.ofSeconds(10))).until(new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(final WebDriver d) {
        WebElement e = d.findElement(By.id("footer"));
        if (client.equals("admin")) {
          return e.getText().contains(TestDefs.adminFooter);
        }
        if (client.equals("public")) {
          return e.getText().contains(TestDefs.publicFooter);
        }
        return true;
      }
    });

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
