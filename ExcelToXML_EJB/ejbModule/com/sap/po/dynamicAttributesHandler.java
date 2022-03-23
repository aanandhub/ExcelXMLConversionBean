package com.sap.po;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;

/**
 * @author Aman Anand
 *
 *         This class will handle the dynamic Variables
 */
public class dynamicAttributesHandler {
	
	private AdapterDataBean data;
	
	private String FileNameKey = "FileName";
	private String FileNameNS = "http://sap.com/xi/XI/System/File";
	
	
	public String getDynamicFileName(Message message, AuditLogHandler audit, AdapterDataBean data )throws ModuleException
	{
		try {
			audit.AuditlogSuccess("Reading the fileName from Dynamic Property", data.getLog());
			MessagePropertyKey mpk = new MessagePropertyKey(FileNameKey, FileNameNS);
			String filename  = message.getMessageProperty(mpk);
			return filename;
		}
		catch(Exception ee){
			audit.AuditlogError("Enable 'dynamic filename' option ");
			throw new ModuleException("Error wile reading the dynamic fileName");
		}
		
	}
	
	

}
