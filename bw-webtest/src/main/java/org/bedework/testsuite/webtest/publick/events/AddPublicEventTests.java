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

import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author johnsa
 *
 */
@Order(110)
@DisplayName("Public events: Add an event")
public class AddPublicEventTests extends PublicAdminTestBase {
  /**
   */
  @AfterEach
  public void tearDownAfterTest() {
    closeDriver();
  }

  /**
   */
  @Test
  @DisplayName("Public events: Add an event")
  public void testProcess() {
    final String uuid = UUID.randomUUID().toString();
    final String eventTitle =
            getProperty(propPublicEventTitlePrefix) +
                    " CreatePubEventsTest - " + uuid;

    adminLogin(getProperty(propApproverUser),
               getProperty(propApproverUserPw),
               "add public event");

    // get to the Add Event page
    startAddEvent(eventTitle,
                  "selenium test description");

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

    findByXpath(
            "//table[@id='commonListTable']/tbody/tr/td/a[contains(text(),'" +
                    uuid + "')]");
    logout();

    checkPublicPageForEvent(uuid, "2:00 PM");

    // Now remove the event
    adminLogin(getProperty(propAdminSuperUser),
               getProperty(propAdminSuperUserPw),
               "remove the public event");
    manageEventsPage();
    clickByXpath(
            "//table[@id='commonListTable']/tbody/tr/td/a[contains(text(),'" +
                    uuid + "')]");
    clickByXpath("//input[@value='Delete Event']");
    clickByXpath("//input[@value='Yes: Delete Event']");
    assertThat("Must have 'deleted' message",
               findById("messages").getText(),
               containsString("deleted"));
  }
}
