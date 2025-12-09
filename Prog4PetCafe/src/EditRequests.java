import java.util.Scanner;
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
        System.out.println("\tEnter 'q' to go back");

        answer = scanner.next();

        switch (answer) {
			case "a":
				
				break;
			case "b":

				break;
			case "c":
				
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
		    Statement stmt = null;
		    ResultSet result = null;
            String query = String.format("SELECT * FROM lucashamacher.Customer WHERE customerID=%d");
            try {

            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);

            if (answer != null) {

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
            }
            System.out.println();

                // Shut down the connection to the DBMS.

            stmt.close();  
            dbconn.close();

            } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

            }

        } else {
            // name case


        }


    }


}
