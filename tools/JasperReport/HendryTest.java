import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;


public class HendryTest {
	
	static String EXPORTFILE = "C:\\Users\\Z510\\Desktop\\TestReport"+
			new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".xlsx";
    
    public static void main(String args[]){
        
        /* new workbook */
        XSSFWorkbook wb = new XSSFWorkbook() ;
        
        /* new sheet */
        XSSFSheet sh = (XSSFSheet)wb.createSheet("test") ;
        
        /* new excel table */
        XSSFTable tb = sh.createTable();
        
        /* get CTTable object*/
        CTTable cttable = tb.getCTTable();
        
        /* Let us define the required Style for the table */
//        CTTableStyleInfo table_style = cttable.addNewTableStyleInfo();
//        table_style.setName("TableStyleMedium9");
         
         /* Set Table Style Options */
//        table_style.setShowColumnStripes(false); //showColumnStripes=0
//        table_style.setShowRowStripes(true); //showRowStripes=1
//        
        /* Define the data range including headers */
        AreaReference my_data_range = new AreaReference(new CellReference(0, 0), new CellReference(4, 2));
        
        /* Set Range to the Table */
        cttable.setRef(my_data_range.formatAsString());
        cttable.setDisplayName("TableName");      /* this is the display name of the table */
        cttable.setName("Test");    /* This maps to "displayName" attribute in <table>, OOXML */
        cttable.setId(1L); //id attribute against table as long value
              
        CTTableColumns columns = cttable.addNewTableColumns();
        columns.setCount(3L); //define number of columns

         /* Define Header Information for the Table */
        for (int i = 0; i < 3; i++)
        {
            CTTableColumn column = columns.addNewTableColumn();
            column.setName("Column" + i);
            column.setId(i+1);            
        }
        
//        sh.protectSheet("1");


        /* Add remaining Table Data */
        for (int i = 0; i <= 4; i++) //we have to populate 4 rows
        {
            /* Create a Row */
            XSSFRow row = sh.createRow(i);
            for (int j = 0; j < 3; j++) //Three columns in each row
            {
                XSSFCell localXSSFCell = row.createCell(j);
                if (i == 0) {

                    localXSSFCell.setCellValue("Heading" + j);
                    localXSSFCell.getCellStyle().setLocked(true);

                } else {

                    localXSSFCell.setCellValue(i + j);
                    if (j == 0) {
                        localXSSFCell.getCellStyle().setLocked(true);

                    } else {
                        localXSSFCell.getCellStyle().setLocked(false);
                    }
                }

                if (i == 1) {
                    sh.setColumnHidden(i, true);
                }
            }
        }
          
        sh.createFreezePane(0, 1);
        sh.lockFormatColumns();
        
            try{
        /* Write output as File */
        FileOutputStream fileOut = new FileOutputStream(EXPORTFILE);
        wb.write(fileOut);
        fileOut.close();
                
//                    Runtime.getRuntime().exec("cmd /c start " + path) ;
                }
            catch(IOException e){
                e.printStackTrace();
            }
    }
}
