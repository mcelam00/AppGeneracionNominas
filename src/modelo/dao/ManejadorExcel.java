package modelo.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hpsf.Date;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sun.webkit.Utilities;

import modelo.Categorias;
import modelo.Empresas;
import modelo.Trabajadorbbdd;

public class ManejadorExcel {

	
	private String RUTA_EXCEL = "./resources/SistemasInformacionII.xlsx"; 
	private int HOJA_1 = 0;
	private int HOJA_2 = 1;
	private int HOJA_3 = 2;
	
	public void cargarHojaExcel() {
	
        ArrayList<Trabajadorbbdd> trabajadores = new ArrayList<Trabajadorbbdd>();
        DataFormatter formatter = new DataFormatter();

		try {
			FileInputStream libro = new FileInputStream(RUTA_EXCEL);
			XSSFWorkbook workbook = new XSSFWorkbook(libro);
	        XSSFSheet sheet = workbook.getSheetAt(HOJA_3);	
	        
	        for (Row row : sheet) {
	        	
	        	if(!(row.getRowNum() == 0)) {
	        		

		        	Trabajadorbbdd trabajador = new Trabajadorbbdd();
		        	Categorias categoria = new Categorias();
		        	Empresas empresa = new Empresas();
		        		 
		        	//Mas adelante meter todos los atributos de trabajador 
		        	empresa.setNombre(formatter.formatCellValue(row.getCell(0)));
		        		        
		        	trabajador.setApellido1(formatter.formatCellValue(row.getCell(4)));
		        	trabajador.setApellido2(formatter.formatCellValue(row.getCell(5)));
		        	trabajador.setNombre(formatter.formatCellValue(row.getCell(6)));
		        	
		        	//trabajador.setNifnie(row.getCell(7).getStringCellValue());
		        	trabajador.setNifnie(formatter.formatCellValue(row.getCell(7)));
	
					//trabajador.setCodigoCuenta(row.getCell(9).getStringCellValue());
		        	trabajador.setCodigoCuenta(formatter.formatCellValue(row.getCell(9)));
	
					trabajador.setIban(formatter.formatCellValue(row.getCell(10)));
		        	trabajador.setEmpresas(empresa);
					
		        	trabajadores.add(trabajador);
	      
	        	}
	        	
	    	}
	        workbook.close();
	        libro.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		sistemas20202021.Utilities.corregir(trabajadores);
       
	}
	

	
}
