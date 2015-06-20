package dating.model;

public class Responder extends Customer {

	public Responder() {
		super();
		this.type = Type.RESPONDER;
	}

	public boolean sendMessage(Advertiser advertiser, String text) {
		Message message = new Message();
		message.text = text;
		message.owner = this;
		return dating.service.Customer.sendMessage(advertiser, message);
	}
}
