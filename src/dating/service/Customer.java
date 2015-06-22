package dating.service;

import java.util.ArrayList;

import dating.model.*;

public class Customer {
	public static ArrayList<dating.model.Customer> customers = new ArrayList<dating.model.Customer>();

	/**
	 * Check username already added to system
	 * 
	 * @param username
	 * @return boolean
	 */
	public static boolean hasCustomer(String username) {
		for (dating.model.Customer customer : customers) {
			if (customer.getUsername().equalsIgnoreCase(username)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * function to get the customer by username
	 * 
	 * @param username
	 * @return dating.model.Customer
	 */
	public static dating.model.Customer getCustomer(String username) {
		for (dating.model.Customer customer : customers) {
			if (customer.getUsername().equalsIgnoreCase(username)) {
				return customer;
			}
		}
		return null;
	}

	public static ArrayList<dating.model.Customer> getAllCustomers() {
		return customers;
	}

	public static ArrayList<dating.model.Advertiser> getAllAdvertisers() {
		ArrayList<dating.model.Advertiser> result = new ArrayList<dating.model.Advertiser>();
		for (dating.model.Customer customer : customers) {
			if (customer instanceof dating.model.Advertiser) {
				result.add((Advertiser) customer);
			}
		}
		return result;
	}

	public static ArrayList<dating.model.Responder> getAllResponders() {
		ArrayList<dating.model.Responder> result = new ArrayList<dating.model.Responder>();
		for (dating.model.Customer customer : customers) {
			if (customer instanceof dating.model.Responder) {
				result.add((Responder) customer);
			}
		}
		return result;
	}

	public static ArrayList<dating.model.Advertiser> getMatchingAdvertisersOfResponder(
			Responder responder) {
		ArrayList<dating.model.Advertiser> result = new ArrayList<dating.model.Advertiser>();
		ArrayList<Advertiser> allAdvertisers = Customer.getAllAdvertisers();
		for (Advertiser advertiser : allAdvertisers) {
			if (advertiser.match(responder)) {
				result.add(advertiser);
			}
		}
		return result;
	}

	public static dating.model.Advertiser createAdvertiser(String username) {
		if (!hasCustomer(username)) {
			return new Advertiser(username);
		}
		return null;
	}

	public static dating.model.Responder createResponder(String username) {
		if (!hasCustomer(username)) {
			return new Responder(username);
		}
		return null;
	}

	public static boolean remove(dating.model.Customer customer) {
		return Customer.customers.remove(customer);
	}

	public static boolean receiveMessage(Advertiser advertiser, Message message) {
		if (advertiser != null) {
			return advertiser.addMessage(message);
		}
		return false;
	}

	public static boolean removeMessage(Advertiser advertiser, Message message) {
		if (advertiser != null) {
			return advertiser.removeMessage(message);
		}
		return false;
	}

	/**
	 * Login function: Match username and password then return matched customer
	 * 
	 * @param username
	 * @param password
	 * @return dating.model.Customer
	 */
	public static dating.model.Customer login(String username, String password) {
		dating.model.Customer customer = getCustomer(username);
		if (customer != null && customer.password.equalsIgnoreCase(password)) {
			return customer;
		}
		return null;
	}
}
