/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.submissions;

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

/**
 * User: mike Date: 8/21/24 Time: 17:02
 */
public class SubmissionTestBase extends PublicAdminTestBase {
  @Override
  public String getClientName() {
    return getProperty("submissionsClientName");
  }

  /**
   * Login to the submissions web client
   */
  public void submissionsLogin(final String userProp,
                               final String passwordProp,
                               final String purposeProp) {
    gotoLogin("submissionsHome", userProp, passwordProp,
              purposeProp);
  }

  public void clickSubmitEventNoErrors() {
    clickByNameNoErrors("submit", "submissionsErrors");
  }
}
