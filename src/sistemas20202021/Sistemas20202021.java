/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemas20202021;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import modelo.Empresas;
//import modelo.HibernateUtil;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
//import modelo.dao.EmpresasDAO;
//import modelo.dao.TrabajadorbbddDAO;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
import modelo.dao.ManejadorExcel;

/**
 *
 * @author 
 */
public class Sistemas20202021 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	System.out.println("Práctica III - Sistemas de Información II");
    	
    	
    	System.out.println("Por favor, indique el mes y año del que se desean generar las nóminas (formato mm/aaaa)");
    	Scanner teclado = new Scanner(System.in);
    	String fecha = teclado.nextLine();
    	teclado.close();
    	
    	
    	Utilities.setEntrada(fecha);
    	
    	ManejadorExcel a = new ManejadorExcel();
    	a.cargarHojaExcel();
    	
    	 
    	
    	/*
    	
    	if(opcion.compareTo("C") == 0) {
    		//ACTUALIZAR SOBRE LA HOJA DE ENTRADA
        	a.corregirExcelExistente(Utilities.getTrabajadores()); 
    	}else if (opcion.compareTo("N") == 0) {
    	 	//GENERAR UNA HOJA EXCEL NUEVA CORREGIDA
        	a.crearExcelCorregido(Utilities.getTrabajadores());
    	}else {
    		System.err.println("Entrada inválida");
     	} 
    	   
    	
    	
    	System.out.println("FIN DEL PROGRAMA");
    	
    	
    	*/
    	
    	
    	
//        //pedir dni por consola
//        Scanner teclado = new Scanner(System.in);
//        System.out.println("Por favor, infroduzca el NIF del trabajador: ");
//        String dni = teclado.nextLine();
//
//        //llamada al metodo y control de si viene null que salte el error
//        TrabajadorbbddDAO camposTrabajador = new TrabajadorbbddDAO();
//        EmpresasDAO empresa = new EmpresasDAO();
//
//        Trabajadorbbdd trabajadorEncontrado;
//
//        trabajadorEncontrado = camposTrabajador.recuperarDatosTrabajador(dni);
//
//        if (trabajadorEncontrado != null) {
//
//            System.out.println("Nombre: " + trabajadorEncontrado.getNombre());
//            System.out.println("Apellidos: " + trabajadorEncontrado.getApellido1() + " " + trabajadorEncontrado.getApellido2());
//            System.out.println("NIF: " + trabajadorEncontrado.getNifnie());
//            System.out.println("Categoria: " + trabajadorEncontrado.getCategorias().getNombreCategoria());
//            System.out.println("Empresa: " + trabajadorEncontrado.getEmpresas().getNombre());
//            System.out.println("--");
//
//            for (Iterator<Nomina> it = trabajadorEncontrado.getNominas().iterator(); it.hasNext();) {
//                Nomina n = it.next();
//                System.out.println("Nomina: " + n.getMes() + "/" + n.getAnio() + " | " + n.getBrutoNomina() + "â‚¬");
//            }
//
//            System.out.println("-----------------------");
//
//            boolean exitosa = empresa.actualizarNombreEmpresas(trabajadorEncontrado);
//
//            if (exitosa == true) {
//                System.out.println("Actualizacion Fructifera");
//            } else {
//                System.out.println("Actualizacion Fallida");
//            }
//
//            exitosa = camposTrabajador.eliminarTrabajadoryNomina(trabajadorEncontrado);
//
//            if (exitosa == true) {
//                System.out.println("Borrado Fructifero");
//            } else {
//                System.out.println("Borrado Fallido");
//            }
//
//        } else {
//            System.out.println("ERROR - El trabajador no esta en la base de datos");
//
//        }
//
//        HibernateUtil.shutdown();

    }

}
