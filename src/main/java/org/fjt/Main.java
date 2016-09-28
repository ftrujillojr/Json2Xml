package org.fjt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/*
 * SVN information
 * $Revision: 3687 $
 * $Author: ftrujillo $
 * $Date: 2015-09-02 11:55:43 -0600 (Wed, 02 Sep 2015) $
 * $HeadURL: http://svn/NSG/comp_ssd/software/trunk/NetBeansProjects/Examples/simple-java-app/src/main/resources/archetype-resources/src/main/java/Main.java $
 *
 */
public class Main {

    public Main() {
    }

    private static List<String> subExps = new ArrayList<>();
    private static Options options = new Options();

    /**
     * Need help on Apache Cli
     *
     * http://www.javaworld.com/article/2072482/command-line-parsing-with-apache-commons-cli.html
     *
     * https://commons.apache.org/proper/commons-cli/javadocs/api-release/org/apache/commons/cli/package-tree.html
     */
    public static void main(String[] args) {

        try {
            Main.loadUpOptions(); //  This method will load up private static Options options or throw MainException on duplicate options.

            // Next 3 lines are 100%  Apache CLI
            CommandLineParser parser = new DefaultParser();
            boolean allowNonOptions = false;  // set to false to throw exception on non arg parsed.  
            CommandLine cmd = parser.parse(options, args, allowNonOptions);

            // This is MY post processing to get 'cmd' into something with defaults and honoring the type()'s by type conversion.
            Map<String, Object> cliMap = Main.generateCliMap(cmd);

            if ((Integer) cliMap.get("debug") >= 3) {
                Main.displayCliMap(cliMap);
            }

            // If you threw the --help or -h option.
            if ((Boolean) cliMap.get("help")) {
                displayUsageAndExit(-1);
            }

            String jsonStr = "{\n"
                    + "  \"status\": 200,\n"
                    + "  \"data\": [\n"
                    + "    {\n"
                    + "      \"email\": \"ftrujillojr@gmail.com\",\n"
                    + "      \"firstName\": \"Francis\",\n"
                    + "      \"lastName\": \"Trujillo\",\n"
                    + "      \"phone\": \"208-555-5555\",\n"
                    + "      \"age\": null\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"email\": \"n/a\",\n"
                    + "      \"firstName\": \"Benny\",\n"
                    + "      \"lastName\": \"Trujillo\",\n"
                    + "      \"phone\": \"208-123-4567\",\n"
                    + "      \"age\": 20\n"
                    + "    }\n"
                    + "  ],\n"
                    + "  \"method\": \"GET\",\n"
                    + "  \"uri\": \"/StrutsWebApp/emailData/\",\n"
                    + "  \"simple\": { \"hello\": \"world\"},\n"
                    + "  \"ids\": [116, 943, 234, 38793]"
                    + "}\n";

            Json2Xml json2xml;
            try {
                json2xml = new Json2Xml();
                
                String xmlStr = json2xml.toXML(jsonStr);
                                
                System.out.println("\n" + xmlStr);
                
                
                
            } catch (Json2XmlException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.exit(0);

        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            Main.displayUsageAndExit(10);
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
            Main.displayUsageAndExit(11);
        } catch (MainException ex) {
            System.out.println(ex.getMessage());
            System.exit(12);
        }
    }

    /**
     * This is where you will define your options.
     *
     * Please read =>
     * https://commons.apache.org/proper/commons-cli/javadocs/api-release/org/apache/commons/cli/Option.Builder.html
     *
     * @throws MainException if options fail Main.verifyAndAddOptions()
     */
    public static void loadUpOptions() throws MainException {
        List<Option> optionList = new ArrayList<>();

        optionList.add(Option.builder()
                .longOpt("debug")
                .required(false)
                .hasArg(true)
                .argName("LEVEL")
                .desc("Debug  1,2,3,...")
                .type(Integer.class)
                .build());

        optionList.add(Option.builder("h")
                .longOpt("help")
                .required(false)
                .hasArg(false)
                .desc("This help message")
                .type(Boolean.class)
                .build());

        Main.verifyAndAddOptions(optionList);
    }

    /**
     * Display formatted cliMap when --debug > 3 or --help
     *
     * @param cliMap Map&lt;String, Object&gt;
     *
     */
    public static void displayCliMap(Map<String, Object> cliMap) {
        Iterator<String> itr = cliMap.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String val = cliMap.get(key).toString();
            System.out.println(String.format("%20s: %s", key, val));
        }
        System.out.println("");
    }

