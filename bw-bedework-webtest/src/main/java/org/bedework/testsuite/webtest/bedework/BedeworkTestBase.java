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
package org.bedework.testsuite.webtest.bedework;

import org.bedework.testsuite.webtest.TestBase;

import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.property.DtStart;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author mike douglass
 *
 */
public abstract class BedeworkTestBase extends TestBase {
  public void setUUID() {
    setProperty("uuid", UUID.randomUUID().toString());
  }


  public void checkLoggedIn(final String checkPar) {
    // Verify that we are logged in
    mustBeTrue("assertionFooterMustContain",
               presentByXpath("commonFooterLinkHrefPath"));
  }
  /**
   * Login to the web client by going to an href
   */
  public void gotoLogin(final String pageHrefProp,
                        final String userProp,
                         final String passwordProp,
                         final String purposeProp) {
    goToHref(pageHrefProp);

    // Log in to the client
    sendLoginCheck(userProp,
                   passwordProp,
                   purposeProp,
                   this::checkLoggedIn,
                   null);
  }

  public void logout() {
    // Scroll to the top
    driver().scrollToTop();
    clickByXpath("commonLogoutButton");
    findById("loginBox");
  }

  public void clickByNameNoErrors(final String name,
                                  final String message) {
    clickByName(name);

    if (presentById("errors")) {
      fail(message + ": " +
               textById("errors"));
    }
  }

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

    setProperty("time", time);
    mustContain("commonExpectedTime", actual, "time");
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
}
