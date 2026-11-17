package com.auca.main;
import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class Main {

    
    private static final String PROJECT = "AUCA_ID";

    
    private static final Scanner SC = new Scanner(System.in);

    private static final Object BACK = new Object();
    private static final Object CANCEL = new Object();

    private static final int PAST = 1;
    private static final int NOT_FUTURE = 2;

    
    private static Object editing = null;

    
    private static final List<Department> departments = new ArrayList<>();
    private static final List<Student> students = new ArrayList<>();
    private static final List<Teacher> teachers = new ArrayList<>();
    private static final List<Administrator> administrators = new ArrayList<>();
    private static final List<Course> courses = new ArrayList<>();
    private static final List<Classroom> classrooms = new ArrayList<>();
    private static final List<Enrollment> enrollments = new ArrayList<>();
    private static final List<Assessment> assessments = new ArrayList<>();
    private static final List<FeePayment> payments = new ArrayList<>();
    private static final List<LibraryBook> books = new ArrayList<>();
    private static final List<BookLoan> loans = new ArrayList<>();

    
    @FunctionalInterface
    private interface Parser {
        Object parse(String text, Object[] values) throws Exception;
    }

    @FunctionalInterface
    private interface Asker {
        Object ask(String prompt, Object[] values) throws Exception;
    }

    @FunctionalInterface
    private interface Check {
        void check(Identifiable item) throws Exception;
    }

    @FunctionalInterface
    private interface Rule {
        void check(Object built) throws Exception;
    }

    @FunctionalInterface
    private interface Hook {
        void run(Object[] values, Object built) throws Exception;
    }

    
    private static final class Field {
        final String label;
        final String hint;
        final int step;
        final Asker asker;                          
        final Function<Object, Object> getter;      
        final BiConsumer<Object, Object> setter;    

        Field(String label, String hint, int step, Asker asker,
              Function<Object, Object> getter, BiConsumer<Object, Object> setter) {
            this.label = label;
            this.hint = hint;
            this.step = step;
            this.asker = asker;
            this.getter = getter;
            this.setter = setter;
        }
    }

    
    private static final class Form {
        final String title;
        final String noun;
        final String[] steps;
        final List<Field> fields;
        final Function<Object[], Object> builder;
        final Supplier<List<? extends Identifiable>> list;
        final Consumer<Object> saver;
        Hook preSave;               
        Rule rule;                  
        Consumer<Object> postSave;  

        Form(String title, String noun, String[] steps, List<Field> fields,
             Function<Object[], Object> builder, Supplier<List<? extends Identifiable>> list,
             Consumer<Object> saver) {
            this.title = title;
            this.noun = noun;
            this.steps = steps;
            this.fields = fields;
            this.builder = builder;
            this.list = list;
            this.saver = saver;
        }
    }

    // ================================================================== MAIN MENU
    public static void main(String[] args) {
        System.out.println("Welcome to the " + PROJECT + " School Management System.");
        boolean running = true;
        while (running) {
            printMenu();
            String line = read("Select an option: ");
            int option;
            try {
                option = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("! Please type a number from the menu (0-19).");
                continue;
            }
            try {
                switch (option) {
                    case 1 -> registerFromMenu(Main::departmentForm);
                    case 2 -> registerFromMenu(Main::studentForm);
                    case 3 -> registerFromMenu(Main::teacherForm);
                    case 4 -> registerFromMenu(Main::administratorForm);
                    case 5 -> registerFromMenu(Main::courseForm);
                    case 6 -> registerFromMenu(Main::classroomForm);
                    case 7 -> registerFromMenu(Main::enrollmentForm);
                    case 8 -> registerFromMenu(Main::assessmentForm);
                    case 9 -> registerFromMenu(Main::feePaymentForm);
                    case 10 -> registerFromMenu(Main::libraryBookForm);
                    case 11 -> registerFromMenu(Main::bookLoanForm);
                    case 12 -> returnBook();
                    case 13 -> viewAllRecords();
                    case 14 -> searchById();
                    case 15 -> updateRecord();
                    case 16 -> studentAcademicSummary();
                    case 17 -> studentPaymentHistory();
                    case 18 -> viewAvailableBooks();
                    case 19 -> demonstratePolymorphism();
                    case 0 -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("! Invalid option. Choose a number from 0 to 19.");
                }
            } catch (ValidationException | DuplicateRecordException | RecordNotFoundException
                     | PrerequisiteMissingException | BookUnavailableException e) {
                System.out.println("! " + e.getMessage());
                System.out.println("  Returning to the main menu. Nothing was saved for the failed step.");
            } catch (Exception e) {
                System.out.println("! Unexpected problem: " + e.getMessage());
                System.out.println("  Returning to the main menu.");
            }
        }
    }

    private static void printMenu() {
        String[] left = {"1. Register Department", "2. Register Student", "3. Register Teacher",
                "4. Register Administrator", "5. Register Course", "6. Register Classroom",
                "7. Enroll Student", "8. Record Assessment", "9. Record Fee Payment",
                "10. Register Library Book"};
        String[] right = {"11. Borrow Library Book", "12. Return Library Book", "13. View All Records",
                "14. Search Record by ID", "15. Update Existing Record", "16. Student Academic Summary",
                "17. Student Payment History", "18. View Available Books", "19. Demonstrate Polymorphism",
                "0. Exit"};
        System.out.println();
        System.out.println("============== " + PROJECT + " SCHOOL MANAGEMENT ==============");
        for (int i = 0; i < left.length; i++) {
            System.out.printf("%-30s%s%n", left[i], right[i]);
        }
        System.out.println();
    }

    
    private static void registerFromMenu(Supplier<Form> factory) throws Exception {
        while (true) {
            Object saved = runForm(factory.get());
            if (saved == null) {
                return;
            }
            System.out.println("  [1] Register another   [2] View record   [0] Main menu");
            String s = read("Select option: ");
            if (s.equals("1")) {
                continue;
            }
            if (s.equals("2")) {
                ((Registrable) saved).printDetails();
            }
            return;
        }
    }

    private static Object runForm(Form f) throws Exception {
        int n = f.fields.size();
        Object[] v = new Object[n];
        int i = 0;
        int shownStep = 0;
        while (i < n) {
            Field fl = f.fields.get(i);
            if (fl.asker == null) {          
                i++;
                continue;
            }
            if (fl.step != shownStep) {
                shownStep = fl.step;
                printHeader(f, shownStep);
            }
            Object r = fl.asker.ask(prompt(fl, i + 1), v);
            if (r == CANCEL) {
                System.out.println("Cancelled. Nothing was saved.");
                return null;
            }
            if (r == BACK) {
                int p = previousAskable(f, i);
                if (p < 0) {
                    System.out.println("! You are already at the first field.");
                } else {
                    i = p;
                    int ps = f.fields.get(p).step;
                    if (ps != shownStep) {
                        shownStep = ps;
                        printHeader(f, shownStep);
                    }
                }
                continue;
            }
            v[i] = r;
            i++;
        }
        return reviewAndSave(f, v);
    }

    private static Object reviewAndSave(Form f, Object[] v) throws Exception {
        while (true) {
            printReview(f, v);
            String s = read("Select option: ");
            switch (s) {
                case "1" -> {
                    try {
                        Object built = f.builder.apply(v);
                        ((Registrable) built).validate();
                        if (f.rule != null) {
                            f.rule.check(built);
                        }
                        if (f.preSave != null) {
                            f.preSave.run(v, built);
                        }
                        f.saver.accept(built);
                        if (f.postSave != null) {
                            f.postSave.accept(built);
                        }
                        System.out.println("+ " + capitalize(f.noun) + " " + ((Identifiable) built).getId()
                                + " registered successfully.");
                        return built;
                    } catch (Exception e) {
                        System.out.println("! Cannot save: " + e.getMessage());
                        System.out.println("  Use option 2 to correct a field, or 0 to cancel.");
                    }
                }
                case "2" -> editFieldInReview(f, v);
                case "0" -> {
                    System.out.println("Cancelled. Nothing was saved.");
                    return null;
                }
                default -> System.out.println("! Choose 1, 2 or 0.");
            }
        }
    }

    private static void editFieldInReview(Form f, Object[] v) throws Exception {
        String s = read("Field number to edit: ");
        int k;
        try {
            k = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("! Please enter a field number.");
            return;
        }
        if (k < 1 || k > f.fields.size() || f.fields.get(k - 1).asker == null) {
            System.out.println("! That field cannot be edited.");
            return;
        }
        Field fl = f.fields.get(k - 1);
        System.out.println("Current value: " + show(v[k - 1]));
        Object r = fl.asker.ask(prompt(fl, k), v);
        if (r == CANCEL || r == BACK) {
            System.out.println("Edit cancelled; value unchanged.");
        } else {
            v[k - 1] = r;
        }
    }

    private static int previousAskable(Form f, int from) {
        for (int j = from - 1; j >= 0; j--) {
            if (f.fields.get(j).asker != null) {
                return j;
            }
        }
        return -1;
    }

    private static String prompt(Field fl, int number) {
        return String.format("%02d  %-20s [%s] : ", number, fl.label, fl.hint);
    }

    private static void box(String text) {
        System.out.println(String.format("| %-58s |", text));
    }

    private static void printHeader(Form f, int step) {
        int total = f.steps.length + 1;
        String line = "+" + "-".repeat(60) + "+";
        System.out.println();
        System.out.println(line);
        box(PROJECT + "  /  SCHOOL MANAGEMENT");
        box(String.format("%-42s Step %d of %d", f.title, step, total));
        System.out.println(line);
        List<String> labels = new ArrayList<>();
        for (int k = 0; k < f.steps.length; k++) {
            labels.add("[" + (k + 1) + "] " + f.steps[k]);
        }
        labels.add("[" + total + "] Review & confirm");
        for (int k = 0; k < labels.size(); k += 2) {
            String a = labels.get(k);
            String b = k + 1 < labels.size() ? labels.get(k + 1) : "";
            box(String.format("%-28s%s", a, b));
        }
        System.out.println(line);
        box("Enter the requested value. Type BACK to return;");
        box("CANCEL to discard the unsaved " + f.noun + ".");
        System.out.println(line);
        System.out.println("[STEP " + step + "/" + total + "] " + f.steps[step - 1].toUpperCase());
    }

    private static void printReview(Form f, Object[] v) {
        String line = "+" + "-".repeat(60) + "+";
        System.out.println();
        System.out.println(line);
        box("REVIEW " + f.noun.toUpperCase());
        System.out.println(line);
        int lastStep = 0;
        for (int i = 0; i < f.fields.size(); i++) {
            Field fl = f.fields.get(i);
            if (fl.step != lastStep) {
                lastStep = fl.step;
                System.out.println("-- " + f.steps[lastStep - 1].toUpperCase() + " --");
            }
            String value = fl.asker == null && v[i] == null ? "(not returned yet)" : show(v[i]);
            System.out.println(String.format("%02d %-20s : %s", i + 1, fl.label, value));
        }
        System.out.println(line);
        System.out.println("| [1] Save " + f.noun + "   [2] Edit field   [0] Cancel");
        System.out.println(line);
    }

    
    private static String label(Identifiable item) {
        return "[" + item.getId() + "] " + item.getDisplayName();
    }

    private static String show(Object o) {
        if (o == null) {
            return "-";
        }
        if (o instanceof Boolean b) {
            return b ? "YES" : "NO";
        }
        if (o instanceof Identifiable item) {
            return label(item);
        }
        return String.valueOf(o);
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    
    private static String read(String prompt) {
        System.out.print(prompt);
        try {
            return SC.nextLine().trim();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Input closed. Goodbye.");
            System.exit(0);
            return "";
        }
    }

    private static boolean confirm(String question) {
        System.out.println(question + "  [1] Yes   [0] No");
        return read("Select option: ").equals("1");
    }

    private static LocalDate parseDate(String label, String s) throws ValidationException {
        try {
            return LocalDate.parse(Validator.requireText(label, s));
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date for " + label + ": use YYYY-MM-DD (example: 2004-05-16).");
        }
    }

    private static int parseInt(String label, String s) throws ValidationException {
        try {
            return Integer.parseInt(Validator.requireText(label, s));
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a whole number.");
        }
    }

    private static BigDecimal parseMoney(String label, String s) throws ValidationException {
        try {
            BigDecimal value = new BigDecimal(Validator.requireText(label, s));
            Validator.requirePositive(label, value);
            return value.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a valid amount (example: 150000.50).");
        }
    }

    private static double parseDouble(String label, String s) throws ValidationException {
        try {
            double d = Double.parseDouble(Validator.requireText(label, s));
            if (!Double.isFinite(d)) {
                throw new ValidationException(label + " must be a valid number.");
            }
            return d;
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a number (example: 17.5).");
        }
    }

    private static Parser textP(String label, int min, int max) {
        return (s, v) -> {
            Validator.requireLength(label, s, min, max);
            return s.trim();
        };
    }

    private static Parser uniqueTextP(String label, int min, int max, Predicate<String> exists) {
        return (s, v) -> {
            Validator.requireLength(label, s, min, max);
            if (exists.test(s.trim())) {
                throw new DuplicateRecordException(label + " '" + s.trim() + "' already exists.");
            }
            return s.trim();
        };
    }

    private static Parser plainP(String label) {
        return (s, v) -> Validator.requireText(label, s);
    }

    private static Parser nameP(String label) {
        return (s, v) -> {
            Validator.requireName(label, s);
            return s.trim();
        };
    }

    private static Parser idP(String label, Predicate<String> exists) {
        return (s, v) -> {
            Validator.requireId(label, s);
            if (exists.test(s.trim())) {
                throw new DuplicateRecordException(label + " '" + s.trim() + "' already exists.");
            }
            return s.trim();
        };
    }

    private static Parser emailP(String label, Predicate<String> exists) {
        return (s, v) -> {
            Validator.requireEmail(label, s);
            if (exists != null && exists.test(s.trim())) {
                throw new DuplicateRecordException(label + " '" + s.trim() + "' already exists.");
            }
            return s.trim();
        };
    }

    private static Parser phoneP(String label) {
        return (s, v) -> {
            Validator.requirePhone(label, s);
            return s.trim();
        };
    }

    private static Parser dateP(String label, int rule) {
        return (s, v) -> {
            LocalDate d = parseDate(label, s);
            if (rule == PAST) {
                Validator.requirePast(label, d);
            } else if (rule == NOT_FUTURE) {
                Validator.requireNotFuture(label, d);
            }
            return d;
        };
    }

    private static Parser intP(String label, int min, int max) {
        return (s, v) -> {
            int n = parseInt(label, s);
            Validator.requireRange(label, n, min, max);
            return n;
        };
    }

    private static Parser moneyP(String label) {
        return (s, v) -> parseMoney(label, s);
    }

    
    private static <T extends Identifiable> Predicate<String> idIn(Supplier<List<? extends Identifiable>> source) {
        return id -> source.get().stream().anyMatch(o -> o != editing && o.getId().equalsIgnoreCase(id));
    }

    private static <T> Predicate<String> uniq(Supplier<? extends Collection<T>> source, Function<T, String> key) {
        return x -> source.get().stream().anyMatch(o -> o != editing && key.apply(o).equalsIgnoreCase(x));
    }

    private static List<Person> persons() {
        List<Person> all = new ArrayList<>(students);
        all.addAll(teachers);
        all.addAll(administrators);
        return all;
    }

    private static List<AcademicMember> members() {
        List<AcademicMember> all = new ArrayList<>(students);
        all.addAll(teachers);
        return all;
    }

    private static List<Identifiable> allRecords() {
        List<Identifiable> all = new ArrayList<>();
        all.addAll(departments);
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        all.addAll(courses);
        all.addAll(classrooms);
        all.addAll(enrollments);
        all.addAll(assessments);
        all.addAll(payments);
        all.addAll(books);
        all.addAll(loans);
        return all;
    }

    
    private static Asker txt(Parser parser) {
        return (prompt, v) -> {
            while (true) {
                String s = read(prompt);
                if (s.equalsIgnoreCase("CANCEL")) {
                    return CANCEL;
                }
                if (s.equalsIgnoreCase("BACK")) {
                    return BACK;
                }
                try {
                    return parser.parse(s, v);
                } catch (Exception e) {
                    System.out.println("! " + e.getMessage() + "  Please re-enter this field.");
                }
            }
        };
    }

    
    private static Asker choice(List<String> options) {
        return (prompt, v) -> {
            while (true) {
                String s = read(prompt);
                if (s.equalsIgnoreCase("CANCEL")) {
                    return CANCEL;
                }
                if (s.equalsIgnoreCase("BACK")) {
                    return BACK;
                }
                for (String option : options) {
                    if (option.equalsIgnoreCase(s)) {
                        return option;
                    }
                }
                try {
                    int k = Integer.parseInt(s);
                    if (k >= 1 && k <= options.size()) {
                        return options.get(k - 1);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("! Type the number of your choice.");
                    continue;
                }
                System.out.println("! Choose a number from 1 to " + options.size() + ".");
            }
        };
    }

    private static Asker yesNo() {
        Asker inner = choice(List.of("YES", "NO"));
        return (prompt, v) -> {
            Object r = inner.ask(prompt, v);
            return r instanceof String s ? Boolean.valueOf(s.equals("YES")) : r;
        };
    }

    private static String hintOf(List<String> options) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            sb.append(i == 0 ? "" : " / ").append(i + 1).append(' ').append(options.get(i));
        }
        return sb.toString();
    }

    
    private static Asker select(String type, String emptyMessage, Supplier<List<? extends Identifiable>> items,
                                Supplier<Form> register, Check check, boolean optional) {
        return (prompt, v) -> {
            while (true) {
                List<? extends Identifiable> list = items.get();
                if (list.isEmpty()) {
                    System.out.println(emptyMessage);
                    if (optional) {
                        return null;
                    }
                    System.out.println("  1  Register " + type + " now");
                    System.out.println("  0  Cancel");
                    String s = read("Select: ");
                    if (s.equals("1")) {
                        runForm(register.get());
                        continue;
                    }
                    if (s.equals("0") || s.equalsIgnoreCase("CANCEL")) {
                        throw new PrerequisiteMissingException(emptyMessage);
                    }
                    if (s.equalsIgnoreCase("BACK")) {
                        return BACK;
                    }
                    System.out.println("! Enter 1 to register or 0 to cancel.");
                    continue;
                }
                System.out.println();
                System.out.println("+---------- CHOOSE " + type.toUpperCase() + " ----------+");
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("  " + (i + 1) + "  " + label(list.get(i)));
                }
                int plus = list.size() + 1;
                if (register != null) {
                    System.out.println("  " + plus + "  [+] Register a new " + type);
                }
                if (optional) {
                    System.out.println("  S  Skip (leave unassigned; assign later)");
                }
                System.out.println("  0  Cancel");
                String s = read("Select option: ");
                if (s.equals("0") || s.equalsIgnoreCase("CANCEL")) {
                    return CANCEL;
                }
                if (s.equalsIgnoreCase("BACK")) {
                    return BACK;
                }
                if (optional && s.equalsIgnoreCase("S")) {
                    return null;
                }
                int k;
                try {
                    k = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.out.println("! Please type a number from the list.");
                    continue;
                }
                if (register != null && k == plus) {
                    runForm(register.get());
                    continue;
                }
                if (k < 1 || k > list.size()) {
                    System.out.println("! Choose a number from the list.");
                    continue;
                }
                Identifiable chosen = list.get(k - 1);
                if (check != null) {
                    try {
                        check.check(chosen);
                    } catch (Exception e) {
                        System.out.println("! " + e.getMessage());
                        continue;
                    }
                }
                return chosen;
            }
        };
    }

    private static <T> Field f(String label, String hint, int step, Asker asker, Class<T> type,
                               Function<T, Object> get, BiConsumer<T, Object> set) {
        return new Field(label, hint, step, asker, o -> get.apply(type.cast(o)),
                set == null ? null : (o, x) -> set.accept(type.cast(o), x));
    }

    private static <T> Field derived(String label, String hint, int step, Class<T> type, Function<T, Object> get) {
        return new Field(label, hint, step, null, o -> get.apply(type.cast(o)), null);
    }

    private static Asker departmentSelector(String who) {
        return select("Department", "No Department exists. Register a Department before registering a " + who + ".",
                () -> departments, Main::departmentForm, null, false);
    }

    private static Asker teacherSelector(String emptyMessage) {
        return select("Teacher", emptyMessage, () -> teachers, Main::teacherForm, null, false);
    }

    private static Asker studentSelector(String emptyMessage) {
        return select("Student", emptyMessage, () -> students, Main::studentForm, null, false);
    }

    
    private static List<Field> personFields() {
        List<Field> l = new ArrayList<>();
        l.add(f("Person ID", "String | unique | 4-30 chars", 1,
                txt(idP("Person ID", idIn(Main::persons))), Person.class, Person::getPersonId, null));
        l.add(f("First name", "String | 2-60 chars", 1, txt(nameP("First name")),
                Person.class, Person::getFirstName, (p, x) -> p.setFirstName((String) x)));
        l.add(f("Last name", "String | 2-60 chars", 1, txt(nameP("Last name")),
                Person.class, Person::getLastName, (p, x) -> p.setLastName((String) x)));
        l.add(f("Email", "String | unique | email", 1,
                txt(emailP("Email", uniq(Main::persons, Person::getEmail))),
                Person.class, Person::getEmail, (p, x) -> p.setEmail((String) x)));
        l.add(f("Phone", "String | 10-15 digits, optional +", 1, txt(phoneP("Phone")),
                Person.class, Person::getPhoneNumber, (p, x) -> p.setPhoneNumber((String) x)));
        l.add(f("Date of birth", "LocalDate | YYYY-MM-DD | past", 1, txt(dateP("Date of birth", PAST)),
                Person.class, Person::getDateOfBirth, (p, x) -> p.setDateOfBirth((LocalDate) x)));
        return l;
    }

   
    private static List<Field> memberFields(String who) {
        List<Field> l = new ArrayList<>();
        l.add(f("Member number", "String | unique | 4-30", 2,
                txt(idP("Member number", uniq(Main::members, AcademicMember::getMemberNumber))),
                AcademicMember.class, AcademicMember::getMemberNumber, null));
        l.add(f("Department", "Department | select", 2, departmentSelector(who),
                AcademicMember.class, AcademicMember::getDepartment, (m, x) -> m.setDepartment((Department) x)));
        l.add(f("Join date", "LocalDate | YYYY-MM-DD", 2, txt(dateP("Join date", NOT_FUTURE)),
                AcademicMember.class, AcademicMember::getJoinDate, (m, x) -> m.setJoinDate((LocalDate) x)));
        l.add(f("Academic status", hintOf(Validator.ACADEMIC_STATUSES), 2, choice(Validator.ACADEMIC_STATUSES),
                AcademicMember.class, AcademicMember::getAcademicStatus, (m, x) -> m.setAcademicStatus((String) x)));
        l.add(f("Institutional email", "String | unique | email", 2,
                txt(emailP("Institutional email", uniq(Main::members, AcademicMember::getInstitutionalEmail))),
                AcademicMember.class, AcademicMember::getInstitutionalEmail,
                (m, x) -> m.setInstitutionalEmail((String) x)));
        l.add(f("Advisor name", "String | 2-120 chars", 2, txt(textP("Advisor name", 2, 120)),
                AcademicMember.class, AcademicMember::getAdvisorName, (m, x) -> m.setAdvisorName((String) x)));
        return l;
    }

    
    private static Form departmentForm() {
        List<Field> l = new ArrayList<>();
        l.add(f("Department ID", "String | unique | 4-30", 1,
                txt(idP("Department ID", idIn(() -> departments))), Department.class, Department::getDepartmentId, null));
        l.add(f("Department name", "String | 2-60 chars", 1, txt(nameP("Department name")),
                Department.class, Department::getDepartmentName, (d, x) -> d.setDepartmentName((String) x)));
        l.add(f("Department code", "String | unique | 2-20", 1,
                txt(uniqueTextP("Department code", 2, 20,
                        uniq(() -> departments, Department::getDepartmentCode))),
                Department.class, Department::getDepartmentCode, (d, x) -> d.setDepartmentCode((String) x)));
        l.add(f("Office location", "String | not blank", 1, txt(plainP("Office location")),
                Department.class, Department::getOfficeLocation, (d, x) -> d.setOfficeLocation((String) x)));
        l.add(f("Contact email", "String | email", 1, txt(emailP("Contact email", null)),
                Department.class, Department::getContactEmail, (d, x) -> d.setContactEmail((String) x)));
        l.add(f("Head teacher", "Teacher | optional | select", 2,
                select("Teacher", "No Teacher is registered yet; the head teacher stays unassigned "
                        + "(assign it later with option 15).", () -> teachers, null, null, true),
                Department.class, Department::getHeadTeacher, (d, x) -> d.setHeadTeacher((Teacher) x)));
        Form form = new Form("DEPARTMENT REGISTRATION", "department",
                new String[]{"Department details", "Head teacher (optional)"}, l,
                v -> new Department((String) v[0], (String) v[1], (String) v[2], (String) v[3], (String) v[4],
                        (Teacher) v[5]),
                () -> departments, o -> departments.add((Department) o));
        return form;
    }

    private static Form studentForm() {
        List<Field> l = personFields();
        l.addAll(memberFields("student"));
        l.add(f("Student number", "String | unique | 4-30", 3,
                txt(idP("Student number", uniq(() -> students, Student::getStudentNumber))),
                Student.class, Student::getStudentNumber, null));
        l.add(f("Program name", "String | 2-120 chars", 3, txt(textP("Program name", 2, 120)),
                Student.class, Student::getProgramName, (s, x) -> s.setProgramName((String) x)));
        l.add(f("Admission date", "LocalDate | YYYY-MM-DD", 3, txt(dateP("Admission date", NOT_FUTURE)),
                Student.class, Student::getAdmissionDate, (s, x) -> s.setAdmissionDate((LocalDate) x)));
        l.add(f("Study year", "int | 1-6", 3, txt(intP("Study year", 1, 6)),
                Student.class, Student::getStudyYear, (s, x) -> s.setStudyYear((Integer) x)));
        l.add(f("Guardian phone", "String | 10-15 digits", 3, txt(phoneP("Guardian phone")),
                Student.class, Student::getGuardianPhone, (s, x) -> s.setGuardianPhone((String) x)));
        l.add(f("Scholarship", "1 YES / 2 NO", 3, yesNo(),
                Student.class, Student::isScholarshipStatus, (s, x) -> s.setScholarshipStatus((Boolean) x)));
        return new Form("STUDENT REGISTRATION", "student",
                new String[]{"Personal details", "Academic details", "Student details"}, l,
                v -> new Student((String) v[0], (String) v[1], (String) v[2], (String) v[3], (String) v[4],
                        (LocalDate) v[5], (String) v[6], (Department) v[7], (LocalDate) v[8], (String) v[9],
                        (String) v[10], (String) v[11], (String) v[12], (String) v[13], (LocalDate) v[14],
                        (int) v[15], (String) v[16], (boolean) v[17]),
                () -> students, o -> students.add((Student) o));
    }

    private static Form teacherForm() {
        List<Field> l = personFields();
        l.addAll(memberFields("teacher"));
        l.add(f("Employee number", "String | unique | 4-30", 3,
                txt(idP("Employee number", uniq(() -> teachers, Teacher::getEmployeeNumber))),
                Teacher.class, Teacher::getEmployeeNumber, null));
        l.add(f("Specialization", "String | 2-120 chars", 3, txt(textP("Specialization", 2, 120)),
                Teacher.class, Teacher::getSpecialization, (t, x) -> t.setSpecialization((String) x)));
        l.add(f("Hire date", "LocalDate | YYYY-MM-DD", 3, txt(dateP("Hire date", NOT_FUTURE)),
                Teacher.class, Teacher::getHireDate, (t, x) -> t.setHireDate((LocalDate) x)));
        l.add(f("Salary", "BigDecimal | > 0", 3, txt(moneyP("Salary")),
                Teacher.class, Teacher::getSalary, (t, x) -> t.setSalary((BigDecimal) x)));
        l.add(f("Office number", "String | not blank", 3, txt(plainP("Office number")),
                Teacher.class, Teacher::getOfficeNumber, (t, x) -> t.setOfficeNumber((String) x)));
        l.add(f("Employment type", hintOf(Validator.EMPLOYMENT_TYPES), 3, choice(Validator.EMPLOYMENT_TYPES),
                Teacher.class, Teacher::getEmploymentType, (t, x) -> t.setEmploymentType((String) x)));
        return new Form("TEACHER REGISTRATION", "teacher",
                new String[]{"Personal details", "Academic details", "Teacher details"}, l,
                v -> new Teacher((String) v[0], (String) v[1], (String) v[2], (String) v[3], (String) v[4],
                        (LocalDate) v[5], (String) v[6], (Department) v[7], (LocalDate) v[8], (String) v[9],
                        (String) v[10], (String) v[11], (String) v[12], (String) v[13], (LocalDate) v[14],
                        (BigDecimal) v[15], (String) v[16], (String) v[17]),
                () -> teachers, o -> teachers.add((Teacher) o));
    }

    private static Form administratorForm() {
        List<Field> l = personFields();
        l.add(f("Staff number", "String | unique | 4-30", 2,
                txt(idP("Staff number", uniq(() -> administrators, Administrator::getStaffNumber))),
                Administrator.class, Administrator::getStaffNumber, null));
        l.add(f("Job title", "String | 2-120 chars", 2, txt(textP("Job title", 2, 120)),
                Administrator.class, Administrator::getJobTitle, (a, x) -> a.setJobTitle((String) x)));
        l.add(f("Assigned office", "String | not blank", 2, txt(plainP("Assigned office")),
                Administrator.class, Administrator::getAssignedOffice, (a, x) -> a.setAssignedOffice((String) x)));
        l.add(f("Employment date", "LocalDate | YYYY-MM-DD", 2, txt(dateP("Employment date", NOT_FUTURE)),
                Administrator.class, Administrator::getEmploymentDate, (a, x) -> a.setEmploymentDate((LocalDate) x)));
        l.add(f("Access level", "int | 1-5", 2, txt(intP("Access level", 1, 5)),
                Administrator.class, Administrator::getAccessLevel, (a, x) -> a.setAccessLevel((Integer) x)));
        l.add(f("Active", "1 YES / 2 NO", 2, yesNo(),
                Administrator.class, Administrator::isActive, (a, x) -> a.setActive((Boolean) x)));
        return new Form("ADMINISTRATOR REGISTRATION", "administrator",
                new String[]{"Personal details", "Staff details"}, l,
                v -> new Administrator((String) v[0], (String) v[1], (String) v[2], (String) v[3], (String) v[4],
                        (LocalDate) v[5], (String) v[6], (String) v[7], (String) v[8], (LocalDate) v[9],
                        (int) v[10], (boolean) v[11]),
                () -> administrators, o -> administrators.add((Administrator) o));
    }

    private static Form courseForm() {
        List<Field> l = new ArrayList<>();
        l.add(f("Course ID", "String | unique | 4-30", 1,
                txt(idP("Course ID", idIn(() -> courses))), Course.class, Course::getCourseId, null));
        l.add(f("Course code", "String | unique | 2-20", 1,
                txt(uniqueTextP("Course code", 2, 20, uniq(() -> courses, Course::getCourseCode))),
                Course.class, Course::getCourseCode, (c, x) -> c.setCourseCode((String) x)));
        l.add(f("Course title", "String | 2-120 chars", 1, txt(textP("Course title", 2, 120)),
                Course.class, Course::getCourseTitle, (c, x) -> c.setCourseTitle((String) x)));
        l.add(f("Credit hours", "int | 1-6", 1, txt(intP("Credit hours", 1, 6)),
                Course.class, Course::getCreditHours, (c, x) -> c.setCreditHours((Integer) x)));
        l.add(f("Department", "Department | select", 2, departmentSelector("course"),
                Course.class, Course::getDepartment, (c, x) -> c.setDepartment((Department) x)));
        l.add(f("Assigned teacher", "Teacher | select", 2,
                teacherSelector("No Teacher exists. Register a Teacher before registering a course."),
                Course.class, Course::getAssignedTeacher, (c, x) -> c.setAssignedTeacher((Teacher) x)));
        return new Form("COURSE REGISTRATION", "course", new String[]{"Course details", "Department & teacher"}, l,
                v -> new Course((String) v[0], (String) v[1], (String) v[2], (int) v[3], (Department) v[4],
                        (Teacher) v[5]),
                () -> courses, o -> courses.add((Course) o));
    }

    private static Form classroomForm() {
        List<Field> l = new ArrayList<>();
        l.add(f("Classroom ID", "String | unique | 4-30", 1,
                txt(idP("Classroom ID", idIn(() -> classrooms))), Classroom.class, Classroom::getClassroomId, null));
        l.add(f("Building name", "String | not blank", 1, txt(plainP("Building name")),
                Classroom.class, Classroom::getBuildingName, (c, x) -> c.setBuildingName((String) x)));
        l.add(f("Room number", "String | not blank", 1, txt(plainP("Room number")),
                Classroom.class, Classroom::getRoomNumber, (c, x) -> c.setRoomNumber((String) x)));
        l.add(f("Capacity", "int | > 0", 1, txt(intP("Capacity", 1, 100000)),
                Classroom.class, Classroom::getCapacity, (c, x) -> c.setCapacity((Integer) x)));
        l.add(f("Floor number", "int | 0 or more", 1, txt(intP("Floor number", 0, 200)),
                Classroom.class, Classroom::getFloorNumber, (c, x) -> c.setFloorNumber((Integer) x)));
        l.add(f("Available", "1 YES / 2 NO", 1, yesNo(),
                Classroom.class, Classroom::isAvailable, (c, x) -> c.setAvailable((Boolean) x)));
        return new Form("CLASSROOM REGISTRATION", "classroom", new String[]{"Classroom details"}, l,
                v -> new Classroom((String) v[0], (String) v[1], (String) v[2], (int) v[3], (int) v[4],
                        (boolean) v[5]),
                () -> classrooms, o -> classrooms.add((Classroom) o));
    }

    private static boolean duplicateEnrollment(Enrollment built) {
        return built.isActive() && enrollments.stream().anyMatch(e -> e != built && e.isActive()
                && e.getStudent() == built.getStudent() && e.getCourse() == built.getCourse()
                && e.getSemester().equalsIgnoreCase(built.getSemester()));
    }

    private static Form enrollmentForm() {
        List<Field> l = new ArrayList<>();
        l.add(f("Enrollment ID", "String | unique | 4-30", 1,
                txt(idP("Enrollment ID", idIn(() -> enrollments))), Enrollment.class, Enrollment::getEnrollmentId, null));
        l.add(f("Student", "Student | select", 1,
                studentSelector("No Student exists. Register a student before enrolling."),
                Enrollment.class, Enrollment::getStudent, null));
        l.add(f("Course", "Course | select", 1,
                select("Course", "No Course exists. Register a course before enrolling a student.",
                        () -> courses, Main::courseForm, null, false),
                Enrollment.class, Enrollment::getCourse, null));
        l.add(f("Enrollment date", "LocalDate | YYYY-MM-DD", 2, txt(dateP("Enrollment date", NOT_FUTURE)),
                Enrollment.class, Enrollment::getEnrollmentDate, (e, x) -> e.setEnrollmentDate((LocalDate) x)));
        l.add(f("Semester", hintOf(Validator.SEMESTERS), 2, choice(Validator.SEMESTERS),
                Enrollment.class, Enrollment::getSemester, (e, x) -> e.setSemester((String) x)));
        l.add(f("Enrollment status", hintOf(Validator.ENROLLMENT_STATUSES), 2, choice(Validator.ENROLLMENT_STATUSES),
                Enrollment.class, Enrollment::getEnrollmentStatus, (e, x) -> e.setEnrollmentStatus((String) x)));
        Form form = new Form("ENROLL STUDENT", "enrollment", new String[]{"Student & course", "Enrollment details"}, l,
                v -> new Enrollment((String) v[0], (Student) v[1], (Course) v[2], (LocalDate) v[3], (String) v[4],
                        (String) v[5]),
                () -> enrollments, o -> enrollments.add((Enrollment) o));
        form.rule = built -> {
            if (duplicateEnrollment((Enrollment) built)) {
                throw new DuplicateRecordException("This student already has an active enrollment in this course "
                        + "for that semester.");
            }
        };
        return form;
    }

    private static Form assessmentForm() {
        List<Field> l = new ArrayList<>();
        l.add(f("Assessment ID", "String | unique | 4-30", 1,
                txt(idP("Assessment ID", idIn(() -> assessments))), Assessment.class, Assessment::getAssessmentId, null));
        l.add(f("Enrollment", "Enrollment | active | select", 1,
                select("Enrollment", "No active Enrollment exists. Enroll a student before recording an assessment.",
                        () -> enrollments.stream().filter(Enrollment::isActive).toList(),
                        Main::enrollmentForm, null, false),
                Assessment.class, Assessment::getEnrollment, null));
        l.add(f("Assessment type", hintOf(Validator.ASSESSMENT_TYPES), 2, choice(Validator.ASSESSMENT_TYPES),
                Assessment.class, Assessment::getAssessmentType, (a, x) -> a.setAssessmentType((String) x)));
        l.add(f("Maximum score", "double | > 0", 2, txt((s, v) -> {
                    double d = parseDouble("Maximum score", s);
                    if (d <= 0) {
                        throw new ValidationException("Maximum score must be greater than zero.");
                    }
                    return d;
                }),
                Assessment.class, Assessment::getMaximumScore, (a, x) -> a.setMaximumScore((Double) x)));
        l.add(f("Obtained score", "double | 0 to maximum", 2, txt((s, v) -> {
                    double d = parseDouble("Obtained score", s);
                    double max = (Double) v[3];
                    if (d < 0 || d > max) {
                        throw new ValidationException("Obtained score must be between 0 and " + max + ".");
                    }
                    return d;
                }),
                Assessment.class, Assessment::getObtainedScore, (a, x) -> a.setObtainedScore((Double) x)));
        l.add(f("Assessment date", "LocalDate | YYYY-MM-DD", 2, txt(dateP("Assessment date", NOT_FUTURE)),
                Assessment.class, Assessment::getAssessmentDate, (a, x) -> a.setAssessmentDate((LocalDate) x)));
        return new Form("RECORD ASSESSMENT", "assessment", new String[]{"Enrollment", "Assessment details"}, l,
                v -> new Assessment((String) v[0], (Enrollment) v[1], (String) v[2], (double) v[3], (double) v[4],
                        (LocalDate) v[5]),
                () -> assessments, o -> assessments.add((Assessment) o));
    }

    private static Form feePaymentForm() {
        List<Field> l = new ArrayList<>();
        l.add(f("Payment ID", "String | unique | 4-30", 1,
                txt(idP("Payment ID", idIn(() -> payments))), FeePayment.class, FeePayment::getPaymentId, null));
        l.add(f("Student", "Student | select", 1,
                studentSelector("No Student exists. Register a student before recording a payment."),
                FeePayment.class, FeePayment::getStudent, null));
        l.add(f("Amount", "BigDecimal | > 0", 2, txt(moneyP("Amount")),
                FeePayment.class, FeePayment::getAmount, (p, x) -> p.setAmount((BigDecimal) x)));
        l.add(f("Payment date", "LocalDate | YYYY-MM-DD", 2, txt(dateP("Payment date", NOT_FUTURE)),
                FeePayment.class, FeePayment::getPaymentDate, (p, x) -> p.setPaymentDate((LocalDate) x)));
        l.add(f("Payment method", hintOf(Validator.PAYMENT_METHODS), 2, choice(Validator.PAYMENT_METHODS),
                FeePayment.class, FeePayment::getPaymentMethod, (p, x) -> p.setPaymentMethod((String) x)));
        l.add(f("Receipt number", "String | unique | not blank", 2,
                txt(uniqueTextP("Receipt number", 1, 40, uniq(() -> payments, FeePayment::getReceiptNumber))),
                FeePayment.class, FeePayment::getReceiptNumber, null));
        return new Form("RECORD FEE PAYMENT", "fee payment", new String[]{"Student", "Payment details"}, l,
                v -> new FeePayment((String) v[0], (Student) v[1], (BigDecimal) v[2], (LocalDate) v[3],
                        (String) v[4], (String) v[5]),
                () -> payments, o -> payments.add((FeePayment) o));
    }

    private static Form libraryBookForm() {
        List<Field> l = new ArrayList<>();
        l.add(f("Book ID", "String | unique | 4-30", 1,
                txt(idP("Book ID", idIn(() -> books))), LibraryBook.class, LibraryBook::getBookId, null));
        l.add(f("ISBN", "String | unique | 10-17 chars", 1,
                txt(uniqueTextP("ISBN", 10, 17, uniq(() -> books, LibraryBook::getIsbn))),
                LibraryBook.class, LibraryBook::getIsbn, (b, x) -> b.setIsbn((String) x)));
        l.add(f("Title", "String | 1-150 chars", 1, txt(textP("Title", 1, 150)),
                LibraryBook.class, LibraryBook::getTitle, (b, x) -> b.setTitle((String) x)));
        l.add(f("Author", "String | 2-100 chars", 1, txt(textP("Author", 2, 100)),
                LibraryBook.class, LibraryBook::getAuthor, (b, x) -> b.setAuthor((String) x)));
        l.add(f("Total copies", "int | 0 or more", 1, txt(intP("Total copies", 0, 100000)),
                LibraryBook.class, LibraryBook::getTotalCopies, (b, x) -> b.setTotalCopies((Integer) x)));
        l.add(f("Available copies", "int | 0 to total", 1, txt((s, v) -> {
                    int n = parseInt("Available copies", s);
                    int total = (Integer) v[4];
                    if (n < 0 || n > total) {
                        throw new ValidationException("Available copies must be between 0 and total copies (" + total + ").");
                    }
                    return n;
                }),
                LibraryBook.class, LibraryBook::getAvailableCopies, (b, x) -> b.setAvailableCopies((Integer) x)));
        return new Form("REGISTER LIBRARY BOOK", "library book", new String[]{"Book details"}, l,
                v -> new LibraryBook((String) v[0], (String) v[1], (String) v[2], (String) v[3], (int) v[4],
                        (int) v[5]),
                () -> books, o -> books.add((LibraryBook) o));
    }

    private static Form bookLoanForm() {
        List<Field> l = new ArrayList<>();
        l.add(f("Loan ID", "String | unique | 4-30", 1,
                txt(idP("Loan ID", idIn(() -> loans))), BookLoan.class, BookLoan::getLoanId, null));
        l.add(f("Student", "Student | select", 1,
                studentSelector("No Student exists. Register a student before borrowing a book."),
                BookLoan.class, BookLoan::getStudent, null));
        l.add(f("Library book", "LibraryBook | select", 1,
                select("Library Book", "No Library Book exists. Register a book before borrowing.",
                        () -> books, Main::libraryBookForm, item -> {
                            LibraryBook b = (LibraryBook) item;
                            if (b.getAvailableCopies() <= 0) {
                                throw new BookUnavailableException("No copy of \"" + b.getTitle()
                                        + "\" is available right now.");
                            }
                        }, false),
                BookLoan.class, BookLoan::getBook, null));
        l.add(f("Borrowed date", "LocalDate | YYYY-MM-DD", 2, txt(dateP("Borrowed date", NOT_FUTURE)),
                BookLoan.class, BookLoan::getBorrowedDate, null));
        l.add(f("Due date", "LocalDate | after borrowed date", 2, txt((s, v) -> {
                    LocalDate due = parseDate("Due date", s);
                    if (!due.isAfter((LocalDate) v[3])) {
                        throw new ValidationException("Due date must be after the borrowed date (" + v[3] + ").");
                    }
                    return due;
                }),
                BookLoan.class, BookLoan::getDueDate, (b, x) -> b.setDueDate((LocalDate) x)));
        l.add(derived("Returned date", "empty until returned", 2, BookLoan.class, BookLoan::getReturnedDate));
        Form form = new Form("BORROW LIBRARY BOOK", "book loan", new String[]{"Student & book", "Loan dates"}, l,
                v -> new BookLoan((String) v[0], (Student) v[1], (LibraryBook) v[2], (LocalDate) v[3],
                        (LocalDate) v[4], null),
                () -> loans, o -> loans.add((BookLoan) o));
        form.preSave = (v, built) -> {
            LibraryBook b = ((BookLoan) built).getBook();
            if (b.getAvailableCopies() <= 0) {
                throw new BookUnavailableException("No copy of \"" + b.getTitle() + "\" is available right now.");
            }
        };
        
        form.postSave = built -> {
            LibraryBook b = ((BookLoan) built).getBook();
            b.setAvailableCopies(b.getAvailableCopies() - 1);
            System.out.println("  Stock updated: " + b.getTitle() + " now has " + b.getAvailableCopies()
                    + " of " + b.getTotalCopies() + " copies available.");
        };
        return form;
    }

    
    private static void returnBook() throws Exception {
        List<BookLoan> open = loans.stream().filter(BookLoan::isActive).toList();
        if (open.isEmpty()) {
            System.out.println("No borrowed books are waiting to be returned.");
            return;
        }
        BookLoan loan = (BookLoan) pick("BORROWED BOOKS", open,
                i -> label(i) + "  due " + ((BookLoan) i).getDueDate());
        if (loan == null) {
            System.out.println("Return cancelled.");
            return;
        }
        Object r = txt((s, v) -> {
            LocalDate d = parseDate("Returned date", s);
            Validator.requireNotFuture("Returned date", d);
            if (d.isBefore(loan.getBorrowedDate())) {
                throw new ValidationException("Returned date cannot be before the borrowed date ("
                        + loan.getBorrowedDate() + ").");
            }
            return d;
        }).ask("Returned date [LocalDate | YYYY-MM-DD] : ", null);
        if (r == CANCEL || r == BACK) {
            System.out.println("Return cancelled. Nothing changed.");
            return;
        }
        LibraryBook book = loan.getBook();
        if (book.getAvailableCopies() + 1 > book.getTotalCopies()) {
            throw new ValidationException("Stock error: available copies would exceed total copies.");
        }
        if (!confirm("Confirm return of \"" + book.getTitle() + "\" by " + loan.getStudent().getDisplayName() + "?")) {
            System.out.println("Return cancelled. Nothing changed.");
            return;
        }
        loan.setReturnedDate((LocalDate) r);
        book.setAvailableCopies(book.getAvailableCopies() + 1);     // incremented exactly once
        System.out.println("+ Book returned. " + book.getTitle() + " now has " + book.getAvailableCopies()
                + " of " + book.getTotalCopies() + " copies available.");
    }

    
    private static Identifiable pick(String title, List<? extends Identifiable> items,
                                     Function<Identifiable, String> describe) {
        while (true) {
            System.out.println();
            System.out.println("+---------- " + title + " ----------+");
            for (int i = 0; i < items.size(); i++) {
                System.out.println("  " + (i + 1) + "  " + describe.apply(items.get(i)));
            }
            System.out.println("  0  Cancel");
            String s = read("Select option: ");
            if (s.equals("0") || s.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            int k;
            try {
                k = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("! Please type a number from the list.");
                continue;
            }
            if (k >= 1 && k <= items.size()) {
                return items.get(k - 1);
            }
            System.out.println("! Choose a number between 1 and " + items.size() + " (or 0 to cancel).");
        }
    }

    private static void printSection(String title, List<? extends Identifiable> list) {
        System.out.println();
        System.out.println("== " + title + " (" + list.size() + ") ==");
        if (list.isEmpty()) {
            System.out.println("   (none registered yet)");
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ((Registrable) list.get(i)).getRegistrationSummary());
        }
    }

    private static void viewAllRecords() {
        printSection("DEPARTMENTS", departments);
        printSection("STUDENTS", students);
        printSection("TEACHERS", teachers);
        printSection("ADMINISTRATORS", administrators);
        printSection("COURSES", courses);
        printSection("CLASSROOMS", classrooms);
        printSection("ENROLLMENTS", enrollments);
        printSection("ASSESSMENTS", assessments);
        printSection("FEE PAYMENTS", payments);
        printSection("LIBRARY BOOKS", books);
        printSection("BOOK LOANS", loans);
        System.out.println();
        read("Press ENTER to return to the menu... ");
    }

    private static boolean matches(Identifiable item, String q) {
        if (item.getId().equalsIgnoreCase(q)) {
            return true;
        }
        if (item instanceof Student s && s.getStudentNumber().equalsIgnoreCase(q)) {
            return true;
        }
        if (item instanceof Teacher t && t.getEmployeeNumber().equalsIgnoreCase(q)) {
            return true;
        }
        if (item instanceof Administrator a && a.getStaffNumber().equalsIgnoreCase(q)) {
            return true;
        }
        return item instanceof AcademicMember m && m.getMemberNumber().equalsIgnoreCase(q);
    }

    private static void searchById() throws Exception {
        String q = Validator.requireText("Search ID", read("Enter the ID to search (record ID, student/employee/staff number): "));
        int found = 0;
        for (Identifiable item : allRecords()) {
            if (matches(item, q)) {
                found++;
                System.out.println();
                System.out.println("--- " + item.getClass().getSimpleName() + " found ---");
                ((Registrable) item).printDetails();
            }
        }
        if (found == 0) {
            throw new RecordNotFoundException("No record found with ID '" + q + "'.");
        }
    }

    private static Form formFor(int type) {
        return switch (type) {
            case 1 -> departmentForm();
            case 2 -> studentForm();
            case 3 -> teacherForm();
            case 4 -> administratorForm();
            case 5 -> courseForm();
            case 6 -> classroomForm();
            case 7 -> enrollmentForm();
            case 8 -> assessmentForm();
            case 9 -> feePaymentForm();
            case 10 -> libraryBookForm();
            case 11 -> bookLoanForm();
            default -> null;
        };
    }

    private static void updateRecord() throws Exception {
        String[] names = {"Department", "Student", "Teacher", "Administrator", "Course", "Classroom",
                "Enrollment", "Assessment", "Fee Payment", "Library Book", "Book Loan"};
        System.out.println();
        System.out.println("+---------- UPDATE EXISTING RECORD ----------+");
        for (int i = 0; i < names.length; i++) {
            System.out.println("  " + (i + 1) + "  " + names[i]);
        }
        System.out.println("  0  Back");
        String s = read("Which type of record? ");
        int type;
        try {
            type = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("! Please type a number from the list.");
            return;
        }
        if (type == 0) {
            return;
        }
        Form f = formFor(type);
        if (f == null) {
            System.out.println("! Invalid choice.");
            return;
        }
        List<? extends Identifiable> list = f.list.get();
        if (list.isEmpty()) {
            System.out.println("No " + names[type - 1] + " records exist yet.");
            return;
        }
        Identifiable target = pick(names[type - 1].toUpperCase() + " RECORDS", list, Main::label);
        if (target == null) {
            return;
        }
        editing = target;
        try {
            Object[] v = new Object[f.fields.size()];
            for (int i = 0; i < v.length; i++) {
                v[i] = f.fields.get(i).getter.apply(target);
            }
            while (true) {
                System.out.println();
                ((Registrable) target).printDetails();
                System.out.println("Editable fields:");
                boolean any = false;
                for (int i = 0; i < v.length; i++) {
                    Field fl = f.fields.get(i);
                    if (fl.setter != null && fl.asker != null) {
                        any = true;
                        System.out.println(String.format("  %02d  %-20s : %s", i + 1, fl.label, show(v[i])));
                    }
                }
                if (!any) {
                    System.out.println("  (this record has no editable fields)");
                    return;
                }
                String in = read("Field number to change (0 = finish): ");
                int k;
                try {
                    k = Integer.parseInt(in);
                } catch (NumberFormatException e) {
                    System.out.println("! Please type a field number.");
                    continue;
                }
                if (k == 0) {
                    return;
                }
                if (k < 1 || k > v.length || f.fields.get(k - 1).setter == null
                        || f.fields.get(k - 1).asker == null) {
                    System.out.println("! That field is not editable (IDs and relationships cannot change).");
                    continue;
                }
                Field fl = f.fields.get(k - 1);
                Object old = fl.getter.apply(target);
                Object r = fl.asker.ask(prompt(fl, k), v);
                if (r == CANCEL || r == BACK) {
                    System.out.println("Change cancelled.");
                    continue;
                }
                System.out.println("Old value: " + show(old) + "   New value: " + show(r));
                if (!confirm("Apply this change?")) {
                    System.out.println("No change made.");
                    continue;
                }
                fl.setter.accept(target, r);
                try {
                    ((Registrable) target).validate();
                    if (f.rule != null) {
                        f.rule.check(target);
                    }
                    v[k - 1] = r;
                    System.out.println("+ Record updated.");
                } catch (Exception e) {
                    fl.setter.accept(target, old);
                    System.out.println("! Update rejected: " + e.getMessage() + " (previous value kept)");
                }
            }
        } finally {
            editing = null;
        }
    }

    
    private static void studentAcademicSummary() {
        if (students.isEmpty()) {
            System.out.println("No Student is registered yet. Register a student first (option 2).");
            return;
        }
        Student s = (Student) pick("SELECT STUDENT", students, Main::label);
        if (s == null) {
            return;
        }
        System.out.println();
        System.out.println("=========== ACADEMIC SUMMARY ===========");
        System.out.println("Student    : " + label(s));
        System.out.println("Program    : " + s.getProgramName() + " (year " + s.getStudyYear() + ")");
        System.out.println("Department : " + show(s.getDepartment()));
        System.out.println("Scholarship: " + show(s.isScholarshipStatus()));
        System.out.println("Advisor    : " + s.getAdvisorName());
        System.out.println("---- Enrollments and assessments ----");
        double percentSum = 0;
        int scored = 0;
        boolean any = false;
        for (Enrollment e : enrollments) {
            if (e.getStudent() != s) {
                continue;
            }
            any = true;
            System.out.println(" * " + e.getCourse().getDisplayName() + " | " + e.getSemester() + " | "
                    + e.getEnrollmentStatus());
            for (Assessment a : assessments) {
                if (a.getEnrollment() == e) {
                    double pct = a.getObtainedScore() / a.getMaximumScore() * 100;
                    percentSum += pct;
                    scored++;
                    System.out.println(String.format("     - %-10s %.1f / %.1f  (%.1f%%)  %s",
                            a.getAssessmentType(), a.getObtainedScore(), a.getMaximumScore(), pct,
                            a.getAssessmentDate()));
                }
            }
        }
        if (!any) {
            System.out.println(" (no enrollments yet)");
        }
        if (scored > 0) {
            System.out.println(String.format("Average score: %.1f%% over %d assessment(s)", percentSum / scored, scored));
        } else {
            System.out.println("Average score: no assessments recorded yet");
        }
        BigDecimal paid = payments.stream().filter(p -> p.getStudent() == s)
                .map(FeePayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long active = loans.stream().filter(b -> b.getStudent() == s && b.isActive()).count();
        System.out.println("Total fees paid     : " + paid);
        System.out.println("Books currently out : " + active);
        read("Press ENTER to return to the menu... ");
    }

    private static void studentPaymentHistory() {
        if (students.isEmpty()) {
            System.out.println("No Student is registered yet. Register a student first (option 2).");
            return;
        }
        Student s = (Student) pick("SELECT STUDENT", students, Main::label);
        if (s == null) {
            return;
        }
        List<FeePayment> history = payments.stream().filter(p -> p.getStudent() == s)
                .sorted(Comparator.comparing(FeePayment::getPaymentDate)).toList();
        System.out.println();
        System.out.println("====== PAYMENT HISTORY: " + s.getDisplayName() + " ======");
        if (history.isEmpty()) {
            System.out.println("(no payments recorded for this student)");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (FeePayment p : history) {
            System.out.println(String.format("  %s | %-10s | %-13s | receipt %s", p.getPaymentDate(),
                    p.getAmount(), p.getPaymentMethod(), p.getReceiptNumber()));
            total = total.add(p.getAmount());
        }
        System.out.println("Total paid: " + total);
        read("Press ENTER to return to the menu... ");
    }

    private static void viewAvailableBooks() {
        List<LibraryBook> available = books.stream().filter(b -> b.getAvailableCopies() > 0).toList();
        System.out.println();
        System.out.println("====== AVAILABLE BOOKS ======");
        if (available.isEmpty()) {
            System.out.println(books.isEmpty() ? "No books registered yet (option 10)."
                    : "All copies are currently borrowed.");
        }
        for (LibraryBook b : available) {
            System.out.println("  " + b.getRegistrationSummary());
        }
        read("Press ENTER to return to the menu... ");
    }

    
    private static void demonstratePolymorphism() {
        System.out.println();
        System.out.println("=== PART 1: List<Person> (superclass polymorphism) ===");
        List<Person> people = persons();
        if (people.isEmpty()) {
            System.out.println("No Student/Teacher/Administrator registered yet - register some first.");
        }
        for (Person p : people) {
           
            String extra = p instanceof AcademicMember m ? " | academic category: " + m.getAcademicCategory() : "";
            System.out.println(" - " + p.getRoleDescription() + " -> " + p.getRegistrationSummary()
                    + " | active=" + p.isActive() + extra);
        }
        System.out.println();
        System.out.println("=== PART 2: List<Identifiable> (interface polymorphism, unrelated classes) ===");
        List<Identifiable> all = allRecords();
        if (all.isEmpty()) {
            System.out.println("No records yet.");
        }
        for (Identifiable item : all) {
            System.out.println(String.format(" - %-14s id=%-12s display=%s | active=%s",
                    item.getClass().getSimpleName(), item.getId(), item.getDisplayName(), item.isActive()));
        }
        System.out.println();
        System.out.println("Why can Person and AcademicMember not be instantiated? They are abstract: they are");
        System.out.println("incomplete on purpose (getRoleDescription / getAcademicCategory have no body), so");
        System.out.println("'new Person(...)' is a compile error. Only Student, Teacher and Administrator are real.");
        System.out.println("Inheritance: Person -> AcademicMember -> Student/Teacher (multilevel);");
        System.out.println("Person -> Student, Teacher, Administrator (hierarchical).");
        read("Press ENTER to return to the menu... ");
    }
}