    /**
     * This method is 99% Apache Cli, but I added a System.exit(status) for
     * Main.main() to return exit status.
     *
     * @param status 0,1,2,3,4,5,...
     */
    public static void displayUsageAndExit(int status) {
        HelpFormatter formatter = new HelpFormatter();
        System.out.println("");
        String header = "\nThis app will do this ...\n\n";
        String footer = "\nPlease report any issues to *******@micron.com\n\nSTATUS: " + status + "\n";
        int width = 132;
        formatter.printHelp(width, "myProgramName", header, options, footer, true);
        System.exit(status);
    }

    /**
     * Generate a Map of Objects based on CLI CommandLine parsed args based on
     * original Options. *** Also uses Main.options are original option list.
     *
     * @param cmd CLI CommandLine object
     * @return Map&lt;String, Objects&gt; with each value converted into it's
     * type() as defined in Option.
     * @throws NumberFormatException
     */
    public static Map<String, Object> generateCliMap(CommandLine cmd) throws NumberFormatException {
        Map<String, Object> cliMap = new TreeMap<>(); // Result Map
        Collection<Option> collection = Main.options.getOptions();  // Original Options
        Iterator<Option> itr = collection.iterator();

        while (itr.hasNext()) {
            Option l_option = itr.next();
            String key = (l_option.hasLongOpt()) ? l_option.getLongOpt() : l_option.getOpt();
            String type = l_option.getType().toString();
            String val = cmd.getOptionValue(key);

            switch (type) {
                case "class java.lang.Boolean":
                    val = (cmd.hasOption(key)) ? "true" : "false";
                    cliMap.put(key, Boolean.parseBoolean(val));
                    break;
                case "class java.lang.Integer":
                    if (cmd.hasOption(key) == false) {
                        val = "0";
                    }
                    try {
                        cliMap.put(key, Integer.parseInt(val));
                    } catch (NumberFormatException ex) {
                        String msg = "\nERROR: CLI parse error for KEY[" + key + "].  The VALUE should have been an Integer.  The VALUE was => " + val + "\n";
                        throw new NumberFormatException(msg);
                    }
                    break;
                case "class java.lang.Double":
                    if (cmd.hasOption(key) == false) {
                        val = "0.0";
                    }
                    try {
                        cliMap.put(key, Double.parseDouble(val));
                    } catch (NumberFormatException ex) {
                        String msg = "\nERROR: CLI parse error for KEY[" + key + "].  The VALUE should have been a Double.  The VALUE was => " + val + "\n";
                        throw new NumberFormatException(msg);
                    }
                    break;
                default:
                    if (cmd.hasOption(key) == false) {
                        val = "";
                    }
                    cliMap.put(key, val);
                    break;
            }
        }

        return cliMap;
    }

    /*
        This is needed due to having duplicate short or long name for option is not handled properly in Apache CLI.
     */
    public static void verifyAndAddOptions(List<Option> optionList) throws MainException {
        Iterator<Option> itr = optionList.iterator();
        Set<String> shortArgs = new TreeSet<>();
        Set<String> longArgs = new TreeSet<>();

        while (itr.hasNext()) {
            Option option = itr.next();
            String key = option.getOpt();
            String lkey = option.getLongOpt();

            if (key != null) {
                if (shortArgs.contains(key)) {
                    String msg = "ERROR:  Duplicate SHORT arg => " + key;
                    throw new MainException(msg);
                } else {
                    shortArgs.add(key);
                }
            }

            if (lkey != null) {
                if (longArgs.contains(lkey)) {
                    String msg = "ERROR:  Duplicate LONG arg => " + lkey;
                    throw new MainException(msg);
                } else {
                    longArgs.add(lkey);
                }
            }

            // If you get this far, then no duplicates.  Just add option.
            // This entire method should be part of Apache CLI in addOption method.
            Main.options.addOption(option);
        }
    }

    public static boolean isMatch(String myRegEx, String myString) {
        Main.subExps.clear();
        Pattern pattern = Pattern.compile(myRegEx);
        Matcher matcher = pattern.matcher(myString);
        boolean found = matcher.matches();

        if (found) {
            Integer numGroups = matcher.groupCount();
            for (int ii = 0; ii <= numGroups; ii++) {
                Main.subExps.add(matcher.group(ii));
            }
        }

        return (found);
    }

    // this.isMatch() will populate this List.
    public static List<String> getSubExps() {
        return Main.subExps;
    }

}
