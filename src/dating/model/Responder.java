package dating.model;

public class Responder extends Customer {

	public Responder(String username) {
		super(username);
		this.type = Type.RESPONDER;
	}

	public boolean sendMessage(Advertiser advertiser, String text) {
		Message message = new Message(this);
		message.text = text;
		return dating.service.Customer.sendMessage(advertiser, message);
	}
}
