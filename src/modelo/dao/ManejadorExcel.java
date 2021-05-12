package modelo.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hpsf.Date;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.sun.webkit.Utilities;

import modelo.Categorias;
import modelo.Empresas;
import modelo.Trabajadorbbdd;

public class ManejadorExcel {


	private String RUTA_EXCEL = "./resources/SistemasInformacionII.xlsx";
	private String RUTA_EXCEL_CORREGIDO = "./resources/Datos_Corregidos.xlsx";
	private int HOJA_1 = 0;
	private int HOJA_2 = 1;
	private int HOJA_3 = 2;
	private ArrayList<String> prorratasExtra;
	//private ArrayList<String> paisOrigenCC = new ArrayList<String>();
	
	private static LinkedHashMap<Integer, Double> brutoAnual_retencion; 
	private static HashMap<String, Double> descuentos;
    

	private HashMap<String, Double> categoria_salarioBase; 
	private HashMap<String, Double> categoria_complementos; 
	private static HashMap<Integer, Double> nTrienio_importeBruto; 



	/**
	 * Getters de los hashMaps
	 * @return
	 */


	public static HashMap<Integer, Double> getnTrienio_importeBruto() {
		return nTrienio_importeBruto;
	}
    public static LinkedHashMap<Integer, Double> getBrutoAnual_retencion() {
		return brutoAnual_retencion;
	}
	public static HashMap<String, Double> getDescuentos() {
		return descuentos;
	}

	
	
	
	/**
	 * Constructor de la clase
	 */
	
