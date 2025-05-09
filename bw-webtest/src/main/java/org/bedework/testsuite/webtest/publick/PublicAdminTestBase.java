/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.publick;

import org.bedework.testsuite.webtest.util.TestBase;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * User: mike Date: 8/6/24 Time: 23:40
 */
public class PublicAdminTestBase extends TestBase {
  public static final String propAdminHome = "adminHome";

  public static final String propAdminFooter = "adminFooter";

  public static final String propAdminSuperUser = "adminSuperUser";
  public static final String propAdminSuperUserPw = "adminSuperUserPw";

  // Admin - approver user
  public static final String propApproverUser =
          "approverUser";
  public static final String propApproverPrincipal =
          "approverPrincipal";
  public static final String propApproverUserPw =
          "approverUserPw";
  public static final String propApproverUserGroupName =
          "approverUserGroupName";

  // Admin - non-approver user
  public static final String propNonApproverUser =
          "nonApproverUser";
  public static final String propNonApproverPrincipal =
          "nonApproverPrincipal";
  public static final String propNonApproverUserPw =
          "nonApproverUserPw";
  public static final String propNonApproverUserGroupName =
          "nonApproverUserGroupName";
  public static final String propNonApproverGroupParentName =
          "nonApproverGroupParentName";

  // Admin - approver user
  public static final String propApproverUser2Groups =
          "approverUser2Groups";
  public static final String propApproverPrincipal2Groups =
          "approverPrincipal2Groups";
  public static final String propApproverUser2GroupsPw =
          "approverUser2GroupsPw";

  public static final String propAdminEventInfoTitle =
          "adminEventInfoTitle";
  public static final String propAdminManageEventsTitle =
          "adminManageEventsTitle";
  public static final String propAdminErrorNoTopicalArea =
          "adminErrorUpdateEventTopicalArea";
  public static final String propAdminErrorNoTitle =
          "adminErrorUpdateEventNoTitle";
  public static final String propAdminErrorNoDescription =
          "adminErrorUpdateEventNoDescription";
  public static final String propAdminErrorNoLocation =
          "adminErrorUpdateEventNoLocation";
  public static final String propAdminErrorNoContact =
          "adminErrorUpdateEventNoContact";

  // Values for added event
  public static final String propAdminEventTopicalArea1 =
          "adminEventTopicalArea1";
  public static final String propAdminEventTopicalArea1Xpath =
          "adminEventTopicalArea1Xpath";
  public static final String propAdminEventLink =
          "adminEventLink";

  // Tab paths
  public static final String propAdminTabMainPath =
          "adminTabMainPath";
  public static final String propAdminTabApprovalqPath =
          "adminTabApprovalqPath";
  public static final String propAdminTabSuggestionqPath =
          "adminTabSuggestionqPath";
  public static final String propAdminTabPendingqPath =
          "adminTabPendingqPath";
  public static final String propAdminTabCalendarSuitePath =
          "adminTabCalendarSuitePath";
  public static final String propAdminTabUsersPath =
          "adminTabUsersPath";
  public static final String propAdminTabSystemPath =
          "adminTabSystemPath";

  /**
   * Login to the admin web client
   */
  public void adminLogin(final String user,
                         final String password,
                         final String purpose) {
    msg("About to log in to admin client for %s\n", purpose);
    final var driver = getWebDriver();

    driver.get(getProperty(propAdminHome));

    // Log in to the client
    final var element = sendLoginGetFooter(user, password);

    assertEquals(element.getText(),
                 getProperty(propAdminFooter));

    // Output the footer text:
    msg("Logged into admin client as user \"" +
                 user + "\"");
  }

  public void startAddEvent(final String summary,
                            final String description) {
    // get to the Add Event page
    addEventPage();

    // Page requires summary, description before submit button works

    addSummary(summary);
    addDescription(description);
  }

  public void startAddEvent(final String summary,
                            final String description,
                            final String cost,
                            final String eventLink,
                            final String imageThumbURL,
                            final String imageURL) {
    // get to the Add Event page
    addEventPage();

    // Page requires summary, description before submit button works

    addSummary(summary);
    addDescription(description);
    setDefaultTopicalArea();
    setALocation();
    setAContact();
    setTime();

    // set the cost and link
    setTextByName("eventCost", cost);
    if (eventLink != null) {
      setTextByName("eventLink", eventLink);
    }

    if (imageThumbURL != null) {
      setTextByName("xBwImageHolder", imageURL);
      setTextByName("xBwImageThumbHolder", imageThumbURL);
    }
  }

  public void checkPage() {
    final var e = findById("footer");
    assertThat("Footer must contain correct text: ",
               e.getText(),
               containsString(getProperty(propAdminFooter)));
  }

  public void getAdminPageByHref(final String href) {
    getWebDriver().get(href);
    checkPage();
  }

  public void getAdminPageByXpath(final String xpath) {
    findByXpath(xpath).click();
    checkPage();
  }

  public void getAdminPageByHrefSeg(final String hrefseg) {
    findByXpath("//a[contains(@href,'" +
                        hrefseg +
                        "')]").click();
    checkPage();
  }

  public void getAdminPageById(final String id) {
    findById(id).click();
    checkPage();
  }

  public void addEventPage() {
    getAdminPageByHrefSeg("initAddEvent.do");

    assertEquals(getTextByTag("h2"),
                 getProperty(propAdminEventInfoTitle));
    msg("On " + getProperty(propAdminEventInfoTitle) + " page.");
  }

