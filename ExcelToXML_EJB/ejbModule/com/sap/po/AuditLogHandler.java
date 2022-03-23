package com.sap.po;

import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

/**
 * @author Aman Anand
 *
 *         This class will handle the AuditLog related activities
 */

public class AuditLogHandler {
	MessageKey key = null;
	AuditAccess audit = null;

	/**
	 * constructor to get the AuditAccess
	 * 
	 * @param message
	 *            is a actual message of type Message
	 * @throws Exception
	 *             in case of invalid value
	 */
	public AuditLogHandler(Message message) {
		key = new MessageKey(message.getMessageId(), message.getMessageDirection());
		try {
			audit = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();
		} catch (MessagingException e) {
			AuditlogError(e.getMessage());
		}
	}

	/**
	 * Write the Success message log on the monitoring log console
	 * 
	 * @param auditLog
	 *            is the user defined message which needs to be printed
	 */

	public void AuditlogSuccess(String auditLog, Boolean log) {
		if(log)
		{
			auditLog = "ExcelXMLConversionBean: "+auditLog;
			this.audit.addAuditLogEntry(key, AuditLogStatus.SUCCESS, auditLog);
		}	
	}

	/**
	 * Write the Warning message log on the monitoring log console
	 * 
	 * @param auditLog
	 *            is the user defined message which needs to be printed
	 */
	public void AuditlogWarning(String auditLog) {
		this.audit.addAuditLogEntry(key, AuditLogStatus.WARNING, "ExcelXMLConversionBean: "+auditLog);
	}

	/**
	 * Write the Error message log on the monitoring log console
	 * 
	 * @param auditLog
	 *            is the user defined message which needs to be printed
	 */
	public void AuditlogError(String auditLog) {
		this.audit.addAuditLogEntry(key, AuditLogStatus.ERROR, "ExcelXMLConversionBean: "+auditLog);
	}

}
