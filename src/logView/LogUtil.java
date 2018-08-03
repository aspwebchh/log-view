package logView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogUtil {
    public static String getLogPath() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            String confFilePath = System.getProperty("user.dir") + "\\log.xml";
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(confFilePath));

            Element rootElement = doc.getDocumentElement();

            XPathFactory xf = XPathFactory.newInstance();
            XPath xpath = xf.newXPath();
            XPathExpression xexp = null;
            NodeList nodes = null;
            Element el = null;

            xexp = xpath.compile("/log");
            nodes = (NodeList) xexp.evaluate(rootElement, XPathConstants.NODESET);
            el = (Element) nodes.item(0);
            {
                String path = el.getAttribute("path");
                return path;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getLogTypes() {
        List<String> types = new ArrayList<>();
        types.add("ERROR");
        types.add("WARN");
        types.add("INFO");
        types.add("DEBUG");
        types.add("FATAL ");
        types.add("TRACE ");
        return types;
    }

    public static List<String> getPathNames() {
         String rootPath = getLogPath();
         File file = new File(rootPath);
         File[] files = file.listFiles();
         List<String> pathNames = Arrays.stream(files).filter( item -> item.isDirectory()).map(item -> item.getName()).collect(Collectors.toList());
         return pathNames;
    }

    public static  List<LogItem> getLogContent( String dir , int range, String type, String keyword, String exceptKeyword, String regex) {
        String rootPath = getLogPath();
        String path = rootPath + "\\" + dir;
        File file = new File(path);
        List<File> logFiles = Arrays.stream(file.listFiles()).filter( item -> item.isFile() && inRange(item.getName(), range) ).collect(Collectors.toList());

        CountDownLatch countDownLatch = new CountDownLatch(logFiles.size());
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<LogItem> allLogItem = new ArrayList<>();

        for(File fileItem : logFiles) {
            executorService.execute( ()->{
                synchronized (allLogItem) {
                    allLogItem.addAll( LogItem.asList(fileItem, type,keyword,exceptKeyword, regex));
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();

        allLogItem.sort( (b,a) -> {
            long diff =  a.date().getTime()  - b.date().getTime();
            return (int)diff;
        });

        return allLogItem;
    }

    private static boolean inRange( String name, int range ) {
        Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(name);
        if( !matcher.find()) {
            return false;
        }
        int year = Integer.parseInt( matcher.group(1) );
        int month = Integer.parseInt( matcher.group(2) );
        int day = Integer.parseInt( matcher.group(3) );

        Calendar calendar = new GregorianCalendar(year, month - 1, day,0,0,0);

        Calendar now = Calendar.getInstance();
        Calendar endCal = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),23,59,59);
        Calendar beginCal = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),23,59,59);
        beginCal.add(Calendar.DAY_OF_MONTH, -range);

        if( calendar.getTimeInMillis() <= endCal.getTimeInMillis() && calendar.getTimeInMillis() >= beginCal.getTimeInMillis() ) {
            return true;
        }
        return false;
    }
}
