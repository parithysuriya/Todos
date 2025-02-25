package com.suriya.todo.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class TestBase {
	
	// WebDriver is isolated for parallel execution using ThreadLocal
    private static ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>();
    public static WebDriver driver;
    public static ExtentReports extent;
    public static ExtentTest extentTest;
    public static Properties prop;

    // Constructor to load properties file
    public TestBase() {
        try {
            prop = new Properties();
            FileInputStream ip = new FileInputStream(
                    System.getProperty("user.dir") + "/src/main/java/com/suriya/todo/config/config.properties");
            prop.load(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialize WebDriver
    public static void initialization() {
       driver = new ChromeDriver();
    	
        threadDriver.set(driver);

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        driver.get(prop.getProperty("url"));
    }

    // Get ThreadLocal WebDriver
    public static WebDriver getDriver() {
        return threadDriver.get();
    }
    
    
    public static void quitDriver() {
        WebDriver driver = threadDriver.get();
        if (driver != null) {
            driver.quit();
            threadDriver.remove(); // Clean up the driver instance
        }
    }

    // Set up Extent Reports
    public static void setExtent() {
        extent = new ExtentReports("./test-output/Reports/Report.html", true);
        extent.addSystemInfo("User Name", "Suriya")
              .addSystemInfo("Environment", "Automation Testing")
              .addSystemInfo("Application", "ToDo")
              .addSystemInfo("Test Scenario", "Functionality Testing");
    }

    // End Extent Reports
    public void endReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    // Capture screenshot
    public String getScreenshot(String screenshotName) throws IOException {
        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String destination = System.getProperty("user.dir") + "/FailedTestsScreenshots/" + screenshotName + dateName + ".png";
        FileUtils.copyFile(source, new File(destination));
        return destination;
    }

    // Log test result to Extent Reports
    public void logTestResult(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            extentTest.log(LogStatus.FAIL, "TEST CASE FAILED: " + result.getName());
            extentTest.log(LogStatus.FAIL, "ERROR: " + result.getThrowable());

            String screenshotPath = getScreenshot(result.getName());
            extentTest.log(LogStatus.FAIL, extentTest.addScreenCapture(screenshotPath));
        } else if (result.getStatus() == ITestResult.SKIP) {
            extentTest.log(LogStatus.SKIP, "TEST CASE SKIPPED: " + result.getName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            extentTest.log(LogStatus.PASS, "TEST CASE PASSED: " + result.getName());
        }

        extent.endTest(extentTest);
    }



    // Send email report
    public void sendEmailReport() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("suriyaparithy@gmail.com", "vgca rwbe emuo uhea");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("suriyaparithy@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("sivasystimanx@gmail.com"));
            message.setSubject("Automation Testing Report");

            Multipart multipart = new MimeMultipart();

            MimeBodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText("This is the TestNG report.");
            multipart.addBodyPart(messageBodyPart1);

            String[] filenames = {
                    System.getProperty("user.dir") + "/test-output/Reports/Report.html",
                    System.getProperty("user.dir") + "/test-output/emailable-report.html"
            };

            for (String filename : filenames) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(new File(filename).getName());
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("===== Email Sent Successfully =====");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}