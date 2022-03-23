package com.sap.po;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.ArrayList;


/**
 * @author Aman Anand
 *
 *  This class will Create the XML file based on the List data created by the "ExcelToXMLConversion" class
 */
public class toXMLConversion {
	
	 private DocumentBuilder builder;
	 private Document document;
	 
	 private String outputString;
	 private AuditLogHandler audit;
	 private AdapterDataBean data;
	 private ArrayList excelHeader;
	 private ArrayList excelData;
	 private ArrayList multipleSheetsData;
	 private int columns;
	 private int rows;
	 
	private String recordName;
	private String documentName;
	private String documentNamespace;
	 
	 public toXMLConversion(ExcelToXMLConversion conversion, AdapterDataBean data, Boolean processAllSheets) throws ParserConfigurationException
	 {
		 this.audit = data.getAudit();
		 
		 try
		 {
			 builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			 document = builder.newDocument();
		 }catch(Exception e)
		 {
			 throw new ParserConfigurationException( "ParserConfigurationException raised "+e.getMessage());
		 }
		 
		 this.data = data;
		 this.recordName = data.getRecordName();
		 this.documentName = data.getDocumentName();
		 this.documentNamespace = data.getDocumentNamespace();
		 
		 if(processAllSheets)
		 {
			 this.multipleSheetsData = conversion.getMultipeSheetsData();
			 convertAllSheetstoXML();
		 }
		 else
		 {
			 this.excelHeader = conversion.getExcelHeader();
			 this.excelData = conversion.getExcelData();
			 this.rows = conversion.getRowCount();
			 this.columns = this.excelHeader.size();
			 converttoXML();
		 }
		 
	 }
	 
	 public String getOutput()
	 {
		 return outputString;
	 }
	 
	 public void converttoXML() {
		 audit.AuditlogSuccess("The XML file is being processed", data.getLog());
		 
		 Element root = document.createElement(documentName);
		 document.appendChild(root);
		 
		 Attr attr = document.createAttribute("id");
         attr.setValue(documentNamespace);
		 root.setAttributeNode(attr);
		 
		 int count = 0;
		 for(int i = 0; i < rows-1 ; i++)
		 {
			 Element records = document.createElement(recordName);
			 for(int j = 0 ; j < columns ; j++)
			 {
				 Element node  = document.createElement(excelHeader.get(j).toString());
				 node.appendChild(document.createTextNode(excelData.get(count).toString()));
				 records.appendChild(node);
				 count ++;
			 }
			 
			 root.appendChild(records);
		 }
		 handleTranformation();
	 }
	 
	 public void convertAllSheetstoXML() {
	 
		 Element root = document.createElement(documentName);
		 document.appendChild(root);
		 
		 Attr attr = document.createAttribute("id");
         attr.setValue(documentNamespace);
		 root.setAttributeNode(attr);
		 
		 int count = 0 ;
		 for (int i = 0 ; i < multipleSheetsData.size()/4; i++)
		 {
			 audit.AuditlogSuccess(multipleSheetsData.get(count+0).toString(), data.getLog());
			 audit.AuditlogSuccess(multipleSheetsData.get(count+1).toString(), data.getLog());
			 audit.AuditlogSuccess(multipleSheetsData.get(count+2).toString(), data.getLog());
			 audit.AuditlogSuccess(multipleSheetsData.get(count+3).toString(), data.getLog());
			 
			 this.rows = Integer.parseInt(""+ multipleSheetsData.get(count+0));
			 this.columns = Integer.parseInt(""+ multipleSheetsData.get(count+1));
			 this.excelHeader = (ArrayList) multipleSheetsData.get(count+2);
			 this.excelData = (ArrayList) multipleSheetsData.get(count+3);
			
			 count = count+4;
			 
			 Element sheets = document.createElement("sheets");
			 
			 int countforexceldata = 0;
			 for(int j = 0; j < rows-1 ; j++)
			 {
				 Element records = document.createElement(recordName);
				 for(int k = 0 ; k < columns ; k++)
				 {
					 Element node  = document.createElement(excelHeader.get(k).toString());
					 node.appendChild(document.createTextNode(excelData.get(countforexceldata).toString()));
					 records.appendChild(node);
					 countforexceldata ++;
				 }
				 
				 sheets.appendChild(records);
			 }
			 root.appendChild(sheets);
		 }
		 handleTranformation();
	 }
	
	 private void handleTranformation()
	 {
		 TransformerFactory transformerFactory = TransformerFactory.newInstance();		 
		 Transformer transformer = null ;         
		 try {			
			 transformer = transformerFactory.newTransformer();		
			 } 
		 catch (TransformerConfigurationException e) 
		 {			
			 e.printStackTrace();		
		 }              
		 transformer.setOutputProperty(OutputKeys.INDENT, "yes");         
		 transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");                  
		 DOMSource source = new DOMSource(document);                  
		 StringWriter writer = new StringWriter();         
		 try {			
			 	transformer.transform(source, new StreamResult(writer));		
			 } catch (TransformerException e) 
		 {			
				 e.printStackTrace();		
		 }                  
		 outputString = writer.getBuffer().toString();          
		 audit.AuditlogSuccess("The XML file has been processed Successfully", data.getLog());


	 }
	 
}