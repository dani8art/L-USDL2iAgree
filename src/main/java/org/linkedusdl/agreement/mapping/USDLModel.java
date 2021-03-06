package org.linkedusdl.agreement.mapping;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.openrdf.rdf2go.RepositoryModelFactory;
import org.openrdf.rio.RDFParseException;

import com.viceversatech.rdfbeans.RDFBeanManager;

public class USDLModel {
	
	private ModelFactory modelFactory;
	private Model model;
	
	//private InputStream source; // inputStream para cargar desde la clase y no desde el test
	private RDFBeanManager manager;
	
	public USDLModel (InputStream sourcein) throws ModelRuntimeException, IOException, RDFParseException{//cambiar atributo a String
		// Loads RDF2Go model
		RDF2Go.register(new RepositoryModelFactory());
		this.modelFactory = RDF2Go.getModelFactory();
		this.model = modelFactory.createModel();
		this.model.open();
					
		// Reads .ttl file into RDF2Go model
			
		this.model.readFrom(sourcein, Syntax.Turtle);
	
		// Initializes the bean manager
		this.manager = new RDFBeanManager(this.model);		
	} 
	
	public RDFBeanManager getManager(){
		return this.manager;
	}
	
	public void closeModel(){
		this.model.close();
	}
}
