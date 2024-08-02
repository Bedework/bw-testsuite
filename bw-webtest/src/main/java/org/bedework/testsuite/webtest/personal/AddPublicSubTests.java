package org.bedework.testsuite.webtest.personal;

import org.bedework.testsuite.webtest.util.TestBase;
import org.bedework.testsuite.webtest.util.TestDefs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.Select;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author johnsa
 *
 */
@DisplayName("Personal events")
public class AddPublicSubTests extends TestBase {
  private Select select;

  /**
   */
  @AfterEach
  public void tearDownAfterTest() {
    closeDriver();
  }

  /**
   */
  @Test
  @Order(1000)
  @DisplayName("Public events: Add apublic event subscription")
  public void testAddPubSub() {
    final String subFinder = UUID.randomUUID().toString().substring(0, 4);

    login("personal","vbede","bedework"); // log in as a typical user

    // get to the Add Event page
    clickByXpath("//a[@class='calManageLink']");

    assertEquals(getTextByTag("h2"),
                 TestDefs.userManageCalTitle);
    System.out.println("On " + TestDefs.userManageCalTitle + " page.");

    // click the add subscription button
    clickById("addSubButton");

    // click the public listing toggle
    clickById("subSwitchPublic");
    clickByXpath("//ul[@id='publicSubscriptionTree']//li[@class='alias']/a = 'Lectures']");

    // determine if the display name updated
    assertEquals(getTextById("intSubDisplayName"),
                 "Lectures");
    // change to a new display name
    setTextById("intSubDisplayName", "Lec" + subFinder);

    // add the subscription
    clickById("intSubSubmit");

    // *****************************************************************
    // Now test the subscription is there in the normal calendar listing
    assertThat("Subscription not created.",
               getTextByXpath("//td[@id='sidebar']//li[@class='alias']/a = 'Lec'" + subFinder + "]"),
               containsString(subFinder)); // this is dumb ... should just check for existence
    System.out.println("Public subscription added.");

    // THIS IS TEMPORARY.
    // just to pause a moment and watch the work prior to teardown.
    // sleep will be removed.
    try {
      Thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
