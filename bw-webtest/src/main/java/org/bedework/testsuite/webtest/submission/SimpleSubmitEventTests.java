/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.submission;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
    final String uuid = UUID.randomUUID().toString();
    final String eventTitle =
            getProperty(propSubmissionsEventTitlePrefix) +
                    " - " + uuid;

    submissionsLogin(getProperty(propSubmissionsUser),
                     getProperty(propSubmissionsPw));

    clickByXpath("//ul[@id='menuTabs']/li/" +
                         "a[contains(@href, 'initEvent.do')]");

    addSummary(eventTitle);
    addDescription("bedework submission test description");
    findByXpath(getProperty(propSubmitEventTopicalArea1Xpath)).click();
    setALocation();
    setAContact();
    setTime();

    setTextByName("xBwEmailHolder", "example@example.org");
    clickSubmitEventNoErrors();

    // Should be in list of own pending events

    clickByXpath("//ul[@id='menuTabs']/li/" +
                         "a[contains(@href, 'initPendingEvents.do')]");

    findByXpath("//table[@id='commonListTable']//" +
                        "a[contains(text(), '" + uuid + "')]");

    logout();

    // ================ Check pending queue and claim it
    adminLogin(getProperty(propApproverUser),
               getProperty(propApproverUserPw),
               "check pending queue");

    tabPendingQueue();

    clickByXpath("//table[@id='commonListTable']//" +
                         "a[contains(text(), '" + uuid + "')]");
    clickByXpath("//form[@id='bwEventForm']//" +
                         "input[@value='Claim Event']");
  }
}
