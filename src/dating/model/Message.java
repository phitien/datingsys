package dating.model;

/**
 * Message
 * 
 * @author Jerome
 *
 */
public class Message {
	public Customer owner;
	public String text;
	public Message(Customer owner) {
		this.owner = owner;
	}
}
