package sistemas20202021;

import java.math.BigInteger;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
	private static String fechaEntrada;
	private static Nomina nomina;	//nomina que estamos calculando
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
			System.out.println(trabajadorbbdd.getSeHaceNomina());

			if (trabajadorbbdd.getSeHaceNomina()) {

				calcularSalarioBrutoAnual(trabajadorbbdd);


			}

		}



		ManejadorXML x = new ManejadorXML();
		x.escribirErroresXML(NIFErrores);
		x.escribirErroresCCCXML(CCCErroneas);


	}
	//////////////////////////////////////CALCULOS

	private static void calcularAntiguedad(Trabajadorbbdd t){

		if (t.getFechaAlta()!=null) {
			String date = "01/"+fechaEntrada;


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
		fechaEntrada = entrada;
	}

	private static int calcularTrienioPorMes(int md1, int year , String fechaAlta){//devuelve el trienio de cada mes del año


		int yd1 = year;//año introducido


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

	private static void calcularSalarioBrutoAnual(Trabajadorbbdd t) {

		if (t.getNombre() != "") {

			double brutoAnual = 0;
			double importeTrienios = 0;
			int md1 = 1;
			int year = nomina.getAnio();
			Date fechaAlta = t.getFechaAlta();
			Format formatter = new SimpleDateFormat("dd-MM-yyyy");
			String f = formatter.format(fechaAlta);

			if (year == Integer.valueOf(f.substring(6))) {//para los que no han estado un año entero

			}


			for (int i = 0; i < 12; i++) {

				int x = calcularTrienioPorMes(md1, year, f);
				System.out.println(x);
				if (x!=0) {
					importeTrienios += ManejadorExcel.getnTrienio_importeBruto().get(x);
				}

				md1++;

			}

			System.out.println(t.getNombre()+" "+t.getApellido1() + "--"+ importeTrienios);

			
			brutoAnual = t.getCategorias().getSalarioBaseCategoria()+t.getCategorias().getComplementoCategoria()+importeTrienios; 
			
			
			System.out.println(t.getNombre()+" "+t.getApellido1() + "--"+ brutoAnual);




			//importeTrienios
			nomina.setImporteTrienios(ManejadorExcel.getnTrienio_importeBruto().get(nomina.getNumeroTrienios()));

			//salario base + complementos + trienios (si los tiene)
			//nomina.setBrutoAnual(t.getCategorias().getSalarioBaseCategoria() + t.getCategorias().getComplementoCategoria() + nomina.getImporteTrienios());


		}
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


