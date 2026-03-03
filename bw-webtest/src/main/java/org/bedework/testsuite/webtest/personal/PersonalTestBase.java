/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.personal;

import org.bedework.testsuite.webtest.TestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * User: mike Date: 8/21/24 Time: 17:02
 */
public class PersonalTestBase extends TestBase {
  public static final String propPersonalHome =
          "personalHome";

  public static final String propPersonalUser =
          "personalUser";
  public static final String propPersonalPw =
          "personalPw";

  public static final String propPersonalFooter =
          "personalFooter";

  /**
   * Login to the admin web client
   */
  public void personalLogin(final String userProp,
                         final String passwordProp) {
    final var driver = getWebDriver();

    driver.get(getProperty(propPersonalHome));

    // Log in to the client
    final var element = sendLoginGetFooter(
            getProperty(userProp),
            getProperty(passwordProp));

    assertEquals(element.getText(),
                 getProperty(propPersonalFooter));

    // Output the footer text:
    msg("Logged into personal client as user \"%s\"",
        getProperty(userProp));
  }
}
