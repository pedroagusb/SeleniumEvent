package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class EventDetailPage extends BasePage{
    @FindBy(css = "")
    private WebElement eventElement;

    public EventDetailPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver, this);

        log().info("EventDetailPage initialized");
    }

    public EventDetailPage verifyEventDetailPageDisplayed(){
        verifyPageLoaded();
        waitFor(eventElement).toBeVisible().withTimeout(15);

        log().info("Event Detail page verified successfully");
        return this;

    }
}
