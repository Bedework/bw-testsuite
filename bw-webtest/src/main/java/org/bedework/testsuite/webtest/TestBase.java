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
package org.bedework.testsuite.webtest;

import org.bedework.testsuite.webtest.Driver.DriverType;

import org.apache.commons.lang3.SystemUtils;
import org.hamcrest.Matcher;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author johnsa
 *
 */
public abstract class TestBase {
  private Driver driver;

  private static final ThreadLocal<TestProperties> props =
      new ThreadLocal<>();
  private static final Object lock = new Object();

  protected static boolean isMac = SystemUtils.IS_OS_MAC;

  /** Returned values for bedework should be something like
   * (English form)
   *
   * "public" for the public web client
   * "personal" for the personal web client
   * "admin" for the public event admin web client
   * "submissions" for the public event submissions web client
   * "event registration" for the public event registration
   * web client
   * <p>
   * These values are NOT used in tests - just for messages
   * so should come from the properties file so they may be
   * localized. The bedework test classes do so.
   * @return a string identifying the client we are testing.
   */
  public abstract String getClientName();

  public Driver driver() {
    if (driver == null) {
      driver = new Driver(
          DriverType.valueOf(getProperty("driverType")));
    }

    return driver;
  }

  /** Close the driver - and the browser.
   *
   */
  public void closeDriver() {
    if (driver != null) {
      driver.close();
    }
  }

  public TestProperties getProperties() {
    if (props.get() == null) {
      synchronized (lock) {
        props.set(new TestProperties());
      }
    }

    return props.get();
  }

  public String getProperty(final String name) {
    final var val = getProperties().getProperty(name);
    assertNotNull(val, "Missing property: " + name);
    return val;
  }

  public void setProperty(final String name,
                          final String value) {
    props.get().setProperty(name, value);
  }

  public void setPropertyFrom(final String name,
                              final String valueProp) {
    props.get().setProperty(name, getProperty(valueProp));
  }

  protected void toIframe(final String id) {
    driver().toIframeById(id);
  }

  protected void toDefault() {
    driver().toDefault();
  }

  public boolean checkboxValue(final String id) {
    return findById(id).isSelected();
  }

  /**
   *
   * @param name of checkbox
   * @param value to set if needed
   * @return true if changed (submit needed)
   */
  public boolean setCheckboxValueIfNeeded(final String name,
                                          final boolean value) {
    final var checkbox = findByName(name);
    if (checkbox.isSelected() == value) {
      return false;
    }

    checkbox.click();
    return true;
  }

  /**
   *
   * @param id of radio button
   * @param value to set if needed
   * @return true if changed (submit needed)
   */
  public boolean setRadioByIdIfNeeded(final String id,
                                      final boolean value) {
    final var radio = findById(id);
    if (radio.isSelected() == value) {
      return false;
    }

    radio.click();
    return true;
  }

  public void clickById(final String id) {
    findById(id).click();
  }

  public void clickByName(final String name) {
    findByName(name).click();
  }

  public void clickByXpath(final String pathProp) {
    findByXpath(pathProp).click();
  }

  public WebElement parentOf(final WebElement val) {
    return val.findElement(By.xpath("./.."));
  }

  public WebElement findById(final String id) {
    return driver().findById(id);
  }

  public WebElement findByName(final String val) {
    return driver().findByName(val);
  }

  public WebElement findByTag(final String val) {
    return driver().findByTag(val);
  }

  public WebElement findByXpath(final String pathProp) {
    return driver().findByXpath(getProperty(pathProp));
  }

  public WebElement findByXpathStr(final String path) {
    return driver().findByXpath(path);
  }

  public WebElement findByAttribute(final String attr) {
    return driver().findByAttribute(attr);
  }

  public String textById(final String id) {
    return findById(id).getText();
  }

  public String textByTag(final String tag) {
    return findByTag(tag).getText();
  }

