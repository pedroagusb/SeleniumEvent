package listeners;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import logging.Logging;

/**
 * Code that is executed before/after every method.
 * 'This listener will only be invoked for configuration (BeforeXXX/AfterXXX)  and test methods' -> From TestNG official documentation
 *
 */
public class TestMethodListener implements IInvokedMethodListener, Logging {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            Reporter.log("******************************************");
            Reporter.log("Beginning Test Execution: " + method.getTestMethod().getMethodName(), true);

            if (null != method.getTestMethod().getDescription() && !method.getTestMethod().getDescription().isEmpty()) {
                Reporter.log(method.getTestMethod().getDescription(), true);
            }
            Reporter.log("******************************************");
        }

    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (null != testResult.getThrowable()) {
            // Exception happened during the Test.
            log().error(testResult.getThrowable().getMessage());
        }
    }

}