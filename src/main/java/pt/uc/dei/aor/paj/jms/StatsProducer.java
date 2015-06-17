package pt.uc.dei.aor.paj.jms;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import pt.uc.dei.aor.paj.handle.DurableTopicRecvClient;
import pt.uc.dei.aor.paj.handle.StatLog;
import pt.uc.dei.aor.paj.handle.XMLValidation;
import pt.uc.dei.aor.paj.handle.XmlJmsConverter;

public class StatsProducer {
	
	org.w3c.dom.Document doc;

	public static void main(String[] args) {
		
		try {
			new DurableTopicRecvClient().listen();
		} catch (JMSException | NamingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void gotNewMessage(TextMessage msg){		
		URL url = StatsProducer.class.getResource("/modelo.xsd");		
		try {
			String tmsg = msg.getText();
			System.out.println("mensagem recebida com " + tmsg.length());
			doc = XmlJmsConverter.loadXMLFromString(tmsg);
			XmlJmsConverter.createXML(doc, "latestnews.xml");
			if(XMLValidation.validateXMLSchema(url, "latestnews.xml")){
				System.out.println("O XML recebido foi validado com sucesso!");
			} else {
				System.out.println("O XML recebido não é válido!");
			}
			System.out.println("Nova mensagem recebida\n"
					+ "Ficheiro \"latestnews.xml\" atualizado!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int numeroNoticias = recolheDatas().size();
		escreveLog(numeroNoticias);
	}
	
	public void escreveLog(int numeroNoticias){
		StatLog log = new StatLog();
		
		DateFormat formato = new SimpleDateFormat( "yyyy-MM-dd' 'HH:mm:ss" );
		Date data_atual = new Date();
				
		log.adicionarLinha("O feed recebido em " + formato.format(data_atual) + 
				" contém " + numeroNoticias + " noticias com menos de 12 horas");
	}
	
	public ArrayList<GregorianCalendar> recolheDatas(){
		File file = new File("latestnews.xml");
		HashSet<GregorianCalendar> datas = new HashSet<>();
		
		Document doc;
		
		try {
			doc = Jsoup.parse(file, "UTF-8");
			Elements dateTags =  doc.getElementsByTag("data");
			
			for (Element element : dateTags) {
				String d = element.text();
				datas.add(StringToGC(d));
			}
			
		} catch (IOException e) {
			System.out.println("Falha ao recolher as data dos artigos");
		}
				
		GregorianCalendar dataatual = (GregorianCalendar) Calendar.getInstance();
		dataatual.add(GregorianCalendar.HOUR_OF_DAY, -12);
		
		ArrayList<GregorianCalendar> datasFinais = new ArrayList<GregorianCalendar>();
		
		for (GregorianCalendar dataartigo : datas) {
			if(dataartigo.after(dataatual))
				datasFinais.add(dataartigo);
		}
				
		return datasFinais;
	}
	
	public GregorianCalendar StringToGC(String data){
		data = data.substring(0, 19);
		Calendar calendar = new GregorianCalendar();
		try {
			DateFormat format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
			Date date = format.parse( data );
			calendar.setTime( date );
		} catch (Exception e) {
			System.out.println("Erro ao converter a data");
		}
		return (GregorianCalendar) calendar;
		
	}

}
