/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.publick.users;

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: mike Date: 8/7/24 Time: 12:05
 */
public class SetupNonApproverTests extends PublicAdminTestBase {
  /**
   */
  @Test
  @Order(10)
  @DisplayName("Public events: Set up a non approver and check")
  public void setupUser() {
    // Login as a superuser
    adminLogin(getProperty(propAdminSuperUser),
               getProperty(propAdminSuperUserPw));

    userRolesPage();

    // In general, we may have to add the user to the page by setting a role
    getAdminPageByXref(getProperty(propNonApproverPrincipal));
    if (setCheckboxValueIfNeeded("editAuthUserApprover", false)) {
      clickByName("modAuthUser");
    }

    // Ensure in group.
    adminGroupPage(getProperty(propNonApproverUserGroupName));
    addUserMemberIfNeeded(getProperty(propNonApproverUser));

    logout();

    // Log in to admin client and check visibility of elements
    adminLogin(getProperty(propNonApproverUser),
          getProperty(propNonApproverUserPw));

    assertThat("Should see main and approval q tabs only",
               presentByXpath(getProperty(propAdminTabMainPath)) &&
                       presentByXpath(getProperty(propAdminTabApprovalqPath)) &&
                       !presentByXpath(getProperty(propAdminTabSuggestionqPath)) &&
                       !presentByXpath(getProperty(propAdminTabPendingqPath)) &&
                       !presentByXpath(getProperty(propAdminTabCalendarSuitePath)) &&
                       !presentByXpath(getProperty(propAdminTabUsersPath)) &&
                       !presentByXpath(getProperty(propAdminTabSystemPath)));
  }
}
