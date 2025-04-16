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
    final String uuid = UUID.randomUUID().toString();
    final String eventTitle =
            getProperty(propPublicEventTitlePrefix) +
                    " CreatePubEventsTest - " + uuid;

    adminLogin(getProperty(propNonApproverUser),
               getProperty(propNonApproverUserPw),
               "Non -approver add event");

    startAddEvent(eventTitle,
                  "bedework public event approval test description", "FREE",
                  getProperty(propAdminEventLink),
                  null, null);
    clickAddEventNoErrors();

    logout();

    // The event should show up in the approval queue for the approver
    adminLogin(getProperty(propApproverUser2Groups),
               getProperty(propApproverUser2GroupsPw),
               "approve event");

    // Select group

    getAdminPageByHrefSeg(getProperty(propNonApproverUserGroupName));

    tabApproverQueue();

    // Need to get event uid from link showing the summary

    final var elementOnAppprovalQ = findByXpath(
            "//table[@id='commonListTable']/tbody/tr/td/a[contains(text(),'" +
                    uuid + "')]");

    final var href = elementOnAppprovalQ.getAttribute("href");
    final var guidStart = href.indexOf("guid=") + 5;
    final var guid = href.substring(guidStart,
                                    href.indexOf('&', guidStart + 1));

    clickAdminButton(guid, "Approve...");

    // Confirm approval on next page
    findByName("approveEvent").click();

    // Should show up in admin manage events.
    tabMainMenu();
    manageEventsPage();

    final var elementOnManageEvents = findByXpath(
            "//table[@id='commonListTable']/tbody/tr/td/a[contains(text(),'" +
                    uuid + "')]");

    checkPublicPageForEvent(uuid, "2:00 PM");
  }
}
