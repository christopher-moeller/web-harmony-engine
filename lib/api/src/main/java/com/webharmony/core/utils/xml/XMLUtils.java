package com.webharmony.core.utils.xml;

import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLUtils {

    private XMLUtils() {

    }

    @SneakyThrows
    public static Document createDocumentByString(String xmlAsString) {
        DocumentBuilderFactory documentFactory = createNewDocumentBuilderFactory();
        DocumentBuilder builder = documentFactory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlAsString)));
    }

    @SneakyThrows
    public static Document createNewEmptyDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    @SneakyThrows
    public static String convertDocumentToString(Document document, boolean pretty) {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = createNewTransformerFactory();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, pretty ? "yes" : "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(document), new StreamResult(sw));
        return sw.toString();
    }

    public static TransformerFactory createNewTransformerFactory() {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        return factory;
    }

    @SneakyThrows
    @SuppressWarnings("all")
    public static DocumentBuilderFactory createNewDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory;
    }

    @SneakyThrows
    public static String convertElementToString(Element element) {
        TransformerFactory transFactory = createNewTransformerFactory();
        Transformer transformer = transFactory.newTransformer();
        StringWriter buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(element),
                new StreamResult(buffer));

        return buffer.toString();
    }

    public static Element getFirstChildElement(Element valueElement) {
        final NodeList nodeList = valueElement.getChildNodes();
        for(int i=0; i<nodeList.getLength(); i++) {
            final Node item = nodeList.item(i);
            if (item instanceof Element e) {
                return e;
            }
        }

        return null;
    }
}
