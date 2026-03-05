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
package org.bedework.testsuite.webtest.eventreg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(110)
@DisplayName("Public event registration: Basic tests")
public class BasicEventregTests extends EventregBase {
  /**
   *
   */
  @AfterEach
  public void tearDownAfterTest() {
    try {
      cleanUpAdmin();
    } finally {
      closeDriver();
    }
  }

  /**
   *
   */
  @Test
  @DisplayName("Public event registration: Add an event and register a user")
  public void testEventreg() {
    setUUID();

    adminLogin("approverUser", "approverUserPw",
               "eventregLoginAddEventPurpose");

    startAddEvent("eventregEventTitle",
                  "eventregEventDescription",
                  "FREE",
                  "adminEventLink",
                  null,
                  null);
    setCheckboxValueIfNeeded("bwIsRegisterableEvent", true);

    // An alert should show up
    // driver().switchTo().alert().accept();

    // Click on add/manage and authenticate

    setTextByNameStr("xBwMaxTicketsHolder", "20");
    // set tickets allowed - xBwMaxTicketsPerUserHolder
    // optionally set max wait list - xBwMaxWaitListHolder
    // leave opens
    setDateAfterByName("xBwRegistrationClosesDate",
                       "P30D");

    clickAddEventNoErrors();

    logout();

    // Now locate the event in the public client

    checkPublicPageForEvent("2:00 PM");

    // Look for the registration fields

    assertTrue(presentById("bwRegistrationBox"),
               getProperty("assertionEventregFieldsMissing"));

    //switch To IFrame using Web Element
    toIframe("evregIframe");

    msg("msgEventregLogin");
    sendLogin("eventregUser", "eventregPw");

    clickById("register");
    assertThat("Should have registered message",
               textByTag("p"),
               containsString("Thank you! Your request for"));

    msg("msgEventregSuccess");
    findByAttribute("href='logout.do'").click();
    toDefault();
  }

  private void cleanUpAdmin() {
    // Try to remove any of our events.

    try {
      // Try a logout - just in case a failure left us logged in
      try {
        logout();
      } catch (final Throwable ignored) {}
      // Login as a superuser
      adminLogin("adminSuperUser", "adminSuperUserPw",
                 "adminLoginCleanupPurpose");
      manageEventsPage();
      var found = false;
      do {
        found = false;
        final var tbody = findById("commonListTableBody");
        for (final var link: tbody.findElements(By.tagName("a"))) {
          msgStr("Found link: " + link.getText());
          if (link.getText().contains(
                  getProperty(propEventregTitlePrefix))) {
            link.click();
            clickByName("delete");
            // Confirm
            clickByName("delete");
            assertThat("Must have 'deleted' message",
                       textById("messages"),
                       containsString("deleted"));

            found = true;
            break;
          }
        }
      } while (found);
    } finally {
      logout();
    }
  }
}
