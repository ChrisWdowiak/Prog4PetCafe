import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.sql.*;

/*+----------------------------------------------------------------------
 ||
 ||  Class EditRequests 
 ||
 ||         Authors: 
 ||                 Alex Herron             aherron@arizona.edu
 ||                 Hengsocheat Pok         hspok@arizona.edu
 ||                 Chris Wdowiak           cwdowiak@arizona.edu
 ||                 Lucas Robert Hamacher   lucashamacher@arizona.edu
 ||
 ||        Purpose:  This class exists to preform all edit requests thorugh
 ||                  Oracle. Separate from App.java to improve modularity.
 ||
 ||  Inherits From:  None.
 ||
 ||     Interfaces:  [If any predefined interfaces are implemented by
 ||                   this class, name them.  If not, ... well, you know.]
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  [Name all public class constants, and provide a very
 ||                   brief (but useful!) description of each.]
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  [List the names and arguments of all defined
 ||                   constructors.]
 ||
 ||  Class Methods:  [List the names, arguments, and return types of all
 ||                   public class methods.]
 ||
 ||  Inst. Methods:  [List the names, arguments, and return types of all
 ||                   public instance methods.]
 ||
 ++-----------------------------------------------------------------------*/
public class EditRequests {
    
    /*---------------------------------------------------------------------
    |  Method memberLanding
    |
    |  Purpose:  this method serves as the landing for an edit member request
    |      so that it can call 3 helpers for update, delete, and add.
    |
	|  Pre-condition:  User requested to edit member and connection to oracle
    |      establisted.
    |
    |  Post-condition: the users request will be handled and the member table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    public static void memberLanding(Connection dbconn, Scanner scanner) {

        String answer = null;

		System.out.println("Would you like to update, delete, or add?:");
		System.out.println("\t(a) Update");
		System.out.println("\t(b) Delete");
		System.out.println("\t(c) Add");
        System.out.println("\t(d) Change membership");
        System.out.println("\tEnter 'q' to go back");

        answer = scanner.next();

        switch (answer) {
			case "a":
				memberUpdate(dbconn, scanner);
				break;
			case "b":
                memberDelete(dbconn, scanner);
				break;
			case "c":
				memberAdd(dbconn, scanner);
				break;
            case "d":
                changeMemberShip(dbconn, scanner);
                break;
			case "q":
				return;
			default:
				System.out.println("Invalid response, please try again.");
                memberLanding(dbconn, scanner);
				return;
		}
		return;
    }

    /*---------------------------------------------------------------------
    |  Method memberUpdate
    |
    |  Purpose:  handles update member in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the member table
    |      will be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public static void memberUpdate(Connection dbconn, Scanner scanner) {

        String answer = null;

		System.out.print("Enter the ID or name of the member you wish to update: ");
        answer = scanner.next();
        System.out.println();

        if (answer.matches("\\d+")) {
            // id case 
            int id = Integer.parseInt(answer);
		    Statement stmt = null;
		    ResultSet result = null;
            String query = String.format("SELECT * FROM lucashamacher.Customer WHERE customerID=%d", id);
            try {

            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);
            boolean found = false;

            if (result.next()) {

                found = true;
                System.out.println("The current tuple is:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                System.out.println(result.getInt("customerID") + "\t" + result.getString("name") + 
                "\t" + result.getString("phone") + "\t" + result.getString("email") + "\t" + 
                result.getDate("dateOfBirth") + "\t" + result.getString("emergencyContactName") +
                "\t" + result.getString("emergencyContactPhone") );
            }
            if (!found) {
               System.out.println("No member with that ID exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }

            String name = null;
            String nameLast = null;
            String phone = null;
            String email = null;
            String dobDay = null;
            String dobMon = null;
            String dobY = null;
            String emergencyName = null;
            String emergencyLast = null;
            String emergencyPhone = null;
            
            answer="n";
            while (answer.contains("n")) {

                System.out.println("Please give new info");
                System.out.print("Enter name: ");
                name = scanner.next();
                nameLast = scanner.next();
                System.out.print("Enter phone: ");
                phone = scanner.next();
                System.out.print("Enter email: ");
                email = scanner.next();
                System.out.print("Enter date of birth, Day: ");
                dobDay = scanner.next();
                System.out.print("\tMonth: ");
                dobMon = scanner.next();
                System.out.print("\tYear: ");
                dobY = scanner.next();
                System.out.print("Enter emergency contact name: ");
                emergencyName = scanner.next();
                emergencyLast = scanner.next();
                System.out.print("Enter emergency contact phone#: ");
                emergencyPhone = scanner.next();
                System.out.println();

                System.out.println("Is this correct y or n?");
                System.out.println(String.format("%s %s, %s, %s, %s/%s/%s, %s %s, %s", name, nameLast, phone, email, dobY, dobMon, dobDay, emergencyName, emergencyLast, emergencyPhone));
                answer = scanner.next();
            }

            // execute sql statement
            String update = String.format("UPDATE lucashamacher.Customer SET name='%s %s', phone='%s', email='%s', dateOfBirth='%s/%s/%s', emergencyContactName='%s %s', emergencyContactPhone='%s' WHERE customerID=%d", name,nameLast,phone,email,dobY,dobMon,dobDay,emergencyName,emergencyLast,emergencyPhone, id);
            //System.out.println(update);
            try {

                Statement upStmt = dbconn.createStatement();
                result = upStmt.executeQuery(update);
                upStmt.close();

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }
            System.out.println("-----Successfully updated member------");
            System.out.println();
            return;

        } else {
            // name case
		    Statement stmt = null;
		    ResultSet result = null;
            String query = "SELECT customerID, name FROM lucashamacher.Customer WHERE name LIKE '%"+answer+"%'";
            ArrayList<Integer> ids = new ArrayList<Integer>();
            try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {

                System.out.println("Results of name search are:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    ids.add(result.getInt("customerID"));
                    System.out.println(result.getInt("customerID") + "\t" + result.getString("name"));
                }
            } else {
               System.out.println("No member with that name exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }

            System.out.print("Please pick an ID# from the list or enter 'q' to go back: ");
            answer = scanner.next();
            while (!answer.contains("q")) {
                if (ids.contains(Integer.parseInt(answer))) {
                    int id = Integer.parseInt(answer);
                    
                    String name = null;
                    String nameLast = null;
                    String phone = null;
                    String email = null;
                    String dobDay = null;
                    String dobMon = null;
                    String dobY = null;
                    String emergencyName = null;
                    String emergencyLast = null;
                    String emergencyPhone = null;
                    
                    answer="n";
                    while (answer.contains("n")) {

                        System.out.println("Please give new info");
                        System.out.print("Enter name: ");
                        name = scanner.next();
                        nameLast = scanner.next();
                        System.out.print("Enter phone: ");
                        phone = scanner.next();
                        System.out.print("Enter email: ");
                        email = scanner.next();
                        System.out.print("Enter date of birth, Day: ");
                        dobDay = scanner.next();
                        System.out.print("\tMonth: ");
                        dobMon = scanner.next();
                        System.out.print("\tYear: ");
                        dobY = scanner.next();
                        System.out.print("Enter emergency contact name: ");
                        emergencyName = scanner.next();
                        emergencyLast = scanner.next();
                        System.out.print("Enter emergency contact phone#: ");
                        emergencyPhone = scanner.next();
                        System.out.println();

                        System.out.println("Is this correct y or n?");
                        System.out.println(String.format("%s %s, %s, %s, %s/%s/%s, %s %s, %s", name, nameLast, phone, email, dobY, dobMon, dobDay, emergencyName, emergencyLast, emergencyPhone));
                        answer = scanner.next();
                    }
                     String update = String.format("UPDATE lucashamacher.Customer SET name='%s %s', phone='%s', email='%s', dateOfBirth=TO_DATE('%s-%s-%s', 'YYYY-MM-DD'), emergencyContactName='%s %s', emergencyContactPhone='%s' WHERE customerID=%d", name,nameLast,phone,email,dobY,dobMon,dobDay,emergencyName,emergencyLast,emergencyPhone, id);
                    //System.out.println(update);
                    try {

                        Statement upStmt = dbconn.createStatement();
                        result = upStmt.executeQuery(update);
                        upStmt.close();

                    } catch (SQLException e) {

                        System.err.println("*** SQLException:  "
                            + "Could not fetch query results.");
                        System.err.println("\tMessage:   " + e.getMessage());
                        System.err.println("\tSQLState:  " + e.getSQLState());
                        System.err.println("\tErrorCode: " + e.getErrorCode());
                        System.exit(-1);

                    }
                    System.out.println("-----Successfully updated member------");
                    System.out.println();
                    return;
                }

                System.out.print("Please pick an ID# from the list or enter 'q' to go back: ");
                answer = scanner.next();
            }
            
        }


    }

    /*---------------------------------------------------------------------
    |  Method memberAdd
    |
    |  Purpose:  handles adding a member in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the member table
    |      will be added.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public static void memberAdd(Connection dbconn, Scanner scanner) {

        // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(customerID), max(customerID) FROM lucashamacher.Customer");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        }


        String name = null;
        String nameLast = null;
        String phone = null;
        String email = null;
        String dobDay = null;
        String dobMon = null;
        String dobY = null;
        String emergencyName = null;
        String emergencyLast = null;
        String emergencyPhone = null;
        
        String answer="n";
        while (answer.contains("n")) {

            System.out.println("Please give new info");
            System.out.print("Enter name: ");
            name = scanner.next();
            nameLast = scanner.next();
            System.out.print("Enter phone: ");
            phone = scanner.next();
            System.out.print("Enter email: ");
            email = scanner.next();
            System.out.print("Enter date of birth, Day: ");
            dobDay = scanner.next();
            System.out.print("\tMonth: ");
            dobMon = scanner.next();
            System.out.print("\tYear: ");
            dobY = scanner.next();
            System.out.print("Enter emergency contact name: ");
            emergencyName = scanner.next();
            emergencyLast = scanner.next();
            System.out.print("Enter emergency contact phone#: ");
            emergencyPhone = scanner.next();
            System.out.println();

            System.out.println("Is this correct y or n?");
            System.out.println(String.format("%s %s, %s, %s, %s/%s/%s, %s %s, %s", name, nameLast, phone, email, dobY, dobMon, dobDay, emergencyName, emergencyLast, emergencyPhone));
            answer = scanner.next();
        }
    
        String add = String.format("INSERT INTO lucashamacher.Customer VALUES ('%d', '%s %s', '%s', '%s', TO_DATE('%s-%s-%s', 'YYYY-MM-DD'), '%s %s', '%s')", id, name, nameLast, phone, email, dobY, dobMon, dobDay, emergencyName, emergencyLast, emergencyPhone);

        try {

            Statement addStmt = dbconn.createStatement();
            result = addStmt.executeQuery(add);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added member------");
        System.out.println();
        return;
    }

    /*---------------------------------------------------------------------
    |  Method memberDelete
    |
    |  Purpose:  handles deleting a member in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the member table
    |      will be deleted.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public static void memberDelete(Connection dbconn, Scanner scanner) {

        String answer = null;

		System.out.print("Enter the ID or name of the member you wish to delete: ");
        answer = scanner.next();
        System.out.println();

        if (answer.matches("\\d+")) {
            // case ID
            int id = Integer.parseInt(answer);
		    Statement stmt = null;
		    ResultSet result = null;
            String query = String.format("SELECT * FROM lucashamacher.Customer WHERE customerID=%d", id);
            try {

            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);
            boolean found = false;

            if (result.next()) {

                found = true;
                System.out.println("The current tuple is:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                System.out.println(result.getInt("customerID") + "\t" + result.getString("name") + 
                "\t" + result.getString("phone") + "\t" + result.getString("email") + "\t" + 
                result.getDate("dateOfBirth") + "\t" + result.getString("emergencyContactName") +
                "\t" + result.getString("emergencyContactPhone") );
            }
            if (!found) {
               System.out.println("No member with that ID exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }

            System.out.println("Is this the member you wish to delete (y/n)?: ");
            answer = scanner.next();

            if (answer.contains("y")) {
                if (!memberDeleteChecks(dbconn, scanner, id)) {
                    System.out.println("This member is unable to be deleted currently");
                    return;   
                }
                String deleteQ = String.format("DELETE FROM lucashamacher.Customer WHERE customerID=%d", id);
                Statement delStmt = null;

                try {

                    delStmt = dbconn.createStatement();
                    delStmt.executeQuery(deleteQ);
                    delStmt.close();  

                } catch (SQLException e) {

                    System.err.println("*** SQLException:  "
                        + "Could not fetch query results.");
                    System.err.println("\tMessage:   " + e.getMessage());
                    System.err.println("\tSQLState:  " + e.getSQLState());
                    System.err.println("\tErrorCode: " + e.getErrorCode());
                    System.exit(-1);
                }                
                System.out.println("-----Successfully deleted member-----");
                System.out.println();
                return;

            } else {
                return;
            }
        }

        // case names
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT customerID, name FROM lucashamacher.Customer WHERE name LIKE '%"+answer+"%'";
        ArrayList<Integer> ids = new ArrayList<Integer>();

        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {

                System.out.println("Results of name search are:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    ids.add(result.getInt("customerID"));
                    System.out.println(result.getInt("customerID") + "\t" + result.getString("name"));
                }
            } else {
               System.out.println("No member with that name exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        
        System.out.print("Enter which ID you wish to delete: ");
        answer = scanner.next();
        
         if (!answer.matches("\\d+")) {
            return;
        }

        if (ids.contains(Integer.parseInt(answer))) {
            int id =Integer.parseInt(answer);

            if (!memberDeleteChecks(dbconn, scanner, id)) {
                System.out.println("This member is unable to be deleted currently");
                return;
            }
            String deleteQ = String.format("DELETE FROM lucashamacher.Customer WHERE customerID=%d", id);
            Statement delStmt = null;

            try {

                delStmt = dbconn.createStatement();
                delStmt.executeQuery(deleteQ);
                delStmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
            }                
            System.out.println("-----Successfully deleted member-----");
            System.out.println();
            return;
        }
        return;
    }

    /*---------------------------------------------------------------------
    |  Method memberDeleteChecks
    |
    |  Purpose:  checks if member that is requested to be deleted can be
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the member table
    |      will be deleted.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |      id -- the customerID
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static boolean memberDeleteChecks(Connection dbconn, Scanner scanner, int id) {

        // event reservation check
        String eventQ = String.format("SELECT registrationID, customerID FROM lucashamacher.EventRegistration WHERE customerID=%d", id);
        Statement eventStmt = null;
        ResultSet result = null;
        try {

            eventStmt = dbconn.createStatement();
            result = eventStmt.executeQuery(eventQ);

            if (result != null) {
                if (result.next()) {
                    eventStmt.close();
                    return false; 
                }
                eventStmt.close();
            }

            eventStmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        // adoption applications check
        String adoptQ = String.format("SELECT applicationID, customerID FROM lucashamacher.AdoptionApplication WHERE customerID=%d", id);
        Statement adoptStmt = null;
        try {

            adoptStmt = dbconn.createStatement();
            result = adoptStmt.executeQuery(adoptQ);

            if (result != null) {
                if (result.next()) {
                    adoptStmt.close();  
                    return false; 
                }
                adoptStmt.close();
            }

            adoptStmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        return true;
    }

    /*---------------------------------------------------------------------
    |  Method changeMembership
    |
    |  Purpose:  modifies membership of given customer
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and the membership of the 
    |      customer in the membership relationship will be changed.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    public static void changeMemberShip(Connection dbconn, Scanner scanner) {
        String answer = null;

		System.out.print("Enter the ID or name of the member you wish to change: ");
        answer = scanner.next();
        System.out.println();

        if (answer.matches("\\d+")) {
            // case ID
            int id = Integer.parseInt(answer);
		    Statement stmt = null;
		    ResultSet result = null;
            String query = String.format("SELECT * FROM lucashamacher.CustomerMembership WHERE customerID=%d", id);
            try {

            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result.next()) {
                System.out.println("The current membership relationship is:");
                System.out.println(result.getInt(1) + "\t" + result.getInt(2) + 
                "\t" + result.getInt(3) + "\t" + result.getDate(4) + "\t" + 
                result.getDate(5) );
                System.out.println();

                modifyMemberShip(dbconn, scanner, id);
                stmt.close();
            } else {
                System.out.print("This member does not have a membership, do you wish to add one (y/n)?: ");
                answer = scanner.next();
                if (answer.equals("y")) {
                    addMembership(dbconn, scanner, id);
                }
                stmt.close();
                return;
            }
            System.out.println();

            stmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }
            return;
        }

        // case names
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT customerID, name FROM lucashamacher.Customer WHERE name LIKE '%"+answer+"%'";
        ArrayList<Integer> ids = new ArrayList<Integer>();

        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {

                System.out.println("Results of name search are:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    ids.add(result.getInt("customerID"));
                    System.out.println(result.getInt("customerID") + "\t" + result.getString("name"));
                }
            } else {
               System.out.println("No member with that name exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        
        System.out.print("Enter which Customer you'd wish to change membership: ");
        answer = scanner.next();
        
         if (!answer.matches("\\d+")) {
            return;
        }

        if (ids.contains(Integer.parseInt(answer))) {
            int id = Integer.parseInt(answer);
		    stmt = null;
		    result = null;
            query = String.format("SELECT * FROM lucashamacher.CustomerMembership WHERE customerID=%d", id);
            try {

            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {
                
                if (result.next()) {
                    System.out.println("The current membership relationship is:");
                    System.out.println(result.getInt(1) + "\t" + result.getInt(2) + 
                    "\t" + result.getInt(3) + "\t" + result.getDate(4) + "\t" + 
                    result.getDate(5) );
                    System.out.println();

                    modifyMemberShip(dbconn, scanner, id);
                    stmt.close();
                } else {
                    System.out.print("This member does not have a membership, do you wish to add one (y/n)?: ");
                    answer = scanner.next();
                    if (answer.equals("y")) {
                        addMembership(dbconn, scanner, id);
                    }
                    stmt.close();
                    return;
                }
            } else {
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }
            return;
        }
        return;
    }

    /*---------------------------------------------------------------------
    |  Method modifyMemberShip
    |
    |  Purpose:  modifies membership of a given customer
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and customer's membership
    |      will be changed.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |      id -- the customerID
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void modifyMemberShip(Connection dbconn, Scanner scanner, int id) {
        
        // get todays date
        String membershipFK;
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        String year = formattedDate.substring(0,4);
        String month = formattedDate.substring(5,7);
        String day = formattedDate.substring(8,10);
        String yearEnd;
        String monthEnd;
        String answer;

        System.out.println("Which membership would you like");
        System.out.println("\t(a) Basic Monthly");
		System.out.println("\t(b) Standard Monthly");
		System.out.println("\t(c) Premium Monthly");
		System.out.println("\t(d) Basic Annual");
        System.out.println("\t(e) Premium Annual");
        System.out.println("\tEnter 'q' to exit");	

        answer = scanner.next();

        switch (answer) {
            case "a":
                membershipFK = "1";
                if (Integer.parseInt(month) == 12) {
                    monthEnd = "01";
                    yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                } else {
                    yearEnd = year;
                    monthEnd = String.valueOf(Integer.parseInt(month) + 1);
                    if (monthEnd.length() == 1) {
                        monthEnd = "0" + monthEnd;
                    }
                }
                System.out.println("Confirm: Basic Monthly from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "b":
                membershipFK = "2";
                if (Integer.parseInt(month) == 12) {
                    monthEnd = "01";
                    yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                } else {
                    yearEnd = year;
                    monthEnd = String.valueOf(Integer.parseInt(month) + 1);
                    if (monthEnd.length() == 1) {
                        monthEnd = "0" + monthEnd;
                    }
                }
                System.out.println("Confirm: Standard Monthly from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "c":
                membershipFK = "3";
                if (Integer.parseInt(month) == 12) {
                    monthEnd = "01";
                    yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                } else {
                    yearEnd = year;
                    monthEnd = String.valueOf(Integer.parseInt(month) + 1);
                    if (monthEnd.length() == 1) {
                        monthEnd = "0" + monthEnd;
                    }
                }
                System.out.println("Confirm: Premium Monthly from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "d":
                membershipFK = "4";
                monthEnd = month;
                yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                System.out.println("Confirm: Basic Annual from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "e":
                membershipFK = "5";
                monthEnd = month;
                yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                System.out.println("Confirm: Premium Annual from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "q":
                return;
            default:
                return;
        }

        answer = scanner.next();
        if (!answer.equals("y")) {
            System.out.println("-----Canceling-----");
            return;
        }

        String addQ = String.format("UPDATE lucashamacher.CustomerMembership SET membershipID='%s', startDate=TO_DATE('%s-%s-%s', 'YYYY-MM-DD'), endDate=TO_DATE('%s-%s-%s', 'YYYY-MM-DD') WHERE customerID=%d", membershipFK,year, month, day,yearEnd,monthEnd,day,id);        
        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(addQ);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added membership------");
        System.out.println();
        return;
    }

    /*---------------------------------------------------------------------
    |  Method addMemberShip
    |
    |  Purpose:  for when a customer doesn't have membership this adds one
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and customer's membership
    |      will be added.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |      id -- the customerID
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void addMembership(Connection dbconn, Scanner scanner, int id) {
        
        // get todays date
        String membershipFK;
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        String year = formattedDate.substring(0,4);
        String month = formattedDate.substring(5,7);
        String day = formattedDate.substring(8,10);
        String yearEnd;
        String monthEnd;
        String answer;

        System.out.println("Which membership would you like");
        System.out.println("\t(a) Basic Monthly");
		System.out.println("\t(b) Standard Monthly");
		System.out.println("\t(c) Premium Monthly");
		System.out.println("\t(d) Basic Annual");
        System.out.println("\t(e) Premium Annual");
        System.out.println("\tEnter 'q' to exit");	

        answer = scanner.next();

        switch (answer) {
            case "a":
                membershipFK = "1";
                if (Integer.parseInt(month) == 12) {
                    monthEnd = "01";
                    yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                } else {
                    yearEnd = year;
                    monthEnd = String.valueOf(Integer.parseInt(month) + 1);
                    if (monthEnd.length() == 1) {
                        monthEnd = "0" + monthEnd;
                    }
                }
                System.out.println("Confirm: Basic Monthly from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "b":
                membershipFK = "2";
                if (Integer.parseInt(month) == 12) {
                    monthEnd = "01";
                    yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                } else {
                    yearEnd = year;
                    monthEnd = String.valueOf(Integer.parseInt(month) + 1);
                    if (monthEnd.length() == 1) {
                        monthEnd = "0" + monthEnd;
                    }
                }
                System.out.println("Confirm: Standard Monthly from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "c":
                membershipFK = "3";
                if (Integer.parseInt(month) == 12) {
                    monthEnd = "01";
                    yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                } else {
                    yearEnd = year;
                    monthEnd = String.valueOf(Integer.parseInt(month) + 1);
                    if (monthEnd.length() == 1) {
                        monthEnd = "0" + monthEnd;
                    }
                }
                System.out.println("Confirm: Premium Monthly from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "d":
                membershipFK = "4";
                monthEnd = month;
                yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                System.out.println("Confirm: Basic Annual from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "e":
                membershipFK = "5";
                monthEnd = month;
                yearEnd = String.valueOf(Integer.parseInt(year) + 1);
                System.out.println("Confirm: Premium Annual from today until " + monthEnd + "/" + day + "/" + yearEnd + " (y/n)?");
                break;
            case "q":
                return;
            default:
                return;
        }

        answer = scanner.next();
        if (!answer.equals("y")) {
            System.out.println("-----Canceling-----");
            return;
        }

        int idShip;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(customerMembershipID), max(customerMembershipID) FROM lucashamacher.CustomerMembership");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            idShip = idMin -1;
        } else {
            idShip = idMax + 1;
        }
        
        String addQ = String.format("INSERT INTO lucashamacher.CustomerMembership VALUES (%d, %d, %s, TO_DATE('%s-%s-%s', 'YYYY-MM-DD'), TO_DATE('%s-%s-%s', 'YYYY-MM-DD'))", idShip,id,membershipFK,year, month, day,yearEnd,monthEnd,day);
        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(addQ);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added membership------");
        System.out.println();
        return;
        
    }

    /*---------------------------------------------------------------------
    |  Method petLanding
    |
    |  Purpose:  this method serves as the landing for an edit pet request
    |      so that it can call 3 helpers for update, delete, and add.
    |
	|  Pre-condition:  User requested to edit member and connection to oracle
    |      establisted.
    |
    |  Post-condition: the users request will be handled and the pet tables will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    public static void petLanding(Connection dbconn, Scanner scanner) {
        String answer = null;

		System.out.println("Would you like to update, delete, or add?:");
		System.out.println("\t(a) Update");
		System.out.println("\t(b) Delete");
		System.out.println("\t(c) Add");
        System.out.println("\tEnter 'q' to go back");

        answer = scanner.next();

        switch (answer) {
			case "a":
                petUpdate(dbconn, scanner);
				break;
			case "b":
                petDelete(dbconn, scanner);
				break;
			case "c":
                petAdd(dbconn, scanner);
				break;
            case "d":
                break;
			case "q":
				return;
			default:
				System.out.println("Invalid response, please try again.");
                petLanding(dbconn, scanner);
				return;
		}
		return;
    }

    /*---------------------------------------------------------------------
    |  Method petUpdate
    |
    |  Purpose:  handles update pet in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the pet table
    |      will be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void petUpdate(Connection dbconn, Scanner scanner) {
        String answer = null;

		System.out.print("Enter the ID or name of the pet you wish to update: ");
        answer = scanner.next();
        System.out.println();

        if (answer.matches("\\d+")) {
            // id case 
            int id = Integer.parseInt(answer);
		    Statement stmt = null;
		    ResultSet result = null;
            String query = String.format("SELECT * FROM lucashamacher.Pet WHERE petID=%d", id);
            try {

            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);
            boolean found = false;

            if (result.next()) {

                found = true;
                System.out.println("The current tuple is:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                System.out.println(result.getInt("petID") + "\t" + result.getString("name") + 
                "\t" + result.getString("species") + "\t" + result.getString("breed") + "\t" + 
                result.getString("temperament") + "\t" + result.getString("adoptableFlag") +
                "\t" + result.getString("ambassadorFlag") + "\t" + result.getString("rescueFlag"));
            }
            if (!found) {
               System.out.println("No pet with that ID exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }

            String name = null;
            String species = null;
            String breed = null;
            String temperament = null;
            String adoptable = null;
            String ambassador = null;
            String rescue = null;
            
            answer="n";
            while (answer.contains("n")) {

                System.out.println("Please give new info");
                System.out.print("Enter name: ");
                name = scanner.next();
                System.out.print("Enter species: ");
                species = scanner.next();
                System.out.print("Enter breed ");
                breed = scanner.next();
                System.out.print("Enter temperament: ");
                temperament = scanner.next();
                System.out.print("Is this pet adoptable (y/n)?: ");
                answer = scanner.next();
                while (!answer.equals("y") && !answer.equals("n")) {
                    System.out.println("Please enter y or n!");
                    answer = scanner.next();
                }
                if (answer.equals("y")) {
                    adoptable = "Yes";
                } else {
                    adoptable = "No";
                }
                System.out.println();
                System.out.print("Is this pet an ambassador (y/n)?: ");
                answer = scanner.next();
                while (!answer.equals("y") && !answer.equals("n")) {
                    System.out.println("Please enter y or n!");
                    answer = scanner.next();
                }
                if (answer.equals("y")) {
                    ambassador = "Yes";
                } else {
                    ambassador = "No";
                }
                System.out.println();
                System.out.print("Is this pet a rescue (y/n)?: ");
                answer = scanner.next();
                while (!answer.equals("y") && !answer.equals("n")) {
                    System.out.println("Please enter y or n!");
                    answer = scanner.next();
                }
                if (answer.equals("y")) {
                    rescue = "Yes";
                } else {
                    rescue = "No";
                }
                System.out.println();

                System.out.println("Is this correct y or n?");
                System.out.println(name+", "+species+", "+breed+", "+temperament+", adoptable: "+adoptable+", ambassador: "+ambassador+ ", rescue: " + rescue);
                answer = scanner.next();
            }

            // execute sql statement
            String update = String.format("UPDATE lucashamacher.Pet SET name='%s', species='%s', breed='%s', temperament='%s', adoptableFlag='%s', ambassadorFlag='%s', rescueFlag='%s' WHERE petID=%d", name,species,breed,temperament,adoptable,ambassador,rescue, id);
            //System.out.println(update);
            try {

                Statement upStmt = dbconn.createStatement();
                result = upStmt.executeQuery(update);
                upStmt.close();

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }
            System.out.println("-----Successfully updated Pet------");
            System.out.println();
            return;

        } else {
            // name case
		    Statement stmt = null;
		    ResultSet result = null;
            String query = "SELECT petID, name FROM lucashamacher.Pet WHERE name LIKE '%"+answer+"%'";
            ArrayList<Integer> ids = new ArrayList<Integer>();
            try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {

                System.out.println("Results of name search are:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    ids.add(result.getInt("petID"));
                    System.out.println(result.getInt("petID") + "\t" + result.getString("name"));
                }
            } else {
               System.out.println("No pet with that name exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }

            System.out.print("Please pick an ID# from the list or enter 'q' to go back: ");
            answer = scanner.next();
            while (!answer.contains("q")) {
                if (ids.contains(Integer.parseInt(answer))) {
                    int id = Integer.parseInt(answer);
                    
                    String name = null;
                    String species = null;
                    String breed = null;
                    String temperament = null;
                    String adoptable = null;
                    String ambassador = null;
                    String rescue = null;
                    
                    answer="n";
                    while (answer.contains("n")) {

                        System.out.println("Please give new info");
                        System.out.print("Enter name: ");
                        name = scanner.next();
                        System.out.print("Enter species: ");
                        species = scanner.next();
                        System.out.print("Enter breed ");
                        breed = scanner.next();
                        System.out.print("Enter temperament: ");
                        temperament = scanner.next();
                        System.out.print("Is this pet adoptable (y/n)?: ");
                        answer = scanner.next();
                        while (!answer.equals("y") && !answer.equals("n")) {
                            System.out.println("Please enter y or n!");
                            answer = scanner.next();
                        }
                        if (answer.equals("y")) {
                            adoptable = "Yes";
                        } else {
                            adoptable = "No";
                        }
                        System.out.println();
                        System.out.print("Is this pet an ambassador (y/n)?: ");
                        answer = scanner.next();
                        while (!answer.equals("y") && !answer.equals("n")) {
                            System.out.println("Please enter y or n!");
                            answer = scanner.next();
                        }
                        if (answer.equals("y")) {
                            ambassador = "Yes";
                        } else {
                            ambassador = "No";
                        }
                        System.out.println();
                        System.out.print("Is this pet a rescue (y/n)?: ");
                        answer = scanner.next();
                        while (!answer.equals("y") && !answer.equals("n")) {
                            System.out.println("Please enter y or n!");
                            answer = scanner.next();
                        }
                        if (answer.equals("y")) {
                            rescue = "Yes";
                        } else {
                            rescue = "No";
                        }
                        System.out.println();

                        System.out.println("Is this correct y or n?");
                        System.out.println(name+", "+species+", "+breed+", "+temperament+", adoptable: "+adoptable+", ambassador: "+ambassador+ ", rescue: " + rescue);
                        answer = scanner.next();
                    }
                    String update = String.format("UPDATE lucashamacher.Pet SET name='%s', species='%s', breed='%s', temperament='%s', adoptableFlag='%s', ambassadorFlag='%s', rescueFlag='%s' WHERE petID=%d", name,species,breed,temperament,adoptable,ambassador,rescue, id);
                    //System.out.println(update);
                    try {

                        Statement upStmt = dbconn.createStatement();
                        result = upStmt.executeQuery(update);
                        upStmt.close();

                    } catch (SQLException e) {

                        System.err.println("*** SQLException:  "
                            + "Could not fetch query results.");
                        System.err.println("\tMessage:   " + e.getMessage());
                        System.err.println("\tSQLState:  " + e.getSQLState());
                        System.err.println("\tErrorCode: " + e.getErrorCode());
                        System.exit(-1);

                    }
                    System.out.println("-----Successfully updated Pet------");
                    System.out.println();
                    return;
                }

                System.out.print("Please pick an ID# from the list or enter 'q' to go back: ");
                answer = scanner.next();
            }
            
        }
    }

    /*---------------------------------------------------------------------
    |  Method petDelete
    |
    |  Purpose:  handles deleting a pet in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the pet table
    |      will be deleted.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void petDelete(Connection dbconn, Scanner scanner) {
        
        String answer = null;

		System.out.print("Enter the ID or name of the pet you wish to delete: ");
        answer = scanner.next();
        System.out.println();

        if (answer.matches("\\d+")) {
            // case ID
            int id = Integer.parseInt(answer);
		    Statement stmt = null;
		    ResultSet result = null;
            String query = String.format("SELECT * FROM lucashamacher.Pet WHERE petID=%d", id);
            try {

            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);
            boolean found = false;

            while (result.next()) {

                found = true;
                System.out.println("The current tuple is:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                System.out.println(result.getInt("petID") + "\t" + result.getString("name") + 
                "\t" + result.getString("species") + "\t" + result.getString("breed") + "\t" + 
                result.getString("temperament") + "\t" + result.getString("adoptableFlag") +
                "\t" + result.getString("ambassadorFlag") + "\t" + result.getString("rescueFlag"));
            } 
            if (!found) {
               System.out.println("No pet with that ID exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }

            System.out.println("Is this the pet you wish to delete (y/n)?: ");
            answer = scanner.next();

            if (answer.contains("y")) {
                if (!petDeleteChecks(dbconn, scanner, id)) {
                    System.out.println("This pet is unable to be deleted currently");
                    return;   
                }
                String deleteQ = String.format("DELETE FROM lucashamacher.Pet WHERE petID=%d", id);
                Statement delStmt = null;

                try {

                    delStmt = dbconn.createStatement();
                    delStmt.executeQuery(deleteQ);
                    delStmt.close();  

                } catch (SQLException e) {

                    System.err.println("*** SQLException:  "
                        + "Could not fetch query results.");
                    System.err.println("\tMessage:   " + e.getMessage());
                    System.err.println("\tSQLState:  " + e.getSQLState());
                    System.err.println("\tErrorCode: " + e.getErrorCode());
                    System.exit(-1);
                }                
                System.out.println("-----Successfully deleted Pet-----");
                System.out.println();
                return;

            } else {
                return;
            }
        }

        // case names
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT petID, name FROM lucashamacher.Pet WHERE name LIKE '%"+answer+"%'";
        ArrayList<Integer> ids = new ArrayList<Integer>();

        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {

                System.out.println("Results of name search are:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    ids.add(result.getInt("petID"));
                    System.out.println(result.getInt("petID") + "\t" + result.getString("name"));
                }
            } else {
               System.out.println("No pet with that name exists"); 
                stmt.close();  
                return;
            }
            System.out.println();

            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        
        System.out.print("Enter which ID you wish to delete: ");
        answer = scanner.next();
        
         if (!answer.matches("\\d+")) {
            return;
        }

        if (ids.contains(Integer.parseInt(answer))) {
            int id =Integer.parseInt(answer);

            if (!petDeleteChecks(dbconn, scanner, id)) {
                System.out.println("This pet is unable to be deleted currently");
                return;
            }
            String deleteQ = String.format("DELETE FROM lucashamacher.Pet WHERE petID=%d", id);
            Statement delStmt = null;

            try {

                delStmt = dbconn.createStatement();
                delStmt.executeQuery(deleteQ);
                delStmt.close();  

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);
            }                
            System.out.println("-----Successfully deleted Pet-----");
            System.out.println();
            return;
        }
        return;
    }

    /*---------------------------------------------------------------------
    |  Method petDeleteChecks
    |
    |  Purpose:  checks if pet that is requested to be deleted can be
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the pet table
    |      will be deleted.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |      id -- the customerID
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static boolean petDeleteChecks(Connection dbconn, Scanner scanner, int id) {
        
        // check med records
        String eventQ = String.format("SELECT recordType, nextDate FROM lucashamacher.HealthRecord WHERE petID=%d", id);
        Statement eventStmt = null;
        ResultSet result = null;
        try {

            eventStmt = dbconn.createStatement();
            result = eventStmt.executeQuery(eventQ);

            while (result.next()) {
                if (result.getString(1).contains("eath")) {
                    eventStmt.close();
                    return true;
                }
                if (result.getString(1).contains("accination") || result.getString(1).contains("heckup")) {
                    if (result.getDate(2).toLocalDate().compareTo(LocalDate.now()) <= 0 ) {
                        eventStmt.close();
                        return false;
                    } 
                }
            }

            eventStmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
        
        // check adopted
        eventQ = String.format("SELECT status FROM lucashamacher.AdoptionApplication WHERE petID=%d", id);
        eventStmt = null;
        result = null;
        try {

            eventStmt = dbconn.createStatement();
            result = eventStmt.executeQuery(eventQ);

            while (result.next()) {
                if (result.getString(1).contains("pproved")) {
                   eventStmt.close();
                   return true; 
                }
            }

            eventStmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }


        return false;
    }

    /*---------------------------------------------------------------------
    |  Method petAdd
    |
    |  Purpose:  handles adding a pet in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the pet table
    |      will be added.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void petAdd(Connection dbconn, Scanner scanner) {

        // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(petID), max(petID) FROM lucashamacher.Pet");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        }

        String name = null;
        String species = null;
        String breed = null;
        String temperament = null;
        String adoptable = null;
        String ambassador = null;
        String rescue = null;
        
        String answer="n";
        while (answer.contains("n")) {

            System.out.println("Please give new info");
            System.out.print("Enter name: ");
            name = scanner.next();
            System.out.print("Enter species: ");
            species = scanner.next();
            System.out.print("Enter breed ");
            breed = scanner.next();
            System.out.print("Enter temperament: ");
            temperament = scanner.next();
            System.out.print("Is this pet adoptable (y/n)?: ");
            answer = scanner.next();
            while (!answer.equals("y") && !answer.equals("n")) {
                System.out.println("Please enter y or n!");
                answer = scanner.next();
            }
            if (answer.equals("y")) {
                adoptable = "Yes";
            } else {
                adoptable = "No";
            }
            System.out.println();
            System.out.print("Is this pet an ambassador (y/n)?: ");
            answer = scanner.next();
            while (!answer.equals("y") && !answer.equals("n")) {
                System.out.println("Please enter y or n!");
                answer = scanner.next();
            }
            if (answer.equals("y")) {
                ambassador = "Yes";
            } else {
                ambassador = "No";
            }
            System.out.println();
            System.out.print("Is this pet a rescue (y/n)?: ");
            answer = scanner.next();
            while (!answer.equals("y") && !answer.equals("n")) {
                System.out.println("Please enter y or n!");
                answer = scanner.next();
            }
            if (answer.equals("y")) {
                rescue = "Yes";
            } else {
                rescue = "No";
            }
            System.out.println();

            System.out.println("Is this correct y or n?");
            System.out.println(name+", "+species+", "+breed+", "+temperament+", adoptable: "+adoptable+", ambassador: "+ambassador+ ", rescue: " + rescue);
            answer = scanner.next();
        }
    
        String add = String.format("INSERT INTO lucashamacher.Pet VALUES ('%d', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", id, name, species, breed, temperament, adoptable, ambassador, rescue);

        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(add);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added pet------");
        System.out.println();
        return;
    }

    
    
    /*---------------------------------------------------------------------
    |  Method orderLanding
    |
    |  Purpose:  this method serves as the landing for an edit order request
    |      so that it can call 3 helpers for update, delete, and add.
    |
	|  Pre-condition:  User requested to edit order and connection to oracle
    |      establisted.
    |
    |  Post-condition: the users request will be handled and the order table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    public static void orderLanding(Connection dbconn, Scanner scanner) {
        String answer = null;

		System.out.println("Would you like to update, delete, or add?:");
		System.out.println("\t(a) Update");
		System.out.println("\t(b) Delete");
		System.out.println("\t(c) Add");
        System.out.println("\tEnter 'q' to go back");

        answer = scanner.next();

        switch (answer) {
			case "a":
                orderUpdate(dbconn, scanner);
				break;
			case "b":
                orderDelete(dbconn, scanner);
				break;
			case "c":
                orderAdd(dbconn, scanner);
				break;
            case "d":
                break;
			case "q":
				return;
			default:
				System.out.println("Invalid response, please try again.");
                orderLanding(dbconn, scanner);
				return;
		}
		return;
    }
    /*---------------------------------------------------------------------
    |  Method orderUpdate
    |
    |  Purpose:  handles update orders in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the order table
    |      will be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void orderUpdate(Connection dbconn, Scanner scanner) {
        String answer = null;
        System.out.print("Which order are you paying?: ");
        String orderID =scanner.next();
        int paymentID = addPayment(dbconn, Integer.parseInt(orderID));

        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("UPDATE lucashamacher.OrderItem set paymentID='%d' where orderId=%d", paymentID, Integer.parseInt(orderID));

        try {

        stmt = dbconn.createStatement();
        stmt.executeQuery(query);
        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully payed for order-----");

    }

    private static int addPayment(Connection dbconn, int orderID) {

        int amount = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format(
            "SELECT MI.price * OI.quantity " +
            "FROM lucashamacher.MenuItem MI " +
            "JOIN lucashamacher.OrderItem OI ON MI.menuItemID = OI.menuItemID " +
            "WHERE OI.orderID = %d",
            orderID
        );
        
        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);
            
            if (result != null) {

                while (result.next()) {
                    amount = result.getInt(1);
                }
                
            stmt.close();  
            }

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        stmt = null;
        result = null;
        query = String.format("SELECT min(paymentID), max(paymentID) FROM lucashamacher.Payment");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        }

        float total = amount;
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        String year = formattedDate.substring(0,4);
        String month = formattedDate.substring(5,7);
        String day = formattedDate.substring(8,10);
        stmt = null;
        query = String.format("INSERT INTO lucashamacher.Payment values ( %d, %f, TO_DATE('%s-%s-%s', 'YYYY-MM-DD'), '' )", id, total, year, month, day);
        try {

        stmt = dbconn.createStatement();
        stmt.executeQuery(query);

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        return id;
    }

    /*---------------------------------------------------------------------
    |  Method orderAdd
    |
    |  Purpose:  handles deleting a order in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the order tables
    |      will be added.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void orderDelete(Connection dbconn, Scanner scanner) {

        System.out.print("Enter order num to delete: ");
        String answer = scanner.next();


        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("Delete from lucashamacher.OrderItem where orderItemId=%s", answer);
        
        try {
            stmt = dbconn.createStatement();
            stmt.executeQuery(query);
 
            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully deleted order-----");
    }   

    /*---------------------------------------------------------------------
    |  Method orderAdd
    |
    |  Purpose:  handles adding a order in the table
    |
	|  Pre-condition:  user requested this and oracle connection established
    |
    |  Post-condition: request will be handled and tuple(s) in the order tables
    |      will be added.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
    private static void orderAdd(Connection dbconn, Scanner scanner) {
        // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(orderItemID), max(orderItemID) FROM lucashamacher.OrderItem");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        }

        int orderID = 0;
        int menuItemID = 0;
        int quantity = 0;
        
        String answer="n";
        while (answer.contains("n")) {

            System.out.println("What are you ordering?");
            System.out.println("\t(a) Coffee");
            System.out.println("\t(b) Tea");
            System.out.println("\t(c) Sandwich");
            System.out.println("\t(d) Salad");
            System.out.println("\t(e) Cookie");
            answer =scanner.next();
            switch (answer) {
                case "a":
                    menuItemID = 1;
                    break;
                case "b":
                    menuItemID = 2;
                    break;
                case "c":
                    menuItemID = 3;
                    break;
                case "d":
                    menuItemID = 4;
                    break;
                case "e":
                    menuItemID = 5;
                    break;
                default:
                    System.out.println("Item does not exist on menu");
                    return;
            }
            System.out.println("How many?: ");
            quantity = scanner.nextInt();

            System.out.print("Is this order associated with a reservation (y/n)?: ");
            answer =scanner.next(); 
            while (!answer.equals("y") && !answer.equals("n")) {
                answer =scanner.next();
            }
            if (answer.equals("n")) {
                orderID = addBlankReservationOrder(dbconn);
            } else {
                orderID = findOrderFromReserve(dbconn, scanner);
                if (orderID == -1) {
                    System.out.println("Reservation does not exist");
                    return;
                }
            }
            
            System.out.println();
            
            System.out.println("Is this correct y or n?");
            answer = scanner.next();
        }
        orderReservation(dbconn, orderID, menuItemID, quantity);
        
        String add = "INSERT INTO lucashamacher.OrderItem VALUES ('"+id+ "', '";
        if (orderID != -1) {
            add += orderID;
        }
        add += "', '" + menuItemID+ "', '', '" + quantity + "')";

        System.out.println(add);
        //String add = String.format("INSERT INTO lucashamacher.OrderItem VALUES ('%d', '%d', '%d', '', '%d')", id, orderID, menuItemID, quantity);

        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(add);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added Order------");
        System.out.println();
        return;
    }

    /*---------------------------------------------------------------------
    |  Method findOrderFromReserve
    |
    |  Purpose:  this method finds a order to a reservation
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  int of the orderID
    *-------------------------------------------------------------------*/
    private static int findOrderFromReserve(Connection dbconn, Scanner scanner) {
        System.out.println("Enter customer name");
        String answer = scanner.next();
        int custId = custIdFromName(dbconn, scanner, answer);
        if (custId == -1) {
           // no customer
            return -1;
        }
        int reserveID = reservationFromCust(dbconn, custId);
        if (reserveID == -1) {
            // no reservation
            return -1;
        }
        if (!hasReservationOrder(dbconn, reserveID)) {
            addReservationOrder(dbconn, reserveID);
        }

        int orderID = -1;
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT orderID name FROM lucashamacher.ReservationOrder WHERE reservationID=" + reserveID;
        
        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {
                while (result.next()) {
                    orderID = result.getInt(1);
                
                }
            } else {
                stmt.close();  
                return -1;
            }

            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        return orderID;
    }

