package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TenmoService;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TenmoService tenmoService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.tenmoService = new TenmoService();
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
		System.out.println(tenmoService.retrieveBalance());
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub

		Scanner scanner = new Scanner(System.in);

		currentUser.getUser().setAccountId(tenmoService.getAccountId());
		int accountId = currentUser.getUser().getAccountId();

		List<Integer> transactionIds = new ArrayList<>();

		Transfer[] transfers = tenmoService.listTransfers();
		for(Transfer transfer : transfers){
			transactionIds.add(transfer.getTransferID());
			// if the account id is not the same as the person, then inc transaction must be FROM someone else
			// else it must be a transaction TO someone else
			if (accountId  != transfer.getAccountFrom()) {
				//TODO add code to get username from transfer.getAccountFrom()
				User userFrom = new User();
				userFrom.setAccountId(transfer.getAccountFrom());
				userFrom = tenmoService.getUser(userFrom);
				System.out.println(transfer.getTransferID() + " From: " + userFrom.getUsername() + " $"+ transfer.getAmount());
			} else {
				//TODO add code to get username from transfer.getAccountTo()
				User userTo = new User();
				userTo.setAccountId(transfer.getAccountTo());
				userTo = tenmoService.getUser(userTo);
				System.out.println(transfer.getTransferID() + " To: " + userTo.getUsername() + " $"+ transfer.getAmount());
			}
		}

		boolean idValid = false;

		while (!idValid) {
			System.out.println("Please enter transfer ID to view details (0 to cancel): ");
			String additionalDetailsChoice = scanner.nextLine();
			int choice = Integer.parseInt(additionalDetailsChoice);

			if (choice == 0) {
				break;
			} else if (transactionIds.contains(choice)) {
				for (Transfer transfer : transfers) {
					if (transfer.getTransferID() == choice) {

						System.out.println(transfer);

						idValid = true;
					}
				}
			} else {
				System.out.println("Invalid Selection");
			}
		}





	}

	//optional
	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		Scanner scanner = new Scanner(System.in);

		// Create a list of all registered users

		User[] users = tenmoService.listUsers();
		// create a list of userIds
		List<Integer> userIds = new ArrayList<>();

		for (User user : users) {
			userIds.add(user.getId());
			System.out.println(user);
		}

		boolean idValid = false;
		int sendToUserId = 0;

		while(!idValid) {
			int currentUserId = currentUser.getUser().getId();
			System.out.println("Select UserId from user list: ");
			String choice = scanner.nextLine();
			// Select Id of user from the list
			sendToUserId = Integer.parseInt(choice);
			// if there is a match in the List of userId's AND the user is not sending money to themselves
			// then True and exit loop otherwise sys out error
			if(userIds.contains(sendToUserId) && sendToUserId != currentUserId){
				idValid = true;
			} else if (sendToUserId == currentUserId) {
				System.out.println("Please select a userId other than yourself.");
			} else {
				System.out.println("Invalid UserId, please enter one from the list above.");
			}

		}

		System.out.println("Your current balance: " + tenmoService.retrieveBalance());

		// prompt the user for amount to transfer

		System.out.println("How much would you like to transfer? ");
		String amount = scanner.nextLine();
		BigDecimal amountToTransfer = new BigDecimal(amount);

		// call transfer from balance and display back to the user

		Transfer transfer = tenmoService.transferFromBalance(sendToUserId, amountToTransfer);
		int status = transfer.getTransferStatusID();
		if (status == 2){
			// checking here if the status is approved then displaying to the user the updated balance and success
			System.out.println("Transfer " + transfer.getTransferID() + " was successful, your updated balance is: $" + tenmoService.retrieveBalance());
		} else {
			System.out.println("Transfer was unsuccessful, your current balance remains: $" + tenmoService.retrieveBalance());
		}

		
	}

	//optional
	private void requestBucks() {
		// TODO Auto-generated method stub

		// set transfer status to 1 and transfertypeid to 1 (pending and request)

		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
				tenmoService.setAuthToken(currentUser.getToken());
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