	public ManejadorExcel() {
		
		this.prorratasExtra = new ArrayList<String>();
		
		this.brutoAnual_retencion = new LinkedHashMap<Integer, Double>();
		this.descuentos = new HashMap<String, Double>();
		this.categoria_salarioBase = new HashMap<String, Double>();
		this.categoria_complementos = new HashMap<String, Double>();
		this.nTrienio_importeBruto = new HashMap<Integer, Double>();
		
		
	}
	

	
	
	
	public void cargarHojaExcel() {

		ArrayList<Trabajadorbbdd> trabajadores = new ArrayList<Trabajadorbbdd>();
		DataFormatter formatter = new DataFormatter();

		try {
			FileInputStream libro = new FileInputStream(RUTA_EXCEL);
			XSSFWorkbook workbook = new XSSFWorkbook(libro);
			
			XSSFSheet sheet3 = workbook.getSheetAt(HOJA_3);	
			XSSFSheet sheet2 = workbook.getSheetAt(HOJA_2);	
			XSSFSheet sheet1 = workbook.getSheetAt(HOJA_1);
			
			//LEER HOJA 2

			for (Row row : sheet2) {

				if(!(row.getRowNum() == 0)) {
				  
					brutoAnual_retencion.put((int)row.getCell(0).getNumericCellValue(), row.getCell(1).getNumericCellValue());   
					if(formatter.formatCellValue(row.getCell(5)) == "") {
						continue;
					}
					descuentos.put(row.getCell(5).getStringCellValue(), row.getCell(6).getNumericCellValue());
					
				}else{
					descuentos.put(row.getCell(5).getStringCellValue(), row.getCell(6).getNumericCellValue());
				}
				
				
				
			}
			
			
			//LEER HOJA 1
			
			for (Row row : sheet1) {

				if(!(row.getRowNum() == 0)) {
				
					if (!(formatter.formatCellValue(row.getCell(0)) == "")) {						
					categoria_salarioBase.put(formatter.formatCellValue(row.getCell(0)), row.getCell(1).getNumericCellValue());
					categoria_complementos.put(formatter.formatCellValue(row.getCell(0)), row.getCell(2).getNumericCellValue());
					}
					nTrienio_importeBruto.put((int)row.getCell(5).getNumericCellValue(), row.getCell(6).getNumericCellValue());
					
					
					
				}
			}

			
			//LEER HOJA 3
			
			
			for (Row row : sheet3) {

				if(!(row.getRowNum() == 0)) {


					Trabajadorbbdd trabajador = new Trabajadorbbdd();
					Categorias categoria = new Categorias();
					Empresas empresa = new Empresas();

					//Mas adelante meter todos los atributos de trabajador 
					empresa.setNombre(formatter.formatCellValue(row.getCell(0)));
					empresa.setCif(formatter.formatCellValue(row.getCell(1)));
					
					categoria.setNombreCategoria(formatter.formatCellValue(row.getCell(2)));

					if(formatter.formatCellValue(row.getCell(2)) != "") {
						categoria.setSalarioBaseCategoria(categoria_salarioBase.get(categoria.getNombreCategoria()));
						categoria.setComplementoCategoria(categoria_complementos.get(categoria.getNombreCategoria())); 
					}	
						
					trabajador.setFechaAlta(row.getCell(3).getDateCellValue());

					trabajador.setApellido1(formatter.formatCellValue(row.getCell(4)));
					trabajador.setApellido2(formatter.formatCellValue(row.getCell(5)));
					trabajador.setNombre(formatter.formatCellValue(row.getCell(6)));
					trabajador.setIdTrabajador(row.getRowNum()); //Ponemos el numero de fila que tiene en la hoja excel para volcarlo al XML

					//trabajador.setNifnie(row.getCell(7).getStringCellValue());
					trabajador.setNifnie(formatter.formatCellValue(row.getCell(7)));
					trabajador.setProrrataExtra(formatter.formatCellValue(row.getCell(8)).compareTo("SI")==0 ? true : false);
					prorratasExtra.add(formatter.formatCellValue(row.getCell(8)));

					//trabajador.setCodigoCuenta(row.getCell(9).getStringCellValue());
					trabajador.setCodigoCuenta(formatter.formatCellValue(row.getCell(9)));

					trabajador.setIban(formatter.formatCellValue(row.getCell(10))); //Metemos el nombre del pais para completarlo luego
					//paisOrigenCC.add(formatter.formatCellValue(row.getCell(10)));
					trabajador.setEmpresas(empresa);
					trabajador.setCategorias(categoria); //dejamos guardada la categoria con sus valores al trabajador

					trabajadores.add(trabajador);

				}

			}
			

			workbook.close();
			libro.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		sistemas20202021.Utilities.corregir(trabajadores);

	}
	
	
	

	
	

	public void crearExcelCorregido(ArrayList<Trabajadorbbdd> arrayTrabajadores) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Datos corregidos");
		int nFila = 0;
		int p = 0;
		int nColumna = 0;
				
		Row filaNombres = sheet.createRow(nFila);
				Cell cell0n = filaNombres.createCell(nColumna);
				cell0n.setCellValue("Nombre empresa");
				sheet.autoSizeColumn(0);
				Cell cell1n = filaNombres.createCell(++nColumna);
				cell1n.setCellValue("Cif empresa");
				sheet.autoSizeColumn(1);
				Cell cell2n = filaNombres.createCell(++nColumna);
				cell2n.setCellValue("Categoria");
				sheet.autoSizeColumn(2);
				Cell cell3n = filaNombres.createCell(++nColumna);
				cell3n.setCellValue("FechaAltaEmpresa");
				sheet.autoSizeColumn(3);
				Cell cell4n = filaNombres.createCell(++nColumna);
				cell4n.setCellValue("Apellido1");
				sheet.autoSizeColumn(4);
				Cell cell5n = filaNombres.createCell(++nColumna);
				cell5n.setCellValue("Apellido2");
				sheet.autoSizeColumn(5);
				Cell cell6n = filaNombres.createCell(++nColumna);
				cell6n.setCellValue("Nombre");
				sheet.autoSizeColumn(6);
				Cell cell7n = filaNombres.createCell(++nColumna);
				cell7n.setCellValue("NIF/NIE");
				sheet.autoSizeColumn(7);
				Cell cell8n = filaNombres.createCell(++nColumna);
				cell8n.setCellValue("ProrrataExtra");
				sheet.autoSizeColumn(8);
				Cell cell9n = filaNombres.createCell(++nColumna);
				cell9n.setCellValue("CodigoCuenta");
				sheet.autoSizeColumn(9);
				Cell cell10n = filaNombres.createCell(++nColumna);
				cell10n.setCellValue("Pais Origen Cuenta Bancaria");
				sheet.autoSizeColumn(10);
				Cell cell11n = filaNombres.createCell(++nColumna);
				cell11n.setCellValue("IBAN");
				sheet.autoSizeColumn(11);
				Cell cell12n = filaNombres.createCell(++nColumna);
				cell12n.setCellValue("Email");
				sheet.autoSizeColumn(12);

		for (Trabajadorbbdd t : arrayTrabajadores) {
			
			if (t.getNombre() != "") {
				Row fila = sheet.createRow(++nFila);

				nColumna = -1;

				Cell cell0 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell0.setCellValue(t.getEmpresas().getNombre());
				Cell cell1 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell1.setCellValue(t.getEmpresas().getCif());
				Cell cell2 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell2.setCellValue(t.getCategorias().getNombreCategoria());
				Cell cell3 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell3.setCellValue(t.getFechaAlta());
				Cell cell4 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell4.setCellValue(t.getApellido1());
				Cell cell5 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell5.setCellValue(t.getApellido2());
				Cell cell6 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell6.setCellValue(t.getNombre());
				Cell cell7 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell7.setCellValue(t.getNifnie());
				Cell cell8 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell8.setCellValue(prorratasExtra.get(p++));
				Cell cell9 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell9.setCellValue(t.getCodigoCuenta());
				Cell cell10 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell10.setCellValue(t.getIban().substring(0,2));
				Cell cell11 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell11.setCellValue(t.getIban());
				Cell cell12 = fila.createCell(++nColumna);
				sheet.autoSizeColumn(nColumna);
				cell12.setCellValue(t.getEmail());


			}else {
				Row fila = sheet.createRow(++nFila);//para las lineas en blanco
				p++;
			}

		}


		try {

			FileOutputStream output = new FileOutputStream(RUTA_EXCEL_CORREGIDO);
			workbook.write(output);
			output.close();
			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	
	public void corregirExcelExistente(ArrayList<Trabajadorbbdd> arrayTrabajadores) {

		int i = 0;
		
		try {
			FileInputStream inputStream = new FileInputStream(RUTA_EXCEL);
			XSSFWorkbook libro = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = libro.getSheetAt(HOJA_3);	

			for (Row row : sheet) {

				if(!(row.getRowNum() == 0)) {

									
					Cell cell0 = row.createCell(7);
					cell0.setCellValue(arrayTrabajadores.get(i).getNifnie());
					
					Cell cell1 = row.createCell(9);
					cell1.setCellValue(arrayTrabajadores.get(i).getCodigoCuenta());
					
					Cell cell2 = row.createCell(11);
					cell2.setCellValue(arrayTrabajadores.get(i).getIban());
					
					Cell cell3 = row.createCell(12);
					cell3.setCellValue(arrayTrabajadores.get(i).getEmail());
				
					i++;
					
				}

			}
				
			inputStream.close();
			 
	        FileOutputStream outputStream = new FileOutputStream(RUTA_EXCEL);
	        libro.write(outputStream);
	        libro.close();
	        outputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	


	}
	
	
 
}
