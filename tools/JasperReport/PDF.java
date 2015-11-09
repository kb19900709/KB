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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
 
/**
 *
 * <P>Use PDF_Export constructor to initial。</P>
 * <P>After initial PDF_Export and has succeeded ,use output() can get PDF on website</P>
 * <P>If the compile file has sub report configuration,and those parameters map's key need "_sub" prefix。</P>
 * <P>And don't forget to give the parameter SUBREPORT_DIR。</P>
 * 
 * 
 * @author Kevin
 * @version 2015/11/9
 * 
 */
public class PDF {
	
	private final static String SUB_PREFIX = "_sub";
	
	private static Logger logger = LoggerFactory.getLogger(PDF.class);
	
	public static PDF_Export init(String fileName
			,Map<String,Object> params
			,Object dataSource) throws Exception{
		logger.info("PDF.init >>>>> "
				+ "\nfileName : "+fileName
				+ "\nparams : "+params
				+ "\ndataSource : "+dataSource);
		return new PDF_Export(fileName, params, dataSource);
	}
	
	public static class PDF_Export{
		private JasperReport jr;
		private JasperPrint jp;
		private JRDataSource jrs;
		
		public PDF_Export(String fileName,Map<String,Object> params,Object dataSource) throws Exception{
			params = setParam(params);			
			this.jr = JasperCompileManager.compileReport(fileName);
			this.jrs = parseObjectDataSource(dataSource);
			this.jp = JasperFillManager.fillReport(jr, params, jrs); 
		}

		//中繼method，最後將會拋轉給 castListDataSource 做處理
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
		
		//把資料轉成 JRDataSource (Collection型態)
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
		
		//針對有SUB_PREFIX字樣的參數做額外的處理，將subReporty資料轉成 JRDataSource
		private Map<String,Object> setParam (Map<String,Object> params) throws Exception{
			if(params != null && !params.isEmpty()){
				String key = null;
				Object value = null;
				for(Entry<String, Object> set:params.entrySet()){
					key = set.getKey();
					if(key.indexOf(SUB_PREFIX)!=-1){	
						value = set.getValue();
						if(value instanceof List){
							List<?> valueList = (List<?>)value;
							set.setValue(castListDataSource(valueList));
						}else{
							throw new ClassCastException("not support yet in PDF_Export");
						}
					}
				}
				return params;
			}	
			return null;
		}
		
		//輸出成PDF並回傳到畫面上
		public void outputPDF(HttpServletResponse response) throws Exception{
			byte[] pdf_byte = output();
			if(pdf_byte!=null){
				response.setContentType("application/pdf");
	            response.setContentLength(pdf_byte.length);
	            ServletOutputStream ouputStream = response.getOutputStream();
	            ouputStream.write(pdf_byte, 0, pdf_byte.length);
	            ouputStream.flush();
	            logger.info("PDF_Export.outputPDF has succeeded at "+new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date()));
	            ouputStream.close();
			}else{
				throw new Exception("not initial PDF_Export yet");
			}			
		}
		
		//取得PDF_byte
		public byte[] output() throws Exception{
			return JasperExportManager.exportReportToPdf(jp);
		}		
	}
}
