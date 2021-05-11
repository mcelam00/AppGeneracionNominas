package modelo;
// Generated 08-mar-2021 14:06:09 by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * Trabajadorbbdd generated by hbm2java
 */
public class Trabajadorbbdd  implements java.io.Serializable {
	

     private int idTrabajador;
     
     private transient boolean prorrataExtra;
     private transient int antiguedad; //numero de trienios
     private transient boolean seHaceNomina = true; //si se hace la nomina por fecha o no (porque no esta contratado aun)


     

	private Categorias categorias;
     private Empresas empresas;
     private String nombre;
     private String apellido1;
     private String apellido2;
     private String nifnie;
     private String email;
     private Date fechaAlta;
     private String codigoCuenta;
     private String iban;
     private Set nominas = new HashSet(0); //las nóminas es un conjunto, un atributo de tipo set que lo tengo gracias a la foreign key, puedo recuperar su iterador, reccorrer todo el conjunto y mostrar el bruto de las nóminas por ejemplo
                                            //hacer el get para obtener el iterador y con un for o un while recorrerlo y mostrar el bruto de todas las nominas (nos esta pidiendo una instruccion de cada cosa)
    public Trabajadorbbdd() {
    }

	
    public Trabajadorbbdd(int idTrabajador, Categorias categorias, Empresas empresas, String nombre, String apellido1, String nifnie) {
        this.idTrabajador = idTrabajador;
        this.categorias = categorias;
        this.empresas = empresas;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.nifnie = nifnie;
    }
    public Trabajadorbbdd(int idTrabajador, Categorias categorias, Empresas empresas, String nombre, String apellido1, String apellido2, String nifnie, String email, Date fechaAlta, String codigoCuenta, String iban, Set nominas) {
       this.idTrabajador = idTrabajador;
       this.categorias = categorias;
       this.empresas = empresas;
       this.nombre = nombre;
       this.apellido1 = apellido1;
       this.apellido2 = apellido2;
       this.nifnie = nifnie;
       this.email = email;
       this.fechaAlta = fechaAlta;
       this.codigoCuenta = codigoCuenta;
       this.iban = iban;
       this.nominas = nominas;
    }
   
    public int getIdTrabajador() {
        return this.idTrabajador;
    }
    
    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }
    public Categorias getCategorias() {
        return this.categorias;
    }
    
    public void setCategorias(Categorias categorias) {
        this.categorias = categorias;
    }
    public Empresas getEmpresas() {
        return this.empresas;
    }
    
    public void setEmpresas(Empresas empresas) {
        this.empresas = empresas;
    }
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido1() {
        return this.apellido1;
    }
    
    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }
    public String getApellido2() {
        return this.apellido2;
    }
    
    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }
    public String getNifnie() {
        return this.nifnie;
    }
    
    public void setNifnie(String nifnie) {
        this.nifnie = nifnie;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public Date getFechaAlta() {
        return this.fechaAlta;
    }
    
    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
    public String getCodigoCuenta() {
        return this.codigoCuenta;
    }
    
    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }
    public String getIban() {
        return this.iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban;
    }
    public Set getNominas() {
        return this.nominas;
    }
    
    public void setNominas(Set nominas) {
        this.nominas = nominas;
    }

    public boolean getProrrataExtra() {
        return this.prorrataExtra;
    }
    
    public void setProrrataExtra(boolean prorrataExtra) {
        this.prorrataExtra = prorrataExtra;
    }

    public int getAntiguedad() {
		return this.antiguedad;
	}

	public void setAntiguedad(int antiguedad) {
		this.antiguedad = antiguedad;
	}
	public boolean getSeHaceNomina() {
		return this.seHaceNomina;
	}

	public void setSeHaceNomina(boolean seHaceNomina) {
		this.seHaceNomina = seHaceNomina;
	}
}


