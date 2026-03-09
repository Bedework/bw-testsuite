/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.publick.users;

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * User: mike Date: 8/7/24 Time: 12:05
 */
@Order(10)
@DisplayName("Setup non-approver user for later tests")
public class SetupNonApproverTests extends PublicAdminTestBase {
  /**
   */
  @Test
  @DisplayName("Public events: Set up a non approver and check")
  public void setupUser() {
    // Login as a superuser
    adminLogin("adminSuperUser", "adminSuperUserPw",
               "adminLoginSetupNonapproverPurpose");

    msgStr("Visit admin user roles page");
    userRolesPage();

    // In general, we may have to add the user to the page by setting a role
    getAdminPageByHrefSeg(getProperty("nonApproverPrincipal"));
    if (setCheckboxValueIfNeeded("editAuthUserApprover", false)) {
      clickByName("modAuthUser");
    }

    // Ensure in non-approver group.
    msgStr("Visit admin group list page");
    adminGroupListPage();

    if (!adminGroupManageMembersPage("nonApproverUserGroupName")) {
      msgStr("Create admin group");
      // Create group
      clickAdminInputButton("admingroup/initAdd.do");

      setTextByName("updAdminGroup.account",
                    "nonApproverUserGroupName");
      setTextByNameStr("updAdminGroup.description", "For tests");
      setTextByNameStr("adminGroupGroupOwner", "admin");
      clickByNameNoErrors("updateAdminGroup",
                          "Adding non-approver admin group");
    }

    // Ensure in main group -

    msgStr("Add non-approver group to parent group");
    addGroupToGroup("nonApproverUserGroupName",
                    "nonApproverGroupParentName");

    msgStr("Add non-approver user to non-approver group");
    addUserToGroup("nonApproverUser",
                   "nonApproverGroupParentName");

    msgStr("Logging out from non-approver setup");
    logout();

    msg("msgAdminTryNonapproverLogin");

    // Log in to admin client and check visibility of elements
    adminLogin("nonApproverUser", "nonApproverUserPw",
               "adminLoginNonapproverCheckTabsPurpose");

    mustBeTrue("assertionAdminNonappproverVisibleTabs",
               presentByXpath("adminTabHomePath") &&
                   presentByXpath("adminTabAddEventPath") &&
                   presentByXpath("adminTabApprovalqPath") &&
                   !presentByXpath("adminTabSuggestionqPath") &&
                   !presentByXpath("adminTabPendingqPath") &&
                   !presentByXpath("adminTabCalendarSuitePath") &&
                   !presentByXpath("adminTabUsersPath") &&
                   !presentByXpath("adminTabSystemPath"));

    logout();
  }
}
