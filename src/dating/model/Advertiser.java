package dating.model;

import java.util.ArrayList;

/**
 * 
 * @author Jerome
 *
 */
public class Advertiser extends Customer {
	public String advertText = "";
	public PartnerCriteria partnerCriteria = new PartnerCriteria();
	public ArrayList<Message> messages = new ArrayList<Message>();

	/**
	 * Constructor
	 */
	public Advertiser(String username) {
		super(username);
		this.type = Type.ADVERTISER;
	}

	/**
	 * 
	 * @param responder
	 * @return
	 */
	public boolean match(Responder responder) {
		return this.partnerCriteria.match(responder);
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean addMessage(Message message) {
		this.messages.add(message);
		return true;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean removeMessage(Message message) {
		this.messages.remove(message);
		return true;
	}
}
