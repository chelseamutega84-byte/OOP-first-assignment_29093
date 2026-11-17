AUCA_ID - School Management System (console, Java)

HOW TO RUN IN INTELLIJ
1. File > Open > select the AUCA_ID folder.
2. Right-click the "src" folder > Mark Directory as > Sources Root (if it is not blue).
3. File > Project Structure > Project SDK: choose JDK 17 or newer (21 works).
4. Open src/com/auca/main/Main.java and click the green arrow next to main().

STRUCTURE
com.auca.interfaces   Identifiable, Registrable
com.auca.abstracts    Person, AcademicMember
com.auca.<entity>     Student, Teacher, Administrator, Department, Course, Classroom,
                      Enrollment, Assessment, FeePayment, LibraryBook, BookLoan (one package each)
com.auca.exceptions   5 custom exceptions
com.auca.main         Main (menu + forms + workflows), Validator (reusable validation rules)

TO PERSONALISE
- Change PROJECT = "AUCA_ID" at the top of Main.java to your real project name / student ID.
- Rename the project folder to AUCA_<yourID>.
