package org.bedework.testsuite.webtest.personal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author johnsa
 *
 */
@DisplayName("Personal events")
@Order(5000)
public class AddPublicSubTests extends PersonalTestBase {
  /**
   */
  @AfterEach
  public void tearDownAfterTest() {
    closeDriver();
  }

  /**
   */
  @Test
  @DisplayName("Personal events: Add a public event subscription")
  public void testAddPubSub() {
    final String subFinder = "-" +
            UUID.randomUUID().toString().substring(0, 4);

    personalLogin(getProperty(propPersonalUser),
          getProperty(propPersonalPw));

    // get to the manage calendars page
    clickByXpath("//a[@class='calManageLink']");

    assertEquals(getTextByXpath(
                         "//table[@class='withNotices']/tbody/tr/" +
                                 "td[@id='bodyContent']/h2"),
                 getProperty(propUserManageCalTitle));
    info("On " + getProperty(propUserManageCalTitle)
                 + " page.");

    // Ensure no previous subscription
    final var topicalAreaName = getProperty(propSubTopicalArea1Name);
    final var navXpath = "//td[@id='sideBar']/ul[@class='calendarTree']//" +
            "li[@class='alias']/" +
            "a[contains(text(), '" + topicalAreaName + "')]";
    final var subXpath = "//table[@id='calendarTable']//ul[@class='calendarTree']//" +
            "li[@class='alias']/" +
            "a[contains(text(), '" + topicalAreaName + "')]";
    if (presentByXpath(navXpath)) {
      info("Delete already existing subscription");

      clickByXpath("//a[@class='calManageLink']");
      clickByXpath(subXpath);
      clickByXpath("//form[@id='modCalForm']//" +
                           "input[@value='Delete Subscription']");
      clickByXpath("//input[@value='Yes: Delete Calendar!']");
    }

    // click the add subscription button
    clickById("addSubButton");

    // click the public listing toggle
    clickById("subSwitchPublic");

    clickByXpath("//ul[@id='publicSubscriptionTree']//" +
                         "li[@class='alias']/" +
                         "a[text()='" + topicalAreaName + "']");

    // determine if the display name updated
    assertEquals(topicalAreaName,
                 findById("intSubDisplayName").getAttribute("value"));
    // change to a new display name
    setTextById("intSubDisplayName", subFinder);

    // add the subscription
    clickById("intSubSubmit");

    // *****************************************************************
    // Now check the subscription is there in the normal calendar listing
    // and select it
    clickByXpath(navXpath);

    info("Public subscription added.");

    // See if any events we added are visible

    final var event = findByXpath(
            "//table[@id='monthCalendarTable']//" +
                    "a[contains(text(), '" +
                    getProperty(propPublicEventTitlePrefix) +
                    "')]");

    // Now delete the subscription
    clickByXpath("//a[@class='calManageLink']");
    clickByXpath(subXpath);
    clickByXpath("//input[@value='Delete Subscription']");
    clickByXpath("//input[@value='Yes: Delete Calendar!']");

    findByXpath("//ul[@id='messages']/li[contains(text(), 'Deleted')]");
  }
}
