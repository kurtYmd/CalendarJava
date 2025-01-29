package com.calendar.utils.helpers;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;



public class XmlHelper {

    public static Document openXmlAsDocument(String filePath) throws XmlHelperError, FileNotFoundException {

        File xmlFile = new File(filePath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        
        try {
        	builder = factory.newDocumentBuilder();
        } catch(ParserConfigurationException e) {
        	e.printStackTrace();
        	throw new XmlHelperError("ParserConfigurationException");
        }

        
        Document document;
        
        try {
        	document = builder.parse(xmlFile);
        } catch (IOException e) { 
        	throw new FileNotFoundException();
        } catch (SAXException e) {
			e.printStackTrace();
        	throw new XmlHelperError("SAXException");
		}

        return document;

    }

    static void saveDocumentToFile(Document document, String filePath) throws XmlHelperError {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;

        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        	throw new XmlHelperError("TransformerConfigurationException");
        }

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

}
