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

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author johnsa
 *
 */
@DisplayName("Public events: Add an event")
public class AddPublicEventTests extends PublicAdminTestBase {
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

    adminLogin(getProperty(propApproverUser),
               getProperty(propApproverUserPw)); // log in as a typical event admin

    // get to the Add Event page
    addEventPage();

    // Page requires summary, description before submit button works

    addSummary(eventTitle);
    addDescription("selenium test description");

    // test basic validation without filling in the form
    clickAddEvent();

    errorMustContain(
            "Error should be thrown for no topical area: ",
            getProperty(propAdminErrorNoTopicalArea));

    // we must select a topical area to get to the next errors
    setDefaultTopicalArea();

    // test next validation error (no location)
    clickAddEvent();
    errorMustContain(
            "Error should be shown for 'no location': ",
            getProperty(propAdminErrorNoLocation));

    setALocation();

    // test next validation error (no contact)
    clickAddEvent();

    errorMustNotContain(
            "Should not have 'no location' error: ",
            getProperty(propAdminErrorNoLocation));

    errorMustContain(
            "Error should be shown for 'no contact': ",
            getProperty(propAdminErrorNoContact));

    setAContact();

    // Fill in the rest of the form
    // ================================================

    setTime();

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
    clickAddEventNoErrors();

    /* The event should show up in the manage events list. */
    manageEventsPage();

    final WebElement elementOnManageEvents = findByXpath(
            "//table[@id='commonListTable']/tbody/tr/td/a[contains(text(),'" +
                    uuid + "')]");

    // ****************************************
    // Now test the event in the public client.
    System.out.println("Event is published. " +
                               "Now checking event in public web client.");

    getPublicPage(getProperty(propPublicHome));

    // The event should exist today.  It should be on the current page.
    // The following will fail if not found:
    getAdminPageByXpath(
            "//div[@id='listEvents']//div[@class='bwSummary']/a[contains(text(),'" +
                    uuid + "')]");

    System.out.println("Event \"" + uuid + "\" found.");

    assertThat("Time should be at \"2:00 PM\": ",
               findByXpath("//div[@class='eventWhen']//span[@class='time']").getText(),
               containsString("2:00 PM"));
    System.out.println("Time matches 2:00 PM");
  }

}
