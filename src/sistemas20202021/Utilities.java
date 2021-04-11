package sistemas20202021;

import java.util.ArrayList;

import modelo.Trabajadorbbdd;

public class Utilities{
    
private static ArrayList<Trabajadorbbdd> trabajadores; 

public static void corregir(ArrayList<Trabajadorbbdd> arrayTrabajadores){
    
    for (Trabajadorbbdd trabajadorbbdd : arrayTrabajadores) {
        validarNifnie(trabajadorbbdd);
        verificarCCC(trabajadorbbdd);
        generarEmail(trabajadorbbdd);
        generarEmail(trabajadorbbdd);
    }
 
    		
}

private static void validarNifnie(Trabajadorbbdd t){

	
	
	
}

private static void verificarCCC(Trabajadorbbdd t){
    
	
	
	
}

private static void generarIBAN(Trabajadorbbdd t) {

	
	
	
}

private static void generarEmail(Trabajadorbbdd t){
    
	
	
	
}

}