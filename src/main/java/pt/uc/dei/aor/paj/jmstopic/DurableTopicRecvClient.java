package pt.uc.dei.aor.paj.jmstopic;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSubscriber;
import javax.jms.TopicSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DurableTopicRecvClient implements MessageListener
{
    TopicConnection conn = null;
    TopicSession session = null;
    Topic topic = null;
    
    /**
     * SETUP JMS CONNECTION
     * @throws JMSException
     * @throws NamingException
     */
    public void setupPubSub() throws JMSException, NamingException
    {
        InitialContext iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("jms/RemoteConnectionFactory");

        TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
        conn = tcf.createTopicConnection("user", "123");
        conn.setClientID("jms-stats");
        topic = (Topic) iniCtx.lookup("jms/topic/PlayTopic");

        session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE); 
        conn.start();
    }
    
    
    @Override
    public void onMessage(Message msg) {
    	System.out.println("////////////////////////////////////// onMessage ///////////////////////////////////////");
    	TextMessage tmsg = (TextMessage) msg;
    	try {
			System.out.println("Got message: " + tmsg.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	pt.uc.dei.aor.paj.StatsProducerMain.gotNewMessage(tmsg);

    	System.out.println("////////////////////////////////////// FIM onMessage ///////////////////////////////////////");
    }    
    
    private void recvAsync() throws JMSException, NamingException {
        System.out.println("Begin recvAsync");
        // Setup the pub/sub connection, session
        setupPubSub();
        TopicSubscriber recv = session.createDurableSubscriber(topic, "jms-stats");
        recv.setMessageListener(this);
    }

    
    public void stop() throws JMSException {
        conn.stop();
        session.close();
        conn.close();
    }
    
    
    public void listenJMS() throws JMSException, NamingException, IOException {
    	System.out.println("Begin DurableTopicRecvClient, now=" + System.currentTimeMillis());
    	recvAsync();
    	stop();
    	System.out.println("End DurableTopicRecvClient");
    }
    
}
