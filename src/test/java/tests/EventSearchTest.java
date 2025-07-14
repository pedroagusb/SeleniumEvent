package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import testrunner.BaseTest;

public class EventSearchTest extends BaseTest {
    @Test(description = "Search events and verify event detail page")
    public void searchEventsBasicFlow(){
        HomePage homePage = new HomePage(getDriver());

        String summaryEvent = homePage.verifyHomePageLoaded().
                verifyNavigationElementsPresent().
                searchForEvents("congreso"). //"running" no da resultados
                verifySearchResultsLoaded().
                clickFirstEvent().
                verifyEventDetailPageDisplayed().
                getEventSummary();

        Assert.assertNotNull(summaryEvent);
    }
}
