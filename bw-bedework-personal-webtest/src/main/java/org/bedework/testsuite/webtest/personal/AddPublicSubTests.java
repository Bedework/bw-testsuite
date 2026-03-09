package org.bedework.testsuite.webtest.personal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

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
    setUUID();
    final var subFinder = "-" +
        getProperty("uuid").substring(0, 4);

    personalLogin("personalUser", "personalPw",
                  "personalAddSubscriptionPurpose");

    // get to the manage calendars page
    clickByXpath("personalManageCalendarsLink");

    Assertions.assertEquals(
        textByXpath(
                    "personalManageCalendarsValidationPath"),
        getProperty("personalManageCalendarsValidationValue"));
    msg("msgPersonalOnManageCalPage");

    // Ensure no previous subscription
    if (presentByXpath("personalNavXpathSubmissionConcerts")) {
      msg("msgDeleteExistingSubscription");

      deleteSubscription();
    }

    msg("msgAboutToSubscribeToConcerts");

    // click the add subscription button
    clickById("addSubButton");

    // click the public listing toggle
    clickById("subSwitchPublic");

    clickByXpath("personalManageCalendarsSelectSubscriptionConcerts");

    // determine if the display name updated
    Assertions.assertEquals(getProperty("personalSubmissionConcertsName"),
                            findById("intSubDisplayName").getDomAttribute("value"));
    // change to a new display name
    setTextByIdStr("intSubDisplayName", subFinder);

    // add the subscription
    clickById("intSubSubmit");

    msg("msgPublicSubscriptionAdded");

    // **************************************************
    // Now check the subscription is there in the normal calendar listing
    // and select it
    clickByXpath("personalNavXpathSubmissionConcerts");

    msg("msgPublicSubscriptionClicked");

    // See if any events we added are visible

    findByXpath("personalSubFindPublicEvent");

    // Now delete the subscription
    deleteSubscription();
  }

  private void deleteSubscription() {
    clickByXpath("personalManageCalendarsLink");
    clickByXpath("personalXpathManageSubmissionConcerts");
    clickByXpath("personalDeleteSubscriptionPath");
    clickByXpath("personalDeleteSubscriptionConfirmPath");

    mustContain("msgMustHaveDeleted",
                textById("messages"),
                "personalDeletedSubCalText");
  }
}
