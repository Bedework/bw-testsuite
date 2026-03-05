/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.personal;

import org.bedework.testsuite.webtest.TestBase;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: mike Date: 8/21/24 Time: 17:02
 */
public class PersonalTestBase extends TestBase {
  /**
   * Login to the admin web client
   */
  public void personalLogin(final String userProp,
                         final String passwordProp) {
    setPropertyFrom("user", userProp);
    goToHref("personalHome");

    // Log in to the client and check footer
    assertThat(getProperty("assertionFooterMustContain"),
               sendLoginGetFooter(userProp,
                                  passwordProp).getText(),
               containsString(getProperty("publicFooter")));

    msg("Logged into personal client as user \"%s\"",
        getProperty(userProp));
  }
}
