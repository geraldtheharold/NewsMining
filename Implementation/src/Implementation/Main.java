package Implementation;

import Entities.Page;

import java.io.*;
import java.util.LinkedList;
import java.util.TreeSet;
import static Entities.Page.extractString;

public class Main {

    private static TreeSet<String> subsections = new TreeSet<>();
    private static TreeSet<String> finalSubs = new TreeSet<>();
    private static LinkedList<Page> pageList = new LinkedList<>();
    private final static String FILE_NAME = "./raw.txt";
    private final static String SUBSECTION_REGEX = "<a href=\"https://www.faz.net/aktuell/(.*?)\"";
    private final static String NEXTLINE = "class=\"lay-MegaMenu_SubsectionLink\"";
    final static String SUBPAGE_REGEX = "href=\"https://www.faz.net/aktuell(?!/news-des-tages-per-whatsapp-telegram)(?!/reise/routenplaner/)(?!ueber-uns/)(?!hilfe/)(?!faz-net-services/)(?!datenschutzerklaerung)(?!asv/vor-denker/)(?!allgemeine-nutzungsbedingungen)(.*?).html\"";

    public static void main(String[] args) {
        Download.mainPage();
        extractSubsections();
        int i = 0;
        // int debug = 0;
        for (String subsection : subsections) {
            System.out.println(subsection);
            Download.fromPath(subsection);
            fetchSubs();
            // debug++;
            // if (debug == 10) break;
        }
        // debug = 0;
        for (String sub : finalSubs) {
            System.out.printf("%d\t" + sub + "\n", i++);
            mine(sub);
            // debug++;
            // if (debug == 10) break;
        }
        pageList.forEach(Page -> Page.printPage());
    }

    private static void fetchSubs() {
        String line, buffer;
        try {
            BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));
            while((line = in.readLine()) != null) {
                if ((buffer = extractString(SUBPAGE_REGEX, line)) != null) {
                    finalSubs.add("https://www.faz.net/aktuell" + buffer);
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    static void mine(String s) {
        Download.fromPath(s);
        Page p = new Page();
        String line;
        boolean pageExists = false;
        try {
            BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));
            while((line = in.readLine()) != null) {
                p.createPage(line);
            }
            if (p.getType() != null) {
                for (Page page : pageList) {
                    try {
                        if ((p.getTitle().compareTo(page.getTitle()) == 0 && p.getLastUpdated().getDate().compareTo(page.getPublication().getDate()) == 0) || (p.getPublication().getDate() == null)) {
                            pageExists = true;
                        }
                    } catch (NullPointerException e) {}
                }
                if (!pageExists)
                    pageList.add(p);
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    private static void extractSubsections() {
        String firstLine, line, buffer;
        try {
            BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));
            while((firstLine = in.readLine()) != null) {
                if ((buffer = extractString(SUBSECTION_REGEX, firstLine)) != null && ((line = in.readLine()).contains(NEXTLINE))) {
                    subsections.add("https://www.faz.net/aktuell/" + buffer);
                }
                if ((buffer = extractString(SUBPAGE_REGEX, firstLine)) != null) {
                    finalSubs.add("https://www.faz.net/aktuell" + buffer);
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }



}
