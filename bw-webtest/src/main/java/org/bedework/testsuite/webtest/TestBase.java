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

import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.property.DtStart;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author johnsa
 *
 */
public class TestBase {
  private Driver driver;

  private static final ThreadLocal<TestProperties> props =
      new ThreadLocal<>();
  private static final Object lock = new Object();

  protected static boolean isMac = SystemUtils.IS_OS_MAC;

  /** Logout string - found in the URL */
  public static final String propLogoutText = "logoutText";

  public String getDateAfter(final String duration) {
    final var dur = new Dur(duration);
    final var tm = dur.getTime(new Date());
    final var dt = new DtStart(new net.fortuna.ical4j.model.Date(tm));

    return dt.toString();
  }

  public void setDateAfterByName(final String fieldName,
                                 final String duration) {
    /*
    /html/body/div[2]/div/a[2]
     */
    clickByName(fieldName);

    // Date picker should be up

    final var datePickDiv = findById("ui-datepicker-div");
    // Move on one month
    datePickDiv.findElement(
            By.cssSelector("[data-handler='next']")).click();
    // Go for the 27th
    final List<WebElement> links = datePickDiv.findElements(
            By.tagName("a"));

    for (final var el: links) {
      if ("27".equals(el.getText())) {
        el.click();
      }
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
    return getProperties().getProperty(name);
  }

  public void setUUID() {
    setProperty("uuid", UUID.randomUUID().toString());
  }

  public void setProperty(final String name,
                          final String value) {
    props.get().setProperty(name, value);
  }

  public void setPropertyFrom(final String name,
                              final String valueProp) {
    props.get().setProperty(name, getProperty(valueProp));
  }

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

  protected void toIframe(final String id) {
    driver().toIframeById(id);
  }

  protected void toDefault() {
    driver().toDefault();
  }

  protected void sendLogin(
          final String userProp,
          final String passwordProp) {
    // Log in to the client
    findByName("j_username").sendKeys(getProperty(userProp));
    findByName("j_password").sendKeys(getProperty(passwordProp));
    clickByName("j_security_check");
  }

  protected WebElement sendLoginGetFooter(
          final String userProp,
          final String passwordProp) {
    sendLogin(userProp, passwordProp);

    // Verify that we are logged in
    return findById("footer");
  }

  public void logout() {
    // Scroll to the top
    driver().scrollToTop();
    clickByXpath("commonLogoutButton");
    findById("loginBox");
  }

  public boolean checkboxValue(final String id) {
    return findById(id).isSelected();
  }

  public void checkPublicPageForEvent(final String time) {
    // ****************************************
    // Now test the event in the public client.
    msg("msgEventPublishedCheckingPublic");

    getPublicPage("publicHome");

    // The event should exist today.  It should be on the current page.
    // The following will fail if not found:
    getPublicPageByXpath("publicEventByUUID");

    msg("msgEventFound");

    final var actual = textByXpath("publicEventTime")
        .replaceAll("\\u202F", " ");
    // May need to replace other localization characters.

    msgStr(format("Actual time is \"%s\" required \"%s\"", actual, time));

    assertThat("Time should be at \"" +
                       time +
                       "\": ",
               actual,
               containsString(time));
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

  public void clickByNameNoErrors(final String name,
                                    final String message) {
    clickByName(name);

    if (presentById("errors")) {
      fail(message + ": " +
               textById("errors"));
    }
  }

  public void clickByXpath(final String pathProp) {
    findByXpath(pathProp).click();
  }

  public void errorMustContain(final String reason,
                               final String matchValue) {
    assertThat(reason,
               textById("errors"),
               containsString(matchValue));
  }

  public void errorMustNotContain(
          final String reason,
          final String matchValue) {
    assertThat(reason,
               textById("errors"),
               not(containsString(matchValue)));
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

  public void getPublicPage(final String hrefProp) {
    goToHref(hrefProp);
    checkPublicPage();
  }

  public void getPublicPageByXpath(final String xpathProp) {
    clickByXpath(xpathProp);
    checkPublicPage();
  }

  public void checkPublicPage() {
    assertThat("Footer must contain correct text: ",
               textById("footer"),
               containsString(getProperty("publicFooter")));
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

  final static DateTimeFormatter fmt =
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  protected void msg(final String msgProp) {
    System.out.println(LocalDateTime.now().format(fmt) + ": " +
                               getProperty(msgProp));
  }

  protected void msgStr(final String msg) {
    System.out.println(LocalDateTime.now().format(fmt) + ": " + msg);
  }

  protected void msg(final String msg, final String... msgPars) {
    System.out.printf(LocalDateTime.now().format(fmt) + ": " + msg, msgPars);
  }
}
