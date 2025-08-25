package pages.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class EventDetailPage extends BasePage{
    @FindBy(css = "h1.event-title")
    private WebElement eventTitleElement;

    @FindBy(css = "[data-spec='eds-modal-body']")
    private WebElement ticketsModal;

    @FindBy(css = "[data-testid='checkout-link']")
    private WebElement priceButton;

    @FindBy(css = "[data-testid='ticket-price__price']")
    private WebElement priceValueTextModal;

    @FindBy(css = "[data-testid='summary']")
    private WebElement summaryEvent;

    public EventDetailPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver, this);

        log().info("EventDetailPage initialized");
    }

    public EventDetailPage verifyEventDetailPageDisplayed(){
        verifyPageLoaded();
        waitFor(eventTitleElement).toBeVisible().withTimeout(10);

        log().info("Event Detail page verified successfully");
        return this;
    }

    /**
     * Gets the text from the summary element
     *
     * @return String current event's summary
     */
    public String getEventSummary(){
        log().info("Getting the event's summary");

        waitFor(summaryEvent).toBeVisible().withTimeout(5);
        return summaryEvent.getText();
    }

    /**
     * Verifies if the event has any ticket available
     *
     * @return this same instance to allow method chaining
     */
    public boolean isTicketAvailable(){
        log().info("Checking if there is any ticket available");

        waitFor(priceButton).toBeClickable().withTimeout(5);
        return priceButton.isDisplayed();
    }

    /**
     * Gets the value of the current event's price
     *
     * @return String current event's price
     */
    public String getEventPrice(){

        try {
            if(isTicketAvailable()){
                log().debug("Ticket available. Clicking it");
                priceButton.click();
            } else{
                log().debug("No tickets available for this event");
                return "No ticket available to get the price";
            }

            log().info("Checking if modal is opened");
            waitFor(ticketsModal).toBeVisible().withTimeout(10);

            waitFor(priceValueTextModal).toBeVisible().withTimeout(5);
            log().info("Event price found");
            return priceValueTextModal.getText();
        } catch (Exception e) {
            log().debug("No ticket available for this event. Error: {}", e.getMessage());
            return "Error trying to get the event price";
        }
    }
}
