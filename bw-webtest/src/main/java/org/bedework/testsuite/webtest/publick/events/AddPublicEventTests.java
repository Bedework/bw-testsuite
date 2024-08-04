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
package org.bedework.testsuite.webtest.publick.events;

import org.bedework.testsuite.webtest.util.TestBase;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author johnsa
 *
 */
@DisplayName("Public events: Add an event")
public class AddPublicEventTests extends TestBase {
  private Select select;

  /**
   */
  @AfterEach
  public void tearDownAfterTest() {
    closeDriver();
  }

  /**
   */
  @Test
  @Order(10)
  @DisplayName("Public events: Add an event")
  public void testProcess() {
    final String uuid = UUID.randomUUID().toString();
    final String eventTitle = "SELENIUM - CreatePubEventsTest - " + uuid;

    login("admin", getProperty(propAdminUser),
          getProperty(propAdminUserPw)); // log in as a typical event admin

    // get to the Add Event page
    clickByXpath("//a[contains(@href,'initAddEvent.do')]");
    checkPage("admin");

    assertEquals(findByTag("h2").getText(),
                 getProperty(propAdminEventInfoTitle));
    System.out.println("On " + getProperty(propAdminEventInfoTitle) + " page.");

    // Page requires summary, description before submit button works

    setTextByName("summary", eventTitle);
    setTextByName("description", "selenium test description");

    // test basic validation without filling in the form
    clickByName("addEvent");

    MatcherAssert.assertThat(
            "Error should be thrown for no topical area: ",
            findById("errors").getText(),
            containsString(
                    getProperty(propAdminErrorNoTopicalArea)));

    // we must select a topical area to get to the next errors
    findByXpath(getProperty(propAdminEventTopicalArea1Xpath)).click();

    // test next validation error (no location)
    clickByName("addEvent");
    failByIdErrorContains(
            "Error should be shown for 'no location': ",
            getProperty(propAdminErrorNoLocation));

    // select a location
    /* Only have search option
    findById("bwLocationAllButton").click();
    select = new Select(findById("bwAllLocationList"));
    select.selectByIndex(1);
     */

    // Set text in search box
    setTextById("bwLocationSearch", "loc");
    final var selectedLoc = findByXpath(
            "//div[@id=\"bwLocationSearchResults\"]/ul/li[1]");
    selectedLoc.click();

    // test next validation error (no contact)
    clickByName("addEvent");

    failByIdErrorDoesNotContain(
            "Should not have 'no location' error: ",
            getProperty(propAdminErrorNoLocation));

    failByIdErrorContains(
            "Error should be shown for 'no contact': ",
            getProperty(propAdminErrorNoContact));

    // select a contact
    /* Only have search option
    findById("bwContactAllButton").click();
    select = new Select(findById("bwAllContactList"));
    select.selectByIndex(1);
     */

    // Set text in search box
    setTextById("bwContactSearch", "co");
    final var selectedContact = findByXpath(
            "//div[@id=\"bwContactSearchResults\"]/ul/li[1]");
    selectedContact.click();

    // Fill in the rest of the form
    // ================================================

    // set the time to 2pm or 14:00; the date should be "today" by default
    select = new Select(findById("eventStartDateHour"));
    if (presentById("eventStartDateAmpm")) {
      // The American way
      select.selectByIndex(1);
      select = new Select(findById("eventStartDateAmpm"));
      select.selectByIndex(1); //pm
    } else {
      // Everyone else?
      select.selectByIndex(14);
    }
    select = new Select(findById("eventStartDateMinute"));
    select.selectByIndex(0);

    // set the cost and link
    setTextByName("eventCost", "FREE");
    setTextByName("eventLink",
                  getProperty(propAdminEventLink));

    // image and thumbnail URLs
    // we'll check image uploads in update event test, because we need to also test image removal
    setTextByName("xBwImageHolder",
                  getProperty(propBedeworkLogo));
    setTextByName("xBwImageThumbHolder",
                  getProperty(propBedeworkLogoThumb));

    // submit the event
    clickByName("addEvent");

    if (presentById("errors")) {
      fail("Errors on submission: " +
                   findById("errors").getText());
    }

    // ****************************************
    // Now test the event in the public client.
    System.out.println("Event is published.");
    System.out.println("Waiting ten seconds for indexer to get the event in the public client....");
    try {
      Thread.sleep(10000);
    } catch (final InterruptedException e) {
      fail("Interrupted while waiting for indexer to get the event in the public client....");
    }

    System.out.println("Now checking event in public web client.");

    getPublicPage(getProperty(propPublicHome));

    // The event should exist today.  It should be on the current page.
    // The following will fail if not found:
    final WebElement element = findByXpath(
            "//li[@class='titleEvent']/a[contains(text(),'" +
                    uuid + "')]");

    // We found it, now click the link to visit the event detail page.
    element.click();
    System.out.println("Event \"" + uuid + "\" found.");

    assertThat("Time should be at \"2:00 PM\": ",
               findByXpath("//div[@class='eventWhen']/span[@class='time']").getText(),
               containsString("2:00 PM"));
    System.out.println("Time matches 2:00 PM");
  }

}
