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
import org.openqa.selenium.By;

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
    setUUID();

    adminLogin("nonApproverUser", "nonApproverUserPw",
               "adminLoginNonapproverAddEventPurpose");

    startAddEvent("publicEventTitle",
                  "publicEventApprovalDescription",
                  "FREE",
                  "adminEventLink",
                  null, null);
    clickAddEventNoErrors();

    logout();

    // The event should show up in the approval queue for the approver
    adminLogin("approverUser2Groups", "approverUser2GroupsPw",
               "adminLoginApproverApproveEventPurpose");

    // Select group

    getAdminPageByHrefSeg(getProperty("nonApproverUserGroupName"));

    approvalQueuePage();

    // Need to get event uid from link showing the summary

    final var summaryLink = findByXpath("adminEventByUUID");

    final var href = summaryLink.getDomAttribute("href");
    Assertions.assertNotNull(href);
    final var guidStart = href.indexOf("guid=") + 5;
    final var guid = href.substring(guidStart,
                                    href.indexOf('&', guidStart + 1));

    parentOf(parentOf(summaryLink)).findElement(By.xpath(getProperty("adminApproveButtonInRow"))).click();

    // Confirm approval on next page
    clickByName("approveEvent");

    // Should show up in admin manage events.
    homePage();
    manageEventsPage();

    final var elementOnManageEvents =
        findByXpath("adminEventByUUID");

    checkPublicPageForEvent("2:00 PM");
  }
}
