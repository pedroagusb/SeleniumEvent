package tests.mobile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import testrunner.MobileBaseTest;

public class MobileSettingTest extends MobileBaseTest {
    @Test(description="Launch Setting app and verify it's displayed")
    public void launchSettingTest() {
        log().info("Starting first mobile test!");

        WebDriver driver = getDriver();

        WebElement settingElement = driver.findElement(By.id("android:id/title"));

        Assert.assertNotNull(settingElement, "Setting element should not be null");

        log().info("Mobile test completed successfully!");

    }

}
