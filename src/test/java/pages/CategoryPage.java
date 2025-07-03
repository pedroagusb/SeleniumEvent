package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CategoryPage extends BasePage{
    @FindBy(css = "")
    private WebElement categoryElement;

    public CategoryPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);

        log().info("CategoryPage initialized");
    }

    public CategoryPage verifyCategoryPageDisplayed(){
        verifyPageLoaded();
        waitFor(categoryElement).toBeVisible().withTimeout(15);

        log().info("Category page verified successfully");
        return this;
    }
}
