package dating.model;

public class Message {
	public Customer owner;
	public String text;
	public Message(Customer owner) {
		this.owner = owner;
	}
}
