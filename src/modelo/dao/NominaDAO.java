/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.dao;

import java.util.List;
import modelo.Nomina;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author 
 */
public class NominaDAO {
    

    //Una nómina ya existe si coinciden el mes, el año, el trabajador y los valores bruto mensual y líquido mensual de dicha nómina
    
    public boolean checkExisteNomina(Nomina nomina, SessionFactory sf, Session session){

                    boolean existe = false;

                    String consutaHQL = "FROM Nomina n WHERE n.mes = :mes and n.anio = :anio and n.trabajadorbbdd = :trabajador and n.brutoNomina = :brutoMes and n.liquidoNomina = :liquidoNomina"; //traemos todas las nominas que cumplan la sentencia

	            Query query = session.createQuery(consutaHQL);
	            query.setParameter("mes", nomina.getMes());
                    query.setParameter("anio", nomina.getAnio());
	            query.setParameter("trabajador", nomina.getTrabajadorbbdd());
	            query.setParameter("brutoMes", nomina.getBrutoNomina());
	            query.setParameter("liquidoNomina", nomina.getLiquidoNomina());
                                 
	            List<Nomina> listaResultado = query.list(); //tenemos las nominas

        	    if(listaResultado.isEmpty()){ //si el tamaño de la lista de objetos categoria con el mismo nombre es ninguno, no existe
	             existe = false;
	            
	            }else{
	              existe = true;
	            }

	      return existe;
        
   }

    public void addNomina(Nomina nomina, SessionFactory sf, Session session){

        Transaction tx = null;

		try {

                    //Como ya metimos el trabajador en su nómina, ya tiene el ID que referencia a la otra tabla
                                            
			tx=session.beginTransaction();
			session.save(nomina);
			tx.commit();

		} catch (Exception e) {
                    e.printStackTrace();
			if (tx != null) { //se ha iniciado pero no ha ido bien
				tx.rollback(); //si hay cualquier problema la transaccion no se hace
			}
		}
                session.clear();


    }

    
    public void updateNomina(Nomina nomina, SessionFactory sf, Session session) {

                Transaction tx = null;

        
                    String consutaHQL = "FROM Nomina n WHERE n.mes = :mes and n.anio = :anio and n.trabajadorbbdd = :trabajador and n.brutoNomina = :brutoMes and n.liquidoNomina = :liquidoNomina"; //traemos todas las nominas que cumplan la sentencia

	            Query query = session.createQuery(consutaHQL);
	            query.setParameter("mes", nomina.getMes());
                    query.setParameter("anio", nomina.getAnio());
	            query.setParameter("trabajador", nomina.getTrabajadorbbdd());
	            query.setParameter("brutoMes", nomina.getBrutoNomina());
	            query.setParameter("liquidoNomina", nomina.getLiquidoNomina());
                    List<Nomina> listaResultado = query.list(); //tenemos las categorías
                    
                try {  
                    
                    tx=session.beginTransaction();
                    
                        int nuevoID = listaResultado.get(0).getIdNomina(); //siempre hay una porque siempre pisamos sobre esa con  la nueva
                        nomina.setIdNomina(nuevoID); //cambio el id de la nueva con la que voy a actualizar
                        session.merge(nomina);
			tx.commit();
 
		} catch (Exception e) {
                    e.printStackTrace();
			if (tx != null) { //se ha iniciado pero no ha ido bien
				tx.rollback(); //si hay cualquier problema la transaccion no se hace
			}
		}
                session.clear();

    }

}
