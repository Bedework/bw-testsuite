/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.submission;

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * User: mike Date: 8/21/24 Time: 17:02
 */
public class SubmissionTestBase extends PublicAdminTestBase {
  /**
   * Login to the admin web client
   */
  public void submissionsLogin(final String userProp,
                               final String passwordProp) {
    setPropertyFrom("user", userProp);
    goToHref("submissionsHome");

    // Log in to the client
    assertThat(getProperty("assertionFooterMustContain"),
               sendLoginGetFooter(userProp,
                                  passwordProp).getText(),
               containsString(getProperty("submissionsFooter")));

    // Output the footer text:
    msg("msgSubmissionLoggedIn");
  }

  public void clickSubmitEvent() {
    clickByName("submit");
  }

  public void clickSubmitEventNoErrors() {
    clickSubmitEvent();

    if (presentById("errors")) {
      fail("Errors on event submission: " +
               textById("errors");
    }
  }
}
