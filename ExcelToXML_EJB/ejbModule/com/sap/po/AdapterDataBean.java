package com.sap.po;

import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sun.org.apache.xpath.internal.operations.Bool;


/**
 * @author Aman Anand
 *
 *         This class will act as a Bean Factory
 */

public class AdapterDataBean {
	
	// Variables for Adapter parameters 
	private String sheetName;
	private String sheetIndex;
	private String recordName;
	private String documentName;
	private String documentNamespace;
	private Boolean log = false;
	private Boolean processAllSheets = false;
	

	//Message Audit log 
	private AuditLogHandler audit;
	private Message message = null;

	

	public AdapterDataBean(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException {
		try {
			
			setContextdata(moduleContext, inputModuleData);
			this.audit.AuditlogSuccess("reading the adapter parameters", log);
			setAdapterParameter(moduleContext);
			this.audit.AuditlogSuccess("reading the adapter parameters completed successfully", log);
		} catch (Exception e) {
			this.audit.AuditlogError("reading the adapter parameter failed");
			throw new ModuleException(e.getLocalizedMessage());
		}
	}
	

	private void setContextdata(ModuleContext context, ModuleData inputModuleData)
	{
		this.message = (Message) inputModuleData.getPrincipalData();
		this.audit = new AuditLogHandler(message);
		Boolean logparam = Boolean.parseBoolean(context.getContextData("debug"));
		
		if(logparam)
		{
			log = logparam;
			audit.AuditlogSuccess(message.getMessageDirection().toString(), log);
		}
	}
	
	// Set the AdapterParameter Value to the Variables
	private void setAdapterParameter(ModuleContext context)
	{
			this.sheetName = context.getContextData("sheetName");
			this.sheetIndex = context.getContextData("sheetIndex");
			this.recordName = context.getContextData("recordName");
			this.documentName = context.getContextData("documentName");
			this.documentNamespace = context.getContextData("documentNamespace");
			this.processAllSheets = Boolean.parseBoolean(context.getContextData("processAllSheets"));
			printLog();
			ParameterDataValidation();
	}
	
	
	private void printLog() {
		if(sheetName != null)
		{
			audit.AuditlogSuccess("sheetName = "+sheetName, log);
		}
		if(sheetIndex != null)
		{
			audit.AuditlogSuccess("sheetIndex = "+sheetIndex, log);
		}
		if(documentName != null)
		{
			audit.AuditlogSuccess("documentName = "+documentName, log);
		}
		if(documentNamespace != null)
		{
			audit.AuditlogSuccess("documentNamespace = "+documentNamespace, log);
		}
		if(recordName != null)
		{
			audit.AuditlogSuccess("recordName = "+recordName, log);
		}
		if(processAllSheets)
		{
			audit.AuditlogSuccess("processAllSheets = "+processAllSheets, log);
		}
		
	}


	// Function to Validate the Parameter data 
	private void ParameterDataValidation()
	{
		if(this.sheetName == null && this.sheetIndex == null && processAllSheets)
		{
			audit.AuditlogWarning("By default first sheet will be processed");
			sheetIndex = "0";
		}
		
		if(this.sheetName != null && this.sheetIndex != null)
		{
			audit.AuditlogError("Either 'sheetName' or 'sheetIndex' value can be used");
		}
		
		if(this.documentName == null || this.documentNamespace == null || this.recordName == null)
		{
			audit.AuditlogError("Manadatory parameters missing ");
		}
		
	}
	
	
	
	public String getSheetName() {
		return sheetName;
	}

	public int getSheetIndex() {
		return Integer.parseInt(sheetIndex);
	}
	
	public void setSheetIndex(String sheetIndex)
	{
		this.sheetIndex = sheetIndex;
	}


	public AuditLogHandler getAudit() {
		return audit;
	}

	public Message getMessage() {
		return message;
	}
	
	public String getRecordName() {
		return recordName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public String getDocumentNamespace() {
		return documentNamespace;
	}
	
	public Boolean getLog() {
		return log;
	}
	
	public Boolean getProcessAllSheets() {
		return processAllSheets;
	}

}
