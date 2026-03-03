/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.publick.events;

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * User: mike Date: 8/6/24 Time: 23:25
 */
@DisplayName("Public events: Workflow tests")
@Order(1000)
public class WorkflowTests extends PublicAdminTestBase {
  /**
   */
  @AfterEach
  public void tearDownAfterTest() {
    closeDriver();
  }

  /**
   */
  @Test
  @DisplayName("Public events: Add an event and have it approved")
  public void testApproval() {
    setProperty("uuid", UUID.randomUUID().toString());

    adminLogin(getProperty(propNonApproverUser),
               getProperty(propNonApproverUserPw),
               "Non -approver add event");

    startAddEvent(getProperty("publicEventTitle"),
                  getProperty("publicEventApprovalDescription"),
                  "FREE",
                  getProperty("adminEventLink"),
                  null, null);
    clickAddEventNoErrors();

    logout();

    // The event should show up in the approval queue for the approver
    adminLogin(getProperty(propApproverUser2Groups),
               getProperty(propApproverUser2GroupsPw),
               "approve event");

    // Select group

    getAdminPageByHrefSeg(getProperty("nonApproverUserGroupName"));

    tabApproverQueue();

    // Need to get event uid from link showing the summary

    final var elementOnAppprovalQ =
        findByXpath("adminEventByUUID");

    final var href = elementOnAppprovalQ.getDomAttribute("href");
    Assertions.assertNotNull(href);
    final var guidStart = href.indexOf("guid=") + 5;
    final var guid = href.substring(guidStart,
                                    href.indexOf('&', guidStart + 1));

    clickAdminButton(guid, "Approve...");

    // Confirm approval on next page
    findByName("approveEvent").click();

    // Should show up in admin manage events.
    tabMainMenu();
    manageEventsPage();

    final var elementOnManageEvents =
        findByXpath("adminEventByUUID");

    checkPublicPageForEvent("2:00 PM");
  }
}
