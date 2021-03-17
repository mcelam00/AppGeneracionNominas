/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.dao;

import java.util.List;
import modelo.Empresas;
import modelo.HibernateUtil;
import modelo.Trabajadorbbdd;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author
 */
public class EmpresasDAO {
 
    
    
public boolean actualizarNombreEmpresas(Trabajadorbbdd trabajador){
    
    //traemos las empresas todas y cambiamos el nombre de todas salvo la que
    //coincida con la del trabajador segun el Codigo de Identificación Fiscal
    boolean testigo = true;
    SessionFactory sf = null;
    Session session = null;
    Transaction tx = null;
        
    try{
        sf = HibernateUtil.getSessionFactory();
        session = sf.openSession();
        String consutaHQL = "from Empresas e"; //traemos todas las empresas
        
        Query query = session.createQuery(consutaHQL);
        List<Empresas> listaResultado = query.list(); //tenemos las empresas
        
        for(int i = 0; i < listaResultado.size(); i++){

            if(listaResultado.get(i).getCif().compareTo(trabajador.getEmpresas().getCif()) != 0){
                //No coincide la empresa, la renombramos cambiando el nombre en java y ya safe or update lo hace por nosotros en la base
                listaResultado.get(i).setNombre(listaResultado.get(i).getNombre()+"2021");
                 /*MEJORA: Actualizar solo 1 vez el objeto lista en lugar de cada empresa 1 a 1*/
                tx = session.beginTransaction();
                session.saveOrUpdate(listaResultado.get(i));
                tx.commit(); //todo ha ido bien
                                
            }

        }
       
    }catch(Exception e){
        testigo = false;
        e.printStackTrace();
        if (tx != null) { //se ha iniciado pero no ha ido bien
            tx.rollback(); //si hay cualquier problema la transaccion no se hace
        }
    }    
    
    return testigo;
} 
    
    
    
    
    
    
}
