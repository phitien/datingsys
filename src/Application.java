import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import dating.model.Customer.*;
import dating.model.Advertiser;
import dating.model.AgeRange;
import dating.model.IncomeRange;
import dating.model.Message;
import dating.model.PartnerCriteria;
import dating.model.Responder;
import dating.service.Customer;

public class Application {

	public enum APP_STATE {
		DASHBOARD, SHOW_CUSTOMERS, //
		ADDING_CUSTOMER, REMOVING_CUSTOMER, LOGIN, SENDING_MESSAGE //
	}

	public static APP_STATE state = APP_STATE.DASHBOARD;
	public static HashMap<String, APP_STATE> actionsMap = new HashMap<String, APP_STATE>();
	public static dating.model.Customer currentCustomer;

	public static void main(String[] args) throws IOException {
		setupCustomers();
		printInstruction();
		setupMaps();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		while (true) {
			process(bufferedReader);
		}
	}

	public static void setupMaps() {
		actionsMap.put("-1", APP_STATE.DASHBOARD);
		actionsMap.put("1", APP_STATE.SHOW_CUSTOMERS);
		actionsMap.put("2", APP_STATE.ADDING_CUSTOMER);
		actionsMap.put("3", APP_STATE.REMOVING_CUSTOMER);
		actionsMap.put("4", APP_STATE.LOGIN);
		actionsMap.put("5", APP_STATE.SENDING_MESSAGE);
	}

	public static void printInstruction() throws IOException {
		System.out.println("Please choose one option below:");
		switch (state) {
		case DASHBOARD:
			if (Customer.customers.size() > 0) {
				System.out.println("1. Print list of customers");
			}
			System.out.println("2. Add a customer");
			if (Customer.customers.size() > 0) {
				System.out.println("3. Remove a customer");
				System.out.println("4. Login");
			}
			break;
		default:
			System.out.println("-1. Dashboard");
			break;
		}
	}

	public static void reset() throws IOException {
		currentCustomer = null;
		setState("0");
		printInstruction();
	}

	private static String readParam(BufferedReader bufferedReader)
			throws IOException {
		return bufferedReader.readLine().trim();
	}

	public static void process(BufferedReader bufferedReader)
			throws IOException {
		String input = readParam(bufferedReader);
		setState(input);
		printInstruction();
		switch (state) {
		case SHOW_CUSTOMERS:
			printCustomers(bufferedReader);
			break;
		case ADDING_CUSTOMER:
			createCustomer(bufferedReader);
			break;
		case REMOVING_CUSTOMER:
			removeCustomer(bufferedReader);
			break;
		case LOGIN:
			login(bufferedReader);
			break;
		case DASHBOARD:
		default:
			break;
		}
	}

