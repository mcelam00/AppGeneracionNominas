package sistemas20202021;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.sun.glass.ui.Size;

import modelo.Categorias;
import modelo.Empresas;
import modelo.HibernateUtil;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
import modelo.dao.CategoriasDAO;
import modelo.dao.EmpresasDAO;
import modelo.dao.ManejadorExcel;
import modelo.dao.ManejadorXML;
import modelo.dao.NominaDAO;
import modelo.dao.TrabajadorbbddDAO;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Utilities{

	private static ArrayList<Trabajadorbbdd> trabajadores; 
	private static ArrayList<Trabajadorbbdd> NIFErrores;
	private static ArrayList<Trabajadorbbdd> CCCErroneas;
	private static boolean mal = false;
	private static String fechaGeneracion;
	private static Nomina nomina;	//nomina que estamos calculando
	private static Nomina nominaExtra;
	private static int idNomina = 0;

	private static String RUTA_PDFs = "./resources/nominas";
	private static String RUTA_imagen = "./resources/imagenTecnoProject.png";


	public static ArrayList<Trabajadorbbdd> getTrabajadores() {
		return Utilities.trabajadores;
	}

	public static void corregir(ArrayList<Trabajadorbbdd> arrayTrabajadores){

		NIFErrores = new ArrayList<Trabajadorbbdd>();
		CCCErroneas = new ArrayList<Trabajadorbbdd>();

		trabajadores = arrayTrabajadores;

		componerArrayNIFErrores();
		ordenarArrayNIFErrores();

		for (Trabajadorbbdd trabajadorbbdd : arrayTrabajadores) {
			validarNifnie(trabajadorbbdd);
			verificarCCC(trabajadorbbdd);
			generarIBAN(trabajadorbbdd);
			generarEmail(trabajadorbbdd);
			calcularAntiguedad(trabajadorbbdd);
			//System.out.println(trabajadorbbdd.getSeHaceNomina());

			if (trabajadorbbdd.getSeHaceNomina() && trabajadorbbdd.getNombre() != "") { //si esta contratado en la empresa en la fecha introducida y no es una linea vacia en la excel

				componerNominaMensual(trabajadorbbdd);
				//System.out.println("a");

			}

		}

                 
		ManejadorXML x = new ManejadorXML();
		x.escribirErroresXML(NIFErrores);
		x.escribirErroresCCCXML(CCCErroneas);
                almacenarEnBBDD();

	}
	
        
        
            
        
        
        
//////////////////////////////////////CALCULOS/////////////////////////////////////////////////////////////////////

	private static void calcularAntiguedad(Trabajadorbbdd t){

		if (t.getFechaAlta()!=null) {
			String date = "01/"+fechaGeneracion;


			String[] d1 = date.split("/");
			int md1 = Integer.valueOf(d1[1]);//mes fecha introducida
			int yd1 = Integer.valueOf(d1[2]);//año fecha introducida

			Format formatter = new SimpleDateFormat("dd-MM-yyyy");
			String s = formatter.format(t.getFechaAlta());

			String[] d2 = s.split("-");
			int md2 = Integer.valueOf(d2[1]);//mes fecha alta
			int yd2 = Integer.valueOf(d2[2]);//año fecha alta

			if (((yd2 > yd1)) || ((md2 > md1) && (yd2 >= yd1))) {
				t.setSeHaceNomina(false);
				return;
			}

			int years = yd1 - yd2;
			float trienios = years/3;


			if(years % 3 == 0) { //el año es multiplo de los trienios y estoy en el año que hago el trienio

				//puedo haberlo hecho o no
				if(md1 > md2) { 
					//pasé el mes y ya lo estoy cobrando

				}else { //aún no lo pasé y cobro el anterior
					trienios = trienios -1;

				}

			}else{
				//no me toca hacer ningun trienio este año, o lo hice ya, o lo haré en los años futuros

			}

			if(trienios < 0) { //la resta de years sale menor a 3, por tanto dividendo < divisor y el modulo salta 0 (no hemos cumplido tres años aún en la empresa y puede salir -1 si el mes es menor o igual al de cumplimiento del trienio)
				trienios = 0;
			}



			//System.out.println(t.getNombre()+" "+t.getApellido1() + "--"+ trienios);
			nomina = new Nomina(idNomina, t, md1, yd1, (int)trienios);
			idNomina++;
			HashSet<Nomina> nominas = new HashSet<Nomina>();
			//nominas.add(nomina);

			t.setNominas(nominas);
		}

	}

	public static void setEntrada(String entrada) {
		fechaGeneracion = entrada;
	}

	private static int calcularTrienioPorMes(int md1, int anioGeneracion , String fechaAlta){//devuelve el trienio de cada mes del año


		int yd1 = anioGeneracion;//año introducido por consola


		String[] d2 = fechaAlta.split("-");
		int md2 = Integer.valueOf(d2[1]);//mes fecha alta
		int yd2 = Integer.valueOf(d2[2]);//año fecha alta

		int years = yd1 - yd2;
		float trienios = years/3;


		if(years % 3 == 0) { //el año es multiplo de los trienios y estoy en el año que hago el trienio

			//puedo haberlo hecho o no
			if(md1 > md2) { 
				//pasé el mes y ya lo estoy cobrando

			}else { //aún no lo pasé y cobro el anterior
				trienios = trienios -1;

			}

		}else{
			//no me toca hacer ningun trienio este año, o lo hice ya, o lo haré en los años futuros

		}

		if(trienios < 0) { //la resta de years sale menor a 3, por tanto dividendo < divisor y el modulo salta 0 (no hemos cumplido tres años aún en la empresa y puede salir -1 si el mes es menor o igual al de cumplimiento del trienio)
			trienios = 0;
		}

		return (int)trienios;
	}

	private static Double calcularAntiguedadAnioGeneracion(int anioGeneracion, Date fechaAlta) {


		double brutoAnual = 0;
		double importeTrienios = 0;
		int md1 = 1;
		//int year = nomina.getAnio();
		int year = anioGeneracion;
		//Date fechaAlta = t.getFechaAlta();

		Format formatter = new SimpleDateFormat("dd-MM-yyyy");
		String f = formatter.format(fechaAlta);

		if (year == Integer.valueOf(f.substring(6))) {//para los que no han estado un año entero

		}


		for (int i = 0; i < 12; i++) {

			int x = calcularTrienioPorMes(md1, year, f);
			//System.out.println(x);
			if (x!=0) {
				importeTrienios += ManejadorExcel.getnTrienio_importeBruto().get(x);
			}

			md1++;

		}
		/*
			System.out.println(t.getNombre()+" "+t.getApellido1() + "--"+ importeTrienios);
			brutoAnual = t.getCategorias().getSalarioBaseCategoria()+t.getCategorias().getComplementoCategoria()+importeTrienios; 
			System.out.println(t.getNombre()+" "+t.getApellido1() + "--"+ brutoAnual);
			//nomina.setImporteTrienios(ManejadorExcel.getnTrienio_importeBruto().get(nomina.getNumeroTrienios()));
			//salario base + complementos + trienios (si los tiene)
			//nomina.setBrutoAnual(t.getCategorias().getSalarioBaseCategoria() + t.getCategorias().getComplementoCategoria() + nomina.getImporteTrienios());
		 */

		return importeTrienios;

	}

	private static void componerNominaMensual(Trabajadorbbdd trabajador) {


		String fechaGen = "01/"+fechaGeneracion;
		String[] d1 = fechaGen.split("/");
		int mesGeneracion = Integer.valueOf(d1[1]);//mes fecha introducida
		int anioGeneracion = Integer.valueOf(d1[2]);//año fecha introducida

		Date fechaAlta = trabajador.getFechaAlta();
		Format formatter = new SimpleDateFormat("dd-MM-yyyy");
		String fechaAltaTrab = formatter.format(fechaAlta);





		Double salarioBaseMes = (trabajador.getCategorias().getSalarioBaseCategoria())/14; //el salario base segun la categoria lo tenemos guardado en cada uno ya desde manejaroExcel
		nomina.setImporteSalarioMes(salarioBaseMes);
		Double complementoMes = (trabajador.getCategorias().getComplementoCategoria())/14;
		nomina.setImporteComplementoMes(complementoMes);

		int numeroTrienios = nomina.getNumeroTrienios(); //si es 0 porque no ha hecho el primero, para que el get al buscar 0 en la tabla de trienios no falle lo ponemos en 0.0 (dejamos el valor de inicializacion en este caso)

		Double importeTrienios = 0.0;

		if(numeroTrienios != 0) {
			importeTrienios = ManejadorExcel.getnTrienio_importeBruto().get(numeroTrienios); //dinero a cobrar justo el mes que se solicita
		}
		nomina.setImporteTrienios(importeTrienios);





		//CALCULAMOS SIEMPRE COMO SI ESTUVIERA PRORRATEADA LA NOMINA
		//Si la nomina la tiene prorrateada va cobrando 1/6 de la siguiente nomina extra 


		if(mesGeneracion >= 1 && mesGeneracion <= 5) { //Nomina a generar entre enero y mayo incluido
			//le corresponde extra de Junio

			Double importeTrieniosExtra = recalculoTrieniosExtra(6,anioGeneracion, fechaAltaTrab);
			Double importeBrutoExtra = salarioBaseMes + complementoMes + importeTrieniosExtra;

			//hago 1/6 y lo grabo en el atributo como el valor del prorrateo correspondiente para la nomina que nos piden si cae en este tramo

			nomina.setValorProrrateo(importeBrutoExtra/6);


		}else if(mesGeneracion >= 6 && mesGeneracion <= 11) {
			//le corresponde la extra de Diciembre


			Double importeTrieniosExtra = recalculoTrieniosExtra(12,anioGeneracion, fechaAltaTrab);
			Double importeBrutoExtra = salarioBaseMes + complementoMes + importeTrieniosExtra;

			//hago 1/6 y lo grabo en el atributo como el valor del prorrateo correspondiente para la nomina que nos piden si cae en este tramo

			nomina.setValorProrrateo(importeBrutoExtra/6);


		}else {
			//ESPECIAL Es el caso de Diciembre que le corresponde la extra de Junio del sisguiente año

			Double importeTrieniosExtra = recalculoTrieniosExtra(6,anioGeneracion+1, fechaAltaTrab);
			Double importeBrutoExtra = salarioBaseMes + complementoMes + importeTrieniosExtra;

			//hago 1/6 y lo grabo en el atributo como el valor del prorrateo correspondiente para la nomina que nos piden si cae en este tramo

			nomina.setValorProrrateo(importeBrutoExtra/6);


		}



		//DISTINGUIMOS SI ESTA PRORRATEADA O NO
		Double brutoMes = 0.0;

		if(!trabajador.getProrrataExtra()) {
			//como asumimos que siempre se calcula como prorrateada, para la base se pone el bruto igual que si estuviera porque hay que pasarle por adelantado sobre ella los descuentos para sisarle lo que queda a deber por no pagarlos en la extra
			nomina.setBaseEmpresario(salarioBaseMes + complementoMes + importeTrienios + nomina.getValorProrrateo());
			nomina.setValorProrrateo(0.0); //para calcular el bruto que se pinta en la nomina que no incluye el prorrateo
			brutoMes = salarioBaseMes + complementoMes + importeTrienios + nomina.getValorProrrateo(); 
			nomina.setBrutoNomina(brutoMes);

			Double valorARestarDescuentos = calcularDescuentosTrabajador(nomina.getBaseEmpresario());
			Double IRPF = calcularIRPF(brutoMes, nomina, fechaAlta, anioGeneracion, trabajador);


			Double liquidoNomina = brutoMes-valorARestarDescuentos-IRPF;
			nomina.setLiquidoNomina(liquidoNomina);

			Double totalEmpresario = calcularDescuentosEmpresario(nomina.getBaseEmpresario());

			nomina.setCosteTotalEmpresario(nomina.getBrutoNomina()+totalEmpresario); //bruto nomina mas los descuentos del empresario sumados
			//es la suma desc empresario + el bruto de la nomina 

		}else {


			brutoMes = salarioBaseMes + complementoMes + importeTrienios + nomina.getValorProrrateo();
			nomina.setBrutoNomina(brutoMes);

			nomina.setBaseEmpresario(brutoMes); //el bruto incluyendo el prorrateo adelantado es la base para este caso (son lo mismo)

			Double valorARestarDescuentos = calcularDescuentosTrabajador(nomina.getBaseEmpresario());
			Double IRPF = calcularIRPF(brutoMes, nomina, fechaAlta, anioGeneracion, trabajador);


			Double liquidoNomina = brutoMes-valorARestarDescuentos-IRPF;
			nomina.setLiquidoNomina(liquidoNomina);

			Double totalEmpresario = calcularDescuentosEmpresario(nomina.getBaseEmpresario());
			nomina.setCosteTotalEmpresario(nomina.getBrutoNomina()+totalEmpresario); //bruto nomina mas los descuentos del empresario sumados
			//es la suma desc empresario + el bruto de la nomina 
		}





		//si prorrateo (o no prorrateo pero el mes no es de extra) grabo una
                nomina.setTrabajadorbbdd(trabajador); //le ponemos el trabajador a la nomina normal
                trabajador.getNominas().add(nomina);

		sacarPdf(trabajador, nomina, false);




		if(trabajador.getProrrataExtra() == false && (mesGeneracion == 6 || mesGeneracion == 12)) { //no tiene prorrateo y estoy en un mes de EXTRA, a parte de la nomina basica saco la extra tambien del trabajador
			//si no prorrateo y el mes es de extra tiene dos nominas
			nominaExtra = new Nomina();

			nominaExtra.setAnio(nomina.getAnio());
			nominaExtra.setMes(nomina.getMes());
			nominaExtra.setValorProrrateo(0.0);

			/**Parte descuentos trabajador nomina Extra**/

			nominaExtra.setSeguridadSocialTrabajador(nomina.getSeguridadSocialTrabajador());
			nominaExtra.setImporteSeguridadSocialTrabajador(0.0);
			nominaExtra.setDesempleoTrabajador(nomina.getDesempleoTrabajador());
			nominaExtra.setImporteDesempleoTrabajador(0.0);
			nominaExtra.setFormacionTrabajador(nomina.getFormacionTrabajador());
			nominaExtra.setImporteFormacionTrabajador(0.0);
			nominaExtra.setBaseEmpresario(0.0);

			/**Parte descuentos Empresario nomina Extra**/

			nominaExtra.setSeguridadSocialEmpresario(nomina.getSeguridadSocialEmpresario());
			nominaExtra.setImporteSeguridadSocialEmpresario(0.0);
			nominaExtra.setFogasaempresario(nomina.getFogasaempresario());
			nominaExtra.setImporteFogasaempresario(0.0);
			nominaExtra.setDesempleoEmpresario(nomina.getDesempleoEmpresario());
			nominaExtra.setImporteDesempleoEmpresario(0.0);
			nominaExtra.setFormacionEmpresario(nomina.getFormacionEmpresario());
			nominaExtra.setImporteFormacionEmpresario(0.0);
			nominaExtra.setAccidentesTrabajoEmpresario(nomina.getAccidentesTrabajoEmpresario());
			nominaExtra.setImporteAccidentesTrabajoEmpresario(0.0);



			nominaExtra.setImporteSalarioMes(salarioBaseMes);
			nominaExtra.setImporteComplementoMes(complementoMes);
			Double importeTrieniosExtra;

			if(mesGeneracion == 6) {
				//es la extra de junio

				importeTrieniosExtra = recalculoTrieniosExtra(6, anioGeneracion, fechaAltaTrab);
				nominaExtra.setImporteTrienios(importeTrieniosExtra);


			}else {
				//es la extra de diciembre

				importeTrieniosExtra = recalculoTrieniosExtra(12, anioGeneracion, fechaAltaTrab);
				nominaExtra.setImporteTrienios(importeTrieniosExtra);

			}

			Double importeBrutoExtra = salarioBaseMes + complementoMes + importeTrieniosExtra;
			nominaExtra.setBrutoNomina(importeBrutoExtra);

			Double IRPFExtra = calcularIRPF(brutoMes, nominaExtra, fechaAlta, anioGeneracion, trabajador);

			Double importeLiquidoExtra = importeBrutoExtra - IRPFExtra;
			nominaExtra.setLiquidoNomina(importeLiquidoExtra);

			nominaExtra.setCosteTotalEmpresario(nominaExtra.getBrutoNomina()+ 0.0); //es la suma desc empresario que es 0 para la extra + el bruto de la nomina 

			//la añado al set y le ponemos el trabajador
                        nominaExtra.setTrabajadorbbdd(trabajador);
			trabajador.getNominas().add(nominaExtra);
			sacarPdf(trabajador, nominaExtra, true);

			//System.out.println("");
		}


		/**Pintamos los resultados por consola**/

		System.out.println("---------------------");
		System.out.println("-"+trabajador.getEmpresas().getNombre() + " (" + trabajador.getEmpresas().getCif()+")");
		System.out.println("-"+trabajador.getNombre()+" "+trabajador.getApellido1()+" "+ trabajador.getApellido2()+ " (" +trabajador.getNifnie()+")");
		System.out.println("-Categoría: "+ trabajador.getCategorias().getNombreCategoria());
		System.out.println("-Fecha de alta: "+trabajador.getFechaAlta());
		System.out.println("-IBAN: "+ trabajador.getIban());
		System.out.println("-Bruto anual: "+nomina.getBrutoAnual());
		System.out.println("-Fecha de la nómina: "+nomina.getMes()+"/"+nomina.getAnio());
		System.out.println("-Importes a percibir:");
		System.out.println("\t-Salario base mes: "+nomina.getImporteSalarioMes());
		System.out.println("\t-Prorrateo mes: "+nomina.getValorProrrateo());
		System.out.println("\t-Complemento mes: "+nomina.getImporteComplementoMes());
		System.out.println("\t-Antigüedad mes: "+nomina.getImporteTrienios());
		System.out.println("-Descuentos trabajador:");
		System.out.println("\t-Contingencias generales: "+nomina.getSeguridadSocialTrabajador()+"% de "+nomina.getBaseEmpresario()+"\t"+nomina.getImporteSeguridadSocialTrabajador());
		System.out.println("\t-Desempleo: "+nomina.getDesempleoTrabajador()+"% de "+nomina.getBaseEmpresario()+"\t"+nomina.getImporteDesempleoTrabajador());
		System.out.println("\t-Cuota formación: "+nomina.getFormacionTrabajador()+"% de "+nomina.getBaseEmpresario()+"\t"+nomina.getImporteFormacionTrabajador());
		System.out.println("\t-IRPF: "+nomina.getIrpf()+"% de "+nomina.getBrutoNomina()+"\t"+nomina.getImporteIrpf());
		System.out.println("-Total ingresos: "+nomina.getBrutoNomina());
		System.out.println("-Total deducciones: "+(nomina.getImporteSeguridadSocialTrabajador()+nomina.getImporteDesempleoTrabajador()+nomina.getImporteFormacionTrabajador()+nomina.getImporteIrpf()));
		System.out.println("-Liquido a percibir: "+nomina.getLiquidoNomina());
		System.out.println("-Pagos empresario:");
		System.out.println("\tBASE empresario: "+nomina.getBaseEmpresario());
		System.out.println("\tContingencias generales: "+nomina.getSeguridadSocialEmpresario()+"%\t"+nomina.getImporteSeguridadSocialEmpresario());
		System.out.println("\tDesempleo: "+nomina.getDesempleoEmpresario()+"%\t"+nomina.getImporteDesempleoEmpresario());
		System.out.println("\tFormacion: "+nomina.getFormacionEmpresario()+"%\t"+nomina.getImporteFormacionEmpresario());
		System.out.println("\tAccidentes: "+nomina.getAccidentesTrabajoEmpresario()+"%\t"+nomina.getImporteAccidentesTrabajoEmpresario());
		System.out.println("\tFOGASA: "+nomina.getFogasaempresario()+"%\t"+nomina.getImporteFogasaempresario());
		System.out.println("\tTotal empresario: "+(nomina.getImporteSeguridadSocialEmpresario()+nomina.getImporteDesempleoEmpresario()+nomina.getImporteFormacionEmpresario()+nomina.getImporteAccidentesTrabajoEmpresario()+nomina.getImporteFogasaempresario()));
		System.out.println("-Coste total trabajador:"+nomina.getCosteTotalEmpresario());//total que le cuesta el trabajador al empresario


		/**si el trabajador tiene 2 nominas (no prorrateada)**/

		if (trabajador.getNominas().size() == 2) {

			System.out.println("---------------------");
			System.out.println("-"+trabajador.getEmpresas().getNombre() + " (" + trabajador.getEmpresas().getCif()+")");
			System.out.println("-"+trabajador.getNombre()+" "+trabajador.getApellido1()+" "+ trabajador.getApellido2()+ " (" +trabajador.getNifnie()+")");
			System.out.println("-Categoría: "+ trabajador.getCategorias().getNombreCategoria());
			System.out.println("-Fecha de alta: "+trabajador.getFechaAlta());
			System.out.println("-IBAN: "+ trabajador.getIban());
			System.out.println("-Bruto anual: "+nominaExtra.getBrutoAnual());
			System.out.println("-Fecha de la nómina: "+nominaExtra.getMes()+"/"+nominaExtra.getAnio()+" (EXTRA)");
			System.out.println("-Importes a percibir:");
			System.out.println("\t-Salario base mes: "+nominaExtra.getImporteSalarioMes());
			System.out.println("\t-Prorrateo mes: "+nominaExtra.getValorProrrateo());
			System.out.println("\t-Complemento mes: "+nominaExtra.getImporteComplementoMes());
			System.out.println("\t-Antigüedad mes: "+nominaExtra.getImporteTrienios());
			System.out.println("-Descuentos trabajador:");
			System.out.println("\t-Contingencias generales: "+nominaExtra.getSeguridadSocialTrabajador()+"% de "+nominaExtra.getBaseEmpresario()+"\t"+nominaExtra.getImporteSeguridadSocialTrabajador());
			System.out.println("\t-Desempleo: "+nominaExtra.getDesempleoTrabajador()+"% de "+nominaExtra.getBaseEmpresario()+"\t"+nominaExtra.getImporteDesempleoTrabajador());
			System.out.println("\t-Cuota formación: "+nominaExtra.getFormacionTrabajador()+"% de "+nominaExtra.getBaseEmpresario()+"\t"+nominaExtra.getImporteFormacionTrabajador());
			System.out.println("\t-IRPF: "+nominaExtra.getIrpf()+"% de "+nominaExtra.getBrutoNomina()+"\t"+nominaExtra.getImporteIrpf());
			System.out.println("-Total ingresos: "+nominaExtra.getBrutoNomina());
			System.out.println("-Total deducciones: "+(nominaExtra.getImporteSeguridadSocialTrabajador()+nominaExtra.getImporteDesempleoTrabajador()+nominaExtra.getImporteFormacionTrabajador()+nominaExtra.getImporteIrpf()));
			System.out.println("-Liquido a percibir: "+nominaExtra.getLiquidoNomina());
			System.out.println("-Pagos empresario:");
			System.out.println("\tBASE empresario: "+nominaExtra.getBaseEmpresario());
			System.out.println("\tContingencias generales: "+nominaExtra.getSeguridadSocialEmpresario()+"%\t"+nominaExtra.getImporteSeguridadSocialEmpresario());
			System.out.println("\tDesempleo: "+nominaExtra.getDesempleoEmpresario()+"%\t"+nominaExtra.getImporteDesempleoEmpresario());
			System.out.println("\tFormacion: "+nominaExtra.getFormacionEmpresario()+"%\t"+nominaExtra.getImporteFormacionEmpresario());
			System.out.println("\tAccidentes: "+nominaExtra.getAccidentesTrabajoEmpresario()+"%\t"+nominaExtra.getImporteAccidentesTrabajoEmpresario());
			System.out.println("\tFOGASA: "+nominaExtra.getFogasaempresario()+"%\t"+nominaExtra.getImporteFogasaempresario());
			System.out.println("\tTotal empresario: "+(nominaExtra.getImporteSeguridadSocialEmpresario()+nominaExtra.getImporteDesempleoEmpresario()+nominaExtra.getImporteFormacionEmpresario()+nominaExtra.getImporteAccidentesTrabajoEmpresario()+nominaExtra.getImporteFogasaempresario()));
			System.out.println("-Coste total trabajador:"+nominaExtra.getCosteTotalEmpresario());//total que le cuesta el trabajador al empresario


		}


               
                
	}
      
        
//////////////////////////////////////SACARPDF/////////////////////////////////////////////////////////////////////

	private static void sacarPdf(Trabajadorbbdd trabajador, Nomina nomina, boolean esExtra) {

		if (!esExtra) {//Para la normal
			PdfWriter writer;
			String ruta = RUTA_PDFs+"/"+trabajador.getNifnie()+trabajador.getNombre()+trabajador.getApellido1()+trabajador.getApellido2()+nomina.getMes()+nomina.getAnio()+".pdf";
			try {
				writer = new PdfWriter(ruta);

				PdfDocument pdfDoc = new PdfDocument(writer);
				Document doc = new Document(pdfDoc, PageSize.LETTER);


				Table tabla1 = new Table(2);
				tabla1.setWidth(500);


				Cell cell1 = new Cell();
				cell1.setBorder(new SolidBorder(1));
				cell1.setWidth(250);
				cell1.setTextAlignment(TextAlignment.CENTER);
				cell1.add(new Paragraph(trabajador.getEmpresas().getNombre()));//nombre empresa
				cell1.add(new Paragraph("CIF: "+trabajador.getEmpresas().getCif()));//cif empresa
				cell1.add(new Paragraph("Avenida de la facultad - 6"));
				cell1.add(new Paragraph("24001 León"));
				tabla1.addCell(cell1);

				Cell cell2 = new Cell();
				cell2.setBorder(Border.NO_BORDER);
				cell2.setPadding(10);
				cell2.setTextAlignment(TextAlignment.RIGHT);
				cell2.add(new Paragraph("IBAN: "+ trabajador.getIban()));
				cell2.add(new Paragraph("Bruto anual: "+nomina.getBrutoAnual()));///////////*********************************
				cell2.add(new Paragraph("Categoría: "+trabajador.getCategorias().getNombreCategoria()));

				Format formatter = new SimpleDateFormat("dd/MM/yyyy");
				String fechaAltaTrab = formatter.format(trabajador.getFechaAlta());

				cell2.add(new Paragraph("Fecha de alta: "+fechaAltaTrab));
				tabla1.addCell(cell2);


				Table tabla2 = new Table(2);
				tabla2.setWidth(500);

				Image img = new Image(ImageDataFactory.create(RUTA_imagen));
				img.setBorder(Border.NO_BORDER);
				img.setPadding(10);

				Cell cell3 = new Cell();
				cell3.setBorder(Border.NO_BORDER);
				cell3.setTextAlignment(TextAlignment.RIGHT);
				cell3.setPaddingLeft(15);
				cell3.setPaddingTop(20);
				cell3.setWidth(250);
				cell3.add(img);
				tabla2.addCell(cell3);

				Cell cell4 = new Cell();
				cell4.setBorder(new SolidBorder(1));
				cell4.setWidth(250);


				Table tabla2a = new Table(2);

				Cell cell4izda = new Cell();
				cell4izda.setTextAlignment(TextAlignment.LEFT);
				cell4izda.setBorder(Border.NO_BORDER);
				cell4izda.add(new Paragraph("Destinatario:"));

				Cell cell4dcha = new Cell();
				cell4dcha.setTextAlignment(TextAlignment.RIGHT);
				cell4dcha.setBorder(Border.NO_BORDER);
				cell4dcha.add(new Paragraph(trabajador.getNombre() + " " + trabajador.getApellido1()+ " " + trabajador.getApellido2()));//nombre completo trabajador
				cell4dcha.add(new Paragraph("DNI: "+trabajador.getNifnie()));//dni trabajador
				cell4dcha.add(new Paragraph("Avenida de la facultad - 6"));
				cell4dcha.add(new Paragraph("24001 León"));


				tabla2a.addCell(cell4izda);
				tabla2a.addCell(cell4dcha);
				cell4.add(tabla2a);
				tabla2.addCell(cell4);


				Cell cell5 = new Cell();
				cell5.setTextAlignment(TextAlignment.CENTER);
				cell5.setPaddingTop(20);
				cell5.add(new Paragraph("Nómina: "+fechaAltaTrab));

				int dias;
				if (nomina.getMes() == 1 || nomina.getMes() == 3 ||nomina.getMes() == 5 ||nomina.getMes() == 7 ||nomina.getMes() == 8 ||nomina.getMes() == 10 ||nomina.getMes() == 12) {
					dias = 31;
				}else if (nomina.getMes() == 2) {
					dias = 28;

				}else{
					dias = 30;
				}


				Table tabla3 = new Table(5);
				tabla3.setTextAlignment(TextAlignment.CENTER);
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Conceptos")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("Cantidad")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("Imp. Unitario")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("Devengo")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("Deducción")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Salario base")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(dias+" días")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteSalarioMes()/dias))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteSalarioMes()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Prorrateo")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(dias+" días")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getValorProrrateo()/dias))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getValorProrrateo()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Complemento")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(dias+" días")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteComplementoMes()/dias))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteComplementoMes()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Antigüedad")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(nomina.getNumeroTrienios()+" Trienios")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteTrienios()/dias))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteTrienios()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Contingencias generales")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getSeguridadSocialTrabajador())+"% de "+String.format("%.2f",nomina.getBaseEmpresario()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteSeguridadSocialTrabajador()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Desempleo")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getDesempleoTrabajador())+"% de "+String.format("%.2f",nomina.getBaseEmpresario()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteDesempleoTrabajador()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Cuota formación")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getFormacionTrabajador())+"% de "+String.format("%.2f",nomina.getBaseEmpresario()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteFormacionTrabajador()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("IRPF")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getIrpf())+"% de "+String.format("%.2f",nomina.getBrutoNomina()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteIrpf()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Total deducciones")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",(nomina.getImporteSeguridadSocialTrabajador()+nomina.getImporteDesempleoTrabajador()+nomina.getImporteFormacionTrabajador()+nomina.getImporteIrpf())))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Total devengos")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getBrutoNomina()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Líquido a percibir")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getLiquidoNomina()))));


				//espacios
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));



				Table tabla4 = new Table(2);
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Cálculo empresario: BASE")));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getBaseEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingRight(230).add(new Paragraph("Contingencias comunes empresario "+String.format("%.2f",nomina.getSeguridadSocialEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteSeguridadSocialEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Desempleo "+String.format("%.2f",nomina.getDesempleoEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteDesempleoEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Formación "+String.format("%.2f",nomina.getFormacionEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteFormacionEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Accidentes de trabajo "+String.format("%.2f",nomina.getAccidentesTrabajoEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteAccidentesTrabajoEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("FOGASA "+String.format("%.2f",nomina.getFogasaempresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteFogasaempresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Total empresario ")));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",(nomina.getImporteFogasaempresario()+nomina.getImporteAccidentesTrabajoEmpresario()+nomina.getImporteFormacionEmpresario()+nomina.getImporteDesempleoEmpresario()+nomina.getImporteSeguridadSocialEmpresario())))));

				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(10).add(new Paragraph(" ")));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(10).add(new Paragraph(" ")));

				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("COSTE TOTOAL TRABAJADOR ")));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getCosteTotalEmpresario()))));


				doc.add(tabla1);
				doc.add(tabla2);
				doc.add(cell5);
				doc.add(tabla3);
				doc.add(tabla4);

				doc.close();

			} catch (FileNotFoundException | MalformedURLException e) {
				e.printStackTrace();
			}
		}else {//para la extra
			PdfWriter writer;
			String ruta = RUTA_PDFs+"/"+trabajador.getNifnie()+trabajador.getNombre()+trabajador.getApellido1()+trabajador.getApellido2()+nomina.getMes()+nomina.getAnio()+"EXTRA.pdf";
			try {
				writer = new PdfWriter(ruta);

				PdfDocument pdfDoc = new PdfDocument(writer);
				Document doc = new Document(pdfDoc, PageSize.LETTER);


				Table tabla1 = new Table(2);
				tabla1.setWidth(500);


				Cell cell1 = new Cell();
				cell1.setBorder(new SolidBorder(1));
				cell1.setWidth(250);
				cell1.setTextAlignment(TextAlignment.CENTER);
				cell1.add(new Paragraph(trabajador.getEmpresas().getNombre()));//nombre empresa
				cell1.add(new Paragraph("CIF: "+trabajador.getEmpresas().getCif()));//cif empresa
				cell1.add(new Paragraph("Avenida de la facultad - 6"));
				cell1.add(new Paragraph("24001 León"));
				tabla1.addCell(cell1);

				Cell cell2 = new Cell();
				cell2.setBorder(Border.NO_BORDER);
				cell2.setPadding(10);
				cell2.setTextAlignment(TextAlignment.RIGHT);
				cell2.add(new Paragraph("IBAN: "+ trabajador.getIban()));
				cell2.add(new Paragraph("Bruto anual: "+nomina.getBrutoAnual()));///////////*********************************
				cell2.add(new Paragraph("Categoría: "+trabajador.getCategorias().getNombreCategoria()));

				Format formatter = new SimpleDateFormat("dd/MM/yyyy");
				String fechaAltaTrab = formatter.format(trabajador.getFechaAlta());

				cell2.add(new Paragraph("Fecha de alta: "+fechaAltaTrab));
				tabla1.addCell(cell2);


				Table tabla2 = new Table(2);
				tabla2.setWidth(500);

				Image img = new Image(ImageDataFactory.create(RUTA_imagen));
				img.setBorder(Border.NO_BORDER);
				img.setPadding(10);

				Cell cell3 = new Cell();
				cell3.setBorder(Border.NO_BORDER);
				cell3.setTextAlignment(TextAlignment.RIGHT);
				cell3.setPaddingLeft(15);
				cell3.setPaddingTop(20);
				cell3.setWidth(250);
				cell3.add(img);
				tabla2.addCell(cell3);

				Cell cell4 = new Cell();
				cell4.setBorder(new SolidBorder(1));
				cell4.setWidth(250);


				Table tabla2a = new Table(2);

				Cell cell4izda = new Cell();
				cell4izda.setTextAlignment(TextAlignment.LEFT);
				cell4izda.setBorder(Border.NO_BORDER);
				cell4izda.add(new Paragraph("Destinatario:"));

				Cell cell4dcha = new Cell();
				cell4dcha.setTextAlignment(TextAlignment.RIGHT);
				cell4dcha.setBorder(Border.NO_BORDER);
				cell4dcha.add(new Paragraph(trabajador.getNombre() + " " + trabajador.getApellido1()+ " " + trabajador.getApellido2()));//nombre completo trabajador
				cell4dcha.add(new Paragraph("DNI: "+trabajador.getNifnie()));//dni trabajador
				cell4dcha.add(new Paragraph("Avenida de la facultad - 6"));
				cell4dcha.add(new Paragraph("24001 León"));


				tabla2a.addCell(cell4izda);
				tabla2a.addCell(cell4dcha);
				cell4.add(tabla2a);
				tabla2.addCell(cell4);


				Cell cell5 = new Cell();
				cell5.setTextAlignment(TextAlignment.CENTER);
				cell5.setPaddingTop(20);
				cell5.add(new Paragraph("Nómina: "+fechaAltaTrab));

				int dias;
				if (nomina.getMes() == 1 || nomina.getMes() == 3 ||nomina.getMes() == 5 ||nomina.getMes() == 7 ||nomina.getMes() == 8 ||nomina.getMes() == 10 ||nomina.getMes() == 12) {
					dias = 31;
				}else if (nomina.getMes() == 2) {
					dias = 28;

				}else{
					dias = 30;
				}


				Table tabla3 = new Table(5);
				tabla3.setTextAlignment(TextAlignment.CENTER);
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Conceptos")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("Cantidad")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("Imp. Unitario")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("Devengo")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("Deducción")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Salario base")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(dias+" días")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteSalarioMes()/dias))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteSalarioMes()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Prorrateo")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(dias+" días")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getValorProrrateo()/dias))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getValorProrrateo()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Complemento")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(dias+" días")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteComplementoMes()/dias))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteComplementoMes()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Antigüedad")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(nomina.getNumeroTrienios()+" Trienios")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteTrienios()/dias))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteTrienios()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Contingencias generales")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getSeguridadSocialTrabajador())+"% de "+String.format("%.2f",nomina.getBaseEmpresario()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteSeguridadSocialTrabajador()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Desempleo")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getDesempleoTrabajador())+"% de "+String.format("%.2f",nomina.getBaseEmpresario()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteDesempleoTrabajador()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Cuota formación")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getFormacionTrabajador())+"% de "+String.format("%.2f",nomina.getBaseEmpresario()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteFormacionTrabajador()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("IRPF")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getIrpf())+"% de "+String.format("%.2f",nomina.getBrutoNomina()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getImporteIrpf()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Total deducciones")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",(nomina.getImporteSeguridadSocialTrabajador()+nomina.getImporteDesempleoTrabajador()+nomina.getImporteFormacionTrabajador()+nomina.getImporteIrpf())))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Total devengos")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getBrutoNomina()))));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Líquido a percibir")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph("")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(20).add(new Paragraph(""+String.format("%.2f",nomina.getLiquidoNomina()))));


				//espacios
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));
				tabla3.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(40).add(new Paragraph(" ")));



				Table tabla4 = new Table(2);
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Cálculo empresario: BASE")));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getBaseEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Contingencias comunes empresario "+String.format("%.2f",nomina.getSeguridadSocialEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingLeft(274).add(new Paragraph(""+String.format("%.2f",nomina.getImporteSeguridadSocialEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Desempleo "+String.format("%.2f",nomina.getDesempleoEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteDesempleoEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Formación "+String.format("%.2f",nomina.getFormacionEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteFormacionEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Accidentes de trabajo "+String.format("%.2f",nomina.getAccidentesTrabajoEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteAccidentesTrabajoEmpresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("FOGASA "+String.format("%.2f",nomina.getFogasaempresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getImporteFogasaempresario()))));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("Total empresario ")));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",(nomina.getImporteFogasaempresario()+nomina.getImporteAccidentesTrabajoEmpresario()+nomina.getImporteFormacionEmpresario()+nomina.getImporteDesempleoEmpresario()+nomina.getImporteSeguridadSocialEmpresario())))));

				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(10).add(new Paragraph(" ")));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddingBottom(10).add(new Paragraph(" ")));

				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(new Paragraph("COSTE TOTOAL TRABAJADOR ")));
				tabla4.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).add(new Paragraph(""+String.format("%.2f",nomina.getCosteTotalEmpresario()))));



				doc.add(tabla1);
				doc.add(tabla2);
				doc.add(cell5);
				doc.add(tabla3);
				doc.add(tabla4);

				doc.close();

			} catch (FileNotFoundException | MalformedURLException e) {
				e.printStackTrace();
			}
		}



	}
        
