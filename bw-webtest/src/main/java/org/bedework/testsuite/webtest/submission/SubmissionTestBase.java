/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.submission;

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * User: mike Date: 8/21/24 Time: 17:02
 */
public class SubmissionTestBase extends PublicAdminTestBase {
  public static final String propSubmissionsHome =
          "submissionsHome";

  public static final String propSubmissionsUser =
          "submissionsUser";
  public static final String propSubmissionsPw =
          "submissionsPw";

  public static final String propSubmissionsFooter =
          "submissionsFooter";

  public static final String propSubmissionsEventTitlePrefix =
          "submissionsEventTitlePrefix";

  public static final String propSubmitEventTopicalArea1Xpath =
          "submitEventTopicalArea1Xpath";


  /**
   * Login to the admin web client
   */
  public void submissionsLogin(final String user,
                               final String password) {
    final var driver = getWebDriver();

    driver.get(getProperty(propSubmissionsHome));

    // Log in to the client
    final var element = sendLoginGetFooter(user, password);

    assertEquals(element.getText(),
                 getProperty(propSubmissionsFooter));

    // Output the footer text:
    info("Logged into submissions client as user \"" +
                 user + "\"");
  }

  public void clickSubmitEvent() {
    clickByName("submit");
  }

  public void clickSubmitEventNoErrors() {
    clickSubmitEvent();

    if (presentById("errors")) {
      fail("Errors on event submission: " +
                   findById("errors").getText());
    }
  }
}