  public String textByXpath(final String pathProp) {
    return findByXpath(pathProp).getText();
  }

  public String textByAttribute(final String attr) {
    return findByAttribute(attr).getText();
  }

  public boolean presentById(final String id) {
    try {
      findById(id);
      return true;
    } catch (final NoSuchElementException ignored) {
      return false;
    }
  }

  public boolean presentByXpath(final String pathProp) {
    return driver().presentByXpath(getProperty(pathProp));
  }

  public void setTextById(final String id,
                          final String valProp) {
    driver().setTextById(id, getProperty(valProp));
  }

  public void setTextByIdStr(final String id,
                             final String val) {
    findById(id).sendKeys(val);
  }

  public void setTextByName(final String name,
                            final String valProp) {
    findByName(name).sendKeys(getProperty(valProp));
  }

  public void setTextByNameStr(final String name,
                               final String val) {
    findByName(name).sendKeys(val);
  }

  public boolean tableHasElementText(final String id,
                                     final String valProp) {
    final var table = findById(id);
    final List<WebElement> cells =
            table.findElements(By.tagName("td"));
    final var val = getProperty(valProp);

    for (final var cell: cells) {
      if (cell.getText().equals(val)) {
        return true;
      }
    }
    return false;
  }

  public void goToHref(final String hrefProp) {
    driver().toHref(getProperty(hrefProp));
  }

  public ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
    return driver -> {
      final WebElement toReturn = driver.findElement(locator);
      if (toReturn.isDisplayed()) {
        return toReturn;
      }
      return null;
    };
  }

  /* ---------------------- Assertions using properties */

  public void mustBeTrue(final String reasonProp,
                         final boolean expectedValue) {
    assertThat(getProperty(reasonProp), expectedValue);
  }

  public void mustBeEqual(final String expectedValue,
                          final String actualValue) {
    assertEquals(getProperty(expectedValue), actualValue);
  }

  public void mustContain(
      final String reasonProp,
      final String actual,
      final String valProp) {
    assertThat(getProperty(reasonProp), actual,
               containsString(getProperty(valProp)));
  }

  public <T> void assertionThat(
      final String reasonProp,
      final T actual,
      final Matcher<? super T> matcher) {
    assertThat(getProperty(reasonProp), actual, matcher);
  }

  public Matcher<String> hasString(final String valProp) {
    return containsString(getProperty(valProp));
  }

  @FunctionalInterface
  public interface CheckLoggedIn {
    void check(String checkProp);
  }

  /** Standard java login form
   *
   * @param userProp account
   * @param passwordProp password text
   */
  protected void sendLogin(
      final String userProp,
      final String passwordProp) {
    // Log in to the client
    findByName("j_username").sendKeys(getProperty(userProp));
    findByName("j_password").sendKeys(getProperty(passwordProp));
    clickByName("j_security_check");
  }

  /* Login and check */
  protected void sendLoginCheck(
      final String userProp,
      final String passwordProp,
      final String msgPurposeProp,
      final CheckLoggedIn checkLoggedIn,
      final String checkProp) {
    setPropertyFrom("user", userProp);
    setPropertyFrom("loginPurpose", msgPurposeProp);
    msg("msgCommonAboutToLogin");

    sendLogin(userProp, passwordProp);

    checkLoggedIn.check(checkProp);

    msg("msgCommonLoggedIn");
  }

  /* ---------------------- Output and formatting */

  final static DateTimeFormatter fmt =
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  protected void msg(final String msgProp) {
    System.out.println(LocalDateTime.now().format(fmt) + ": " +
                               getProperty(msgProp));
  }

  protected void msgStr(final String msg) {
    System.out.println(LocalDateTime.now().format(fmt) + ": " + msg);
  }

  protected void msgStr(final String msg, final String... msgPars) {
    System.out.printf(LocalDateTime.now().format(fmt) + ": " + msg, msgPars);
  }
}