    /*---------------------------------------------------------------------
    |  Method reserveIdFromCust
    |
    |  Purpose:  this method finds a reservation id from a given custID
    |
	|  Pre-condition:  we need to determine a reserver id
    |
    |  Post-condition: the users will help us determine which one
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |      custID -- fk in our fk pk pair
    |
    |  Returns:  int of the reservationID
    *-------------------------------------------------------------------*/
    private static int reservationFromCust(Connection dbconn, int custID) {
        int reservationID = -1;
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT reservationID name FROM lucashamacher.Reservation WHERE customerID=" + custID;

        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {
                while (result.next()) {
                    reservationID = result.getInt(1);
                
                }
            } else {
                stmt.close();  
                return -1;
            }

            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        return reservationID;
    }

    /*---------------------------------------------------------------------
    |  Method custIdFromName
    |
    |  Purpose:  this method finds a customer id from a given name pattern
    |
	|  Pre-condition:  we need to determine a cust id
    |
    |  Post-condition: the users will help us determine which one
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |      name -- name we are matching with
    |
    |  Returns:  int of the custID
    *-------------------------------------------------------------------*/
    private static int custIdFromName(Connection dbconn, Scanner scanner, String name) {
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT customerID, name FROM lucashamacher.Customer WHERE name LIKE '%"+name+"%'";
        ArrayList<Integer> ids = new ArrayList<Integer>();

        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {

                System.out.println("Results of name search are:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    ids.add(result.getInt("customerID"));
                    System.out.println(result.getInt("customerID") + "\t" + result.getString("name"));
                }
            } else {
               System.out.println("No member with that name exists"); 
                stmt.close();  
                return -1;
            }
            System.out.println();

            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        
        System.out.print("Enter which ID you are looking for: ");
        String answer = scanner.next();
        
         if (!answer.matches("\\d+")) {
            return -1;
        }

        if (ids.contains(Integer.parseInt(answer))) {
            return Integer.parseInt(answer);
        }
        return -1;
    }

