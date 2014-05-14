package org.linkedusdl.agreement.mapping;

import java.io.File;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.us.isa.ada.document.AbstractDocument;
import es.us.isa.ada.wsag10.Agreement;
import es.us.isa.ada.wsag10.BusinessValueList;
import es.us.isa.ada.wsag10.Context;
import es.us.isa.ada.wsag10.CreationConstraints;
import es.us.isa.ada.wsag10.GeneralConstraint;
import es.us.isa.ada.wsag10.GuaranteeTerm;
import es.us.isa.ada.wsag10.OfferItem;
import es.us.isa.ada.wsag10.Penalty;
import es.us.isa.ada.wsag10.ServiceDescriptionTerm;
import es.us.isa.ada.wsag10.ServiceProperties;
import es.us.isa.ada.wsag10.StringSLO;
import es.us.isa.ada.wsag10.StringValueExpr;
import es.us.isa.ada.wsag10.Term;
import es.us.isa.ada.wsag10.Variable;

public class WriteXMLFromiAgree {
	
	protected DocumentBuilderFactory docFactory;
	protected DocumentBuilder docBuilder;
	protected DOMImplementation domImpl;
	
	protected TransformerFactory transformerFactory;
	protected Transformer transformer;

	protected Document doc;

	public WriteXMLFromiAgree(){
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.transformerFactory = TransformerFactory.newInstance();
		
		try {
			this.docBuilder = this.docFactory.newDocumentBuilder();
			this.transformer = this.transformerFactory.newTransformer();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		
		this.doc = this.docBuilder.newDocument();
	}
	
	public void writeFile(AbstractDocument doc, CreationConstraints cc, String destination){
		
		Agreement ag = (Agreement)doc;
		//create root element
		Element rootElement = getDoc().createElementNS("http://schemas.ggf.org/graap/2007/03/ws-agreement", "wsag:Template");
		getDoc().appendChild(rootElement);
		
		//add namespaces
		addAttr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", rootElement);
		addAttr("xmlns:xs", "http://www.w3.org/2001/XMLSchema", rootElement);
		addAttr("xsi:schemaLocation", "http://schemas.ggf.org/graap/2007/03/wsagreement", rootElement);
		addAttr("agreement_types.xsd", "http://www.w3.org/2001/XMLSchema XMLSchema.xsd", rootElement);
		
		//name of template
		addElement("wsag:Name", ag.getName(), rootElement);		
		
		//context 
		Context c = ag.getContext();
		addElement("wsag:AgreementResponder","Provider",addElement("wsag:Context", rootElement));
	
		//terms
		//compositorTerm		
		Collection<Term> cterms = ag.getAllTerms();
		Element terms = getDoc().createElement("wsag:Terms");
		addAttr("wsag:Name", "AWS-EC2", terms);
		Element compositor = addElement("wsag:All", terms);		
		rootElement.appendChild(terms);
		for( Term t : cterms){
			String termType = t.getClass().getSimpleName();
			
			if (termType.equals("GuaranteeTerm")) {
				GuaranteeTerm gTerm = (GuaranteeTerm) t;
				Element guaranteeTerm = getDoc().createElement("wsag:GuaranteeTerm");
				addAttr("wsag:Name", gTerm.getName(), guaranteeTerm);
				addAttr("wsag:Obligated", gTerm.getObligated(), guaranteeTerm);
				//add SLO
				addElement("wsag:CustomServiceLevel", ((StringSLO)gTerm.getSlo()).getSlo(), 
						addElement("wsag:ServiceLevelObjective", guaranteeTerm));				
				//add BSL
				BusinessValueList bsl = gTerm.getBvl();
				//penalty
				Element businessValueList = getDoc().createElement("wsag:BusinessValueList");
				for (Penalty p : bsl.getPenalties()){
					Element penalty = getDoc().createElement("wsag:Penalty");
					//assesmentInterval
					Element assesmentInterval = addElement("wsag:AssesmentInterval", penalty);
					addElement("wsag:TimeInterval", "", assesmentInterval);
					addElement("wsag:Count", "", assesmentInterval);
					//valueUnit
					addElement("wsag:ValueUnit", p.getValueUnit(), penalty);
					//valueExp
					addElement("wsag:ValueExpr", ((StringValueExpr)p.getVExp()).getValueExpr(), penalty);
					
					businessValueList.appendChild(penalty);
				}
				guaranteeTerm.appendChild(businessValueList);
				
				compositor.appendChild(guaranteeTerm);
				
			}else if (termType.equals("ServiceProperties")){
				ServiceProperties sps = (ServiceProperties) t;
				Element serviceProperties = getDoc().createElement("wsag:ServiceProperties");
				addAttr("wsag:Name", sps.getName(), serviceProperties);
				addAttr("wsag:ServiceName", sps.getServiceName(), serviceProperties);
				Element variables = getDoc().createElement("wsag:VariableSet");
				for (Variable v : sps.getVariableSet()){
					Element var = getDoc().createElement("wsag:Variable");
					addAttr("wsag:Name", v.getName(), var);
					addElement("wsag:Location", v.getLocation().getContent(),var);
					variables.appendChild(var);
				}
				serviceProperties.appendChild(variables);
				compositor.appendChild(serviceProperties);
			}else if (termType.equals("ServiceDescriptionTerm")){
				ServiceDescriptionTerm SDT = (ServiceDescriptionTerm) t;
				Element serviceDescriptionTerm = getDoc().createElement("wsag:ServiceDescriptionTerm");
				addAttr("wsag:Name", "SDT_"+SDT.getServiceName(),  serviceDescriptionTerm);
				addAttr("wsag:ServiceName", SDT.getServiceName(),  serviceDescriptionTerm);
				for (OfferItem offi : SDT.getOfferItems()){
					Element offerItem = getDoc().createElement("wsag:OfferItem");
					addAttr("name", offi.getName(), offerItem);
					addAttr("wsag:Metric", offi.getRestriction().getBaseType(), offerItem);
					serviceDescriptionTerm.appendChild(offerItem);
				}
				compositor.appendChild(serviceDescriptionTerm);
			}
			
		}
		
		Element creationConstraint = getDoc().createElement("wsag:CreationConstraints");
		int i= 0;
		for (GeneralConstraint gc: cc.getConstraints()){
			Element Constraint = getDoc().createElement("wsag:Constraint");
			Element name =getDoc().createElement("Name");
			name.appendChild(getDoc().createTextNode("C"+i));
			i++;
			Element content = getDoc().createElement("Content");
			content.appendChild(getDoc().createTextNode(gc.getConstraint()));
			Constraint.appendChild(name); Constraint.appendChild(content);
			creationConstraint.appendChild(Constraint);
		}
		
		rootElement.appendChild(creationConstraint);
		DOMSource source = new DOMSource(getDoc());
		StreamResult result = new StreamResult(new File(destination));
		//StreamResult result = new StreamResult(System.out);
		
		
		try {
			this.transformer.transform(source, result);
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("Document created");
	}
	
	public Document getDoc() {
		return this.doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	private void addAttr(String name, String value, Element element){
		Attr attr = getDoc().createAttribute(name);
		attr.setValue(value);
		element.setAttributeNode(attr);
	}
	
	private Element addElement (String elementName, Element root){
		Element e = getDoc().createElement(elementName);
		root.appendChild(e);
		return e;
	}
	private Element addElement (String elementName, String textNode, Element root){
		Element e = getDoc().createElement(elementName);
		e.appendChild(getDoc().createTextNode(textNode));
		root.appendChild(e);
		return e; 
	}
}
