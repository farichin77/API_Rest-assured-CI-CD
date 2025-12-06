package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;
    private static final String REPORT_PATH = "reports/AutomationReport.html"; // path tetap

    public static ExtentReports getInstance() {

        if (extent == null) {

            // Spark reporter
            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);

            // Konfigurasi report
            spark.config().setDocumentTitle("Sport Category & Activity API - Automation API Report");
            spark.config().setReportName("API Testing Execution Results");
            spark.config().setTheme(Theme.DARK);
            spark.config().setEncoding("UTF-8");
            spark.config().setTimeStampFormat("EEEE, dd MMM yyyy HH:mm:ss");

            // Setup ExtentReports
            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Tambahan system info
            extent.setSystemInfo("Tester", "Ahmad Farichin");
            extent.setSystemInfo("Environment", "Staging");
            extent.setSystemInfo("Base URL", ConfigReader.getProperty("baseUrl"));
            extent.setSystemInfo("Framework", "Rest Assured + TestNG + Gradle");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("API Version", "v1.2.3");
        }

        return extent;
    }
}
