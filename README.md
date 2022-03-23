# ExcelXMLConversionBean

ExcelXMLConversionBean is a Custom Module Adapter EJB 3.0 Version project. It is based on the Apache POI API. it uses a single logic to read all kinds of Excel files (XLS and XLSX) and transform into XML. It can process one sheet as well as all sheets of an Excel File.



**Module Parameter Reference**

Below is a list of the parameters for configuration of the module for Excel to XML conversion.

| Parameter Name | Allowed Value | Default value | Mandatory Field | Remarks|
| ---------------  |---------------|---------------|-----------------|--------|
| sheetName        |               |               |      No  | The name of the Excel sheet from which the data needs to be extracted. Either **sheetName** or **sheetIndex** should be populated.
| sheetIndex       | Any Integer Value starting from 0 |    0         | No | The index of the Excel sheet from which the data needs to be extracted. Either **sheetName** or **sheetIndex** should be populated.|
| recordName       | Any String Value | |Yes| XML element name for row of record in output|
| documentName     |Any String Value | |Yes| Document name of root element of XML output|
| documentNamespace| Any String Value | | Yes |Namespace of root element of XML output|
| processAllSheets |  true/false |  false  |    No   |  Will process all the excel sheets. Remove the **sheetName** or **sheetIndex** paramter from the configuration |
| debug            | true/false  |  false  |  No     | should only be used for debug purpose




