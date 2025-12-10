# Prog4PetCafe

============================================================
                PET CAFE MANAGEMENT SYSTEM
             CSC 460 - Final Project (Java + Oracle)
============================================================

Author:          Alex Herron             aherron@arizona.edu
                 Hengsocheat Pok         hspok@arizona.edu
                 Chris Wdowiak           cwdowiak@arizona.edu
                 Lucas Robert Hamacher   lucashamacher@arizona.edu
                 
Date:   Fall 2025
Files:  App.java, QueryRequests.java, EditRequests.java (optional),
        plus any supporting .java files.


============================================================
1. OVERVIEW
============================================================

This program implements a command-line interface for interacting with
the Pet Café relational database through JDBC and Oracle SQL.

The system allows users to:

• Add, update, or delete information in the database  
• Run pre-written SQL queries (Pet, Customer, Event, Reservation, etc.)  
• View adoption applications  
• View customer visit histories  
• List upcoming events with available capacity  
• Show detailed health records for any pet  

The program connects to the University of Arizona Oracle server
and uses PreparedStatements to safely query and modify data.


============================================================
2. HOW TO COMPILE
============================================================

The program requires:

• Java 17+  
• Oracle JDBC driver (ojdbc8.jar or ojdbc11.jar)
• A working UofA CS Oracle account

Compile using:

    javac App.java QueryRequests.java EditRequests.java

Example (Windows):

    javac App.java QueryRequests.java EditRequests.java


============================================================
3. HOW TO RUN
============================================================

Run the program using:

    java App.java (or App) <oracle_username> <oracle_password>

Example:

    java App lucashamacher mypassword

The program will attempt to connect to:

    jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle

If the username/password are incorrect, the program exits
with an error message.


============================================================
4. PROGRAM STRUCTURE
============================================================

------------------------
(A) App.java
------------------------

This is the main entry point for the entire system.

Major responsibilities:

• Loads JDBC driver  
• Establishes Oracle connection  
• Creates a Scanner for user input  
• Begins the main query loop  
• Closes database connection on exit  

The main menu supports:

(a) Add/update/delete data → handled in EditRequests.java  
(b) Enter query mode → handled in QueryRequests.java  
(c) Placeholder for extra features  
(d) Placeholder for extra features  
(q) Quit the program  


------------------------
(B) QueryRequests.java
------------------------

Contains all SQL SELECT queries used in the program.
Each method uses PreparedStatements for safety.

Current implemented queries:

1. listAdoptionApplications()
   - Given a pet name, prints all adoption applications,
     including applicant, date, status, and coordinator.

2. showVisitHistory()
   - Displays a customer's reservation/visit history,
     including room info, food orders, membership tier,
     and total amount spent per visit.

3. listAllUpcomingEvents()
   - Shows all events whose eventDate >= SYSDATE
     and where capacity is not yet full.

4. showPetHealthRecord()
   - Given a pet name, prints the pet's species, breed,
     each health record, and staff caretaker responsible.


------------------------
(C) EditRequests.java
------------------------

(Not shown here, but referenced by App.java.)

Handles INSERT, UPDATE, and DELETE operations for:
• Members
• Pets
• Orders
• Reservations
• Health Records
• Adoption Applications
• Events


============================================================
5. HOW USER INPUT WORKS
============================================================

All input is taken via a single Scanner object.
Users enter menu letters (a–d) or 'q' to quit.

When a query requires additional information (e.g., pet name),
the user is prompted repeatedly until valid input is entered.

String validation ensures:
• No empty input
• Alphabetic input where required

*Note: if an input is entered and no result is returned, it means
the entry with the input does not exist (e.g, input name)

============================================================
6. SQL SAFETY & ERROR HANDLING
============================================================

The program uses:

✓ PreparedStatement to prevent SQL injection  
✓ try/catch blocks for all JDBC operations  
✓ CLEAN closing of ResultSet and PreparedStatement  
✓ Logging of SQL errors for debugging  


============================================================
7. SAMPLE INTERACTION
============================================================

Enter the letter for which type of request you'd like:
    (a) Add, update, or delete from system
    (b) Enter query mode
    (c) Additional functionality?
    (d) Additional functionality?
    Enter 'q' to exit
> b

Which type of query would you like to see?:
    (a) List all adoption applications for a given pet
    (b) Show visit history of given customer
    (c) List all upcoming events with open capacity
    (d) Pet's Health Records
    Enter 'q' to go back
> a

Please type in the pet name (or "exit" to exit):
> Buddy

Pet's Name      : Buddy
Applicant's Name: Alice Johnson
Application Date: 2025-01-10
Status          : Pending
Coordinator     : Grace Kim
-------------------------


============================================================
8. COMMON ERRORS & FIXES
============================================================

ERROR: failed to load JDBC driver  
→ Make sure ojdbc8.jar is in your classpath.

ERROR: invalid arguments  
→ Run the program with:  
      java App <username> <password>

ERROR: failed to open JDBC connection  
→ Incorrect Oracle credentials or server unreachable.


============================================================
9. CONTACT / AUTHOR INFORMATION
============================================================

Author: Heng Pok
Course: CSC 460  
Instructor: Lester I. McCann
Email: hspok@arizona.edu  
University of Arizona 

============================================================
10. CONTRIBUTIONS
============================================================

Alex Herron (aherron)
- Oracle DB setup
- DB design
- Normalization analysis
- Design changes

Chris Wdowiak (cwdowiak)
- Created the GitHub repo
- The Majority of the JDBC application
- Design changes

Lucas Hamacher (lucashamacher)
- Oracle DB setup
- Desgin document
- Design changes

Heng Pok (hspok)
- JDBC application
- DB/E-R design & Planning
- Queries creation
- readme

============================================================
END OF FILE
============================================================


