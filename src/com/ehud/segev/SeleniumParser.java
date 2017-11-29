package com.ehud.segev;
import com.googlecode.jmeter.plugins.webdriver.sampler.WebDriverSampler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SEGEV05 on 11/28/2016.
 */
public class SeleniumParser {

    private List<WebDriverSampler> samplelist;


    public static List<Object> ConvertScript(String pathOrig)
    {

        String endline = "\n";
        String samplercodeHeader = "var pkg = JavaImporter(org.openqa.selenium, org.openqa.selenium.support.ui)" + endline +
                "var wait = new pkg.WebDriverWait(WDS.browser, ${timeout})" + endline;
        List<Object> samplelist= new ArrayList<Object>();


        char[] delimiters = new char[] { ',' };
        String html ="";
        try {
            html = new String(Files.readAllBytes(Paths.get(pathOrig)));
        }
        catch(IOException ex)
        {
            //TODO exception handling
        }


        String script = samplercodeHeader;
        String wdsline = "";
        String patt= "<tr>.*[\n].*<td>(?<action>.*)</td>.*[\n].*<td>(?<path>.*)</td>.*[\n].*<td>(?<value>.*)</td>.*[\n]</tr>";
        Pattern p = Pattern.compile(patt, Pattern.MULTILINE);
        Matcher m = p.matcher(html);
        while (m.find()) {
            String action = m.group("action");
            String path = m.group("path");
            String value = m.group("value");
            wdsline = WebDriverMapJS(action,path,value) + "\n";


            script += wdsline;


            if(action.equalsIgnoreCase("echo") && value.equalsIgnoreCase("end") ) {
                //System.out.println(script);
                samplelist.add(WebDriverJMeterGen.getWebDriverSampler(path,script,"javascript"));
                script = samplercodeHeader;
            }
            if(action.equalsIgnoreCase("pause"))
            {
                samplelist.add(WebDriverJMeterGen.getConstantTimer(path));
            }
        }

        return samplelist;
    }

    private static String WebDriverMapJS(String action,String path,String value)
    {
        String endline = "\n";
        String startSample = "WDS.sampleResult.sampleStart()";
        String endSample = "WDS.sampleResult.sampleEnd()";

        String tab = "\t";
        String result = "";
        switch (action)
        {
            case "echo":
                if(value.equalsIgnoreCase("start")) result += startSample;
                if(value.equalsIgnoreCase("end"))result += endSample;
                break;
            case "pause":
                result="";
                break;
            case "open":

                result += "WDS.browser"+ ".get('" + path + "');";

                break;
            case "clickAndWait":
            case "click":
                result = "WDS.browser" + ".findElement(" + searchContext(path) + ").click()";
                break;
            case "type":
                result = "WDS.browser" + ".findElement(" + searchContext(path) +").clear()";
                result += endline;
                result += "WDS.browser" + ".findElement(" + searchContext(path) + ").sendKeys(['" + value + "'])";

                break;
            case "waitForElementPresent":
                result = "wait.until(pkg.ExpectedConditions.visibilityOfElementLocated(" + searchContext(path) + "))";
                break;
            case "waitForText":
                result = "wait.until(pkg.ExpectedConditions.textToBePresentInElementLocated(" + searchContext(path) + ",'"+value+"'))";
                break;
            default:
                result = "// The command: #####" + action + "##### is not supported in the current version of the exporter";
                break;
        }
        return result;
    }
    private static String searchContext(String locator)
    {
        if (locator.startsWith("xpath"))
        {
            return "pkg.By.xpath('" + locator.substring("xpath=".length()) + "')";
        }
        else if (locator.startsWith("//"))
        {
            return "pkg.By.xpath('" + locator + "')";
        }
        else if (locator.startsWith("css"))
        {
            return "pkg.By.cssSelector('" + locator.substring("css=".length()) + "')";
        }
        else if (locator.startsWith("link"))
        {

            return "pkg.By.linkText('" + locator.substring("link=".length()) + "')";
        }
        else if (locator.startsWith("name"))
        {

            return "pkg.By.name('" + locator.substring("name=".length()) + "')";
        }
        else if (locator.startsWith("tag_name"))
        {

            return "pkg.By.tagName('" + locator.substring("tag_name=".length()) + "')";
        }
        else if (locator.startsWith("partialID"))
        {

            return "pkg.By.xpath('" + "//*[contains(@id,\"" + locator.substring("partialID=".length()) + "\")]" + "')";
        }
        else if (locator.startsWith("id"))
        {

            return "pkg.By.id('" + locator.substring("id=".length()) + "')";
        }
        else
        {
            return "pkg.By.id('" + locator + "')";
        }
    }
}
