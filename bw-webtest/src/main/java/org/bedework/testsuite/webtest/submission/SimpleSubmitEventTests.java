/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.submission;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * User: mike Date: 8/21/24 Time: 17:23
 */
@DisplayName("Submit events")
@Order(3000)
public class SimpleSubmitEventTests extends SubmissionTestBase {
  /**
   */
  @AfterEach
  public void tearDownAfterTest() {
    closeDriver();
  }

  /**
   */
  @Test
  @DisplayName("Submit events: Submit a public event that is accepted")
  public void testSubmit() {
    setUUID();

    submissionsLogin("submissionsUser", "submissionsPw");

    clickByXpath("submissionsAddEventPath");

    addSummary("submissionsEventTitle");
    addDescription("submissionsEventDescription");
    clickByXpath("submitEventTopicalArea1Xpath");
    setALocation();
    setAContact();
    setTime();

    setTextByNameStr("xBwEmailHolder", "example@example.org");
    clickSubmitEventNoErrors();

    // Should be in list of own pending events
    clickByXpath("submissionsPendingTabPath");
    findByXpath("submissionsEventByUUID");

    logout();

    // ================ Check pending queue and claim it
    adminLogin("approverUser", "approverUserPw",
               "adminLoginCheckPendingPurpose");

    pendingQueuePage();

    clickByXpath("adminEventByUUID");

    // On the editForm page
    clickByXpath("adminClaimSubmittedEventPath");
  }
}
