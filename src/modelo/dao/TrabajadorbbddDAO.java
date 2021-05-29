/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import modelo.Categorias;
import modelo.Empresas;
import modelo.HibernateUtil;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author 
 */

public class TrabajadorbbddDAO {

    public void buscarTrabajadorDNI(String dni) {

//Vamos a hacer un ejemplo en el main, pero no debería ser aqui sino en una clase DAO --> si un metodo consulta del trabajador por nif tendria un trabajadorDAO y ahi estaria el método
        //los .java van al modelo, las clases dao van a la parte del modelo de acceso a datos
        //HibernateUtil me retorna un tipo SessionFactory; Necesitare 3 atributos, uno de tipo SessoinFactory, otro Session y otro Transaccion(para inserciones y eliminaciones) una query no necesita transacciones
        //Acostumbrarse a usar try catch para capturar exceptions 
        SessionFactory sf = null;
        Session session = null;
        Transaction tx = null; //control+shift+i para importar librerias

        try {
            /*Pedimos a hibernate a traves de hibernate util que abra una conexión con la bd*/
            sf = HibernateUtil.getSessionFactory();//metodo de HIbernateUtil
            session = sf.openSession(); //abro la sesion con la base de datos

            /*Voy a hacer una consulta; viene mucha mas informacion, no hago la consulta contra la tabla sino contra trabajadorbbdd.java; Respetar la T mayuscula de la clase
            Ademas en hql no existe el Select, sino que me traigo un objeto completo, empezamos en from y la clase, no la tabla
             */
            // String consultaHQL = "from Trabajadorbbdd t"; //dar un alias a la clase es obligatorio
            /*Voy a consultar todos aquellos trabajadores cuyo NIF sea ese parámetro
          No solo me estoy trayendo el nombre y el nif, o toda la info suya de la tabla trabajador, me estoy trayendo todas sus nóminas 
             */
            String consuta2HQL = "from Trabajadorbbdd t WHERE t.nifnie = :parametro"; //si quisiera mas seguiria con and :param2.... pasar parametros siempre, para evitar las inyecciones y nifnie es el atributo de la clase tal cual

            // Query query = session.createQuery(consultaHQL); //estoy creando la consulta, sobre la sesion abierta en hibernate
            Query query = session.createQuery(consuta2HQL); //aqui cuando compongo la consulta con el string de arriba, al tener un parámetro, tengo que dárselo a la query
            query.setParameter("parametro", dni);//antes de ejecutarla le cargo el parametro

            List<Trabajadorbbdd> listaResultado = query.list(); //devuelve una lista de la base de datos sobre el objeto perdido, trabajadoresbbdd, en este caso retorna todos los trabajadores de la base de datos

            //PRECONDICION: tengo toda la info de los trabajadores de la base de datos
            //Pinto de cada uno de ellos, su nombre y su NIF
            for (int i = 0; i < listaResultado.size(); i++) {
                System.out.println("Nombre: " + listaResultado.get(i).getNombre());
                System.out.println("NIF: " + listaResultado.get(i).getNifnie());
                System.out.println("=============================================================");
            }

            //Destruyo la sesion con Hibernate
            HibernateUtil.shutdown();

        } catch (Exception e) {
            System.out.println("Ha ocurrido un error: " + e.getMessage());
        }

    }
    

    public Trabajadorbbdd recuperarDatosTrabajador(String dni) {

        SessionFactory sf = null;
        Session session = null;
        Transaction tx = null;
        Trabajadorbbdd trabajador = new Trabajadorbbdd();
        Categorias categoria = new Categorias();
        Empresas empresa = new Empresas();
        Nomina nomina = new Nomina();
        Set nominas = new HashSet(0);

        try {
            sf = HibernateUtil.getSessionFactory();
            session = sf.openSession();

            String consultaHQL = "FROM Trabajadorbbdd t WHERE t.nifnie = :param1";

            //String dni = "10200147S";
            Query query = session.createQuery(consultaHQL);
            query.setParameter("param1", dni);

            List<Trabajadorbbdd> listaResultado = query.list();
            if (listaResultado.size() != 0) {

                for (Trabajadorbbdd tbd : listaResultado) {
                    //se rellena el trabajador pedido
                    trabajador.setNombre(tbd.getNombre());
                    trabajador.setApellido1(tbd.getApellido1());
                    trabajador.setApellido2(tbd.getApellido2());
                    trabajador.setNifnie(tbd.getNifnie());
                    categoria.setNombreCategoria(tbd.getCategorias().getNombreCategoria());
                    trabajador.setCategorias(categoria);
                    empresa.setNombre(tbd.getEmpresas().getNombre());
                    empresa.setCif(tbd.getEmpresas().getCif()); //le guardamos el CIF para el punto 2
                    trabajador.setEmpresas(empresa);

                    for (Iterator<Nomina> it = tbd.getNominas().iterator(); it.hasNext();) {
                        Nomina n = it.next();
                        //se rellena la nómina
                        nomina.setMes(n.getMes());
                        nomina.setAnio(n.getAnio());
                        nomina.setBrutoNomina(n.getBrutoNomina());
                        //se añade la nomina al set
                        nominas.add(nomina);

                    }
                    //se añade el conjunto de nóminas al trabajador buscado
                    trabajador.setNominas(nominas);

                }
            } else {
                trabajador = null;
            }

        } catch (Exception e) {
            System.out.println("error");
        }

        return trabajador;
    }

