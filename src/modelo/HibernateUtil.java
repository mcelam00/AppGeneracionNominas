package modelo;
//
/**
 * Created by yusufcakmak on 8/3/15.
 */
import java.util.Properties;
import org.hibernate.Session;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
 
    private static StandardServiceRegistry registry;

    //XML based configuration
    private static SessionFactory sessionFactory;


    private static SessionFactory buildSessionFactory() { //metodo que es la construccion del session factory
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml"); //fichero de configuracion
            System.out.println("Hibernate Configuration loaded");

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            System.out.println("Hibernate serviceRegistry created");

            registry = (StandardServiceRegistry) serviceRegistry; //lo registra en el sistema
            
            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
           
            return sessionFactory; //nos devuelve un session factory
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }





    public static SessionFactory getSessionFactory() { //para conectar a la base de datos
        if(sessionFactory == null) sessionFactory = buildSessionFactory(); //con llamar aquí la primera vez ya lo crea y lo mantiene, y en llamadas sucesivas lo retorna
        return sessionFactory;
    }


    public static void cerrarSessionFactory(){
        
        if (!sessionFactory.isClosed())     {
            sessionFactory.close();
            
        }
    }
    
    
        public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }    
        }
    
}