///////////////////////////////////////////////////////////////////////////////////////////////////////////


	private static Double recalculoTrieniosExtra(int mesDeLaExtra , int anioGeneracion, String fechaAltaTrab) {

		Double importeTrieniosExtra;

		int numTrieniosExtra = calcularTrienioPorMes(mesDeLaExtra,anioGeneracion, fechaAltaTrab); //calculamos nº de trienios en la fecha de la extra

		if(numTrieniosExtra == 0) {
			importeTrieniosExtra = 0.0;
		}else {
			importeTrieniosExtra = ManejadorExcel.getnTrienio_importeBruto().get(numTrieniosExtra); //sacamos el importe recalculado de la extra que hay que añadirle en 1/6 a la nomina

		}


		return importeTrieniosExtra;
	}


	private static Double calcularDescuentosEmpresario(Double baseEmpresario) {

		//Cuota Obrera General
		//entrada de la tabla a la nomina
		Double porcentSegSoc = ManejadorExcel.getDescuentos().get("Contingencias comunes EMPRESARIO");
		nomina.setSeguridadSocialEmpresario(porcentSegSoc);

		Double importeSegSoc = baseEmpresario*(porcentSegSoc/100);

		nomina.setImporteSeguridadSocialEmpresario(importeSegSoc);		

		//fogasa del empresario
		//entrada de la tabla a la nomina
		Double porcentFogasa = ManejadorExcel.getDescuentos().get("Fogasa EMPRESARIO");
		nomina.setFogasaempresario(porcentFogasa);

		Double importeFogasa = baseEmpresario*(porcentFogasa/100);

		nomina.setImporteFogasaempresario(importeFogasa);


		//Desempleo
		//entrada de la tabla a la nomina
		Double porcentDes = ManejadorExcel.getDescuentos().get("Desempleo EMPRESARIO");
		nomina.setDesempleoEmpresario(porcentDes);

		Double importeDes = baseEmpresario*(porcentDes/100);

		nomina.setImporteDesempleoEmpresario(importeDes);		


		//Formacion
		//entrada de la tabla a la nomina
		Double porcentForm = ManejadorExcel.getDescuentos().get("Formacion EMPRESARIO");
		nomina.setFormacionEmpresario(porcentForm);

		Double importeForm = baseEmpresario*(porcentForm/100);

		nomina.setImporteFormacionEmpresario(importeForm);			


		//Accidentes trabajo empresario
		//entrada de la tabla a la nomina
		Double porcentAcc = ManejadorExcel.getDescuentos().get("Accidentes trabajo EMPRESARIO");
		nomina.setAccidentesTrabajoEmpresario(porcentAcc);

		Double importeAcc = baseEmpresario*(porcentAcc/100);

		nomina.setImporteAccidentesTrabajoEmpresario(importeAcc);			


		return (importeSegSoc+ importeDes +importeFogasa+ + importeForm + importeAcc);

	}

	private static Double calcularDescuentosTrabajador(Double base) {

		//Cuota Obrera General
		//entrada de la tabla a la nomina
		Double porcentSegSoc = ManejadorExcel.getDescuentos().get("Cuota obrera general TRABAJADOR");
		nomina.setSeguridadSocialTrabajador(porcentSegSoc);

		Double importeSegSoc = base*(porcentSegSoc/100);

		nomina.setImporteSeguridadSocialTrabajador(importeSegSoc);		


		//Desempleo
		//entrada de la tabla a la nomina
		Double porcentDes = ManejadorExcel.getDescuentos().get("Cuota desempleo TRABAJADOR");
		nomina.setDesempleoTrabajador(porcentDes);

		Double importeDes = base*(porcentDes/100);

		nomina.setImporteDesempleoTrabajador(importeDes);		


		//Formacion
		//entrada de la tabla a la nomina
		Double porcentForm = ManejadorExcel.getDescuentos().get("Cuota formación TRABAJADOR");
		nomina.setFormacionTrabajador(porcentForm);

		Double importeForm = base*(porcentForm/100);

		nomina.setImporteFormacionTrabajador(importeForm);			


		return (importeSegSoc+ importeDes + importeForm);

	}


	private static Double calcularIRPF(Double brutoMes, Nomina nomina, Date fechaAlta, int anioGeneracion, Trabajadorbbdd trabajador) {

		Format formatter = new SimpleDateFormat("dd-MM-yyyy");
		String fechaAltaTrab = formatter.format(fechaAlta);
		String[] d2 = fechaAltaTrab.split("-");
		int mesFechaAlta = Integer.valueOf(d2[1]);//mes fecha alta
		int anioFechaAlta = Integer.valueOf(d2[2]);//año fecha alta


		//CALCULAR EL BRUTO ANUAL (venenoso)

		int mesesTrabajadosAnioCurso; 

		if(anioFechaAlta == anioGeneracion) { //si entro en el mismo año que generamos la nomina (salvo si entro en enero, no lo ha trabajado completo)

			mesesTrabajadosAnioCurso = 12 - (mesFechaAlta-1);
			//si entro en febero a trabajar, es el mes 2, 2-1 = 1 --> 12-1 = 11 meses trabajados del año

		}else { //si el año que entro es anterior al de generacion de nomina (ha trabajado el año completo)

			mesesTrabajadosAnioCurso = 12; //así 12 dividiendo y 12 multiplicando se van y la formula de bruto queda como para año entero trabajado

		}

		Double brutoAnual = ((trabajador.getCategorias().getSalarioBaseCategoria())/12)*mesesTrabajadosAnioCurso + ((trabajador.getCategorias().getComplementoCategoria())/12)*mesesTrabajadosAnioCurso + calcularAntiguedadAnioGeneracion(anioGeneracion, fechaAlta); //si el año es el que acaba de entrar la antiguedad del año es 0
		nomina.setBrutoAnual(brutoAnual);


		//SACAR EL % DE LA TABLA SEGUN EL RANGO --> EL MAYOR DE LOS DOS DEL RANGO
		Double porcentIRPF = 0.0;
		Double importeIRPF;

		LinkedHashMap<Integer, Double> mp = ManejadorExcel.getBrutoAnual_retencion();

		for (HashMap.Entry<Integer, Double> entry : mp.entrySet()) {
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

			if(brutoAnual <= entry.getKey()) { //si el brutoAnual es menor o igual que la entrada de la tabla cojo el valor

				porcentIRPF = entry.getValue();
				nomina.setIrpf(porcentIRPF);
				break;

			}
		}


		//LO APLICO AL BRUTO DEL MES Y LO RETORNO
		importeIRPF = brutoMes*(porcentIRPF/100);
		nomina.setImporteIrpf(importeIRPF);


		return importeIRPF;


	}	




        
        
        

