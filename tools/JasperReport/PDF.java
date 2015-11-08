package com.trg.retail.pl.common.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

/**
 *
 * <P>Use PDF_Utils constructor to initial and get PDF byte array to use</P>
 * <P>After initial PDF_Utils and has succeeded ,use autoPrintPDF can get PDF on website</P>
 * <P>
 * 		If the compile file has sub report configuration,and those parameters map's key need "_sub" previous。
 *      And don't forget give the parameter SUBREPORT_DIR。
 * </P>
 * 
 * @author Kevin
 * @version 2015/11/7
 * 
 */
public class PDF {
	
	private final static String SUB_PREVIOUS = "_sub";
	
	private static Logger logger = LoggerFactory.getLogger(PDF.class);
	
	public static PDF_Utils init(String fileName
			,Map<String,Object> params
			,Object dataSource) throws Exception{
		logger.info("PDF.init >>>>> "
				+ "\nfileName : "+fileName
				+ "\nparams : "+params
				+ "\ndataSource : "+dataSource);
		return new PDF_Utils(fileName, params, dataSource);
	}
	
	public static class PDF_Utils{
		private JasperReport jr;
		private JasperPrint jp;
		private JRDataSource jrs;
		private byte[] PDF_byte;	
		
		public PDF_Utils(String fileName,Map<String,Object> params,Object dataSource) throws Exception{
			setParam(params);			
			this.jr = JasperCompileManager.compileReport(fileName);
			this.jrs = parseObjectDataSource(dataSource);
			this.jp = JasperFillManager.fillReport(jr, params, jrs); 
			this.PDF_byte = JasperExportManager.exportReportToPdf(jp);
		}

		private JRDataSource parseObjectDataSource(Object dataSource) throws Exception{
			List<Object> objList = null;
			if(dataSource instanceof List){
				objList = (List<Object>) dataSource;
			}else{
				objList = new ArrayList<Object>();
				objList.add(dataSource);
			}		
			return castListDataSource(objList);
		}
		
		private JRDataSource castListDataSource(List<? extends Object> list){
			if(!CollectionUtils.isEmpty(list)){
				Object obj = list.get(0);
				if(obj instanceof Map){
					return new JRMapCollectionDataSource((List<Map<String, ?>>) list);
				}else{
					return new JRBeanCollectionDataSource(list);
				}
			}
			return null;
		}
		
		private void setParam (Map<String,Object> params) throws Exception{
			if(params != null && !params.isEmpty()){
				String key = null;
				Object value = null;
				for(Entry<String, Object> set:params.entrySet()){
					key = set.getKey();
					if(key.indexOf(SUB_PREVIOUS)!=-1){	
						value = set.getValue();
						if(value instanceof List){
							List<?> valueList = (List<?>)value;
							set.setValue(castListDataSource(valueList));
						}else{
							throw new ClassCastException("not support yet in PDF_Utils");
						}
					}
				}
			}	
		}
		
		public void autoPrintPDF(HttpServletResponse response) throws Exception{
			byte[] responsePDF = getPDF_byte();
			if(responsePDF!=null){
				response.setContentType("application/pdf");
	            response.setContentLength(responsePDF.length);
	            ServletOutputStream ouputStream = response.getOutputStream();
	            ouputStream.write(responsePDF, 0, responsePDF.length);
	            ouputStream.flush();
	            logger.info("PDF_Utils.autoPrintPDF has succeeded at "+new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date()));
	            ouputStream.close();
			}else{
				throw new Exception("not initial PDF_Utils yet");
			}			
		}
		
		public byte[] getPDF_byte(){
			return this.PDF_byte;
		}		
	}
}
