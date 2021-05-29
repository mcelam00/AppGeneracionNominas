/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.dao;

import java.util.List;
import modelo.Categorias;
import modelo.Empresas;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author 
 */
public class CategoriasDAO {
    
    
    
    //Una categoría ya existe si coincide el nombre de la categoría.
    
    public boolean checkExisteCategoria(String nombre, SessionFactory sf, Session session){

                    boolean existe = false;

                    String consutaHQL = "FROM Categorias c WHERE c.nombreCategoria = :parametro"; //traemos todas las categorías cuyo nombre sea coincidente con el parametro

	            Query query = session.createQuery(consutaHQL);
	            query.setParameter("parametro", nombre);

	            List<Categorias> listaResultado = query.list(); //tenemos las categorías

        	    if(listaResultado.size() == 0){ //si el tamaño de la lista de objetos categoria con el mismo nombre es ninguno, no existe
	             existe = false;
	            
	            }else{
	              existe = true;
	            }

	      return existe;
        
   }

    public void addCategoria(Categorias categoria, SessionFactory sf, Session session) {

        Transaction tx = null;

		try {

			tx=session.beginTransaction();
			session.save(categoria);
			tx.commit();

		} catch (Exception e) {
                    e.printStackTrace();
			if (tx != null) { //se ha iniciado pero no ha ido bien
				tx.rollback(); //si hay cualquier problema la transaccion no se hace
			}
		}
                session.clear();


    }

    //Si la categoria existe, se procederá a actualizar los valores no coincidentes si los hubiere
    
    public void updateCategoria(Categorias categoria, SessionFactory sf, Session session) {

                Transaction tx = null;

        
                String consutaHQL = "FROM Categorias c WHERE c.nombreCategoria = :parametro"; //traemos todas las categorías cuyo nombre sea coincidente con el parametro

	            Query query = session.createQuery(consutaHQL);
	            query.setParameter("parametro", categoria.getNombreCategoria());
                    List<Categorias> listaResultado = query.list(); //tenemos las categorías
                    
                try {  
                    
                    tx=session.beginTransaction();
                    
                        int nuevoID = listaResultado.get(0).getIdCategoria(); //siempre hay una porque siempre pisamos sobre esa con  la nueva
                        categoria.setIdCategoria(nuevoID); //cambio el id de la nueva con la que voy a actualizar
                        session.merge(categoria);
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