	private static void removeCustomer(BufferedReader bufferedReader) {
		if (Customer.customers.size() > 0) {
			while (true) {
				try {
					System.out
							.println("Please enter customer index you want to delete:");
					String input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						reset();
						break;
					}
					int idx = Integer.parseInt(input);
					if (idx >= 0 && idx < Customer.customers.size()) {
						Customer.customers.remove(idx);
						reset();
						break;
					} else {
						System.out.println("There is no customer of the index "
								+ idx);
						reset();
						break;
					}
				} catch (Exception e) {
				}
			}
		}
	}

	private static void login(BufferedReader bufferedReader) {
		if (Customer.customers.size() > 0) {
			while (true) {
				try {
					System.out.println("Please enter username:");
					String username = readParam(bufferedReader);
					if (username.equalsIgnoreCase("-1")) {
						reset();
						break;
					}
					if (Customer.hasCustomer(username)) {
						System.out.println("Please enter password:");
						String password = readParam(bufferedReader);
						if (password.equalsIgnoreCase("-1")) {
							reset();
							break;
						}
						dating.model.Customer customer = Customer.login(
								username, password);
						if (customer != null) {
							currentCustomer = customer;
							if (currentCustomer instanceof dating.model.Advertiser) {
								printAdvertiser((dating.model.Advertiser) currentCustomer);
								printAdvertiserMessages((dating.model.Advertiser) currentCustomer);
							} else if (currentCustomer instanceof dating.model.Responder) {
								printResponder((dating.model.Responder) currentCustomer);
								printMatchingAdvertisersOfResponder((dating.model.Responder) currentCustomer);
								while (true) {
									System.out
											.println("Enter 5 to send message to an advertiser.");
									System.out
											.println("Enter -1 to logout(go back).");
									String input = readParam(bufferedReader);
									if (input.equalsIgnoreCase("-1")) {
										reset();
										break;
									}
									if (input.equalsIgnoreCase("5")) {
										// TODO send message to an advertiser
									}
								}
							}
						} else {
							System.out.println("Password is incorrect.");
							reset();
						}
						break;
					} else {
						System.out
								.println("User does not exist. Enter -1 to go back or re-enter user name again");
					}
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Create Customer function: user need to select which type of customer
	 * (advertiser or responder) to be created
	 * 
	 * @param bufferedReader
	 * @throws IOException
	 */
	private static void createCustomer(BufferedReader bufferedReader)
			throws IOException {
		System.out.println("Which customer you want to create:");
		System.out.println("1. Advertiser");
		System.out.println("2. Customer");
		while (true) {
			try {
				String input = readParam(bufferedReader);
				if (input.equalsIgnoreCase("-1")) {
					reset();
					break;
				}
				int idx = Integer.parseInt(input);
				if (idx == 1) {
					createAdvertiser(bufferedReader);
					break;
				} else if (idx == 2) {
					createResponder(bufferedReader);
					break;
				}
			} catch (Exception e) {
				System.out.println("Which customer you want to create:");
				System.out.println("1. Advertiser");
				System.out.println("2. Customer");
			}
		}
	}

	/**
	 * Create responder follow the instructions
	 * 
	 * @param bufferedReader
	 * @throws IOException
	 */
	private static void createResponder(BufferedReader bufferedReader)
			throws IOException {
		dating.model.Responder responder;
		while (true) {
			System.out.println("Please enter username:");
			String input = readParam(bufferedReader);
			if (input.equalsIgnoreCase("-1")) {
				reset();
				break;
			}
			if (input != "") {
				responder = Customer.createResponder(input);
				while (true) {
					System.out.println("Please enter password:");
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(responder);
						reset();
						break;
					}
					if (input != "") {
						responder.password = input;
						break;
					}
				}
				while (true) {
					System.out
							.println("Please enter gender:0 for female, not 0 for male:");
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(responder);
						reset();
						break;
					}
					if (input.equalsIgnoreCase("0")) {
						responder.gender = Gender.FEMALE;
					} else {
						responder.gender = Gender.MALE;
					}
					break;
				}
				while (true) {
					System.out.println("Please enter age:");
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(responder);
						reset();
						break;
					}
					try {
						responder.age = Float.parseFloat(input);
						if (responder.age > 0 && responder.age < 200) {
							break;
						}
					} catch (Exception e) {
					}
				}
				while (true) {
					System.out.println("Please enter income range: from-to");
					responder.incomeRange = new IncomeRange();
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(responder);
						reset();
						break;
					}
					String[] range = input.split("-");
					if (range.length == 1) {
						try {
							responder.incomeRange.from = (int) Float
									.parseFloat(range[0]);
						} catch (Exception e) {
						}
					} else if (range.length > 1) {
						try {
							responder.incomeRange.from = (int) Float
									.parseFloat(range[0]);
							responder.incomeRange.to = (int) Float
									.parseFloat(range[1]);
						} catch (Exception e) {
						}
					}
					break;
				}
				reset();
				break;
			}
		}
	}

	/**
	 * Create advertiser follow the instructions
	 * 
	 * @param bufferedReader
	 * @throws IOException
	 */
	private static void createAdvertiser(BufferedReader bufferedReader)
			throws IOException {
		dating.model.Advertiser advertiser;
		while (true) {
			System.out.println("Please enter username:");
			String input = readParam(bufferedReader);
			if (input.equalsIgnoreCase("-1")) {
				reset();
				break;
			}
			if (input != "") {
				advertiser = Customer.createAdvertiser(input);
				while (true) {
					System.out.println("Please enter password:");
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(advertiser);
						reset();
						break;
					}
					if (input != "") {
						advertiser.password = input;
						break;
					}
				}
				while (true) {
					System.out
							.println("Please enter gender:0 for female, not 0 for male:");
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(advertiser);
						reset();
						break;
					}
					if (input.equalsIgnoreCase("0")) {
						advertiser.gender = Gender.FEMALE;
					} else {
						advertiser.gender = Gender.MALE;
					}
					break;
				}
				while (true) {
					System.out.println("Please enter age:");
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(advertiser);
						reset();
						break;
					}
					try {
						advertiser.age = Float.parseFloat(input);
						if (advertiser.age > 0 && advertiser.age < 200) {
							break;
						}
					} catch (Exception e) {
					}
				}
				while (true) {
					System.out.println("Please enter income range: from-to");
					advertiser.incomeRange = new IncomeRange();
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(advertiser);
						reset();
						break;
					}
					String[] range = input.split("-");
					if (range.length == 1) {
						try {
							advertiser.incomeRange.from = (int) Float
									.parseFloat(range[0]);
						} catch (Exception e) {
						}
					} else if (range.length > 1) {
						try {
							advertiser.incomeRange.from = (int) Float
									.parseFloat(range[0]);
							advertiser.incomeRange.to = (int) Float
									.parseFloat(range[1]);
						} catch (Exception e) {
						}
					}
					break;
				}
				while (true) {
					System.out.println("Please enter some advert text:");
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(advertiser);
						reset();
						break;
					}
					advertiser.advertText = input;
					break;
				}
				advertiser.partnerCriteria = new PartnerCriteria();
				while (true) {
					System.out
							.println("Please enter partner gender:0 for female, not 0 for male:");
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(advertiser);
						reset();
						break;
					}
					if (input.equalsIgnoreCase("0")) {
						advertiser.partnerCriteria.gender = Gender.FEMALE;
					} else {
						advertiser.partnerCriteria.gender = Gender.MALE;
					}
					break;
				}
				while (true) {
					System.out
							.println("Please enter partner age range: from-to");
					advertiser.partnerCriteria.ageRange = new AgeRange();
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(advertiser);
						reset();
						break;
					}
					String[] range = input.split("-");
					if (range.length == 1) {
						try {
							advertiser.partnerCriteria.ageRange.from = (int) Float
									.parseFloat(range[0]);
						} catch (Exception e) {
						}
					} else if (range.length > 1) {
						try {
							advertiser.partnerCriteria.ageRange.from = (int) Float
									.parseFloat(range[0]);
							advertiser.partnerCriteria.ageRange.to = (int) Float
									.parseFloat(range[1]);
						} catch (Exception e) {
						}
					}
					break;
				}
				while (true) {
					System.out
							.println("Please enter partner income range: from-to");
					advertiser.partnerCriteria.incomeRange = new IncomeRange();
					input = readParam(bufferedReader);
					if (input.equalsIgnoreCase("-1")) {
						Customer.customers.remove(advertiser);
						reset();
						break;
					}
					String[] range = input.split("-");
					if (range.length == 1) {
						try {
							advertiser.partnerCriteria.incomeRange.from = (int) Float
									.parseFloat(range[0]);
						} catch (Exception e) {
						}
					} else if (range.length > 1) {
						try {
							advertiser.partnerCriteria.incomeRange.from = (int) Float
									.parseFloat(range[0]);
							advertiser.partnerCriteria.incomeRange.to = (int) Float
									.parseFloat(range[1]);
						} catch (Exception e) {
						}
					}
					break;
				}
				reset();
				break;
			}
		}
	}

	/**
	 * Set the current state of application
	 * 
	 * @param newState
	 */
	public static void setState(String newState) {
		if (actionsMap.containsKey(newState)) {
			state = actionsMap.get(newState);
		} else {
			state = APP_STATE.DASHBOARD;
		}
	}

	/**
	 * Print the list of customers
	 * 
	 * @param bufferedReader
	 */
	public static void printCustomers(BufferedReader bufferedReader) {
		if (Customer.customers.size() <= 0) {
			System.out.println("There is no customer.");
			return;
		}
		System.out.println("Please choose one:");
		System.out.println("1. Print all (advertisers and responders)");
		System.out.println("2. Print all advertisers");
		System.out.println("3. Print all responders");
		while (true) {
			try {
				String input = readParam(bufferedReader);
				if (input.equalsIgnoreCase("-1")) {
					reset();
					break;
				}
				int idx = Integer.parseInt(input);
				for (Object object : Customer.customers) {
					if (idx == 1) {
						if (object instanceof dating.model.Advertiser) {
							printAdvertiser((dating.model.Advertiser) object);
						} else if (object instanceof dating.model.Responder) {
							printResponder((dating.model.Responder) object);
						}
					} else if (idx == 2
							&& object instanceof dating.model.Advertiser) {
						printAdvertiser((dating.model.Advertiser) object);
					} else if (idx == 3
							&& object instanceof dating.model.Responder) {
						printResponder((dating.model.Responder) object);
					}
				}
				break;
			} catch (Exception e) {
				System.out.println("Please choose one:");
				System.out.println("1. Print all");
				System.out.println("2. Print advertisers");
				System.out.println("3. Print responders");
			}
		}
	}

	/**
	 * Print the detail of an advertiser
	 * 
	 * @param idx
	 * @param advertiser
	 */
	public static void printAdvertiser(dating.model.Advertiser advertiser) {
		System.out.println("---------------Advertiser---------------");
		System.out.println("Username	: " + advertiser.getUsername());
		System.out.println("Password	: " + advertiser.password);
		System.out.println("Gender		: "
				+ (advertiser.gender == Gender.MALE ? "Male" : "Female"));
		System.out.println("Age		: " + advertiser.age);
		System.out.println("Income		: " + advertiser.incomeRange.from + " -> "
				+ advertiser.incomeRange.to);
		System.out.println("Text advert	: " + advertiser.advertText);
		System.out.println("Partner criteria");
		System.out.println("Gender		: "
				+ (advertiser.partnerCriteria.gender == Gender.MALE ? "Male"
						: "Female"));
		System.out.println("Age		: " + advertiser.partnerCriteria.ageRange.from
				+ " -> " + advertiser.partnerCriteria.ageRange.to);
		System.out.println("Income		: "
				+ advertiser.partnerCriteria.incomeRange.from + " -> "
				+ advertiser.partnerCriteria.incomeRange.to);
	}

	/**
	 * Print the list of message of an advertiser
	 * 
	 * @param advertiser
	 */
	public static void printAdvertiserMessages(
			dating.model.Advertiser advertiser) {
		System.out.println("Messages");
		int idx = 0;
		for (Message message : advertiser.messages) {
			printMessage(idx, message);
			idx++;
		}
	}

	/**
	 * 
	 * @param responder
	 */
	public static void printMatchingAdvertisersOfResponder(
			dating.model.Responder responder) {
		System.out.println("Matches------------------------------------------");
		int i = 0;
		for (Advertiser advertiser : Customer
				.getMatchingAdvertisersOfResponder(responder)) {
			printMatchingAdvertiser(i, advertiser);
			i++;
		}
	}

	/**
	 * Print the matching advertiser detail to responder
	 * 
	 * @param advertiser
	 */
	public static void printMatchingAdvertiser(int idx, Advertiser advertiser) {
		System.out.println("---------------Matching Advertiser---------------");
		System.out.println("Username	: " + advertiser.getUsername());
		System.out.println("Gender		: "
				+ (advertiser.gender == Gender.MALE ? "Male" : "Female"));
		System.out.println("Age		: " + advertiser.age);
		System.out.println("Income		: " + advertiser.incomeRange.from + " -> "
				+ advertiser.incomeRange.to);
		System.out.println("Text advert	: " + advertiser.advertText);
		System.out.println("--> Press " + idx
				+ " to send a message to this advertiser.");
	}

	/**
	 * Print a message
	 * 
	 * @param idx
	 * @param message
	 */
	public static void printMessage(int idx, Message message) {
		System.out.println("" + idx + ") From: " + message.owner.getUsername());
		System.out.println(message.text);
	}

	/**
	 * Print the detail of a responder
	 * 
	 * @param idx
	 * @param responder
	 */
	public static void printResponder(dating.model.Responder responder) {
		System.out.println("---------------Responder----------------");
		System.out.println("Username	: " + responder.getUsername());
		System.out.println("Password	: " + responder.password);
		System.out.println("Gender		: "
				+ (responder.gender == Gender.MALE ? "Male" : "Female"));
		System.out.println("Age		: " + responder.age);
		System.out.println("Income		: " + responder.incomeRange.from + " -> "
				+ responder.incomeRange.to);
	}

	/**
	 * Randomly add some customers to application for testing
	 */
	public static void setupCustomers() {
		Random randomGenerator = new Random();
		for (int i = 0; i < 8; i++) {
			if (randomGenerator.nextInt(2) == 0) {
				Advertiser customer = Customer.createAdvertiser("Advertiser "
						+ i);
				customer.password = "advert" + i;
				customer.gender = randomGenerator.nextInt(2) == 0 ? Gender.FEMALE
						: Gender.MALE;
				customer.age = randInt(16, 60);
				customer.incomeRange.from = randInt(100, 10000);
				customer.incomeRange.to = randInt(
						(int) customer.incomeRange.from, 10000);
				customer.advertText = "Advertiser " + 1 + ": some text advert.";
				customer.partnerCriteria.gender = randomGenerator.nextInt(2) == 0 ? Gender.FEMALE
						: Gender.MALE;
				customer.partnerCriteria.ageRange.from = randInt(18, 60);
				customer.partnerCriteria.ageRange.to = randInt(
						(int) customer.partnerCriteria.ageRange.from, 60);
				customer.partnerCriteria.incomeRange.from = randInt(100, 10000);
				customer.partnerCriteria.incomeRange.to = randInt(
						(int) customer.partnerCriteria.incomeRange.from, 10000);
			} else {
				Responder customer = Customer.createResponder("Responder " + i);
				customer.password = "resp" + i;
				customer.gender = randomGenerator.nextInt(2) == 0 ? Gender.FEMALE
						: Gender.MALE;
				customer.age = randInt(16, 60);
				customer.incomeRange.from = randInt(100, 10000);
				customer.incomeRange.to = randInt(
						(int) customer.incomeRange.from, 10000);
			}
		}
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
