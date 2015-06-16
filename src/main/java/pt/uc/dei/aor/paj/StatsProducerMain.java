package pt.uc.dei.aor.paj;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.w3c.dom.Document;

import pt.uc.dei.aor.paj.handler.XMLValidation;
import pt.uc.dei.aor.paj.handler.XmlJmsConverter;
import pt.uc.dei.aor.paj.jmstopic.DurableTopicRecvClient;

public class StatsProducerMain {
	static Document doc;
	
	public static void main(String[] args) {
		try {
			(new DurableTopicRecvClient()).listenJMS();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	public static void gotNewMessage(TextMessage msg){
		try {
			doc = XmlJmsConverter.loadXMLFromString(msg.getText());
			XmlJmsConverter.createXML(doc, "latestnews.xml");
			if(XMLValidation.validateXMLSchema("src/main/resources/modelo.xsd", "latestnews.xml")){
				System.out.println("O XML recebido foi validado com sucesso!");
			} else {
				System.out.println("O XML recebido não é válido!");
			}
			
			System.out.println("nova mensagem: " );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
