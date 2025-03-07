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
               getProperty(propAdminSuperUserPw));

    userRolesPage();

    // In general, we may have to add the user to the page by setting a role
    getAdminPageByHrefSeg(getProperty(propNonApproverPrincipal));
    if (setCheckboxValueIfNeeded("editAuthUserApprover", false)) {
      clickByName("modAuthUser");
    }

    // Ensure in non-approver group.
    adminGroupListPage();

    final var groupName = getProperty(propNonApproverUserGroupName);
    if (!adminGroupManageMembersPage(groupName)) {
      // Create group
      clickAdminInputButton("admingroup/initAdd.do");

      setTextByName("updAdminGroup.account", groupName);
      setTextByName("updAdminGroup.description", "For tests");
      setTextByName("adminGroupGroupOwner", "admin");
      clickByNameNoErrors("updateAdminGroup",
                          "Adding non-approver admin group");
    }

    // Ensure in main group -
    adminGroupListPage();

    final var parentGroupName = getProperty(propNonApproverGroupParentName);

    assertThat("Admin group " + parentGroupName + " must exist",
               adminGroupManageMembersPage(parentGroupName));
    // Need to flag as group
    setRadioByIdIfNeeded("agGroup", true);
    addUserMemberIfNeeded(groupName);

    addUserToGroup(groupName,
                   getProperty(propApproverUserGroupName));

    logout();

    final var nonApproverUser = getProperty(propNonApproverUser);
    System.out.printf("Try login as non-approver %s\n",
                      nonApproverUser);

    // Log in to admin client and check visibility of elements
    adminLogin(nonApproverUser,
               getProperty(propNonApproverUserPw));

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
