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
@Order(10)
@DisplayName("Setup non-approver user for later tests")
public class SetupNonApproverTests extends PublicAdminTestBase {
  /**
   */
  @Test
  @DisplayName("Public events: Set up a non approver and check")
  public void setupUser() {
    // Login as a superuser
    adminLogin(getProperty(propAdminSuperUser),
               getProperty(propAdminSuperUserPw),
               "set up non-approver user");

    msgStr("Visit admin user roles page");
    userRolesPage();

    // In general, we may have to add the user to the page by setting a role
    getAdminPageByHrefSeg(getProperty(propNonApproverPrincipal));
    if (setCheckboxValueIfNeeded("editAuthUserApprover", false)) {
      clickByName("modAuthUser");
    }

    // Ensure in non-approver group.
    msgStr("Visit admin group list page");
    adminGroupListPage();

    final var groupName = getProperty(propNonApproverUserGroupName);
    if (!adminGroupManageMembersPage(groupName)) {
      msgStr("Create admin group");
      // Create group
      clickAdminInputButton("admingroup/initAdd.do");

      setTextByName("updAdminGroup.account", groupName);
      setTextByName("updAdminGroup.description", "For tests");
      setTextByName("adminGroupGroupOwner", "admin");
      clickByNameNoErrors("updateAdminGroup",
                          "Adding non-approver admin group");
    }

    // Ensure in main group -

    msgStr("Add non-approver group to parent group");
    addGroupToGroup(groupName,
                   getProperty(propNonApproverGroupParentName));

    final var nonApproverUser =
            getProperty(propNonApproverUser);

    msgStr("Add non-approver user to non-approver group");
    addUserToGroup(nonApproverUser, groupName);

    msgStr("Logging out from non-approver setup");
    logout();

    msg("Try login as non-approver %s\n",
        nonApproverUser);

    // Log in to admin client and check visibility of elements
    adminLogin(nonApproverUser,
               getProperty(propNonApproverUserPw),
               "check non-approver tabs");

    assertThat("Should see main and approval q tabs only",
               presentByXpathStr(
                       getProperty(propAdminTabHomePath)) &&
                       presentByXpathStr(getProperty(propAdminTabApprovalqPath)) &&
                       !presentByXpathStr(getProperty(propAdminTabSuggestionqPath)) &&
                       !presentByXpathStr(getProperty(propAdminTabPendingqPath)) &&
                       !presentByXpathStr(getProperty(propAdminTabCalendarSuitePath)) &&
                       !presentByXpathStr(getProperty(propAdminTabUsersPath)) &&
                       !presentByXpathStr(getProperty(propAdminTabSystemPath)));

    logout();
  }
}
