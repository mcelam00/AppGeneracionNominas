/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemas20202021;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import modelo.HibernateUtil;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
import modelo.dao.TrabajadorbbddDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author scalvd01
 */
public class Sistemas20202021 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        //pedir dni por consola
            Scanner teclado = new Scanner(System.in);
            System.out.println("Por favor, infroduzca el NIF del trabajador: ");
            String dni = teclado.nextLine();
        
        //llamada al metodo y control de si viene null que salte el error
        TrabajadorbbddDAO camposTrabajador = new TrabajadorbbddDAO();
        Trabajadorbbdd trabajadorEncontrado;
        
        trabajadorEncontrado = camposTrabajador.recuperarDatosTrabajador(dni);
        
        
        if(trabajadorEncontrado != null){
            
            System.out.println("Nombre: " + trabajadorEncontrado.getNombre());
            System.out.println("Apellidos: " + trabajadorEncontrado.getApellido1() + " " + trabajadorEncontrado.getApellido2());
            System.out.println("NIF: " + trabajadorEncontrado.getNifnie());
            System.out.println("Categoria: " + trabajadorEncontrado.getCategorias().getNombreCategoria());
            System.out.println("Empresa: " + trabajadorEncontrado.getEmpresas().getNombre());
            System.out.println("--");
        
        
        
            for (Iterator<Nomina> it = trabajadorEncontrado.getNominas().iterator(); it.hasNext();) {
                Nomina n = it.next();
                System.out.println("Nomina: " + n.getMes() + "/" + n.getAnio() + " | " + n.getBrutoNomina() + "€");
            }
        
        
            System.out.println("-----------------------");
            
        }else{
            System.out.println("ERROR - El trabajador no esta en la base de datos");
              
        }                    
                            
                            
        
    }

}
