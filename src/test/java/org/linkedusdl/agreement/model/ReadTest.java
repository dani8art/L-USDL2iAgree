package org.linkedusdl.agreement.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.openrdf.rdf2go.RepositoryModelFactory;

import com.viceversatech.rdfbeans.RDFBeanManager;
import com.viceversatech.rdfbeans.exceptions.RDFBeanException;

public class ReadTest {

	@Test
	public void newTest() throws RDFBeanException, ModelRuntimeException, IOException {
		// Loads RDF2Go model
		RDF2Go.register(new RepositoryModelFactory());
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		Model model = modelFactory.createModel();
		model.open();
		
		// Reads amazonEC2.ttl file into RDF2Go model
		InputStream source = getClass().getResourceAsStream("amazonEC2.ttl");
		model.readFrom(source, Syntax.Turtle);

		// Initializes the bean manager
		RDFBeanManager manager = new RDFBeanManager(model);

		// Gets the service offering that corresponds with the URI :amazonEC2ServiceOfferingM1LargeInstance
		ServiceOffering so = manager.create("http://purl.org/cloudComputing/amazonEC2#amazonEC2ServiceOfferingM1LargeInstanceType",
						ServiceOffering.class);
		printServiceOffering(so);

		// Checks everything is fine
		Assert.assertEquals("http://purl.org/cloudComputing/amazonEC2#amazonEC2ServiceOfferingM1LargeInstanceType", so.getId());
		Assert.assertEquals(1, so.getComplyWith().size());
		Assert.assertEquals("http://purl.org/cloudComputing/amazonEC2#ec2ServiceCommitment", so.getComplyWith().iterator().next().getId());
		
		model.close();
	}	

	
	private void printServiceOffering(ServiceOffering so) {
		System.out.println("Service Offering:" + so.getId());

		for (GuaranteeTerm gt: so.getComplyWith()) {
			System.out.println("- Guarantee Term: " + gt.getId());
			AgreementCondition a = gt.getGuarantees();
			System.out.println("----- Guarantees: ");
			System.out.println("------- Type: " + a.getType());
			System.out.println("------- hasValue: " + a.getHasValue().getType());
			System.out.println("--------- Value: " + a.getHasValue().getPropertyValue());			
			System.out.println("------- refersTo: " + a.getRefersTo().getId());
			AgreementCondition b = gt.getHasCompensation();
			System.out.println("----- Compensation: " + b.getId());
			System.out.println("------- refersTo: "+ b.getRefersTo());
			
		}
		for (URI uri : so.getIncludes()){
			System.out.println("- Includes: "+ uri);
		}
	}


}
