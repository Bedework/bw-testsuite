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
    setUUID();

    adminLogin("approverUser", "approverUserPw",
               "adminLoginAddPublicEventPurpose");

    // get to the Add Event page
    startAddEvent("publicEventTitle",
                  "publicEventAddDescription");

    // test basic validation without filling in the form
    clickAddEvent();

    errorMustContain("assertionAdminErrorNoTopicalArea",
                     "adminErrorNoTopicalArea");

    // we must select a topical area to get to the next errors
    setDefaultTopicalArea();

    // test next validation error (no location)
    clickAddEvent();
    errorMustContain("assertionAdminErrorNoLocation",
                     "adminErrorNoLocation");

    setALocation();

    // test next validation error (no contact)
    clickAddEvent();

    errorMustNotContain("assertionAdminErrorNotNoContact",
                        "adminErrorNoContact");

    errorMustContain("assertionAdminErrorNoContact",
                     "adminErrorNoContact");

    setAContact();

    // Fill in the rest of the form
    // ================================================

    setTime();

    // set the cost and link
    setTextByNameStr("eventCost", "FREE");
    setTextByName("eventLink", "adminEventLink");

    // image and thumbnail URLs
    // we'll check image uploads in update event test, because we need to also test image removal
    setTextByName("xBwImageHolder", "bedeworkLogo");
    setTextByName("xBwImageThumbHolder", "bedeworkLogoThumb");

    // submit the event
    clickAddEventNoErrors();

    /* The event should show up in the manage events list. */
    manageEventsPage();

    findByXpath("adminEventByUUID");
    logout();

    checkPublicPageForEvent("2:00 PM");

    // Now remove the event
    adminLogin("adminSuperUser", "adminSuperUserPw",
               "adminLoginRemovePublicEventPurpose");
    manageEventsPage();
    clickByXpath("adminEventByUUID");
    clickByXpath("adminDeleteEventLink");
    clickByXpath("adminDeleteEventConfirm");
    mustContain("assertionAdminMustHaveDeleted",
               textById("messages"),
               "adminDeletedEventText");
  }
}
