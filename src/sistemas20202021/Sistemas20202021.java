/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemas20202021;

import java.util.List;
import modelo.HibernateUtil;
import modelo.Trabajadorbbdd;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Mario
 */
public class Sistemas20202021 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //Vamos a hacer un ejemplo en el main, pero no debería ser aqui sino en una clase DAO --> si un metodo consulta del trabajador por nif tendria un trabajadorDAO y ahi estaria el método
        //los .java van al modelo, las clases dao van a la parte del modelo de acceso a datos
        
        //HibernateUtil me retorna un tipo SessionFactory; Necesitare 3 atributos, uno de tipo SessoinFactory, otro Session y otro Transaccion(para inserciones y eliminaciones) una query no necesita transacciones
        //Acostumbrarse a usar try catch para capturar exceptions 
        
        SessionFactory sf = null;
        Session session = null;
        Transaction tx = null; //control+shift+i para importar librerias
        
        
        try{
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
            
           String dni = "09741138V";
           Query query = session.createQuery(consuta2HQL); //aqui cuando compongo la consulta con el string de arriba, al tener un parámetro, tengo que dárselo a la query
           query.setParameter("parametro", dni);//antes de ejecutarla le cargo el parametro
           
           List<Trabajadorbbdd> listaResultado = query.list(); //devuelve una lista de la base de datos sobre el objeto perdido, trabajadoresbbdd, en este caso retorna todos los trabajadores de la base de datos
            
            
            //PRECONDICION: tengo toda la info de los trabajadores de la base de datos
                
                //Pinto de cada uno de ellos, su nombre y su NIF
            for(int i = 0; i < listaResultado.size(); i++){
                System.out.println("Nombre: " + listaResultado.get(i).getNombre());
                System.out.println("NIF: " + listaResultado.get(i).getNifnie());
                System.out.println("=============================================================");
            }
            
            //Destruyo la sesion con Hibernate
            HibernateUtil.shutdown();
            
            
            
        }catch(Exception e){
            System.out.println("Ha ocurrido un error: " + e.getMessage());
        }
        
        
        
        
        
        
        
    }
    
}
