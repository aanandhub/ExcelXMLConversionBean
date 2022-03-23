package com.sap.po;

import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ejb.Local;
import javax.ejb.LocalHome;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;

import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.af.lib.mp.module.ModuleHome;
import com.sap.aii.af.lib.mp.module.ModuleLocal;
import com.sap.aii.af.lib.mp.module.ModuleLocalHome;
import com.sap.aii.af.lib.mp.module.ModuleRemote;
import com.sap.engine.interfaces.messaging.api.Message;

/**
 * @author Aman Anand
 * Session Bean implementation class ExcelToXML
 */

@Stateless(name = "ExcelToXMLBean")
@Local(value = { ModuleLocal.class })
@Remote(value = { ModuleRemote.class })
@LocalHome(value = ModuleLocalHome.class)
@RemoteHome(value = ModuleHome.class)

public class ExcelToXML implements Module {

	private AdapterDataBean data;
	private toXMLConversion xmldata;
	private Message message;

	/**
	 * Default constructor.
	 */
	public ExcelToXML() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException {
		//Check if the message is empty and process the same
		
		if(inputModuleData.getPrincipalData() == null)
		{
			return inputModuleData;
		}
				
		//read data bean
		data = new AdapterDataBean(moduleContext, inputModuleData);
		message = data.getMessage();
		
		if(message.getMessageDirection().toString().equals("OUTBOUND"))
		{
			ExcelToXMLConversion conversion= new ExcelToXMLConversion(data);
			if(data.getProcessAllSheets())
			{
				conversion.readmultipleFiles();
				try {
					xmldata = new toXMLConversion(conversion,data, data.getProcessAllSheets());
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
			else {
				conversion.readxmlfile();
				try {
					xmldata = new toXMLConversion(conversion,data, data.getProcessAllSheets());
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try {
				byteArrayOutputStream.write(xmldata.getOutput().getBytes());
				message.getDocument().setContent(byteArrayOutputStream.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			inputModuleData.setPrincipalData(message);
		}
		return inputModuleData;
	}

}
