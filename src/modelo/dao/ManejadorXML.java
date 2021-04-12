package modelo.dao;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import modelo.Trabajadorbbdd;

public class ManejadorXML {

	private String RUTA_ERRORESXML = "./resources/Errores.xml"; 
	private String RUTA_ERRORESCCCXML = "./resources/ErroresCCC.xml"; 

	

	public void escribirErroresXML(ArrayList<Trabajadorbbdd> trabajadoresNifErroneo) {
		
		
		 try {

		        Element trabajadores = new Element("Trabajadores"); //creamos el nodo raíz trabajadores
		        Document documento = new Document(trabajadores);
		        
		        for (Trabajadorbbdd t : trabajadoresNifErroneo) { //recorremos los trabajadores erroneos a añadir
					
		        	 Element trabajador = new Element("Trabajador");
				     trabajador.setAttribute(new Attribute("id", String.valueOf(t.getIdTrabajador()+1))); //le sumamos 1 porque el id lo contamos desde 1 y la excel en la fila 1 tiene los titulos
				     trabajador.addContent(new Element("Nombre").setText(t.getNombre()));
				     trabajador.addContent(new Element("PrimerApellido").setText(t.getApellido1()));
				     trabajador.addContent(new Element("SegundoApellido").setText(t.getApellido2()));
				     trabajador.addContent(new Element("Empresa").setText(t.getEmpresas().getNombre()));
				     trabajador.addContent(new Element("Categoria").setText(t.getCategorias().getNombreCategoria()));
				     documento.getRootElement().addContent(trabajador);

				}
		        
		        XMLOutputter xmlOutput = new XMLOutputter();

		        xmlOutput.setFormat(Format.getPrettyFormat());
				//xmlOutput.output(documento, new FileWriter(RUTA_ERRORESXML)); Pinta mal los acentos
		        xmlOutput.output(documento, new FileOutputStream(RUTA_ERRORESXML));


		        
		      } catch (IOException io) {
		        System.out.println("ERROR DE ESCRITURA EN EL XML: Archivo Errores.xml");
		      }
		
		
		
	}



     
	
	public void escribirErroresCCCXML(ArrayList<Trabajadorbbdd> trabajadoresCCCErroneas) {
		
		
		 try {

		        Element cuentas = new Element("Cuentas"); //creamos el nodo raíz 
		        Document documento = new Document(cuentas);
		        
		        for (Trabajadorbbdd t : trabajadoresCCCErroneas) { //recorremos los trabajadores erroneos a añadir
					
		        	 Element cuenta = new Element("Cuenta");
		        	 cuenta.setAttribute(new Attribute("id", String.valueOf(t.getIdTrabajador()+1))); //le sumamos 1 porque el id lo contamos desde 1 y la excel en la fila 1 tiene los titulos
		        	 cuenta.addContent(new Element("Nombre").setText(t.getNombre()));
		        	 cuenta.addContent(new Element("Apellidos").setText(t.getApellido1() + " " + t.getApellido2()));
		        	 cuenta.addContent(new Element("Empresa").setText(t.getEmpresas().getNombre()));
		        	 cuenta.addContent(new Element("CCCErroneo").setText(t.getCodigoCuenta()));
		        	 cuenta.addContent(new Element("IBANCorrecto").setText(t.getIban()));
				     documento.getRootElement().addContent(cuenta);

				}
		        
		        XMLOutputter xmlOutput = new XMLOutputter();

		        xmlOutput.setFormat(Format.getPrettyFormat());
				//xmlOutput.output(documento, new FileWriter(RUTA_ERRORESXML)); Pinta mal los acentos
		        xmlOutput.output(documento, new FileOutputStream(RUTA_ERRORESCCCXML));


		        
		      } catch (IOException io) {
		        System.out.println("ERROR DE ESCRITURA EN EL XML: Archivo Errores.xml");
		      }
		
		
	}
	

}
	