    public boolean eliminarTrabajadoryNomina(Trabajadorbbdd trabajadorActual) {


        SessionFactory sf = null;
        Session session = null;
        Transaction tx = null;
        boolean testigo = true;
        Categorias categoria = new Categorias();
        Empresas empresa = new Empresas();
        Nomina nomina = new Nomina();
        Set nominas = new HashSet(0);

        try {
            sf = HibernateUtil.getSessionFactory();
            session = sf.openSession();

            String consultaHQL = "FROM Trabajadorbbdd t";

            Query query = session.createQuery(consultaHQL);

            List<Trabajadorbbdd> listaResultado = query.list();

            for (Trabajadorbbdd tbd : listaResultado) { //Lista con todos los trabajadores
                //se mira la empresa del trabajador y si es igual que la del actual no se borra
                if (tbd.getEmpresas().getCif().compareTo(trabajadorActual.getEmpresas().getCif()) != 0) {
                    //si la empresa es distinta se borran sus nóminas y el trabajador

                    for (Iterator<Nomina> it = tbd.getNominas().iterator(); it.hasNext();) {
                        Nomina n = it.next();

                        tx = session.beginTransaction();

                        String HQLborrado = "DELETE Nomina n WHERE n.idNomina=:param1";
                        session.createQuery(HQLborrado).setParameter("param1", n.getIdNomina()).executeUpdate();

                        tx.commit();
                    }
                    tx = session.beginTransaction();
                    String HQLborrado2 = "DELETE Trabajadorbbdd t WHERE t.nifnie=:param1";
                    session.createQuery(HQLborrado2).setParameter("param1", tbd.getNifnie()).executeUpdate();
                    tx.commit();

                }

            }

        } catch (Exception e) {
            System.out.println("error");
            testigo = false;
            if (tx != null) { //se ha iniciado pero no ha ido bien
                tx.rollback(); //si hay cualquier problema la transaccion no se hace
            }
        }

        return testigo;

    }



	
    
    public boolean checkExisteTrabajador(Trabajadorbbdd trabajadorbbdd, SessionFactory sf, Session session) {
    	
    	
    	boolean existe = false;
    	
    	String consultaHQL = "FROM Trabajadorbbdd t WHERE t.nombre = :nombre and t.nifnie = :nifnie and t.fechaAlta = :fechaAlta";
    			Query query = session.createQuery(consultaHQL);
    			query.setParameter("nombre", trabajadorbbdd.getNombre());
    			query.setParameter("nifnie", trabajadorbbdd.getNifnie());
    			query.setParameter("fechaAlta", trabajadorbbdd.getFechaAlta());
    			List<Trabajadorbbdd> listaResultado = query.list();
    			
    			if (listaResultado.isEmpty()) {
					existe = false;
				}else {
					existe = true;
				}
    			
		return existe;
	}


	public void addTrabajador(Trabajadorbbdd trabajadorbbdd, SessionFactory sf, Session session) {
		Transaction tx = null;

		try {

			tx=session.beginTransaction();
			//trabajadorbbdd.setEmpresas(trabajadorbbdd.getEmpresas());
                        //System.out.println(trabajadorbbdd.getEmpresas());
                        //System.out.println(trabajadorbbdd.getEmpresas().getIdEmpresa());
                        //System.out.println(trabajadorbbdd.getEmpresas().getNombre());
                        //trabajadorbbdd.getEmpresas().setIdEmpresa(1742);
                        
                        //Le buscamos el id de la empresa existente que le machea
                        String consultaHQL = "FROM Empresas e WHERE e.cif = :nombre";
    			Query query = session.createQuery(consultaHQL);
    			query.setParameter("nombre", trabajadorbbdd.getEmpresas().getCif());
    			List<Empresas> listaResultado = query.list();
                        
                        trabajadorbbdd.getEmpresas().setIdEmpresa(listaResultado.get(0).getIdEmpresa());

                        
                        
                        
                       consultaHQL = "FROM Categorias c WHERE c.nombreCategoria = :nombre";
    			query = session.createQuery(consultaHQL);
    			query.setParameter("nombre", trabajadorbbdd.getCategorias().getNombreCategoria());
    			List<Categorias> listaResultado1 = query.list();
                        
                        trabajadorbbdd.getCategorias().setIdCategoria(listaResultado1.get(0).getIdCategoria());

                        
                        
			session.save(trabajadorbbdd);
			tx.commit();

			System.out.println(trabajadorbbdd.getNombre()+" added");

		} catch (Exception e) {
			if (tx != null) { //se ha iniciado pero no ha ido bien
				tx.rollback(); //si hay cualquier problema la transaccion no se hace
				e.printStackTrace();
			}
		}
		session.clear();
	}


	public void updateTrabajador(Trabajadorbbdd trabajadorbbdd, SessionFactory sf, Session session) {
            Transaction tx = null;
            
            String consultaHQL = "FROM Trabajadorbbdd t WHERE t.nombre = :nombre and t.nifnie = :nifnie and t.fechaAlta = :fechaAlta";
    			Query query = session.createQuery(consultaHQL);
    			query.setParameter("nombre", trabajadorbbdd.getNombre());
    			query.setParameter("nifnie", trabajadorbbdd.getNifnie());
    			query.setParameter("fechaAlta", trabajadorbbdd.getFechaAlta());
    			List<Trabajadorbbdd> listaResultado = query.list();
                                                      
                try {  
                    
                    tx=session.beginTransaction();
                    
                        int nuevoID = listaResultado.get(0).getIdTrabajador();//siempre hay una porque siempre pisamos sobre esa con  la nueva
                        trabajadorbbdd.setIdTrabajador(nuevoID); //cambio el id de la nueva con la que voy a actualizar
                        session.merge(trabajadorbbdd);
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
