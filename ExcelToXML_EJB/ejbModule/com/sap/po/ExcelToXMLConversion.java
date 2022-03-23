package com.sap.po;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.XMLPayload;

/**
 * @author Aman Anand
 *
 * This class will Read the Excel file and convert the data into List
 */
public class ExcelToXMLConversion {
	
	private Message message = null;
	private AuditLogHandler audit = null;
	private AdapterDataBean data = null;
	private dynamicAttributesHandler dynamicAttributes = null;
	private Workbook workbook;
	private HSSFSheet xlssheet;
	private XSSFSheet xlsxsheet;
	private int rowCount;
	private int columnCount;
	
	private ArrayList allExcelData;
	private ArrayList excelData;
	private ArrayList excelHeader;
	private ArrayList multipleSheetsData;
	

	public ExcelToXMLConversion(AdapterDataBean data) {
		this.message = data.getMessage();
		this.audit = data.getAudit();
		this.data = data;	
		allExcelData = new ArrayList();
		dynamicAttributes  = new dynamicAttributesHandler();
	}
	
	
	public void readxmlfile() throws ModuleException  {
	XMLPayload xmlPayload = message.getDocument();
	InputStream is = new ByteArrayInputStream(xmlPayload.getContent());
	
	String fileName  = this.dynamicAttributes.getDynamicFileName(message,audit,data);
	audit.AuditlogSuccess("reading the file "+fileName, data.getLog());
	try
	{
		if(fileName.toLowerCase().contains(".xlsx"))
		{	
			workbook = new XSSFWorkbook(is);
			handleXlsxFile();
			audit.AuditlogSuccess("Reading file data completed successfully", data.getLog());
			audit.AuditlogSuccess("Extracting excel Data", data.getLog());
			extractExcelHeader();
			extractExcelData();
			audit.AuditlogSuccess("Extracting excel data completed", data.getLog());
		}
		else if(fileName.toLowerCase().contains(".xls"))
		{
			audit.AuditlogWarning("came into .xls condition");
			workbook = new HSSFWorkbook(is);
			handleXlsFile();
			audit.AuditlogSuccess("Reading file data completed successfully", data.getLog());
			audit.AuditlogSuccess("Extracting excel Data", data.getLog());
			extractExcelHeader();
			extractExcelData();
			audit.AuditlogSuccess("Extracting excel data completed", data.getLog());
		}
	
		else {
			audit.AuditlogError("Not an Excel file");
		}
		
	}catch(IOException e1) {
		audit.AuditlogError("Error while redaing the excel file");
		throw new ModuleException("Error while reading the file");
	}
}
	
	
	public void readmultipleFiles() throws ModuleException
	{
		multipleSheetsData = new ArrayList();
		XMLPayload xmlPayload = message.getDocument();
		InputStream is = new ByteArrayInputStream(xmlPayload.getContent());
		
		String fileName  = this.dynamicAttributes.getDynamicFileName(message,audit,data);
		audit.AuditlogSuccess("reading the file "+fileName, data.getLog());
		
		if(fileName.toLowerCase().contains(".xlsx"))
		{	
			try
			{
				workbook = new XSSFWorkbook(is);
				audit.AuditlogWarning(""+workbook.getNumberOfSheets());
				
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
		            data.setSheetIndex(""+i);
		            audit.AuditlogSuccess("Reading sheet "+ i, data.getLog());
		            handleXlsxFile();
		            multipleSheetsData.add(rowCount);
		            multipleSheetsData.add(columnCount);
		            audit.AuditlogSuccess("Reading sheet "+ i+" completed successfully", data.getLog());
		            audit.AuditlogSuccess("Extracting excel Sheet "+ i +" data", data.getLog());
		            multipleSheetsData.add(extractExcelHeader());
		            multipleSheetsData.add(extractExcelData());
		        	audit.AuditlogSuccess("Extracting excel Sheet "+ i +" data Completed Successfully", data.getLog());
		        	allExcelData = new ArrayList();
		        	rowCount = 0;
				}
				audit.AuditlogSuccess("Reading file data completed successfully", data.getLog());
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(fileName.toLowerCase().contains(".xls"))
		{	
			try
			{
				workbook = new HSSFWorkbook(is);
				audit.AuditlogWarning(""+workbook.getNumberOfSheets());
				
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
		            data.setSheetIndex(""+i);
		            audit.AuditlogSuccess("Reading sheet "+ i, data.getLog());
		            handleXlsFile();
		            multipleSheetsData.add(rowCount);
		            multipleSheetsData.add(columnCount);
		            audit.AuditlogSuccess("Reading sheet "+ i+" completed successfully", data.getLog());
		            audit.AuditlogSuccess("Extracting excel Sheet "+ i +" data", data.getLog());
		            multipleSheetsData.add(extractExcelHeader());
		            multipleSheetsData.add(extractExcelData());
		        	audit.AuditlogSuccess("Extracting excel Sheet "+ i +" data Completed Successfully", data.getLog());
		        	allExcelData = new ArrayList();
		        	rowCount = 0;
				}
				audit.AuditlogSuccess("Reading file data completed successfully", data.getLog());
			}catch(Exception e)
			{
				audit.AuditlogError("Reading file data failed");
				throw new ModuleException("Reading file data failed");
			}
		}
	}
	
