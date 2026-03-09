/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.personal;

import org.bedework.testsuite.webtest.bedework.BedeworkTestBase;

/**
 * User: mike Date: 8/21/24 Time: 17:02
 */
public class PersonalTestBase extends BedeworkTestBase {
  @Override
  public String getClientName() {
    return getProperty("submissionsClientName");
  }

  /**
   * Login to the personal web client
   */
  public void personalLogin(final String userProp,
                            final String passwordProp,
                            final String purposeProp) {
    gotoLogin("personalHome", userProp, passwordProp,
              purposeProp);
  }
}
