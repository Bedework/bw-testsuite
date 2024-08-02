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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.time.Duration;
import java.util.List;

import static org.bedework.testsuite.webtest.util.SeleniumUtil.getDriverType;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author johnsa
 *
 */
public class TestBase {
  private WebDriver driver;

  /**
   * Get a driver of the current type
   *
   * @return driver
   */
  public WebDriver getWebDriver() {
    if (driver != null) {
      return driver;
    }

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
    System.out.println("Returning driver.");

    return driver;
  }

  /** Close the driver - and the browser.
   *
   */
  public void closeDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  /**
   * Login to the admin web client
   */
  public void login(final String client,
                    final String user,
                    final String password) {
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

  public void logout() {
    final WebElement element = getWebDriver().findElement(By.xpath("//a[contains(@href,TestDefs.logoutText)]"));
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
    final WebElement checkbox = getWebDriver().findElement(By.name(name));
    if (checkbox.isSelected() == value) {
      return false;
    }

    checkbox.click();
    return true;
  }

  public void clickByName(final String name) {
    getWebDriver().findElement(By.name(name)).click();
  }

  public WebElement findById(final String id) {
    return getWebDriver().findElement(By.id(id));
  }

  public void setTextById(final String id,
                                 final String val) {
    findById(id).sendKeys(val);
  }

  public boolean tableHasElementText(final String id,
                                            final String val) {
    final var table = getWebDriver().findElement(By.id(id));
    final List<WebElement> cells =
            getWebDriver().findElements(By.tagName("td"));

    for (final var cell: cells) {
      if (cell.getText().equals(val)) {
        return true;
      }
    }
    return false;
  }

  public void gotoAdminPage(final String hrefSegment) {
    getWebDriver().findElement(
            By.xpath("//a[contains(@href,'" +
                             hrefSegment +
                             "')]")).click();
    checkPage("admin");
  }

  public void checkPage(final String client) {
    final WebElement e = findById("footer");
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
