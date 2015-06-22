import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

/**
 * Main console application
 * 
 * @author Jerome
 *
 */
public class Application {

	/**
	 * Enum application states
	 * 
	 * @author Jerome
	 *
	 */
	public enum APP_STATE {
		DEFAULT, PRINT_CUSTOMERS, //
		ADD_CUSTOMER, REMOVE_CUSTOMER, LOGIN, SEND_MESSAGE //
	}

	/**
	 * Application state
	 */
	public static APP_STATE state = APP_STATE.DEFAULT;

	/**
	 * App state -> Value map
	 */
	public static HashMap<APP_STATE, String> stateValueMap = new HashMap<APP_STATE, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(APP_STATE.DEFAULT, "-1");
			put(APP_STATE.PRINT_CUSTOMERS, "1");
			put(APP_STATE.ADD_CUSTOMER, "2");
			put(APP_STATE.REMOVE_CUSTOMER, "3");
			put(APP_STATE.LOGIN, "4");
			put(APP_STATE.SEND_MESSAGE, "5");
		}
	};

	/**
	 * Get app state from a string value
	 * 
	 * @param value
	 * @return
	 */
	public static APP_STATE getStateByValue(String value) {
		for (APP_STATE state : stateValueMap.keySet()) {
			if (stateValueMap.get(state).equals(value)) {
				return state;
			}
		}
		return APP_STATE.DEFAULT;
	}

	/**
	 * App state -> instruction map
	 */
	public static HashMap<APP_STATE, String> stateInstructionMap = new HashMap<APP_STATE, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(APP_STATE.DEFAULT,
					"Enter " + stateValueMap.get(APP_STATE.DEFAULT)
							+ " to open Dashboard");
			put(APP_STATE.PRINT_CUSTOMERS,
					"Enter " + stateValueMap.get(APP_STATE.PRINT_CUSTOMERS)
							+ " to print list of customers");
			put(APP_STATE.ADD_CUSTOMER,
					"Enter " + stateValueMap.get(APP_STATE.ADD_CUSTOMER)
							+ " to add a customer");
			put(APP_STATE.REMOVE_CUSTOMER,
					"Enter " + stateValueMap.get(APP_STATE.REMOVE_CUSTOMER)
							+ " to remove a customer");
			put(APP_STATE.LOGIN, "Enter " + stateValueMap.get(APP_STATE.LOGIN)
					+ " to login");
			put(APP_STATE.SEND_MESSAGE,
					"Enter " + stateValueMap.get(APP_STATE.SEND_MESSAGE)
							+ " to send a message to an advertiser.");
		}
	};

	/**
	 * Logged customer
	 */
	public static dating.model.Customer currentCustomer;

	/**
	 * Main function
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		setupCustomers();
		process();
	}

	/**
	 * Print application instructions
	 * 
	 * @throws IOException
	 */
	public static void printInstruction() throws IOException {
		switch (state) {
		case DEFAULT:
			if (Customer.customers.size() > 0) {
				System.out.println(stateInstructionMap
						.get(APP_STATE.PRINT_CUSTOMERS));
			}
			System.out.println(stateInstructionMap.get(APP_STATE.ADD_CUSTOMER));
			if (Customer.customers.size() > 0) {
				System.out.println(stateInstructionMap
						.get(APP_STATE.REMOVE_CUSTOMER));
				System.out.println(stateInstructionMap.get(APP_STATE.LOGIN));
			}
			break;
		default:
			// System.out.println(stateInstructionMap.get(APP_STATE.DEFAULT));
			break;
		}
	}

	/**
	 * Reset: logout, set app state to DEFAULT, print default instruction
	 * 
	 * @throws IOException
	 */
	public static void reset() throws IOException {
		currentCustomer = null;
		setState(stateValueMap.get(APP_STATE.DEFAULT));
	}

	/**
	 * Get one line from console
	 * 
	 * @param bufferedReader
	 * @return
	 * @throws IOException
	 */
	private static String readParam(BufferedReader bufferedReader)
			throws IOException {
		return bufferedReader.readLine().trim();
	}

	/**
	 * Process app bases on the input from console
	 * 
	 * @throws IOException
	 */
	public static void process() throws IOException {
		printInstruction();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		while (true) {
			setState(readParam(bufferedReader));
			switch (state) {
			case PRINT_CUSTOMERS:
				printCustomers(bufferedReader);
				break;
			case ADD_CUSTOMER:
				createCustomer(bufferedReader);
				break;
			case REMOVE_CUSTOMER:
				removeCustomer(bufferedReader);
				break;
			case LOGIN:
				login(bufferedReader);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Remove a customer
	 * 
	 * @param bufferedReader
	 * @throws IOException
	 */
	public static void removeCustomer(BufferedReader bufferedReader)
			throws IOException {
		if (Customer.customers.size() > 0) {
			System.out
					.println("Please enter customer's username want to delete:");
			System.out.println(stateInstructionMap.get(APP_STATE.DEFAULT));
			String username = readParam(bufferedReader);
			if (username.equalsIgnoreCase("-1")) {
				reset();
				return;
			}
			dating.model.Customer customer = Customer.getCustomer(username);
			if (customer != null) {
				Customer.remove(customer);
				System.out.println("Customer " + username
						+ " has been removed.");
			} else {
				System.out.println("There is no customer " + username);
			}
		}
	}

	/**
	 * Login
	 * 
	 * @param bufferedReader
	 * @throws IOException
	 */
	public static void login(BufferedReader bufferedReader) throws IOException {
		if (Customer.customers.size() > 0) {
			System.out.println("Please enter username:");
			String username = readParam(bufferedReader);
			if (username.equalsIgnoreCase("-1")) {
				reset();
				return;
			}
			if (Customer.hasCustomer(username)) {
				System.out.println("Please enter password:");
				String password = readParam(bufferedReader);
				if (password.equalsIgnoreCase("-1")) {
					reset();
					return;
				}
				dating.model.Customer customer = Customer.login(username,
						password);
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
									.println("\nPlease enter one option below:");
							System.out.println(stateInstructionMap
									.get(APP_STATE.SEND_MESSAGE));
							System.out.println(stateInstructionMap
									.get(APP_STATE.DEFAULT));
							String input = readParam(bufferedReader);
							if (input.equalsIgnoreCase("-1")) {
								reset();
								break;
							}
							if (input.equalsIgnoreCase("5")) {
								sendMessage(bufferedReader);
							}
						}
					}
				} else {
					System.out.println("Password is incorrect.");
					reset();
				}
				return;
			} else {
				System.out
						.println("User does not exist. Enter -1 to go back or re-enter user name again");
				return;
			}
		}
	}

	/**
	 * Send a message of the logged responder to an advertiser
	 * 
	 * @param bufferedReader
	 * @throws IOException
	 */
	private static void sendMessage(BufferedReader bufferedReader)
			throws IOException {
		if (currentCustomer instanceof Responder) {
			while (true) {
				System.out
						.println("Enter username of the advertiser you want to send message to:");
				System.out.println(stateInstructionMap.get(APP_STATE.DEFAULT));
				String username = readParam(bufferedReader);
				if (username.equalsIgnoreCase("-1")) {
					reset();
					break;
				}
				Advertiser advertiser = (Advertiser) Customer
						.getCustomer(username);
				if (advertiser != null) {
					System.out.println("Please enter the message:");
					Message message = new Message(currentCustomer);
					message.text = readParam(bufferedReader);
					Customer.receiveMessage(advertiser, message);
					System.out
							.println("The message has been sent to the advertiser.");
				} else {
					System.out.println("Advertiser does not exist");
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
		System.out.println("Enter 1 to create an advertiser");
		System.out.println("Enter 2 to create a responder");
		System.out.println(stateInstructionMap.get(APP_STATE.DEFAULT));
		String input = readParam(bufferedReader);
		if (input.equalsIgnoreCase("-1")) {
			reset();
			return;
		}
		if (input.equalsIgnoreCase("1")) {
			createAdvertiser(bufferedReader);
		} else if (input.equalsIgnoreCase("2")) {
			createResponder(bufferedReader);
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
	 * @throws IOException
	 */
	public static void setState(String newState) throws IOException {
		state = getStateByValue(newState);
		printInstruction();
	}

	/**
	 * Print the list of customers
	 * 
	 * @param bufferedReader
	 * @throws IOException
	 */
	public static void printCustomers(BufferedReader bufferedReader)
			throws IOException {
		if (Customer.customers.size() <= 0) {
			System.out.println("There is no customer.");
			return;
		}
		System.out.println("Enter 1 to print all (advertisers and responders)");
		System.out.println("Enter 2 to print all advertisers");
		System.out.println("Enter 3 to print all responders");
		System.out.println(stateInstructionMap.get(APP_STATE.DEFAULT));
		String input = readParam(bufferedReader);
		if (input.equalsIgnoreCase("-1")) {
			reset();
			return;
		}
		for (Object object : Customer.customers) {
			if (input.equalsIgnoreCase("1")) {
				if (object instanceof dating.model.Advertiser) {
					printAdvertiser((dating.model.Advertiser) object);
				} else if (object instanceof dating.model.Responder) {
					printResponder((dating.model.Responder) object);
				}
			} else if (input.equalsIgnoreCase("2")
					&& object instanceof dating.model.Advertiser) {
				printAdvertiser((dating.model.Advertiser) object);
			} else if (input.equalsIgnoreCase("3")
					&& object instanceof dating.model.Responder) {
				printResponder((dating.model.Responder) object);
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
		ArrayList<Advertiser> matchingAdvertisers = Customer
				.getMatchingAdvertisersOfResponder(responder);
		if (matchingAdvertisers.size() > 0) {
			System.out
					.println("Matches------------------------------------------");
			int i = 0;
			for (Advertiser advertiser : matchingAdvertisers) {
				printMatchingAdvertiser(i, advertiser);
				i++;
			}
		}
	}

	/**
	 * Print the matching advertiser detail to responder
	 * 
	 * @param advertiser
	 */
	public static void printMatchingAdvertiser(int idx, Advertiser advertiser) {
		System.out.println("-------------------------------------------------");
		System.out.println("---------------Matching Advertiser---------------");
		System.out.println("Username	: " + advertiser.getUsername());
		System.out.println("Gender		: "
				+ (advertiser.gender == Gender.MALE ? "Male" : "Female"));
		System.out.println("Age		: " + advertiser.age);
		System.out.println("Income		: " + advertiser.incomeRange.from + " -> "
				+ advertiser.incomeRange.to);
		System.out.println("Text advert	: " + advertiser.advertText);
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
		for (int i = 0; i < 6; i++) {
			if (randomGenerator.nextInt(2) == 0) {
				Advertiser customer = Customer.createAdvertiser("advertiser"
						+ i);
				customer.password = "advertiser" + i;
				customer.gender = randomGenerator.nextInt(2) == 0 ? Gender.FEMALE
						: Gender.MALE;
				customer.age = randInt(23, 35);
				customer.incomeRange.from = randInt(100, 10000);
				customer.incomeRange.to = randInt(
						(int) customer.incomeRange.from, 10000);
				customer.advertText = "Advertiser " + 1 + ": some text advert.";
				customer.partnerCriteria.gender = customer.gender == Gender.MALE ? Gender.FEMALE
						: Gender.MALE;
				customer.partnerCriteria.ageRange.from = randInt(18, 19);
				customer.partnerCriteria.ageRange.to = randInt(
						(int) customer.partnerCriteria.ageRange.from, 35);
				customer.partnerCriteria.incomeRange.from = randInt(1000, 2000);
				customer.partnerCriteria.incomeRange.to = randInt(
						(int) customer.partnerCriteria.incomeRange.from, 10000);
			} else {
				Responder customer = Customer.createResponder("responder" + i);
				customer.password = "responder" + i;
				customer.gender = randomGenerator.nextInt(2) == 0 ? Gender.FEMALE
						: Gender.MALE;
				customer.age = randInt(18, 25);
				customer.incomeRange.from = randInt(1000, 2000);
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
