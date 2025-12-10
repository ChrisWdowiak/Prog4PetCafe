
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*=============================================================================
 |   Assignment:  Program #4:  Database Design and Implementation
 |       Authors: 
 |                  Alex Herron             aherron@arizona.edu
 |                  Hengsocheat Pok         hspok@arizona.edu
 |                  Chris Wdowiak           cwdowiak@arizona.edu
 |                  Lucas Robert Hamacher   lucashamacher@arizona.edu 
 |
 |       Grader:  
 |                  Jianwei (James) Shen
 |               	Utkarsh Upadhyay
 |
 |       Course:  CSc460
 |   Instructor:  L. McCann
 |     Due Date:  December 8th, 2025, 2:00 PM
 |
 |  Description:  JDBC Front-end database implementation.
 |
 |     Language:  Java jdk-16.0.2
 |                
 | Deficiencies:  Just starting out, too early.
 *===========================================================================*/
public class App {

	final static String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
    public static void main(String[] args) {

    // gonna assume it starts like program3
    // code to connect to Oracle
		if (args.length != 2) {
			System.out.println("ERROR: invalid arguements");
			System.exit(-1);
		}
		
		String username = args[0];
		String password = args[1];
		
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: failed to load JDBC driver");
			System.exit(-1);
		}
		
		// make connection
		Connection dbconn = null;
		
		try {
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			System.out.println("ERROR: failed to open JDBC connection");
			System.out.println("\t" + e.getMessage());
			System.exit(-1);
		}

		// begin query loop
		Scanner scanner = new Scanner(System.in);
		queryLoop(dbconn, scanner);

		// close connection and cleanup
		scanner.close();
		try {
			dbconn.close();
		} catch (SQLException e) {
			System.out.println("ERROR: failed to close connection");
			System.exit(-1);
		}	
		return;
    }

	/*---------------------------------------------------------------------
    |  Method queryLoop
    |
    |  Purpose:  This method begins the user input query loop.
    |
    |  Pre-condition:  a connection is established to Oracle.
    |
    |  Post-condition: we will begin taking input from users, ready to
	|			satisfy the request with later function calls.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
	private static void queryLoop(Connection dbconn, Scanner scanner) {

		System.out.println("Enter the letter for which type of request you'd like:");
		System.out.println("\t(a) Add, update, or delete from system");
		System.out.println("\t(b) Enter query mode");
		System.out.println("\t(c) Additional functionality?");
		System.out.println("\t(d) Additional functionality?");				
		System.out.println("\tEnter 'q' to exit");

		String answer = null;
		answer = scanner.next();

		while(!answer.equals("q")) {
			switch (answer) {
			case "a":
				editDataHandler(dbconn, scanner);
				break;
			case "b":
				queryHandler(dbconn, scanner);
				break;
			case "c":
				
				break;
			case "d":
				
				break;
			case "q":
				System.out.println("<Exiting System>");
				return;
			default:
				System.out.println("Invalid response, please try again.");
				break;
			}
			
			System.out.println("Enter the letter for which type of request you'd like:");
			System.out.println("\t(a) Add, update, or delete from system");
			System.out.println("\t(b) Enter query mode");
			System.out.println("\t(c) Additional functionality?");
			System.out.println("\t(d) Additional functionality?");				
			System.out.println("\tEnter 'q' to exit");

			answer = scanner.next();
		}
	}

	/*---------------------------------------------------------------------
    |  Method editDataHandler
    |
    |  Purpose:  This handles the request to edit data in the tables.
    |
	|  Pre-condition:  user requested this and connection established
    |
    |  Post-condition: user edits data in specified table to thier liking
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	private static void editDataHandler(Connection dbconn, Scanner scanner) {

		String answer = null;

		System.out.println("Which type of data do you want to edit?:");
		System.out.println("\t(a) Members");
		System.out.println("\t(b) Animals and Pets");
		System.out.println("\t(c) Food and Beverage Orders");
		System.out.println("\t(d) Reservations");	
		System.out.println("\t(e) Health Records");	
		System.out.println("\t(f) Adoption Applications");
		System.out.println("\t(g) Event Bookings");		
		System.out.println("\tEnter 'q' to go back");

		answer = scanner.next();

		switch (answer) {
			case "a":
				EditRequests.memberLanding(dbconn, scanner);
				break;
			case "b":
				EditRequests.petLanding(dbconn, scanner);
				break;
			case "c":
				EditRequests.orderLanding(dbconn, scanner);
				break;
			case "d":
				EditRequests.reservationLanding(dbconn, scanner);
				break;
			case "e":
				EditRequests.healthLanding(dbconn, scanner);
				break;
			case "f":
				EditRequests.adoptionLanding(dbconn, scanner);
				break;
			case "g":
				EditRequests.eventLanding(dbconn, scanner);
				break;
			case "q":
				return;
			default:
				System.out.println("Invalid response, please try again.");
				editDataHandler(dbconn, scanner);
				return;
		}
		return;
	}

	/*---------------------------------------------------------------------
    |  Method queryHandler
    |
    |  Purpose:  This handles any sort of query we need.
    |
	|  Pre-condition:  user wants to query and connection established
    |
    |  Post-condition: user query request is satisfied
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	private static void queryHandler(Connection dbconn, Scanner scanner) {

		String answer = null;

		System.out.println("Which type of query would you like to see?:");
		System.out.println("\t(a) List all adoption applications for a given pet");
		System.out.println("\t(b) Show visit history of given customer");
		System.out.println("\t(c) List all upcoming events with open capacity");
		System.out.println("\t(d) Custom query: Pet's Health Records");			
		System.out.println("\tEnter 'q' to go back");

		answer = scanner.next();

		switch (answer) {
			case "a":
				QueryRequests.listAdoptionApplications(dbconn, scanner);
				break;
			case "b":
				QueryRequests.showVisitHistory(dbconn, scanner);
				break;
			case "c":
				QueryRequests.listAllUpcomingEvents(dbconn);
				break;
			case "d":
				QueryRequests.showPetHealthRecord(dbconn, scanner);
				break;
			case "q":
				return;
			default:
				System.out.println("Invalid response, please try again.");
				queryHandler(dbconn, scanner);
				return;
		}
		return;
	}
}
