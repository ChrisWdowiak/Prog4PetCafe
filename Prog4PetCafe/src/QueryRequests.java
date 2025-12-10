/**
 * QueryRequests.java
 *
 * Provides a collection of static query methods used to retrieve and display
 * information from the Pet Café database system. This class uses JDBC
 * PreparedStatements to safely execute SQL queries and prints formatted
 * results to the console.
 *
 * The methods in this class handle:
 *   - Listing adoption applications for a given pet
 *   - Showing full visit history for a given customer
 *   - Listing all upcoming events with available capacity
 *   - Displaying complete health records for a given pet
 *
 * INPUT:
 *   - A valid java.sql.Connection object
 *   - A java.util.Scanner for user interaction
 *
 * BEHAVIOR:
 *   - Each method prompts the user for required parameters
 *   - Executes SQL queries with PreparedStatement
 *   - Displays formatted results using ResultSet
 *   - Handles SQL errors gracefully using logging
 *
 * NOTE:
 *   All queries rely on valid foreign key relationships
 *   between Customer, Pet, Event, Staff, Reservation, and related tables.
 *
 * Author: Hengsocheat Pok (NETID: hspok)
 * Course: CSC460
 * Date:   10 Dec 2025
 */

import java.sql.*;
import java.util.*;
import java.util.Date;

public class QueryRequests {

    public static String sql = "";
    public static PreparedStatement pstmt;
    public static ResultSet rs;
    public static ResultSetMetaData md;
    public static int columns, rows;

