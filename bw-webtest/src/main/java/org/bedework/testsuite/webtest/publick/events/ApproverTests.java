/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.publick.events;

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * User: mike Date: 8/6/24 Time: 23:25
 */
@DisplayName("Public events: Approval tests")
public class ApproverTests extends PublicAdminTestBase {
  /**
   */
  @AfterEach
  public void tearDownAfterTest() {
    closeDriver();
  }

  /**
   */
  @Test
  @Order(100)
  @DisplayName("Public events: Add an event and have it approved")
  public void testApproval() {
    final String uuid = UUID.randomUUID().toString();
    final String eventTitle = "SELENIUM - CreatePubEventsTest - " + uuid;

    adminLogin(getProperty(propNonApproverUser),
               getProperty(propNonApproverUserPw));
    addEventPage();
    addSummary(eventTitle);
    addDescription("selenium test description");
    setDefaultTopicalArea();
    setALocation();
    setAContact();
    setTime();

    // set the cost and link
    setTextByName("eventCost", "FREE");
    setTextByName("eventLink",
                  getProperty(propAdminEventLink));
    clickAddEventNoErrors();

    // The event should show up in the approval queue
  }
}
