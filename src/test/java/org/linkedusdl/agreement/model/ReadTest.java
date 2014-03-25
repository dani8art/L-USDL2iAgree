package org.linkedusdl.agreement.model;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.junit.Test;
import org.linkedusdl.agreement.mapping.USDL2iAgreeClassMapping;
import org.linkedusdl.agreement.mapping.USDLModel;
import org.linkedusdl.agreement.mapping.WriteXMLFromiAgree;
import org.ontoware.rdf2go.exception.ModelRuntimeException;

import com.viceversatech.rdfbeans.exceptions.RDFBeanException;

import es.us.isa.ada.wsag10.Agreement;
import es.us.isa.ada.wsag10.ServiceProperties;
import es.us.isa.ada.wsag10.StringSLO;
import es.us.isa.ada.wsag10.Variable;

public class ReadTest {

	@Test
	public void newTest() throws RDFBeanException, ModelRuntimeException, IOException {
		try{
			USDLModel model = new USDLModel(getClass().getResourceAsStream("test.ttl"));
			USDL2iAgreeClassMapping mapping = new USDL2iAgreeClassMapping();
			WriteXMLFromiAgree writer = new WriteXMLFromiAgree();
			
			Collection<ServiceOffering> services = model.getServiceOfferings();
			
			for(ServiceOffering so : services){
				printServiceOffering(so);
				Agreement ag = mapping.transform(so);
				writer.writeFile(ag, "src/test/resources/org/linkedusdl/agreement/xml/");	
			}
			
			model.closeModel();
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}	

	
	private void printServiceOffering(ServiceOffering so) {
		System.out.println("Service Offering:" + so.getId());
		Agreement ag = new Agreement();
		ag.setName(so.getId());
		try{
			for (GuaranteeTerm gt: so.getComplyWith()) {
				es.us.isa.ada.wsag10.GuaranteeTerm gr = new es.us.isa.ada.wsag10.GuaranteeTerm();
				
				System.out.println("- Guarantee Term: " + gt.getId());
					gr.setName(gt.getId());
					
				AgreementCondition a = gt.getGuarantees();
					gr.setObligated("Provider");
					StringSLO slo = new StringSLO();
					slo.setSlo( a.getRefersTo().getId() + " > " + a.getHasValue().getHasValueFloat());
					gr.setSlo(slo);
				System.out.println("----- Guarantees: ");
				System.out.println("------- Type: " + a.getType());
					ag.getAllTerms().add(gr);
				System.out.println("------- hasValue: " + a.getHasValue().getType());
				System.out.println("--------- Value: " + a.getHasValue().getHasValueFloat());			
				System.out.println("------- refersTo: " + a.getRefersTo().getId());
					ServiceProperties sp = new ServiceProperties();
					Variable v = new Variable();
					v.setName(a.getRefersTo().getId());					
					sp.getVariableSet().add(v);					
					ag.getAllTerms().add(sp);
				System.out.println("---------hasMetric: " + a.getRefersTo().getHasMetric().getId());
				System.out.println("-----------Expression: "+ a.getRefersTo().getHasMetric().getHasExpression());
				AgreementCondition b = gt.getHasCompensation();
				System.out.println("----- Compensation: " + b.getId());
				System.out.println("------- refersTo: "+ b.getRefersTo().getId());
				
			
			}
			for (URI uri : so.getIncludes()){
				System.out.println("- Includes: "+ uri);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}


}
