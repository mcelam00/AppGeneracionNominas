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

        SessionFactory sf = null;
        Session session = null;
        Transaction tx = null;

        try {
            sf = HibernateUtil.getSessionFactory();
            session = sf.openSession();

            String consultaHQL = "FROM Trabajadorbbdd t WHERE t.nifnie = :param1";

            //pedir dni por consola
            Scanner teclado = new Scanner(System.in);
            System.out.println("Por favor, infroduzca el NIF del trabajador: ");
            String dni = teclado.nextLine();
            
            //String dni = "10200147S";

            Query query = session.createQuery(consultaHQL);

            query.setParameter("param1", dni);

            List<Trabajadorbbdd> listaResultado = query.list();

            if (listaResultado.size() != 0) {

                for (Trabajadorbbdd tbd : listaResultado) {
                    System.out.println("Nombre: " + tbd.getNombre());
                    System.out.println("Apellidos: " + tbd.getApellido1() + " " + tbd.getApellido2());
                    System.out.println("NIF: " + tbd.getNifnie());
                    System.out.println("Categoria: " + tbd.getCategorias().getNombreCategoria());
                    System.out.println("Empresa: " + tbd.getEmpresas().getNombre());
                    System.out.println("--");

                    for (Iterator<Nomina> it = tbd.getNominas().iterator(); it.hasNext();) {
                        Nomina n = it.next();
                        System.out.println("Nomina: " + n.getMes() + "/" + n.getAnio() + " | " + n.getBrutoNomina() + "€");
                    }

                    System.out.println("-----------------------");

                }
            } else {
                System.out.println("ERROR - El trabajador no esta en la base de datos");
            }

            HibernateUtil.shutdown();

        } catch (Exception e) {
            System.out.println("error");
        }
    }

}
