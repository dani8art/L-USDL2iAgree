package org.linkedusdl.agreement.model;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.linkedusdl.agreement.mapping.AmazonUSDL2iAgreeMapper;
import org.linkedusdl.agreement.mapping.USDLModel;
import org.linkedusdl.agreement.mapping.WriteXMLFromiAgree;
import org.linkedusdl.agreement.mapping.WriteiAgreeFromiAgreeModel;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.openrdf.rio.RDFParseException;

import com.viceversatech.rdfbeans.exceptions.RDFBeanException;

import es.us.isa.ada.wsag10.Agreement;


public class WriterTest {

	@Test
	public void newTest() throws RDFBeanException, ModelRuntimeException, IOException {
		try{
			USDLModel model = new USDLModel(getClass().getResourceAsStream("amazonEC2.ttl"));
			AmazonUSDL2iAgreeMapper mapping = new AmazonUSDL2iAgreeMapper(model);
			WriteiAgreeFromiAgreeModel writer = new WriteiAgreeFromiAgreeModel();
			
			Agreement ag = mapping.transform();
			writer.writeFile(ag, mapping.getCC(),mapping.getMetrics(), "src/test/resources/org/linkedusdl/agreement/iAgree/"+"AmazonEC2.iagreetemplate");
			
			model.closeModel();
		}catch(ModelRuntimeException e){
			System.out.println("cath model");
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			System.out.println("cath parser");
		}
	}	
}