    public static void listAdoptionApplications(Connection dbconn, Scanner scanner){
    /**
     * Lists all adoption applications submitted for a given pet name.
     * Displays the applicant name, application date, status,
     * and the adoption coordinator responsible for the application.
     *
     * @param dbconn  Active database connection
     * @param scanner Scanner for user input
     */
        sql = """
              SELECT 
                    p.name AS petName,\r
                    c.name AS applicantName,\r
                    a.applicationDate,\r
                    a.status,\r
                    s.staffName AS coordinatorName\r
              FROM lucashamacher.AdoptionApplication a\r
              JOIN lucashamacher.Customer c ON a.customerID     = c.customerID\r
              JOIN lucashamacher.Staff    s ON a.coordinatorID  = s.staffID\r
              JOIN lucashamacher.Pet      p ON p.petID          = a.petID\r
              WHERE p.Name = ?
              """;

        try {
            pstmt = dbconn.prepareStatement(sql);
            while (true){
                System.out.println("Please type in the pet name (or \"exit\" to exit):");
                String answer = scanner.next();

                boolean isString = answer.matches("[A-Za-z]+");
                if (!isString) continue;

                if (answer.equalsIgnoreCase("exit")) break;

                pstmt.setString(1, answer);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    System.out.println("Pet's Name      : " + rs.getString("petName"));
                    System.out.println("Applicant's Name: " + rs.getString("applicantName"));
                    System.out.println("Application Date: " + rs.getDate("applicationDate"));
                    System.out.println("Status          : " + rs.getString("status"));
                    System.out.println("Coordinator     : " + rs.getString("coordinatorName"));
                    System.out.println("-------------------------\n");
                }
            }
            System.out.println("-----------END OF RESULT----------------");
            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            System.getLogger(QueryRequests.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public static void showVisitHistory(Connection dbconn, Scanner scanner){
    /**
     * Shows the complete visit history for a customer.
     * Includes reservation dates, room visited, food orders,
     * membership tier at the time of visit, and total amount spent.
     *
     * @param dbconn  Active database connection
     * @param scanner Scanner for user input
     */
        sql = """
                SELECT 
                    r.reservationID,
                    r.reservationDate,
                    ro.roomName,
                    m.membershipType AS membershipTier,
                    oi.quantity,
                    mi.itemName,
                    mi.price AS itemPrice,
                    SUM(o.totalAmount) OVER (PARTITION BY o.reservationID) AS totalSpent
                FROM lucashamacher.Reservation r
                JOIN lucashamacher.Customer c
                    ON c.customerID = r.customerID
                JOIN lucashamacher.Room ro
                    ON r.roomID = ro.roomID
                LEFT JOIN lucashamacher.ReservationOrder o
                    ON o.reservationID = r.reservationID
                LEFT JOIN lucashamacher.OrderItem oi
                    ON oi.orderID = o.orderID
                LEFT JOIN lucashamacher.MenuItem mi
                    ON mi.menuItemID = oi.menuItemID
                LEFT JOIN lucashamacher.CustomerMembership cm
                    ON cm.customerID = r.customerID
                AND r.reservationDate BETWEEN cm.startDate AND cm.endDate
                LEFT JOIN lucashamacher.Membership m
                    ON m.membershipID = cm.membershipID
                WHERE c.Name = ?
                ORDER BY r.reservationDate, o.orderID, oi.orderItemID
              """;

        try {
            pstmt = dbconn.prepareStatement(sql);
            while (true){
                System.out.println("Please type in the customer's name (or \"exit\" to exit):");
                System.out.println("First Name:");

                String firstName = scanner.next();
                if (firstName.trim().equalsIgnoreCase("exit")) break;

                System.out.println("Last Name:");
                String lastName = scanner.next();
                if (lastName.trim().equalsIgnoreCase("exit")) break;

                boolean isString = firstName.matches("[A-Za-z]+") && lastName.matches("[A-Za-z]+"); 
                if (!isString) continue;

                String fullName = firstName.trim() + " " + lastName.trim();
                pstmt.setString(1, fullName);

                rs = pstmt.executeQuery();

                while (rs.next()) {

                    int reservationID       = rs.getInt("reservationID");
                    Date reservationDate    = rs.getDate("reservationDate");
                    String roomName         = rs.getString("roomName") == null ? null : rs.getString("roomName");
                    String membership       = rs.getString("membershipTier");
                    Integer quantity        = rs.getObject("quantity") == null ? null : rs.getInt("quantity");
                    String itemName         = rs.getString("itemName");
                    Double itemPrice        = rs.getObject("itemPrice") == null ? null : rs.getDouble("itemPrice");
                    Double totalSpent       = rs.getObject("totalSpent") == null ? null : rs.getDouble("totalSpent");

                    System.out.println("Name            : " + fullName        );
                    System.out.println("Reservation ID  : " + reservationID);
                    System.out.println("Date of visit   : " + reservationDate);
                    System.out.println("Room            : " + roomName);
                    System.out.println("Membership      : " + membership);

                    if (itemName != null) {
                        System.out.println("  Item Ordered : " + itemName);
                        System.out.println("  Quantity     : " + quantity);
                        System.out.println("  Item Price   : " + itemPrice);
                    } else {
                        System.out.println("  No food orders this visit.");
                    }

                    System.out.println("Total Spent    : " + totalSpent);
                    System.out.println("-------------------------------\n");
                }
                System.out.println("-----------END OF RESULT----------------");
            }
            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            System.getLogger(QueryRequests.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public static void listAllUpcomingEvents(Connection dbconn){
    /**
     * Lists all upcoming events that still have available capacity.
     * Includes event name, date, time, room, current attendance count,
     * capacity limit, and staff coordinator assigned to that event.
     *
     * @param dbconn Active database connection
     */
        sql = """
                SELECT 
                    e.eventID,
                    e.eventName,
                    e.eventDate,
                    e.startTime,
                    e.endTime,
                    r.roomName AS roomLocation,
                    s.staffName AS coordinator,
                    COUNT(er.registrationID) AS currentAttendees,
                    e.eventCapacity AS maxCapacity
                FROM lucashamacher.Event e
                JOIN lucashamacher.Room r
                    ON e.roomID = r.roomID
                LEFT JOIN lucashamacher.EventRegistration er
                    ON e.eventID = er.eventID
                LEFT JOIN lucashamacher.StaffAssignment sa
                    ON sa.eventID = e.eventID
                LEFT JOIN lucashamacher.Staff s
                    ON sa.staffID = s.staffID
                WHERE e.eventDate >= SYSDATE
                
                GROUP BY
                    e.eventID, e.eventName, e.eventDate, e.startTime, e.endTime,
                    r.roomName, s.staffName, e.eventCapacity
                HAVING COUNT(er.registrationID) < e.eventCapacity
                ORDER BY e.eventDate, e.startTime
              """;

        try {
            pstmt = dbconn.prepareStatement(sql);
            rs = pstmt.executeQuery();
 
            while (rs.next()) {

                int eventID             = rs.getInt("eventID");
                String eventName        = rs.getString("eventName");
                Date eventDate          = rs.getDate("eventDate");
                Timestamp startTime     = rs.getTimestamp("startTime");
                Timestamp endTime       = rs.getTimestamp("startTime");
                String roomLocation     = rs.getString("roomLocation");
                String coordinator      = rs.getString("coordinator");
                int currentAttendees    = rs.getInt("currentAttendees");
                int maxCapacity         = rs.getInt("maxCapacity");

                System.out.println("EVENT ID        : " + eventID       );
                System.out.println("EVENT NAME      : " + eventName     );
                System.out.println("EVENT DATE      : " + eventDate     );
                System.out.println("START TIME      : " + startTime     );
                System.out.println("END TIME        : " + endTime       );
                System.out.println("ROOM LOCATION   : " + roomLocation  );
                System.out.println("COORDINATOR     : " + coordinator  );
                System.out.println("ATTENDEES       : " + currentAttendees  );
                System.out.println("MAX CAPACITY    : " + maxCapacity  );
                System.out.println("-------------------------------\n" );
            }
            System.out.println("--------------END OF RESULT-----------------" );
            
            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            System.getLogger(QueryRequests.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public static void showPetHealthRecord(Connection dbconn, Scanner scanner){
    /**
     * Displays the complete health record history for a given pet.
     * Includes species, breed, record type, record description,
     * record date, and the staff member responsible.
     *
     * @param dbconn  Active database connection
     * @param scanner Scanner for user input
     */
        sql = """
                SELECT 
                    p.name AS petName,
                    p.species,
                    p.breed,
                    h.recordType,
                    h.description AS healthRecordDescription,
                    h.recordDate,
                    s.staffName AS caretakerName
                FROM lucashamacher.Pet p
                JOIN lucashamacher.HealthRecord h
                    ON h.petID = p.petID
                JOIN lucashamacher.Staff s
                    ON s.staffID = h.staffID
                WHERE p.name = ?
                ORDER BY h.recordDate DESC
              """;

        try {
            pstmt = dbconn.prepareStatement(sql);
            while (true){
                System.out.println("Please type in the pet's name (or \"exit\" to exit):");
                String petName = scanner.next();
                if (petName.trim().equalsIgnoreCase("exit")) break;
                boolean isString = petName.matches("[A-Za-z]+"); 
                if (!isString) continue;
                pstmt.setString(1, petName.trim());

                rs = pstmt.executeQuery();

                while (rs.next()) {

                    String name                     = rs.getString("petName");
                    String species                  = rs.getString("species");
                    String breed                    = rs.getString("breed");
                    String recordType               = rs.getString("recordType");
                    String healthRecordDescription  = rs.getString("healthRecordDescription");
                    Date recordDate                 = rs.getDate("recordDate");
                    String staffName                = rs.getString("caretakername");

                    System.out.println("Pet Name            : " + name                      );
                    System.out.println("Species             : " + species                   );
                    System.out.println("Breed               : " + breed                     );
                    System.out.println("Record Type         : " + recordType                );
                    System.out.println("Record Description  : " + healthRecordDescription   );
                    System.out.println("Record Date         : " + recordDate                );
                    System.out.println("Staff Name          : " + staffName                 );
                    System.out.println("-------------------------------\n");
                }
            }
            System.out.println("-----------END OF RESULT----------------");
            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            System.getLogger(QueryRequests.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
}