/////////////////////////////////////CORRECCIONES/////////////////////////////////////////////////////////////////77
	private static void ordenarArrayNIFErrores() {

		Trabajadorbbdd aux = new Trabajadorbbdd();

		for (int i = 0; i < NIFErrores.size(); i++) {

			if(i+1 <= NIFErrores.size()-1) {
				if (NIFErrores.get(i).getIdTrabajador() > NIFErrores.get(i+1).getIdTrabajador() ) { //si el id es mayor que el del siguiente permutamos 
					aux = NIFErrores.get(i+1);
					NIFErrores.set(i+1, NIFErrores.get(i));
					NIFErrores.set(i, aux);
				}
			}			
		}

	}

	private static void componerArrayNIFErrores() {

		for (int i = 0; i < trabajadores.size(); i++) {
			Trabajadorbbdd t = trabajadores.get(i);
			if(t.getNombre() == "") {
				continue;
			}else if(t.getNifnie() == ""){
				NIFErrores.add(t);
				continue;
			}
			for(int j = i+1; j < trabajadores.size(); j++) {
				Trabajadorbbdd repetido = trabajadores.get(j);

				if (t.getNifnie().compareTo(repetido.getNifnie()) == 0) { //al barrer desde el fijado (exclusive) hacia abajo, pondra solo el segundo y sucesivos, en caso de haberlos 
					NIFErrores.add(repetido);
					break;

				}
			}

		}

	}

	private static void validarNifnie(Trabajadorbbdd t){
		String dniAValidar = t.getNifnie();
		boolean extranjero = false;

		//if (dniAValidar.equals("09741995T")) {
		//	System.out.println("e");
		//}

		//String dni_re = "^[0-9]{8}[a-zA-Z]{1}$";
		String tabla_letras = "TRWAGMYFPDXBNJZSQVHLCKE";

		if(dniAValidar != "") {

			String primera = dniAValidar.substring(0, 1);
			String first = "";
			if ((primera.compareTo("X") == 0) || (primera.compareTo("Y") == 0) || (primera.compareTo("Z") == 0)) {
				if(primera.compareTo("X") == 0){    	   
					dniAValidar = dniAValidar.replaceFirst("X", "0");
					first = "0";
				}else if(primera.compareTo("Y") == 0){
					dniAValidar = dniAValidar.replaceFirst("Y", "1");
					first = "1";
				}else if(primera.compareTo("Z") == 0){
					dniAValidar = dniAValidar.replaceFirst("Z", "2");
					first = "2";
				}
				extranjero = true;
			}

			int numeros = Integer.parseInt(dniAValidar.substring(0, dniAValidar.length()-1));
			char letra = dniAValidar.charAt(8);
			int resto = numeros%23;
			char letraCalculada = tabla_letras.charAt(resto);

			if(letraCalculada != letra) { //la letra NO coincide con la calculada y NO es correcto
				//Actualizar su valor en la hoja Excel con la letra correcta

				dniAValidar = dniAValidar.replaceFirst(Character.toString(letra), Character.toString(letraCalculada)); //reemplazamos la letra por la buena
				if (extranjero) {
					dniAValidar = dniAValidar.replaceFirst(first, primera);
				}

				t.setNifnie(dniAValidar);


			}

		}/*else {
			//si esta en blanco envía los datos del trabajador al fichero "Errores.xml" (la segunda y posteriores apariciones)
			//si entra aqui sabemos que esta en blanco, sea porque es una linea blanca, o porque el trabajador tiene ese campo en blanco
			//si es una linea en blanco no se guarda para volcar al xml
			if(t.getNombre() != "") {
				NIFErrores.add(t); 	 
			}
		}*/


	}


	private static boolean repetido(String dniAValidar) {
		boolean repetido = false;
		int i = 0;
		int contador = 0;

		for (Trabajadorbbdd trabajadorbbdd : trabajadores) {

			if(trabajadorbbdd.getNifnie().compareTo(dniAValidar) == 0) {
				contador++;
				if(contador > 1) {
					repetido = true;
					break;
				}
			}

		}

		return repetido;
	}

	private static void verificarCCC(Trabajadorbbdd t){
		String ccc = "";

		if (t.getNombre() != "") {
			String primerosDiez = "00"+t.getCodigoCuenta().substring(0, 8);
			String segundosDiez = t.getCodigoCuenta().substring(10, 20);
			String digsControl = t.getCodigoCuenta().substring(8, 10);
			String pdc = algoritmoDigitoControl(primerosDiez);		
			String sdc = algoritmoDigitoControl(segundosDiez);

			ccc = t.getCodigoCuenta().substring(0, 8) + pdc + sdc + segundosDiez;

			if(!t.getCodigoCuenta().equals(ccc)){
				//loguear el error al xml
				mal = true;
				Trabajadorbbdd erroneo = new Trabajadorbbdd(); 
				erroneo.setCodigoCuenta(t.getCodigoCuenta()); //cuenta sin corregir,
				erroneo.setNombre(t.getNombre());
				erroneo.setApellido1(t.getApellido1());
				erroneo.setApellido2(t.getApellido2());
				erroneo.setIdTrabajador(t.getIdTrabajador());

				Empresas empresa = new Empresas();
				empresa.setNombre(t.getEmpresas().getNombre());
				erroneo.setEmpresas(empresa);
				CCCErroneas.add(erroneo);

			}
			t.setCodigoCuenta(ccc);
			//System.out.println(ccc);

		}


	}


	private static String algoritmoDigitoControl(String secuenciaDeDiez) {

		int suma = 0;
		int resto = 0;
		int digitoControl = 0;

		for(int i = 0; i < 10; i++) {
			int digito = Integer.parseInt(secuenciaDeDiez.substring(i,i+1));
			digito = digito*(int)((Math.pow(2, i))%11);
			suma = suma + digito;
		}

		resto = suma % 11;

		digitoControl = 11 - resto;

		if(digitoControl == 10) {
			digitoControl = 1;
		}else if (digitoControl == 11) {
			digitoControl = 0;
		}

		return String.valueOf(digitoControl);
	}


	private static void generarIBAN(Trabajadorbbdd t) {


		String cuenta = t.getCodigoCuenta();
		String pais = t.getIban();
		String paisNumeros = "";
		HashMap<String, String> tabla = new HashMap<String, String>(){{
			put("A", "10");
			put("B", "11");
			put("C", "12");
			put("D", "13");
			put("E", "14");
			put("F", "15");
			put("G", "16");
			put("H", "17");
			put("I", "18");
			put("J", "19");
			put("K", "20");
			put("L", "21");
			put("M", "22");
			put("N", "23");
			put("O", "24");
			put("P", "25");
			put("Q", "26");
			put("R", "27");
			put("S", "28");
			put("T", "29");
			put("U", "30");
			put("V", "31");
			put("W", "32");
			put("X", "33");
			put("Y", "34");
			put("Z", "35");
		}};

		if (t.getNombre() != "") {
			paisNumeros = tabla.get(pais.substring(0, 1)) + tabla.get(pais.substring(1, 2));

			String iban = cuenta + paisNumeros + "00";


			BigInteger numeroIban = new BigInteger(iban);
			BigInteger modulo = new BigInteger("97");

			int resto = numeroIban.mod(modulo).intValue();
			int resta = 98-resto;
			String digsControl =  String.format("%02d", resta); //0 para llenar con ceros y 2 para la longitud
			//System.out.println(pais + digsControl + cuenta);
			t.setIban(pais + digsControl + cuenta); 

			if (mal) {
				CCCErroneas.get(CCCErroneas.size() - 1).setIban(pais + digsControl + cuenta);
				mal = false;
			}

		}
	}

	private static void generarEmail(Trabajadorbbdd t){

		StringBuilder email = new StringBuilder();
		if (t.getNombre() != "") {


			if (t.getEmail() != "") {

				email.append(t.getNombre().substring(0, 1));
				email.append(t.getApellido1().substring(0, 1));

				if (t.getApellido2() != "") {
					email.append(t.getApellido2().substring(0, 1));

				}

				email.append(getRepeticiones(t, email.toString()));
				email.append("@"+t.getEmpresas().getNombre()+".com");

			}

			t.setEmail(email.toString());
		}
		//System.out.println(email);
	}

	private static String getRepeticiones(Trabajadorbbdd t, String email) {
		int reps = 0;

		for (Trabajadorbbdd trabajadorbbdd : trabajadores) {
			if (trabajadorbbdd.getNombre() != "") {
				if (trabajadorbbdd.equals(t)) {
					break;
				}
				if ((email.compareTo(trabajadorbbdd.getEmail().substring(0, email.length())) == 0) && (trabajadorbbdd.getEmpresas().getNombre() == t.getEmpresas().getNombre())) {
					reps++;
				}
			}
		}
		return String.format("%02d", reps); //0 para llenar con ceros y 2 para la longitud
	}
  
  
  
  
  
  
  //////////////////////////////////////BBDD////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  
    private static void almacenarEnBBDD(){

		TrabajadorbbddDAO trabajadorDAO = new TrabajadorbbddDAO();
		EmpresasDAO empresaDAO = new EmpresasDAO();
		CategoriasDAO categoriaDAO = new CategoriasDAO();
                NominaDAO nominaDAO = new NominaDAO();

		SessionFactory sf = new HibernateUtil().getSessionFactory();
		Session session = sf.openSession();

		///empresas
		for(Trabajadorbbdd trabajadorbbdd : trabajadores){

                    if(trabajadorbbdd.getNifnie() != "" && trabajadorbbdd.getSeHaceNomina()){
                    
                        //EMPRESAS
			if (trabajadorbbdd.getEmpresas().getCif() != "") {

				if(empresaDAO.checkExisteEmpresa(trabajadorbbdd.getEmpresas().getCif(), sf, session) == false){
					//Si el CIF de la empresa a la que pertenece el trabajador no está, es que no está la empresa y la ponemos
					empresaDAO.addEmpresa(trabajadorbbdd.getEmpresas(), sf, session);
					//System.out.println("a");
				}else {
					empresaDAO.actualizarNombreEmpresa(trabajadorbbdd.getEmpresas(), sf, session);
				}
			}

                        //CATEGORIAS
			if (trabajadorbbdd.getCategorias().getNombreCategoria() != "") {

				if(categoriaDAO.checkExisteCategoria(trabajadorbbdd.getCategorias().getNombreCategoria(), sf, session) == false){

					//Si el nombre de la categoría no está, hay que añadir la categoría
					categoriaDAO.addCategoria(trabajadorbbdd.getCategorias(), sf, session);

				}else{
					//Si está, se actualizan los valores no coincidentes

					categoriaDAO.updateCategoria(trabajadorbbdd.getCategorias(), sf, session);

				}

			}
                        //TRABAJADORES
                        if (trabajadorbbdd.getFechaAlta() != null) {
				if (!trabajadorDAO.checkExisteTrabajador(trabajadorbbdd, sf, session)) {
					trabajadorDAO.addTrabajador(trabajadorbbdd, sf, session);
				}else {
					trabajadorDAO.updateTrabajador(trabajadorbbdd, sf, session);
				}
			}

                        
                        
                        //NOMINAS
                        for (Iterator<Nomina> it = trabajadorbbdd.getNominas().iterator(); it.hasNext();) {
                            Nomina n = it.next();
                            if(!nominaDAO.checkExisteNomina(n, sf, session)){
                                nominaDAO.addNomina(n, sf, session);
                            }else{
                                nominaDAO.updateNomina(n, sf, session);
                            }
                            
                            
                        }
                        
                        
                        

                      
                        
                        
                        
                        
                                
                                
                                
                                
                                
                                
                                
                                
                                
                                
                                
                                
                    }           

		}

		sf.close();
		HibernateUtil.cerrarSessionFactory();
	}


    
}