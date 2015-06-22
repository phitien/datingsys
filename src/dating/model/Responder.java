package dating.model;

/**
 * Responder
 * @author Jerome
 *
 */
public class Responder extends Customer {

	/**
	 * Constructor
	 * @param username
	 */
	public Responder(String username) {
		super(username);
		this.type = Type.RESPONDER;
	}

	/**
	 * Send Message to an advertiser
	 * @param advertiser
	 * @param text
	 * @return
	 */
	public boolean sendMessage(Advertiser advertiser, String text) {
		Message message = new Message(this);
		message.text = text;
		return dating.service.Customer.receiveMessage(advertiser, message);
	}
}
