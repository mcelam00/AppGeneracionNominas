package sistemas20202021;

import java.util.ArrayList;

import modelo.Trabajadorbbdd;

public class Utilities{
    
private static ArrayList<Trabajadorbbdd> trabajadores; 

public static void corregir(ArrayList<Trabajadorbbdd> arrayTrabajadores){

	trabajadores = arrayTrabajadores;
    
    for (Trabajadorbbdd trabajadorbbdd : arrayTrabajadores) {
        validarNifnie(trabajadorbbdd);
        //verificarCCC(trabajadorbbdd);
        //generarEmail(trabajadorbbdd);
        //generarEmail(trabajadorbbdd);
    }
 
    		
}

private static void validarNifnie(Trabajadorbbdd t){
	System.out.println(t.getNombre());
	String dniAValidar = t.getNifnie();
	
	
	if(repetido(dniAValidar)){
	//si entra aqui sabemos que esta repetido y lo metemos a errores
	
}


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
	   }
	   
	   int numeros = Integer.parseInt(dniAValidar.substring(0, dniAValidar.length()-1));
	   char letra = dniAValidar.charAt(8);
	   int resto = numeros%23;
	   char letraCalculada = tabla_letras.charAt(resto);
	   
	   	if(letraCalculada != letra) { //la letra NO coincide con la calculada y NO es correcto
	   	//Actualizar su valor en la hoja Excel con la letra correcta
	   		
	   		dniAValidar = dniAValidar.replaceFirst(Character.toString(letra), Character.toString(letraCalculada)); //reemplazamos la letra por la buena
	        dniAValidar = dniAValidar.replaceFirst(first, primera);
			System.out.println("dni de "+t.getNombre()+ " "+dniAValidar);
	        t.setNifnie(dniAValidar);
	   		
	   	}
   
   }else {
	   //si esta en blanco o repetido envía los datos del trabajador al fichero "Errores.xml" (la segunda y posteriores apariciones)
		//si entra aqui sabemos que esta en blanco
	   
	   
	   
   }

    
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
    
	
	
	
}

private static void generarIBAN(Trabajadorbbdd t) {

	
	
	
}

private static void generarEmail(Trabajadorbbdd t){
    
	
	
	
}

}