/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.testsuite.webtest.publick;

import org.bedework.testsuite.webtest.TestBase;

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
  /**
   * Login to the admin web client
   */
  public void adminLogin(final String userProp,
                         final String passwordProp,
                         final String purposeProp) {
    setPropertyFrom("user", userProp);
    setPropertyFrom("loginPurpose", purposeProp);
    msg("msgAdminLoginPurpose");

    goToHref("adminHome");

    // Log in to the client
    assertThat(getProperty("assertionFooterMustContain"),
               sendLoginGetFooter(userProp,
                                  passwordProp).getText(),
               containsString(getProperty("adminFooter")));

    // Output the footer text:
    msg("msgAdminLoggedIn");
  }

  public void startAddEvent(final String summaryProp,
                            final String descriptionProp) {
    // get to the Add Event page
    addEventPage();

    // Page requires summary, description before submit button works

    addSummary(summaryProp);
    addDescription(descriptionProp);
  }

  public void startAddEvent(final String summaryProp,
                            final String descriptionProp,
                            final String cost,
                            final String eventLinkProp,
                            final String imageThumbURL,
                            final String imageURL) {
    // get to the Add Event page
    addEventPage();

    // Page requires summary, description before submit button works

    addSummary(summaryProp);
    addDescription(descriptionProp);
    setDefaultTopicalArea();
    setALocation();
    setAContact();
    setTime();

    // set the cost and link
    setTextByNameStr("eventCost", cost);
    if (eventLinkProp != null) {
      setTextByName("eventLink", eventLinkProp);
    }

    if (imageThumbURL != null) {
      setTextByNameStr("xBwImageHolder", imageURL);
      setTextByNameStr("xBwImageThumbHolder", imageThumbURL);
    }
  }

  public void checkPage() {
    assertThat("Footer must contain correct text: ",
               textById("footer"),
               containsString(getProperty("adminFooter")));
  }

  public void getAdminPageByHref(final String hrefProp) {
    goToHref(hrefProp);
    checkPage();
  }

  public void getAdminPageByXpath(final String xpathProp) {
    clickByXpath(xpathProp);
    checkPage();
  }

  public void getAdminPageByHrefSeg(final String hrefsegProp) {
    findByXpathStr("//a[contains(@href,'" +
                        getProperty(hrefsegProp) +
                        "')]").click();
    checkPage();
  }

  public void getAdminPageById(final String id) {
    clickById(id);
    checkPage();
  }

  public void addEventPage() {
    getAdminPageById("addEventLink");
    assertEquals(textByTag("h2"),
                 getProperty("adminEventInfoTitle"));
    msg("msgAdminOnEventInfoPage");
  }

  public void eventsListPage() {
    getAdminPageById("manageEventsLink");

    assertEquals(textByTag("h2"),
                 getProperty("adminManageEventsTitle"));
    msg("msgAdminOnManageEventsPage");
  }

  public void adminGroupListPage() {
    getAdminPageByXpath("adminTabUsersPath");
    getAdminPageByXpath("adminGroupManagePath");
  }

  public void homePage() {
    getAdminPageByXpath("adminTabHomePath");
  }

  public void manageEventsPage() {
    getAdminPageByXpath("adminTabManageEventsPath");

    assertEquals(textByTag("h2"),
                 getProperty("adminManageEventsTitle"));
    msg("msgAdminOnManageEventsPage");
  }

  public void approvalQueuePage() {
    getAdminPageByXpath("adminTabApprovalqPath");
  }

  public void pendingQueuePage() {
    getAdminPageByXpath("adminTabPendingqPath");
  }

  /** Sets the "groupName" proeprty and switches to manage that
   * group
   *
   * @param nameProp property containing name
   * @return true for no error
   */
  public boolean adminGroupManageMembersPage(
      final String nameProp) {
    try {
      setPropertyFrom("groupName", nameProp);
      getAdminPageByXpath("adminGroupManageMembersPagePath");
      return true;
    } catch (final NoSuchElementException ignored) {
      return false;
    }
  }

  public void addUserToGroup(final String memberProp,
                             final String groupNameProp) {
    adminGroupListPage();
    setPropertyFrom("groupName", groupNameProp);
    setPropertyFrom("member", memberProp);

    // Assuming group exists for the moment
    assertThat(getProperty("assertionAdminGroupexists"),
               adminGroupManageMembersPage(groupNameProp));
    addUserMemberIfNeeded(memberProp);
  }

  // Positioned by call to adminGroupPage()
  public void addUserMemberIfNeeded(final String memberProp) {
    if (!tableHasElementText("memberAccountList", memberProp)) {
      setTextById("agMember", memberProp);
      clickById("agUser");
      clickByName("addGroupMember");
    }
  }

  public void addGroupToGroup(final String memberProp,
                             final String groupNameProp) {
    setPropertyFrom("groupName", groupNameProp);
    setPropertyFrom("member", memberProp);
    msgStr("Revisit admin group list page");
    adminGroupListPage();

    // Assuming group exists for the moment
    assertThat(getProperty("assertionAdminGroupexists"),
               adminGroupManageMembersPage(groupNameProp));
    addGroupMemberIfNeeded(memberProp);
  }

  // Positioned by call to adminGroupPage()
  public void addGroupMemberIfNeeded(final String memberProp) {
    if (!tableHasElementText("memberAccountList", memberProp)) {
      setTextById("agMember", memberProp);
      clickById("agGroup");
      clickByName("addGroupMember");
    }
  }

  public void userRolesPage() {
    // get to the user roles page
    getAdminPageByXpath("adminTabUsersPath");
    getAdminPageByHrefSeg("authuser/initUpdate.do");
  }

  public void addSummary(final String valProp) {
    setTextByName("summary", valProp);
  }

  public void addDescription(final String valProp) {
    setTextByName("description", valProp);
  }

  public void setTopicalArea(final String pathPropName) {
    clickByXpath(pathPropName);
  }

  public void setDefaultTopicalArea() {
    clickByXpath("adminEventTopicalArea1Xpath");
  }

  public void setALocation() {
    // select a location
    /* Only have search option
    clickById("bwLocationAllButton");
    select = new Select(findById("bwAllLocationList"));
    select.selectByIndex(1);
     */

    // Set text in search box
    setTextByIdStr("bwLocationSearch", "loc");
    clickByXpath("adminSelectInLocSearch");
  }

  public void setAContact() {
    // select a contact
    /* Only have search option
    clickById("bwContactAllButton");
    select = new Select(findById("bwAllContactList"));
    select.selectByIndex(1);
     */

    // Set text in search box
    setTextByIdStr("bwContactSearch", "co");
    clickByXpath("adminSelectInContactSearch");
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
    findByXpathStr("//input[@type='button' and " +
                        "contains(@onclick, '" +
                        locationSeg + "')]").click();
  }

  public void clickAddEventNoErrors() {
    clickAddEvent();

    // Expect a messages id with "added" in text
    assertThat("Must have 'added' message",
               textById("messages"),
               containsString("added"));
    if (presentById("errors")) {
      fail("Errors on event add: " +
                   textById("errors"));
    }
  }
}
