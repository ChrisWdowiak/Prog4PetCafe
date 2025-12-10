import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
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

            if (result != null) {

                System.out.println("The current tuple is:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    System.out.println(result.getInt("customerID") + "\t" + result.getString("name") + 
                    "\t" + result.getString("phone") + "\t" + result.getString("email") + "\t" + 
                    result.getDate("dateOfBirth") + "\t" + result.getString("emergencyContactName") +
                    "\t" + result.getString("emergencyContactPhone") );
                }
            } else {
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

            if (result != null) {

                System.out.println("The current tuple is:");

                ResultSetMetaData resultmetadata = result.getMetaData();

                for (int i = 1; i <= resultmetadata.getColumnCount(); i++) {
                    System.out.print(resultmetadata.getColumnName(i) + "\t");
                }
                System.out.println();

                while (result.next()) {
                    System.out.println(result.getInt("customerID") + "\t" + result.getString("name") + 
                    "\t" + result.getString("phone") + "\t" + result.getString("email") + "\t" + 
                    result.getDate("dateOfBirth") + "\t" + result.getString("emergencyContactName") +
                    "\t" + result.getString("emergencyContactPhone") );
                }
            } else {
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
            // TODO
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
        //String addQ = "UPDATE lucashamacher.CustomerMembership SET membershipID='" + membershipFK + "', startDate='" ;
        //addQ += year+"/"+month+"/"+day+"', endDate='"+yearEnd+"/"+monthEnd+"/"+day+"' WHERE customerID=" + id;
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

}
