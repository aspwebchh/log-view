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

public class Config {
    private static String path;
    private static String url;

    public static String getPath() {
        return path;
    }

    public static String getUrl() {
        return url;
    }

    static {
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
                path = el.getAttribute("path");
                url = el.getAttribute("url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
