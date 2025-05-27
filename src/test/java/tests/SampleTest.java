package tests;

import org.testng.Reporter;
import org.testng.annotations.Test;
import testrunner.BaseTest;

public class SampleTest extends BaseTest {

    @Test(description = "Sample test !")
    public void thisIsAnEmptyTest() throws Exception {
        Reporter.log("Starting Sample test");
    }
}