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

    msg("Visit admin user roles page");
    userRolesPage();

    // In general, we may have to add the user to the page by setting a role
    getAdminPageByHrefSeg(getProperty(propNonApproverPrincipal));
    if (setCheckboxValueIfNeeded("editAuthUserApprover", false)) {
      clickByName("modAuthUser");
    }

    // Ensure in non-approver group.
    msg("Visit admin group list page");
    adminGroupListPage();

    final var groupName = getProperty(propNonApproverUserGroupName);
    if (!adminGroupManageMembersPage(groupName)) {
      msg("Create admin group");
      // Create group
      clickAdminInputButton("admingroup/initAdd.do");

      setTextByName("updAdminGroup.account", groupName);
      setTextByName("updAdminGroup.description", "For tests");
      setTextByName("adminGroupGroupOwner", "admin");
      clickByNameNoErrors("updateAdminGroup",
                          "Adding non-approver admin group");
    }

    // Ensure in main group -
    msg("Revisit admin group list page");
    adminGroupListPage();

    final var parentGroupName = getProperty(propNonApproverGroupParentName);

    msg("Ensure parent page exists");
    assertThat("Admin group " + parentGroupName + " must exist",
               adminGroupManageMembersPage(parentGroupName));
    // Need to flag as group
    setRadioByIdIfNeeded("agGroup", true);
    addUserMemberIfNeeded(groupName);

    msg("Add non-approver group to parent group");
    addUserToGroup(groupName,
                   getProperty(propApproverUserGroupName));

    msg("Logging out from non-approver setup");
    logout();

    final var nonApproverUser = getProperty(propNonApproverUser);
    msg("Try login as non-approver %s\n",
        nonApproverUser);

    // Log in to admin client and check visibility of elements
    adminLogin(nonApproverUser,
               getProperty(propNonApproverUserPw),
               "check non-approver tabs");

    assertThat("Should see main and approval q tabs only",
               presentByXpath(getProperty(propAdminTabMainPath)) &&
                       presentByXpath(getProperty(propAdminTabApprovalqPath)) &&
                       !presentByXpath(getProperty(propAdminTabSuggestionqPath)) &&
                       !presentByXpath(getProperty(propAdminTabPendingqPath)) &&
                       !presentByXpath(getProperty(propAdminTabCalendarSuitePath)) &&
                       !presentByXpath(getProperty(propAdminTabUsersPath)) &&
                       !presentByXpath(getProperty(propAdminTabSystemPath)));

    logout();
  }
}
