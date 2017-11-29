package com.ehud.segev;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import com.googlecode.jmeter.plugins.webdriver.sampler.WebDriverSampler;
import com.googlecode.jmeter.plugins.webdriver.config.FirefoxDriverConfig;

import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.timers.ConstantTimer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.*;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;
/**
 * Created by SEGEV05 on 11/28/2016.
 */
public class WebDriverJMeterGen {

    public static void CreateJMX(String pathDest,List<Object> samplers){
        LoadJMeterProperties();
        ListedHashTree testPlanTree = new ListedHashTree();
        ThreadGroup threadGroup = getThreadGroup();
        ListedHashTree threadGroupTree = new ListedHashTree();
        threadGroupTree.add(getFirefoxDriverConfig());
        threadGroupTree.add(samplers);
        threadGroupTree.add(getResultCollector());
        testPlanTree.add(threadGroup, threadGroupTree);
        TestPlan testPlan = getTestPlan();
        ListedHashTree hashTree = new ListedHashTree();
        hashTree.add(testPlan, testPlanTree);

        // Engine
        FileOutputStream ostream = null;
        try {
            ostream = new FileOutputStream(pathDest);
            SaveService.saveTree(hashTree, ostream);
        }
        catch(Exception ex)
        {

        }

    }



    private static void LoadJMeterProperties() {
        // jmeter.properties
        JMeterUtils.loadJMeterProperties("C:\\apache-jmeter-3.0\\bin\\jmeter.properties");
        JMeterUtils.setLocale(Locale.ENGLISH);
        JMeterUtils.setJMeterHome("C:\\apache-jmeter-3.0\\");
    }

    private static ResultCollector getResultCollector() {
        // Result collector
        SampleSaveConfiguration saveConfiguration = new SampleSaveConfiguration();
        ResultCollector resultCollector = new ResultCollector(null);
        resultCollector.setEnabled(true);
        resultCollector.setName("Aggregate Report");
        resultCollector.setFilename("");
        resultCollector.setProperty(TestElement.GUI_CLASS, "StatVisualizer");
        resultCollector.setProperty(TestElement.TEST_CLASS, "ResultCollector");
        //SampleSaveConfiguration saveConfiguration = new SampleSaveConfiguration();
        resultCollector.setSaveConfig(saveConfiguration);
        return resultCollector;
    }

    private static ThreadGroup getThreadGroup() {
        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Thread Group");
        threadGroup.setEnabled(true);
        threadGroup.setProperty(TestElement.GUI_CLASS, "ThreadGroupGui");
        threadGroup.setProperty(TestElement.TEST_CLASS, "ThreadGroup");
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        LoopController loopCtrl = new LoopController();
        loopCtrl.setLoops(1);
        loopCtrl.setName("Loop Controller");
        loopCtrl.setEnabled(true);
        threadGroup.setSamplerController(loopCtrl);
        return threadGroup;
    }


    //threadGroupTree.add(samplers);

    private static FirefoxDriverConfig getFirefoxDriverConfig() {
        FirefoxDriverConfig fdc = new FirefoxDriverConfig();
        fdc.setName("jp@gc - Firefox Driver Config");
        fdc.setEnabled(true);
        fdc.setProperty(TestElement.GUI_CLASS, "com.googlecode.jmeter.plugins.webdriver.config.gui.FirefoxDriverConfigGui");
        fdc.setProperty(TestElement.TEST_CLASS, "com.googlecode.jmeter.plugins.webdriver.config.FirefoxDriverConfig");
        fdc.setProperty("WebDriverConfig.proxy_type", "SYSTEM");
        fdc.setProperty("WebDriverConfig.proxy_pac_url", "");
        fdc.setProperty("WebDriverConfig.http_host", "");
        fdc.setProperty("WebDriverConfig.http_port", 8080);
        fdc.setProperty("WebDriverConfig.use_http_for_all_protocols", true);
        fdc.setProperty("WebDriverConfig.https_host", "");
        fdc.setProperty("WebDriverConfig.https_port", 8080);
        fdc.setProperty("WebDriverConfig.ftp_host", "");
        fdc.setProperty("WebDriverConfig.ftp_port", 8080);
        fdc.setProperty("WebDriverConfig.socks_host", "");
        fdc.setProperty("WebDriverConfig.socks_port", 8080);
        fdc.setProperty("WebDriverConfig.no_proxy", "localhost");
        fdc.setProperty("WebDriverConfig.maximize_browser", true);
        fdc.setProperty("WebDriverConfig.reset_per_iteration", false);
        fdc.setProperty("WebDriverConfig.dev_mode", false);
        fdc.setProperty("FirefoxDriverConfig.general.useragent.override.enabled", false);
        fdc.setProperty("FirefoxDriverConfig.network.negotiate-auth.allow-insecure-ntlm-v1", false);
        return fdc;

    }

    public static WebDriverSampler getWebDriverSampler(String label, String script, String type) {
        WebDriverSampler wds = new WebDriverSampler();
        wds.setName(label);
        wds.setEnabled(true);
        wds.setProperty(TestElement.GUI_CLASS, "com.googlecode.jmeter.plugins.webdriver.sampler.gui.WebDriverSamplerGui");
        wds.setProperty(TestElement.TEST_CLASS, "com.googlecode.jmeter.plugins.webdriver.sampler.WebDriverSampler");
        wds.setProperty("WebDriverSampler.script", script);
        wds.setProperty("WebDriverSampler.parameters", "");
        wds.setProperty("WebDriverSampler.language", type);
        return wds;
    }
    public static ConstantTimer getConstantTimer(String delay){
        ConstantTimer ct = new ConstantTimer();
        ct.setName("Constant Timer");
        ct.setEnabled(true);
        ct.setProperty(TestElement.GUI_CLASS, "ConstantTimerGui");
        ct.setProperty(TestElement.TEST_CLASS, "ConstantTimer");
        ct.setProperty("ConstantTimer.delay", delay);
        return ct;
    }


    private static TestPlan getTestPlan() {

    // Test plan
    TestPlan testPlan = new TestPlan("Test Plan");
        testPlan.setEnabled(true);
        testPlan.setProperty(TestElement.GUI_CLASS,"TestPlanGui");
        testPlan.setProperty(TestElement.TEST_CLASS,"TestPlan");
        testPlan.setComment("");
        testPlan.setFunctionalMode(false);
        testPlan.setSerialized(false);
    Arguments tpargs = new Arguments();
        tpargs.setEnabled(true);
        tpargs.setName("User Defined Variables");
        tpargs.addArgument("timeout","20","=");
        testPlan.setTestPlanClasspath("");
        testPlan.setUserDefinedVariables(tpargs);
     return testPlan;
    }

}
