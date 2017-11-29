package com.ehud.segev;

public class ConverterMain {

    public static void main(String[] args) {
	    // write your code here
//"D:\\JMeterScripts\\baseSelIDEtest - Copy.html"
        //"D:\\JMeterScripts\\mytest.jmx"
        String pathSrc ="D:\\JMeterScripts\\baseSelIDEtest - Copy.html";
        String pathDest = "D:\\JMeterScripts\\mytest.jmx";

        if (args.length > 0) {
            try {
                if (args.length > 0) {
                    pathSrc = args[0];
                }
                if (args.length > 1) {
                    pathDest = args[1];
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
        WebDriverJMeterGen.CreateJMX(pathDest,SeleniumParser.ConvertScript(pathSrc));

    }
}
