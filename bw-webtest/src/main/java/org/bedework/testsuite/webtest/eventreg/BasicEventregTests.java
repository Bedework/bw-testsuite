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

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
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
    final String uuid = UUID.randomUUID().toString();
    final String eventTitle =
            getProperty(propEventregTitlePrefix) +
                    " EventregTest - " + uuid;

    adminLogin(getProperty(propApproverUser),
               getProperty(propApproverUserPw));

    startAddEvent(eventTitle,
                  "bedework eventreg test description", "FREE",
                  getProperty(propAdminEventLink),
                  null,
                  null);
    setCheckboxValueIfNeeded("bwIsRegisterableEvent", true);

    // An alert should show up
    // getWebDriver().switchTo().alert().accept();

    // Click on add/manage and authenticate

    setTextByName("xBwMaxTicketsHolder", "20");
    // set tickets allowed - xBwMaxTicketsPerUserHolder
    // optionally set max wait list - xBwMaxWaitListHolder
    // leave opens
    setDateAfterByName("xBwRegistrationClosesDate",
                       "P30D");

    clickAddEventNoErrors();

    logout();

    // Now locate the event in the public client

    checkPublicPageForEvent(uuid, "2:00 PM");

    // Look for the registration fields

    assertTrue(presentById("bwRegistrationBox"),
               "Event registration fields missing for event " + uuid);

    //switch To IFrame using Web Element
    toIframe("evregIframe");

    sendLogin(getProperty(propEventregUser),
              getProperty(propEventregPw));

    clickById("register");
    assertThat("Should have registered message",
               findByTag("p").getText(),
               containsString("Thank you! Your request for"));

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
      adminLogin(getProperty(propAdminSuperUser),
                 getProperty(propAdminSuperUserPw));
      eventsListPage();
      var found = false;
      do {
        found = false;
        final var tbody = findById("commonListTableBody");
        for (final var link: tbody.findElements(By.tagName("a"))) {
          System.out.println("Found link: " + link.getText());
          if (link.getText().contains(
                  getProperty(propEventregTitlePrefix))) {
            link.click();
            findByName("delete").click();
            // Confirm
            findByName("delete").click();
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
