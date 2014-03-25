package org.linkedusdl.agreement.mapping;

import java.io.File;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.Namespace;
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
import es.us.isa.ada.io.IDocumentWriter;
import es.us.isa.ada.wsag10.AbstractAgreementDocument;
import es.us.isa.ada.wsag10.Context;
import es.us.isa.ada.wsag10.GuaranteeTerm;
import es.us.isa.ada.wsag10.Term;

public class WriteXMLFromiAgree implements IDocumentWriter {
	
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
	
	public void writeFile(AbstractDocument doc, String destination){
		
		//create root element
		Element rootElement = getDoc().createElementNS("http://schemas.ggf.org/graap/2007/03/ws-agreement", "wsag:Template");
		getDoc().appendChild(rootElement);
		
		//add namespaces
		addAttr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", rootElement);
		addAttr("xmlns:xs", "http://www.w3.org/2001/XMLSchema", rootElement);
		addAttr("xsi:schemaLocation", "http://schemas.ggf.org/graap/2007/03/wsagreement", rootElement);
		addAttr("agreement_types.xsd", "http://www.w3.org/2001/XMLSchema XMLSchema.xsd", rootElement);
		
		Element e = getDoc().createElement("wsag:Name");
		e.appendChild(getDoc().createTextNode("AmazonEC2"));
		rootElement.appendChild(e);
		
		//context 
		Context c = ((AbstractAgreementDocument) doc).getContext();
		Element context = getDoc().createElement("wsag:context");
		Element responder = getDoc().createElement("wsag:AgreementResponder");
		responder.appendChild(getDoc().createTextNode("Provider"));
		context.appendChild(responder);
		rootElement.appendChild(context);
		
		//terms
		Collection<Term> cterms = ((AbstractAgreementDocument) doc).getAllTerms();
		Element terms = getDoc().createElement("wsag:Terms");
		addAttr("wsag:Name", "AWS-EC2", terms);
		rootElement.appendChild(terms);
		for( Term t : cterms){
			String termType = t.getClass().getSimpleName();
			
			if (termType.equals("GuaranteeTerm")) {
				GuaranteeTerm gTerm = (GuaranteeTerm) t;
				Element guaranteeTerm = getDoc().createElement("wsag:GuaranteeTerm");
				addAttr("wsag:Name", gTerm.getName(), guaranteeTerm);
				addAttr("wsag:Obligated", gTerm.getObligated(), guaranteeTerm);
				terms.appendChild(guaranteeTerm);
			}
			
		}
		
		DOMSource source = new DOMSource(getDoc());
		//StreamResult result = new StreamResult(new File(destination+"/AmazonEC3.xml"));
		StreamResult result = new StreamResult(System.out);
		
		
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
}
