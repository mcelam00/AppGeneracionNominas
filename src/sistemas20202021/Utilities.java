package sistemas20202021;

import java.math.BigInteger;
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

import com.sun.glass.ui.Size;

import modelo.Categorias;
import modelo.Empresas;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
import modelo.dao.ManejadorExcel;
import modelo.dao.ManejadorXML;

public class Utilities{

	private static ArrayList<Trabajadorbbdd> trabajadores; 
	private static ArrayList<Trabajadorbbdd> NIFErrores;
	private static ArrayList<Trabajadorbbdd> CCCErroneas;
	private static boolean mal = false;
	private static String fechaGeneracion;
	private static Nomina nomina;	//nomina que estamos calculando
	private static Nomina nominaExtra;
	private static int idNomina = 0;


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


	}
	//////////////////////////////////////CALCULOS

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
			//Si la nomina NO es prorrateada el if salta la parte de sumarle 1/6 de la siguiente nomina extra
			//el valor de prorrateo será la inicializacion del double a 0
			nomina.setBaseEmpresario(salarioBaseMes + complementoMes + importeTrienios + nomina.getValorProrrateo());
			nomina.setValorProrrateo(0.0);
			brutoMes = salarioBaseMes + complementoMes + importeTrienios + nomina.getValorProrrateo(); //si tiene prorrateo y entró en el if anterior, sino suma 0
			nomina.setBrutoNomina(brutoMes);
			
			Double valorARestarDescuentos = calcularDescuentos(nomina.getBaseEmpresario());
			Double IRPF = calcularIRPF(brutoMes, nomina, fechaAlta, anioGeneracion, trabajador);
			
			
			Double liquidoNomina = brutoMes-valorARestarDescuentos-IRPF;
			nomina.setLiquidoNomina(liquidoNomina);
			
			
		}else {
			
			
			brutoMes = salarioBaseMes + complementoMes + importeTrienios + nomina.getValorProrrateo(); //si tiene prorrateo y entró en el if anterior, sino suma 0
			nomina.setBrutoNomina(brutoMes);
			
			Double valorARestarDescuentos = calcularDescuentos(brutoMes);
			Double IRPF = calcularIRPF(brutoMes, nomina, fechaAlta, anioGeneracion, trabajador);
			
			nomina.setBaseEmpresario(brutoMes);
			
			Double liquidoNomina = brutoMes-valorARestarDescuentos-IRPF;
			nomina.setLiquidoNomina(liquidoNomina);

		}
		
				
						

		
		//si prorrateo (o no prorrateo pero el mes no es de extra) grabo una
		
		trabajador.getNominas().add(nomina);
		
		
		
		
		if(trabajador.getProrrataExtra() == false && (mesGeneracion == 6 || mesGeneracion == 12)) { //no tiene prorrateo y estoy en un mes de EXTRA, a parte de la nomina basica saco la extra tambien del trabajador
			//si no prorrateo y el mes es de extra tiene dos nominas
			nominaExtra = new Nomina();
			
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
			
		
			//la añado al set
			trabajador.getNominas().add(nominaExtra);
			
			//System.out.println("");
		}

		
		
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

		if (trabajador.getNominas().size() == 2) {
		System.out.println("---------------------");
		System.out.println("-"+trabajador.getEmpresas().getNombre() + " (" + trabajador.getEmpresas().getCif()+")");
		System.out.println("-"+trabajador.getNombre()+" "+trabajador.getApellido1()+" "+ trabajador.getApellido2()+ " (" +trabajador.getNifnie()+")");
		System.out.println("-Categoría: "+ trabajador.getCategorias().getNombreCategoria());
		System.out.println("-Fecha de alta: "+trabajador.getFechaAlta());
		System.out.println("-IBAN: "+ trabajador.getIban());
		System.out.println("-Bruto anual: "+nominaExtra.getBrutoAnual());
		System.out.println("-Fecha de la nómina: "+nominaExtra.getMes()+"/"+nominaExtra.getAnio()+"EXTRA");
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
		}
	
		
	}
	
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
	
	
	
	private static Double calcularDescuentos(Double brutoMes) {
		
		//Cuota Obrera General
			//entrada de la tabla a la nomina
		Double porcentSegSoc = ManejadorExcel.getDescuentos().get("Cuota obrera general TRABAJADOR");
		nomina.setSeguridadSocialTrabajador(porcentSegSoc);
		
		Double importeSegSoc = brutoMes*(porcentSegSoc/100);
						
		nomina.setImporteSeguridadSocialTrabajador(importeSegSoc);		
				
		
		//Desempleo
			//entrada de la tabla a la nomina
		Double porcentDes = ManejadorExcel.getDescuentos().get("Cuota desempleo TRABAJADOR");
		nomina.setDesempleoTrabajador(porcentDes);
				
		Double importeDes = brutoMes*(porcentDes/100);
							
		nomina.setImporteDesempleoTrabajador(importeDes);		
				
		
		//Formacion
			//entrada de la tabla a la nomina
		Double porcentForm = ManejadorExcel.getDescuentos().get("Cuota formación TRABAJADOR");
		nomina.setFormacionTrabajador(porcentForm);
				
		Double importeForm = brutoMes*(porcentForm/100);
							
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
	
	
	

	
	
	

	/////////////////////////////////////CORRECCIONES
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

}