  public void eventsListPage() {
    getAdminPageById("manageEventsLink");

    assertEquals(getTextByTag("h2"),
                 getProperty(propAdminManageEventsTitle));
    msg("On " + getProperty(propAdminManageEventsTitle) +
                " page.");
  }

  public void adminGroupListPage() {
    getAdminPageByXpath(getProperty(propAdminTabUsersPath));
    getAdminPageByHrefSeg("admingroup/initUpdate.do");
  }

  public void tabMainMenu() {
    getAdminPageByXpath(getProperty(propAdminTabMainPath));
  }

  public void tabApproverQueue() {
    getAdminPageByXpath(getProperty(propAdminTabApprovalqPath));
  }

  public void tabPendingQueue() {
    getAdminPageByXpath(getProperty(propAdminTabPendingqPath));
  }

  public boolean adminGroupManageMembersPage(final String name) {
    try {
      getAdminPageByHrefSeg("admingroup/fetchForUpdateMembers.do?" +
                                    "b=de&adminGroupName=" +
                                    name);
      return true;
    } catch (final NoSuchElementException ignored)
    {
      return false;
    }
  }

  public void addUserToGroup(final String member,
                             final String groupName) {
    adminGroupListPage();

    // Assuming group exists for the moment
    assertThat("Admin group " + groupName + " must exist",
               adminGroupManageMembersPage(groupName));
    addUserMemberIfNeeded(member);
  }

  // Positioned by call to adminGroupPage()
  public void addUserMemberIfNeeded(final String member) {
    if (!tableHasElementText("memberAccountList", member)) {
      setTextById("agMember", member);
      clickById("agUser");
      clickByName("addGroupMember");
    }
  }

  public void addGroupToGroup(final String member,
                             final String groupName) {
    msg("Revisit admin group list page");
    adminGroupListPage();

    // Assuming group exists for the moment
    assertThat("Admin group " + groupName + " must exist",
               adminGroupManageMembersPage(groupName));
    addGroupMemberIfNeeded(member);
  }

  // Positioned by call to adminGroupPage()
  public void addGroupMemberIfNeeded(final String member) {
    if (!tableHasElementText("memberAccountList", member)) {
      setTextById("agMember", member);
      clickById("agGroup");
      clickByName("addGroupMember");
    }
  }

  public void manageEventsPage() {
    getAdminPageByHrefSeg("initUpdateEvent.do");
  }

  public void userRolesPage() {
    // get to the user roles page
    getAdminPageByXpath(getProperty(propAdminTabUsersPath));
    getAdminPageByHrefSeg("authuser/initUpdate.do");
  }

  public void addSummary(final String val) {
    setTextByName("summary", val);
  }

  public void addDescription(final String val) {
    setTextByName("description", val);
  }

  public void setTopicalArea(final String pathPropName) {
    findByXpath(getProperty(pathPropName)).click();
  }

  public void setDefaultTopicalArea() {
    findByXpath(getProperty(propAdminEventTopicalArea1Xpath)).click();
  }

  public void setALocation() {
    // select a location
    /* Only have search option
    findById("bwLocationAllButton").click();
    select = new Select(findById("bwAllLocationList"));
    select.selectByIndex(1);
     */

    // Set text in search box
    setTextById("bwLocationSearch", "loc");
    final var selectedLoc = findByXpath(
            "//div[@id=\"bwLocationSearchResults\"]/ul/li[1]");
    selectedLoc.click();
  }

  public void setAContact() {
    // select a contact
    /* Only have search option
    findById("bwContactAllButton").click();
    select = new Select(findById("bwAllContactList"));
    select.selectByIndex(1);
     */

    // Set text in search box
    setTextById("bwContactSearch", "co");
    final var selectedContact = findByXpath(
            "//div[@id=\"bwContactSearchResults\"]/ul/li[1]");
    selectedContact.click();
  }

  /**
   * Sets time to 2pm (1400)
   */
  public void setTime() {
    // set the time to 2pm or 14:00; the date should be "today" by default
    var select = new Select(findById("eventStartDateHour"));
    if (presentById("eventStartDateAmpm")) {
      // The American way
      select.selectByIndex(1);
      select = new Select(findById("eventStartDateAmpm"));
      select.selectByIndex(1); //pm
    } else {
      // Everyone else?
      select.selectByIndex(14);
    }
    select = new Select(findById("eventStartDateMinute"));
    select.selectByIndex(0);
  }

  public void clickAddEvent() {
    clickByName("addEvent");
  }

  public void clickAdminInputButton(final String locationSeg) {
    findByXpath("//input[@type='button' and " +
                        "contains(@onclick, '" +
                        locationSeg + "')]").click();
  }

  public void clickAdminButton(final String locationSeg,
                               final String... value) {
    final String valuePart;
    if (value.length == 0) {
      valuePart = "";
    } else {
      valuePart = "and contains(text(), '" + value[0] + "')";
    }
    findByXpath("//button[contains(@onclick, '" +
                        locationSeg + "')" +
                        valuePart + "]").click();
  }

  public void clickAddEventNoErrors() {
    clickAddEvent();

    // Expect a messages id with "added" in text
    assertThat("Must have 'added' message",
               findById("messages").getText(),
               containsString("added"));
    if (presentById("errors")) {
      fail("Errors on event add: " +
                   findById("errors").getText());
    }
  }
}