	private ArrayList extractExcelHeader() {
		excelHeader = new ArrayList();
		for(int i = 0 ; i < columnCount ; i++)
		{
			String header = allExcelData.get(i).toString().trim();
			header = header.replaceAll(" ", "_");
			excelHeader.add(header);
				
		}
		return excelHeader;
	}
	
	private ArrayList extractExcelData() {
		excelData = new ArrayList();
		for(int i = columnCount ; i < allExcelData.size() ; i++)
		{
			String data = allExcelData.get(i).toString();
			excelData.add(data);
				
		}
		return excelData;
	}

	// to read xls file 
	private void handleXlsFile()
	{
		audit.AuditlogWarning("came into .xls handler condition");
		if(data.getSheetName() == null || data.getSheetName().trim().equals(""))
		{
			xlssheet = (HSSFSheet) this.workbook.getSheetAt(data.getSheetIndex());
		}
		else
		{
			xlssheet = (HSSFSheet) this.workbook.getSheet(data.getSheetName());
		}
			
		Iterator<Row> iterator= xlssheet.iterator();
		rowColumnCount(iterator);
		Cell cell;
		int tempRowCount = rowCount;
		for(int i = 0 ; i < tempRowCount ;i++)
		{
			for(int j = 0 ; j < columnCount; j++ )
			{
				
				if(xlssheet.getRow(i) != null)
				{
					cell = xlssheet.getRow(i).getCell(j);
					if(cell == null)				
					{		
						allExcelData.add(" ");				
					}				
					else				
					{	
						switch(cell.getCellType())
						{
							case Cell.CELL_TYPE_NUMERIC: allExcelData.add(cell.getNumericCellValue()); break;
							case Cell.CELL_TYPE_STRING: allExcelData.add(cell.getStringCellValue()); break;
						}
					
					}
				}else {
					tempRowCount++;
				}
			}
		}
				
	}
	
	// to read xlsx file 
	private void handleXlsxFile()
	{
		if(data.getSheetName() == null || data.getSheetName().trim().equals(""))
		{
			xlsxsheet = (XSSFSheet) this.workbook.getSheetAt(data.getSheetIndex());
		}
		else
		{
			xlsxsheet = (XSSFSheet) this.workbook.getSheet(data.getSheetName());
		}
		
		Iterator<Row> iterator= xlsxsheet.iterator();
		rowColumnCount(iterator);
		Cell cell;
		int tempRowCount = rowCount;
		for(int i = 0 ; i < tempRowCount ;i++)
		{
			for(int j = 0 ; j < columnCount; j++ )
			{
				
				if(xlsxsheet.getRow(i) != null)
				{
					cell = xlsxsheet.getRow(i).getCell(j);
					if(cell == null)				
					{		
						allExcelData.add("");				
					}				
					else				
					{	
						switch(cell.getCellType())
						{
							case Cell.CELL_TYPE_NUMERIC: allExcelData.add(cell.getNumericCellValue()); break;
							case Cell.CELL_TYPE_STRING: allExcelData.add(cell.getStringCellValue()); break;
						}
					
					}
				}else {
					tempRowCount++;
				}
				
			}
		}
		
	}
	
	private void rowColumnCount(Iterator<Row> iterator)
	{
		while(iterator.hasNext())
		{
			Row nextRow = iterator.next();
			if(nextRow.getLastCellNum() > this.columnCount)
				{
					this.columnCount = nextRow.getLastCellNum();
				}
			rowCount ++;
		}
	}
	
	public ArrayList getExcelData()
	{
		return excelData;
	}
	
	public ArrayList getExcelHeader()
	{
		return excelHeader;
	}
	
	public ArrayList getMultipeSheetsData()
	{
		return multipleSheetsData;
	}
	
	public int returnColumnCount()
	{
		return columnCount;
	}
	
	public int getRowCount()
	{
		return rowCount;
	}
	
}
	