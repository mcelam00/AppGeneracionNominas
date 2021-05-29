/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.dao;

import java.util.List;
import modelo.Empresas;
import modelo.HibernateUtil;
//import modelo.HibernateUtil;
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

	public boolean checkExisteEmpresa(String cif, SessionFactory sf, Session session){

		boolean existe = false;

		String consutaHQL = "FROM Empresas e WHERE e.cif = :parametro"; //traemos todas las empresas con cif el que se pasa como parametro

		Query query = session.createQuery(consutaHQL);
		query.setParameter("parametro", cif);

		List<Empresas> listaResultado = query.list(); //tenemos las empresas


		if(listaResultado.isEmpty()){
			existe = false;

		}else{
			existe = true;
			//System.out.println(listaResultado.get(0).getCif());
		}

		return existe;
	}


	public void addEmpresa(Empresas empresa, SessionFactory sf, Session session){

		Transaction tx = null;

		try {

			tx=session.beginTransaction();
			session.saveOrUpdate(empresa);
			tx.commit();

			System.out.println(empresa.getNombre()+" added");

		} catch (Exception e) {
			if (tx != null) { //se ha iniciado pero no ha ido bien
				tx.rollback(); //si hay cualquier problema la transaccion no se hace
				e.printStackTrace();
			}
		}
		session.clear();

	}


	public void actualizarNombreEmpresa(Empresas empresa, SessionFactory sf, Session session) {


		Transaction tx = null;

		try {

			String consutaHQL = "FROM Empresas e WHERE e.cif = :parametro"; 

			Query query = session.createQuery(consutaHQL);
			query.setParameter("parametro", empresa.getCif());

			List<Empresas> listaResultado = query.list(); //tenemos las empresa que queremos actualizar



			if (!listaResultado.get(0).getNombre().equals(empresa.getNombre())) {//si no coinciden los nombres lo actualizamos
				
				tx = session.beginTransaction();

				listaResultado.get(0).setNombre(empresa.getNombre());

				session.saveOrUpdate(listaResultado.get(0));

				tx.commit(); //todo ha ido bien

				System.out.println(listaResultado.get(0).getNombre()+" modificada por "+empresa.getNombre());
			}




		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) { //se ha iniciado pero no ha ido bien
				tx.rollback(); //si hay cualquier problema la transaccion no se hace
			}
		}

	}



}