    /*---------------------------------------------------------------------
    |  Method hasReservationOrder
    |
    |  Purpose:  this method checks if a reservation has a reservation order
    |
	|  Pre-condition:  the reservation exists
    |
    |  Post-condition: the users request will be handled and the reservation table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
    |
    |  Returns:  the orderID
    *-------------------------------------------------------------------*/
    public static boolean hasReservationOrder(Connection dbconn, int reservationID) {
        
        
        Statement stmt = null;
        ResultSet result = null;
        /* 
        could not figure this shit out
        LocalDateTime ldt = LocalDateTime.now();
        Timestamp ts = Timestamp.valueOf(ldt);
        */
        String query = String.format("Select reservationID from lucashamacher.ReservationOrder WHERE reservationID=%d", reservationID);
        boolean found = false;
        try {

        //System.out.println(query);
        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result.next()) {
            found = true;
        }

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        return found;
    }

    /*---------------------------------------------------------------------
    |  Method reservationLanding
    |
    |  Purpose:  this method updates the reservationorder table specifically
    |
	|  Pre-condition:  User requested to add an order with a reservation
    |
    |  Post-condition: the users request will be handled and the reservation table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |      menuID -- fk that corresponds to pk in menuItem
    |      quantity -- # of items
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    private static void orderReservation(Connection dbconn, int orderId, int menuID, int quantity) {

        int priceAdd = 0;
        int oldPrice = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT price FROM lucashamacher.MenuItem WHERE menuItemID=" + menuID;

        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {

                while (result.next()) {
                    priceAdd = result.getInt(1) * quantity;
                }
                
            stmt.close();  
            }

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        query = "SELECT totalamount from lucashamacher.ReservationOrder where orderid=" + orderId;
        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {
                while (result.next()) {
                    oldPrice = result.getInt(1);
                }
            stmt.close();  
            }

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        priceAdd= oldPrice + priceAdd;
        query = String.format("UPDATE lucashamacher.ReservationOrder SET totalamount='%d' where orderid=%d",priceAdd,orderId);
        try {
            stmt = dbconn.createStatement();
            stmt.executeQuery(query);

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
        return;
    }
    
    /*---------------------------------------------------------------------
    |  Method addBlankReservationOrder
    |
    |  Purpose:  this method adds a reservation order for non reservation orders
    |
	|  Pre-condition:  the order does not have a reservation associated
    |
    |  Post-condition: the users request will be handled and the reservation table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
    |
    |  Returns:  the orderID
    *-------------------------------------------------------------------*/
    public static int addBlankReservationOrder(Connection dbconn) {
        
        // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(orderID), max(orderID) FROM lucashamacher.ReservationOrder");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        } 
        
        stmt = null;
        result = null;
        /* 
        could not figure this shit out
        LocalDateTime ldt = LocalDateTime.now();
        Timestamp ts = Timestamp.valueOf(ldt);
        */
        query = String.format("Insert Into lucashamacher.ReservationOrder values ('%d', '0', '', '0')", id);
        try {

        //System.out.println(query);
        stmt = dbconn.createStatement();
        stmt.executeQuery(query);


        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        return id;
    }

    /*---------------------------------------------------------------------
    |  Method addReservationOrder
    |
    |  Purpose:  this method adds a reservation order for non reservation orders
    |
	|  Pre-condition:  the order does not have a reservation associated
    |
    |  Post-condition: the users request will be handled and the reservation table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
    |
    |  Returns:  the orderID
    *-------------------------------------------------------------------*/
    public static int addReservationOrder(Connection dbconn, int reservationID) {
        
        // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(orderID), max(orderID) FROM lucashamacher.ReservationOrder");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        } 
        
        stmt = null;
        result = null;
        /* 
        could not figure this shit out
        LocalDateTime ldt = LocalDateTime.now();
        Timestamp ts = Timestamp.valueOf(ldt);
        */
        query = String.format("Insert Into lucashamacher.ReservationOrder values ('%d', '%d', '', '0')", id,reservationID);
        try {

        //System.out.println(query);
        stmt = dbconn.createStatement();
        stmt.executeQuery(query);


        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        return id;
    }

    /*---------------------------------------------------------------------
    |  Method reservationLanding
    |
    |  Purpose:  this method serves as the landing for an edit reservation request
    |      so that it can call 3 helpers for update, delete, and add.
    |
	|  Pre-condition:  User requested to edit member and connection to oracle
    |      establisted.
    |
    |  Post-condition: the users request will be handled and the reservation table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    public static void reservationLanding(Connection dbconn, Scanner scanner) {
        String answer = null;

		System.out.println("Would you like to update, delete, or add?:");
		System.out.println("\t(a) Update");
		System.out.println("\t(b) Delete");
		System.out.println("\t(c) Add");
        System.out.println("\tEnter 'q' to go back");

        answer = scanner.next();

        switch (answer) {
			case "a":
                reserveUpdate(dbconn, scanner);
				break;
			case "b":
                reserveDelete(dbconn, scanner);
				break;
			case "c":
                reserveAdd(dbconn, scanner);
				break;
            case "d":
                break;
			case "q":
				return;
			default:
				System.out.println("Invalid response, please try again.");
                reservationLanding(dbconn, scanner);
				return;
		}
		return;
    }

    /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
   static private void reserveUpdate(Connection dbconn, Scanner scanner) {
        System.out.print("Enter reserveid you wish to update: ");
        int id = scanner.nextInt();

        String dobDay = null;
        String dobMon = null;
        String dobY = null;
        String startTime = null;
        String endTime = null;
        String dateEv = null;
        int roomID = 0;
        
        String answer="n";
        while (answer.contains("n")) {

            System.out.println("Please give new info");
            System.out.print("Enter date of event, Day: ");
            dobDay = scanner.next();
            System.out.print("\tMonth: ");
            dobMon = scanner.next();
            System.out.print("\tYear: ");
            dobY = scanner.next();
            System.out.print("Enter starttime: ");
            startTime = scanner.next();
            System.out.print("Enter endtime: ");
            endTime = scanner.next();
            System.out.println("Enter room name:");
            answer = scanner.next();
            roomID = roomNameToID(dbconn, scanner, answer);
            System.out.println();

            dateEv = dobY+"-"+dobMon+"-"+dobDay;

            System.out.println("Is this correct y or n?");
            System.out.println(dobY+"-"+dobMon+"-"+dobDay+", "+startTime+", " + endTime + ", " +roomID);
            answer = scanner.next();
        }
    
        String add = String.format("update lucashamacher.reservation set reservationDate=TO_DATE('%s', 'YYYY-MM-DD'), roomID='%d' where reservationID=%d)", dateEv, roomID, id);

        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(add);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully updated reservation------");
        System.out.println();
        return;
   }

   /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
   static private void reserveAdd(Connection dbconn, Scanner scanner) {
        
    // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(reservationID), max(reservationID) FROM lucashamacher.reservation");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        }

        int custID = -1;
        System.out.print("Enter customer name: ");
        String answer = scanner.next();
        custID = custIdFromName(dbconn, scanner, answer);
        if (custID == -1) {
            return;
        }
        
        int roomID = 0;
        String dobDay = null;
        String dobMon = null;
        String dobY = null;
        String startTime = null;
        String endTime = null;
        String capacity = null;
        String dateEv = null;
        
        answer="n";
        while (answer.contains("n")) {

            System.out.println("Please give new info");
            System.out.print("Enter date of reservation, Day: ");
            dobDay = scanner.next();
            System.out.print("\tMonth: ");
            dobMon = scanner.next();
            System.out.print("\tYear: ");
            dobY = scanner.next();
            System.out.print("Enter starttime: ");
            startTime = scanner.next();
            System.out.print("Enter endtime: ");
            endTime = scanner.next();
            System.out.println("Enter room name:");
            answer = scanner.next();
            roomID = roomNameToID(dbconn, scanner, answer);
            System.out.println();

            dateEv = dobY+"-"+dobMon+"-"+dobDay;

            System.out.println("Is this correct y or n?");
            System.out.println(dobY+"-"+dobMon+"-"+dobDay+", "+startTime+", " + endTime + ", " +roomID);
            answer = scanner.next();
        }
    
        String add = String.format("INSERT INTO lucashamacher.reservation VALUES (%d, %d, %d, TO_DATE('%s', 'YYYY-MM-DD'), '', '', '', '')", id, custID, roomID, dateEv);

        System.out.println(add);
        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(add);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added reservation------");
        System.out.println();
        return;
   }

   /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
   private static void reserveDelete(Connection dbconn, Scanner scanner) {
         System.out.print("Enter reservation num to delete: ");
        String answer = scanner.next();


        // check before
        LocalDate myLocalDate = LocalDate.now();
        LocalDate localDateFromDb = null;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("select reservationDate from lucashamacher.EventRegistration where eventId=%s", answer);
        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);
            
            if (result.next()) {
                localDateFromDb = result.getObject("date_column", LocalDate.class);
            }

            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        if (localDateFromDb == null || localDateFromDb.compareTo(localDateFromDb) > 0) {
            System.out.println("This reservation cannot be deleted");
            return;
        }   

        stmt = null;
        query = String.format("Delete from lucashamacher.reservation where reservationId=%s", answer);
        
        try {
            stmt = dbconn.createStatement();
            stmt.executeQuery(query);
 
            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        System.out.println("-----Successfully deleted reservation-----");
   }

    /*---------------------------------------------------------------------
    |  Method healthLanding
    |
    |  Purpose:  this method serves as the landing for an edit health record request
    |      so that it can call 3 helpers for update, delete, and add.
    |
	|  Pre-condition:  User requested to edit member and connection to oracle
    |      establisted.
    |
    |  Post-condition: the users request will be handled and the health record table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    public static void healthLanding(Connection dbconn, Scanner scanner) {
        String answer = null;

		System.out.println("Would you like to update, delete, or add?:");
		System.out.println("\t(a) Update");
		System.out.println("\t(b) Add");
        System.out.println("\tEnter 'q' to go back");

        answer = scanner.next();

        switch (answer) {
			case "a":
                healthUpdate(dbconn, scanner);
				break;
			case "b":
                healthAdd(dbconn, scanner);
				break;
			case "c":
				break;
            case "d":
                break;
			case "q":
				return;
			default:
				System.out.println("Invalid response, please try again.");
                reservationLanding(dbconn, scanner);
				return;
		}
		return;
    }

    /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
   private static void healthAdd(Connection dbconn, Scanner scanner) {
        
    String answer = null;
        System.out.print("Enter name of pet you wish to add record for: ");
        answer =scanner.next();

        int petID = petIdFromName(dbconn, scanner, answer);
        if (petID == -1) {
            return;
        }

        // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(healthrecordID), max(healthrecordID) FROM lucashamacher.healthrecord");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        }  

        int staffID = 0;
        String description = null;
        String type = null;
        String dateEv = null;

        answer = "n";
        while(answer.equals("n")) {

            System.out.println("Enter staffID for record: ");
            staffID = Integer.parseInt(scanner.next());
            System.out.print("Enter type: ");
            type = scanner.next();
            scanner.nextLine();
            System.out.println("Enter description: ");
            description = scanner.nextLine();
            // get todays date
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = today.format(formatter);
            String year = formattedDate.substring(0,4);
            String month = formattedDate.substring(5,7);
            String day = formattedDate.substring(8,10);

            dateEv = year+"-"+month+"-"+day;

            System.out.println("Is this correct y or n?");
            System.out.println(petID+", "+staffID+", "+type+"-"+dateEv);
            answer = scanner.next();
    }

    query = String.format("Insert into lucashamacher.healthrecord values (%d, %d, %d, '%s', '%s', TO_DATE('%s', 'YYYY-MM-DD'), '' )", id, petID, staffID, type, description, dateEv);

    try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(query);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added health record------");
        System.out.println();
        return;
   }

   /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
   private static void healthUpdate(Connection dbconn, Scanner scanner) {
        System.out.println("Enter health record ID: ");
        int id = scanner.nextInt();

        String type = null;
        String description = null;
        String dateEv = null;

        String answer = "n";
        while(answer.equals("n")) {

            System.out.print("Enter type: ");
            type = scanner.next();
            scanner.nextLine();
            System.out.println("Enter description: ");
            description = scanner.nextLine();
            // get todays date
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = today.format(formatter);
            String year = formattedDate.substring(0,4);
            String month = formattedDate.substring(5,7);
            String day = formattedDate.substring(8,10);

            dateEv = year+"-"+month+"-"+day;

            System.out.println("Is this correct y or n?");
            System.out.println(type+"-"+dateEv);
            answer = scanner.next();
    }

        String query = String.format("Update lucashamacher.healthrecord set recordtype='%s', description='%s', recordDate=TO_DATE('%s', 'YYYY-MM-DD')", type, description, dateEv);

        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(query);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully updated health record------");
        System.out.println();
        return;


   }

   /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
   private static int petIdFromName(Connection dbconn, Scanner scanner, String name) {
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT petID, name FROM lucashamacher.pet WHERE name LIKE '%"+name+"%'";
        ArrayList<Integer> ids = new ArrayList<Integer>();

        try {
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (result != null) {

                System.out.println("Results of name search are:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    ids.add(result.getInt("petID"));
                    System.out.println(result.getInt("petID") + "\t" + result.getString("name"));
                }
            } else {
               System.out.println("No pet with that name exists"); 
                stmt.close();  
                return -1;
            }
            System.out.println();

            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        
        System.out.print("Enter which ID you are looking for: ");
        String answer = scanner.next();
        
         if (!answer.matches("\\d+")) {
            return -1;
        }

        if (ids.contains(Integer.parseInt(answer))) {
            return Integer.parseInt(answer);
        }
        return -1;
    }

    /*---------------------------------------------------------------------
    |  Method adoptionLanding
    |
    |  Purpose:  this method serves as the landing for an edit adoption request
    |      so that it can call 3 helpers for update, delete, and add.
    |
	|  Pre-condition:  User requested to edit member and connection to oracle
    |      establisted.
    |
    |  Post-condition: the users request will be handled and the adoption table will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    public static void adoptionLanding(Connection dbconn, Scanner scanner) {

    }

    /*---------------------------------------------------------------------
    |  Method eventLanding
    |
    |  Purpose:  this method serves as the landing for an edit event request
    |      so that it can call 3 helpers for update, delete, and add.
    |
	|  Pre-condition:  User requested to edit member and connection to oracle
    |      establisted.
    |
    |  Post-condition: the users request will be handled and the event tables will
    |      be modified.
    |
    |  Parameters:
    |      dbconn -- Our Oracle connection object.
	|	   scanner -- just a scanner object so we dont create new ones.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    public static void eventLanding(Connection dbconn, Scanner scanner) {
        String answer = null;

		System.out.println("Would you like to update, delete, or add?:");
		System.out.println("\t(a) Update");
		System.out.println("\t(b) Delete");
		System.out.println("\t(c) Add");
        System.out.println("\tEnter 'q' to go back");

        answer = scanner.next();

        switch (answer) {
			case "a":
                eventUpdate(dbconn, scanner);
				break;
			case "b":
                eventDelete(dbconn, scanner);
				break;
			case "c":
                eventAdd(dbconn, scanner);
				break;
            case "d":
                eventRegister(dbconn, scanner);
                break;
			case "q":
				return;
			default:
				System.out.println("Invalid response, please try again.");
                eventLanding(dbconn, scanner);
				return;
		}
		return;
    }

    /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
    private static void eventUpdate (Connection dbconn, Scanner scanner) {
        System.out.print("Enter eventid you wish to delete: ");
        int id = scanner.nextInt();

        String name = null;
        String type = null;
        String description = null;
        String dobDay = null;
        String dobMon = null;
        String dobY = null;
        String startTime = null;
        String endTime = null;
        String capacity = null;
        String dateEv = null;
        int roomID = 0;
        
        String answer="n";
        while (answer.contains("n")) {

            System.out.println("Please give new info");
            System.out.print("Enter name: ");
            name = scanner.next();
            System.out.print("Enter type: ");
            type = scanner.next();
            scanner.nextLine();
            System.out.println("Enter description: ");
            description = scanner.nextLine();
            System.out.print("Enter date of event, Day: ");
            dobDay = scanner.next();
            System.out.print("\tMonth: ");
            dobMon = scanner.next();
            System.out.print("\tYear: ");
            dobY = scanner.next();
            System.out.print("Enter starttime: ");
            startTime = scanner.next();
            System.out.print("Enter endtime: ");
            endTime = scanner.next();
            System.out.println("Enter room name:");
            answer = scanner.next();
            roomID = roomNameToID(dbconn, scanner, answer);
            System.out.print("Enter capacity: ");
            capacity = scanner.next();
            System.out.println();

            dateEv = dobY+"-"+dobMon+"-"+dobDay;

            System.out.println("Is this correct y or n?");
            System.out.println(name+", "+type+", "+dobY+"-"+dobMon+"-"+dobDay+", "+startTime+", " + endTime + ", " +capacity);
            answer = scanner.next();
        }
    
        String add = String.format("update lucashamacher.Event set eventname='%s', eventType='%s', description='%s', eventDate=TO_DATE('%s', 'YYYY-MM-DD'), roodID='%d', capacity='%s')", id, name, type, description, dateEv, roomID, capacity);

        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(add);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully updated Event------");
        System.out.println();
        return;
    }

    /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
    private static void eventDelete (Connection dbconn, Scanner scanner) {
        System.out.print("Enter order num to delete: ");
        String answer = scanner.next();


        Statement stmt = null;
        String query = String.format("Delete from lucashamacher.Event where eventId=%s", answer);
        
        try {
            stmt = dbconn.createStatement();
            stmt.executeQuery(query);
 
            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        stmt = null;
        query = String.format("Delete from lucashamacher.EventRegistration where eventId=%s", answer);
        try {
            stmt = dbconn.createStatement();
            stmt.executeQuery(query);
 
            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        stmt = null;
        query = String.format("Delete from lucashamacher.Staffassignment where eventId=%s", answer);
        try {
            stmt = dbconn.createStatement();
            stmt.executeQuery(query);
 
            stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        System.out.println("-----Successfully deleted event-----");
    }

    /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
    private static void eventAdd (Connection dbconn, Scanner scanner) {
       
        // need to get a valid id
        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(eventID), max(eventID) FROM lucashamacher.Event");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        }

        String name = null;
        String type = null;
        String description = null;
        String dobDay = null;
        String dobMon = null;
        String dobY = null;
        String startTime = null;
        String endTime = null;
        String room;
        String capacity = null;
        String dateEv = null;
        int roomID = 0;
        
        String answer="n";
        while (answer.contains("n")) {

            System.out.println("Please give new info");
            System.out.print("Enter name: ");
            name = scanner.next();
            System.out.print("Enter type: ");
            type = scanner.next();
            scanner.nextLine();
            System.out.println("Enter description: ");
            description = scanner.nextLine();
            System.out.print("Enter date of event, Day: ");
            dobDay = scanner.next();
            System.out.print("\tMonth: ");
            dobMon = scanner.next();
            System.out.print("\tYear: ");
            dobY = scanner.next();
            System.out.print("Enter starttime: ");
            startTime = scanner.next();
            System.out.print("Enter endtime: ");
            endTime = scanner.next();
            System.out.println("Enter room name:");
            answer = scanner.next();
            roomID = roomNameToID(dbconn, scanner, answer);
            System.out.print("Enter capacity: ");
            capacity = scanner.next();
            System.out.println();

            dateEv = dobY+"-"+dobMon+"-"+dobDay;

            System.out.println("Is this correct y or n?");
            System.out.println(name+", "+type+", "+dobY+"-"+dobMon+"-"+dobDay+", "+startTime+", " + endTime + ", " +capacity);
            answer = scanner.next();
        }
    
        String add = String.format("INSERT INTO lucashamacher.Event VALUES (%d, '%s', '%s', '%s', TO_DATE('%s', 'YYYY-MM-DD'), '', '', %d, '%s')", id, name, type, description, dateEv, dateEv, startTime, dateEv, endTime, roomID, capacity);

        System.out.println(add);
        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(add);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added Event------");
        System.out.println();
        return;
    }

    private static int roomNameToID(Connection dbconn, Scanner scanner, String name) {
        
        int id = -1;
        String answer = null;
        Statement stmt = null;
        ResultSet result = null;
        String query = "SELECT roomID, roomName FROM lucashamacher.Room WHERE roomName LIKE '%"+name+"%'";
        ArrayList<Integer> ids = new ArrayList<Integer>();
        try {
        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            System.out.println("Results of name search are:");

            ResultSetMetaData resultmetadata = result.getMetaData();

            for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                System.out.print(resultmetadata.getColumnName(i) + "\t");
            }
            System.out.println();

            while (result.next()) {
                ids.add(result.getInt("roomID"));
                System.out.println(result.getInt("roomID") + "\t" + result.getString("roomName"));
            }
        } else {
            System.out.println("No room with that name exists"); 
            stmt.close();  
            return -1;
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }

        System.out.print("Please pick an ID# from the list or enter 'q' to go back: ");
        answer = scanner.next();
        while (!answer.contains("q")) {
            if (ids.contains(Integer.parseInt(answer))) {
                id = Integer.parseInt(answer);
                System.out.println("returns ");
                return id;
            }
            System.out.println("Please pick an ID# from the list or enter 'q' to go back ");
        }
        return id;
    }

    /*---------------------------------------------------------------------
    |  Method [Method Name]
    |
    |  Purpose:  [Explain what this method does to support the correct
    |      operation of its class, and how it does it.]
    |
	|  Pre-condition:  [Any non-obvious conditions that must exist
    |      or be true before we can expect this method to function
    |      correctly.]
    |
    |  Post-condition: [What we can expect to exist or be true after
    |      this method has executed under the pre-condition(s).]
    |
    |  Parameters:
    |      parameter_name -- [Explanation of the purpose of this
    |          parameter to the method.  Write one explanation for each
    |          formal parameter of this method.]
    |
    |  Returns:  [If this method sends back a value via the return
    |      mechanism, describe the purpose of that value here, otherwise
    |      state 'None.']
    *-------------------------------------------------------------------*/
    private static void eventRegister(Connection dbconn, Scanner scanner) {

        String answer = null;
        System.out.print("Enter customer name: ");
        answer=scanner.next();
        int custID = custIdFromName(dbconn, scanner, answer);
        if (custID == -1) {
            return;
        }

        int id;
        int idMin = 0;
        int idMax = 0;
        Statement stmt = null;
        ResultSet result = null;
        String query = String.format("SELECT min(registrationID), max(registrationID) FROM lucashamacher.EventRegistration");
        try {

        stmt = dbconn.createStatement();
        result = stmt.executeQuery(query);

        if (result != null) {

            while (result.next()) {
                idMin = result.getInt(1);
                idMax = result.getInt(2);
            }
        } else {
            idMin = 0;
            idMax = 0;
            stmt.close();  
        }
        System.out.println();

        stmt.close();  

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        if (idMin > 0) {
            id = idMin -1;
        } else {
            id = idMax + 1;
        }


        System.out.print("Enter event ID: ");
        int eventID=scanner.nextInt();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        String year = formattedDate.substring(0,4);
        String month = formattedDate.substring(5,7);
        String day = formattedDate.substring(8,10);

        String add = String.format("INSERT INTO lucashamacher.eventRegistration VALUES ('%d', '%d', '%d', '', TO_DATE('%s-%s-%s', 'YYYY-MM-DD'))", id, custID, eventID, year, month, day);

        try {

            Statement addStmt = dbconn.createStatement();
            addStmt.executeQuery(add);
            addStmt.close();

        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);

        }
        System.out.println("-----Successfully added event registration------");
        System.out.println();
        return;

    }
}
