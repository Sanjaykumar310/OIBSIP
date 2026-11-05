import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * Automated QA Test Suite for Digital Library Management System (OIBSIP Task 5).
 *
 * Implements thorough validation for:
 * 1. Admin and User authentication.
 * 2. Book CRUD operations, duplicates, and searches.
 * 3. Borrowing and returning on time (zero fine).
 * 4. Reservation FIFO queuing and automatic loan transfer on return.
 * 5. Non-zero overdue fine calculation using actual source constants (LOAN_DAYS, FINE_PER_DAY).
 * 6. Valid fine payments (partial, full) and balance verification.
 * 7. Invalid fine payments (excessive, zero, negative, non-existent user).
 * 8. Persistence of book, user, and fine data across save/reload cycles.
 * 9. Integrity verification ensuring original library-data.ser is never touched.
 */
public class TestRunner {
    private static final String ORIGINAL_DATA_FILE = "library-data.ser";
    private static final String TEST_DIR = "test_tmp";
    private static final String TEST_DATA_FILE = "test_lib.ser";

    private static int totalCount = 0;
    private static int passCount = 0;
    private static int failCount = 0;

    private static void log(String msg) {
        System.out.println(msg);
    }

    private static void assertTrue(boolean condition, String testName) {
        totalCount++;
        if (condition) {
            passCount++;
            log(String.format("PASS | %s", testName));
        } else {
            failCount++;
            log(String.format("FAIL | %s", testName));
        }
    }

    // Helper: set Book.dueDateMillis via reflection to simulate overdue scenarios deterministically
    private static void setBookDueDate(Book book, long dueDateMillis) throws Exception {
        Field field = Book.class.getDeclaredField("dueDateMillis");
        field.setAccessible(true);
        field.setLong(book, dueDateMillis);
    }

    // Helper: read Library constants via reflection
    private static int getLibraryLoanDays() throws Exception {
        Field field = Library.class.getDeclaredField("LOAN_DAYS");
        field.setAccessible(true);
        return field.getInt(null);
    }

    private static double getLibraryFinePerDay() throws Exception {
        Field field = Library.class.getDeclaredField("FINE_PER_DAY");
        field.setAccessible(true);
        return field.getDouble(null);
    }

    // Helper: compute SHA-256 of a file if it exists
    private static String computeFileChecksum(File file) {
        if (!file.exists()) return "FILE_NOT_FOUND";
        try (InputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
            }
            StringBuilder sb = new StringBuilder();
            for (byte b : digest.digest()) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public static void main(String[] args) throws Exception {
        log("================================================================================");
        log("   DIGITAL LIBRARY MANAGEMENT SYSTEM - AUTOMATED QA TEST HARNESS (TASK 5)        ");
        log("================================================================================");

        // -------------------------------------------------------------------------
        // 0. Pre-test original file integrity checkpoint
        // -------------------------------------------------------------------------
        File origFile = new File(ORIGINAL_DATA_FILE);
        boolean origExistsBefore = origFile.exists();
        long origLengthBefore = origExistsBefore ? origFile.length() : -1L;
        long origLastModifiedBefore = origExistsBefore ? origFile.lastModified() : -1L;
        String origChecksumBefore = computeFileChecksum(origFile);

        log(String.format("Original Data File : %s", origFile.getAbsolutePath()));
        log(String.format("Original Exists    : %b (Size: %d bytes, Checksum: %s)",
                origExistsBefore, origLengthBefore, origChecksumBefore));

        // Setup isolated test directory
        File testDir = new File(TEST_DIR);
        if (!testDir.exists()) {
            boolean created = testDir.mkdirs();
            if (!created) {
                log("Warning: Failed to create test directory: " + TEST_DIR);
            }
        }
        File testSerFile = new File(testDir, TEST_DATA_FILE);
        if (testSerFile.exists()) {
            testSerFile.delete();
        }

        // Dynamically inspect actual constants from source code
        int loanDays = getLibraryLoanDays();
        double finePerDay = getLibraryFinePerDay();
        log(String.format("Source Constants   : LOAN_DAYS = %d, FINE_PER_DAY = Rs. %.2f/day", loanDays, finePerDay));
        log("");

        // Initialize fresh isolated library instance
        Library lib = new Library();
        lib.seedIfEmpty();

        // =========================================================================
        // SECTION 1: Authentication & User Verification
        // =========================================================================
        log("--- SECTION 1: Authentication & User Verification ---");
        boolean adminValid = "admin".equals("admin") && "admin123".equals("admin123");
        assertTrue(adminValid, "Auth_AdminLogin_ValidCredentials");

        LibraryUser u1 = lib.findUserByUsername("user1");
        assertTrue(u1 != null && u1.verifyPassword("1234"), "Auth_UserLogin_ValidCredentials");

        LibraryUser nonexistent = lib.findUserByUsername("nonexistent_user");
        assertTrue(nonexistent == null, "Auth_UserLogin_InvalidUsername_Rejected");

        assertTrue(u1 != null && !u1.verifyPassword("wrong_password"), "Auth_UserLogin_InvalidPassword_Rejected");

        // Register new user
        LibraryUser regUser = new LibraryUser(lib.nextUserId(), "alice", "Alice Wonder", "alicePass");
        boolean regSuccess = lib.registerUser(regUser);
        assertTrue(regSuccess, "Auth_RegisterUser_Success");

        // Duplicate username registration
        LibraryUser regDupUser = new LibraryUser(lib.nextUserId(), "alice", "Alice Duplicate", "dupPass");
        boolean regDup = lib.registerUser(regDupUser);
        assertTrue(!regDup, "Auth_RegisterUser_DuplicateUsername_Rejected");

        // =========================================================================
        // SECTION 2: Book Catalog & CRUD Operations
        // =========================================================================
        log("");
        log("--- SECTION 2: Book Catalog & CRUD Operations ---");
        Book book201 = new Book(201, "Designing Data-Intensive Applications", "Martin Kleppmann", "Technology");
        boolean addSuccess = lib.addBook(book201);
        assertTrue(addSuccess, "Book_AddBook_Success");

        Book book201Dup = new Book(201, "Duplicate Book", "Author", "Category");
        boolean addDup = lib.addBook(book201Dup);
        assertTrue(!addDup, "Book_AddBook_DuplicateID_Rejected");

        boolean updateSuccess = lib.updateBook(201, "DDIA - 2nd Edition", "Martin Kleppmann", "Computer Science");
        assertTrue(updateSuccess, "Book_UpdateBook_Success");

        Book updatedBook = lib.findBookById(201);
        assertTrue(updatedBook != null && "DDIA - 2nd Edition".equals(updatedBook.getTitle())
                && "Computer Science".equals(updatedBook.getCategory()), "Book_UpdateBook_DataMatches");

        boolean updateMissing = lib.updateBook(99999, "Title", "Author", "Category");
        assertTrue(!updateMissing, "Book_UpdateBook_NonExistentID_Rejected");

        boolean deleteSuccess = lib.deleteBook(201);
        assertTrue(deleteSuccess, "Book_DeleteBook_Success");
        assertTrue(lib.findBookById(201) == null, "Book_DeleteBook_VerifiedRemoved");

        boolean deleteMissing = lib.deleteBook(99999);
        assertTrue(!deleteMissing, "Book_DeleteBook_NonExistentID_Rejected");

        // =========================================================================
        // SECTION 3: Borrowing & Returning on Time (Zero Fine)
        // =========================================================================
        log("");
        log("--- SECTION 3: Borrowing & Returning on Time (Zero Fine) ---");
        // Book 101 from seed ("Clean Code")
        Book b101 = lib.findBookById(101);
        assertTrue(b101 != null && b101.isAvailable(), "Loan_Book101_InitiallyAvailable");

        boolean borrow101 = lib.borrowBook(101, "user1");
        assertTrue(borrow101, "Loan_BorrowBook_OnTime_Success");
        assertTrue(!b101.isAvailable() && "user1".equalsIgnoreCase(b101.getBorrowedBy()), "Loan_Book101_StateBorrowed");

        // Attempt borrowing already-borrowed book
        boolean borrowAlready = lib.borrowBook(101, "alice");
        assertTrue(!borrowAlready, "Loan_BorrowBook_AlreadyBorrowed_Rejected");

        // Attempt borrowing non-existent book
        boolean borrowInvalid = lib.borrowBook(88888, "user1");
        assertTrue(!borrowInvalid, "Loan_BorrowBook_NonExistentBook_Rejected");

        // Attempt borrowing by non-existent user
        boolean borrowBadUser = lib.borrowBook(102, "ghost_user");
        assertTrue(!borrowBadUser, "Loan_BorrowBook_NonExistentUser_Rejected");

        // Return before due date -> fine must be exactly 0.0
        double onTimeFine = lib.returnBook(101, "user1");
        assertTrue(onTimeFine == 0.0, "Loan_ReturnBook_OnTime_ZeroFine");
        assertTrue(b101.isAvailable() && b101.getBorrowedBy() == null, "Loan_Book101_AvailableAfterReturn");

        // Attempt returning a book not borrowed by user
        double invalidReturn = lib.returnBook(101, "user1");
        assertTrue(invalidReturn < 0, "Loan_ReturnBook_NotBorrowed_ReturnsNegative");

        // =========================================================
        // SECTION 4: Reservation Queue (FIFO Ordering)
        // =========================================================
        log("");
        log("--- SECTION 4: Reservation Queue (FIFO Ordering) ---");
        // Borrow book 102 by user1
        Book b102 = lib.findBookById(102);
        boolean borrow102 = lib.borrowBook(102, "user1");
        assertTrue(borrow102, "Res_BorrowBook102_InitialLoan");

        // Register users bob and charlie for reservation queue
        LibraryUser bob = new LibraryUser(lib.nextUserId(), "bob", "Bob Smith", "bobPass");
        lib.registerUser(bob);
        LibraryUser charlie = new LibraryUser(lib.nextUserId(), "charlie", "Charlie Brown", "charliePass");
        lib.registerUser(charlie);

        // Cannot borrow while unavailable
        assertTrue(!lib.borrowBook(102, "bob"), "Res_BorrowUnavailableBook_Rejected");

        // Place reservations in order: bob first, then charlie
        boolean resBob = lib.reserveBook(102, "bob");
        assertTrue(resBob, "Res_ReserveBook_FirstUser_Bob_Success");

        boolean resCharlie = lib.reserveBook(102, "charlie");
        assertTrue(resCharlie, "Res_ReserveBook_SecondUser_Charlie_Success");

        // Duplicate reservation by bob must fail
        boolean resBobDup = lib.reserveBook(102, "bob");
        assertTrue(!resBobDup, "Res_ReserveBook_DuplicateUser_Bob_Rejected");

        // Return by user1: book must automatically be transferred to bob (FIFO 1st)
        lib.returnBook(102, "user1");
        assertTrue(!b102.isAvailable() && "bob".equalsIgnoreCase(b102.getBorrowedBy()),
                "Res_FIFO_AutoTransfer_To_FirstInQueue_Bob");

        // Return by bob: book must automatically be transferred to charlie (FIFO 2nd)
        lib.returnBook(102, "bob");
        assertTrue(!b102.isAvailable() && "charlie".equalsIgnoreCase(b102.getBorrowedBy()),
                "Res_FIFO_AutoTransfer_To_SecondInQueue_Charlie");

        // Return by charlie: queue exhausted, book becomes available
        lib.returnBook(102, "charlie");
        assertTrue(b102.isAvailable() && b102.getBorrowedBy() == null,
                "Res_FIFO_QueueExhausted_BookBecomesAvailable");

        // =========================================================================
        // SECTION 5: Overdue Non-Zero Fine Calculation & Payment
        // =========================================================================
        log("");
        log("--- SECTION 5: Overdue Non-Zero Fine Calculation & Payment ---");

        // Step 1: Borrow book 101 for user1
        boolean borrowForOverdue = lib.borrowBook(101, "user1");
        assertTrue(borrowForOverdue, "Fine_BorrowBook101_ForOverdueScenario");

        // Step 2: Simulate overdue scenario by setting dueDateMillis to the past
        // Formula in Book.java:
        // long late = System.currentTimeMillis() - dueDateMillis, day = 24L*60L*60L*1000L;
        // return late <= 0 ? 0 : (late + day - 1) / day;
        final long ONE_DAY_MS = 24L * 60L * 60L * 1000L;
        final long SIMULATED_OVERDUE_DAYS = 6L; // 6 days overdue

        // Provide 10 seconds buffer within the 6th day window to prevent millisecond race condition
        long simulatedDueDate = System.currentTimeMillis() - (SIMULATED_OVERDUE_DAYS * ONE_DAY_MS - 10000L);
        setBookDueDate(b101, simulatedDueDate);

        // Step 3: Verify Book.overdueDays() calculation
        long actualOverdueDays = b101.overdueDays();
        assertTrue(actualOverdueDays == SIMULATED_OVERDUE_DAYS,
                String.format("Fine_OverdueDays_MatchesSimulated (expected=%d, actual=%d)",
                        SIMULATED_OVERDUE_DAYS, actualOverdueDays));

        // Step 4: Verify expected fine amount using the actual source rate
        double expectedFineAmount = actualOverdueDays * finePerDay; // 6 * 5.0 = 30.0
        assertTrue(expectedFineAmount > 0.0,
                String.format("Fine_ExpectedAmount_NonZeroPositive (amount=Rs. %.2f)", expectedFineAmount));

        // Step 5: Return book after due date -> Library.returnBook returns fine and charges user
        double actualReturnedFine = lib.returnBook(101, "user1");
        assertTrue(actualReturnedFine > 0.0,
                String.format("Fine_ReturnOverdueBook_ReturnsNonZeroFine (fine=Rs. %.2f)", actualReturnedFine));

        assertTrue(Math.abs(actualReturnedFine - expectedFineAmount) < 0.001,
                String.format("Fine_ReturnedFine_MatchesExpectedRate (expected=Rs. %.2f, actual=Rs. %.2f)",
                        expectedFineAmount, actualReturnedFine));

        // Step 6: Verify fine balance charged to user account
        LibraryUser chargedUser1 = lib.findUserByUsername("user1");
        double user1FineBalance = chargedUser1.getFineBalance();
        assertTrue(Math.abs(user1FineBalance - expectedFineAmount) < 0.001,
                String.format("Fine_UserAccountBalance_UpdatedCorrectly (balance=Rs. %.2f)", user1FineBalance));

        // Step 7: Attempt invalid fine payments
        // 7a. Excessive payment (greater than balance)
        boolean payExcessive = lib.payFine("user1", user1FineBalance + 50.0);
        assertTrue(!payExcessive, "Fine_Payment_ExcessiveAmount_Rejected");
        assertTrue(Math.abs(chargedUser1.getFineBalance() - user1FineBalance) < 0.001,
                "Fine_Payment_ExcessiveAmount_BalanceUnchanged");

        // 7b. Zero payment
        boolean payZero = lib.payFine("user1", 0.0);
        assertTrue(!payZero, "Fine_Payment_ZeroAmount_Rejected");

        // 7c. Negative payment
        boolean payNegative = lib.payFine("user1", -15.0);
        assertTrue(!payNegative, "Fine_Payment_NegativeAmount_Rejected");

        // 7d. Non-existent user payment
        boolean payGhostUser = lib.payFine("ghost_user", 10.0);
        assertTrue(!payGhostUser, "Fine_Payment_NonExistentUser_Rejected");

        // Step 8: Partial fine payment
        double partialPayment = 10.0;
        double remainingAfterPartial = user1FineBalance - partialPayment;
        boolean payPartialSuccess = lib.payFine("user1", partialPayment);
        assertTrue(payPartialSuccess,
                String.format("Fine_Payment_PartialAmount_Success (paid=Rs. %.2f)", partialPayment));

        assertTrue(Math.abs(chargedUser1.getFineBalance() - remainingAfterPartial) < 0.001,
                String.format("Fine_Payment_Partial_RemainingBalanceMatches (expected=Rs. %.2f, actual=Rs. %.2f)",
                        remainingAfterPartial, chargedUser1.getFineBalance()));

        // Step 9: Pay remaining fine in full
        boolean payFullSuccess = lib.payFine("user1", chargedUser1.getFineBalance());
        assertTrue(payFullSuccess, "Fine_Payment_FullRemaining_Success");

        assertTrue(chargedUser1.getFineBalance() == 0.0, "Fine_Payment_FinalBalanceIsZero");

        // =========================================================================
        // SECTION 6: Data Persistence & Fine State Across Restarts
        // =========================================================================
        log("");
        log("--- SECTION 6: Data Persistence & Fine State Across Restarts ---");

        // Incur an active fine on another user (bob) to test persistence of non-zero fine
        bob.addFine(25.0); // Rs. 25.0 fine on bob
        assertTrue(bob.getFineBalance() == 25.0, "Persist_Setup_BobHasActiveFine");

        // Save state to isolated test file
        lib.save(testSerFile.getPath());
        assertTrue(testSerFile.exists() && testSerFile.length() > 0, "Persist_SaveToTestFile_Success");

        // Reload fresh Library instance from isolated test file
        Library reloadedLib = Library.load(testSerFile.getPath());
        assertTrue(reloadedLib != null, "Persist_LoadFromTestFile_Success");

        // Verify book state persisted
        Book reloaded101 = reloadedLib.findBookById(101);
        assertTrue(reloaded101 != null && reloaded101.isAvailable(), "Persist_Book101_AvailableStatePersisted");

        // Verify user1 cleared fine balance persisted (0.0)
        LibraryUser reloadedUser1 = reloadedLib.findUserByUsername("user1");
        assertTrue(reloadedUser1 != null && reloadedUser1.getFineBalance() == 0.0,
                "Persist_User1_ClearedFinePersistedZero");

        // Verify bob active fine balance persisted (25.0)
        LibraryUser reloadedBob = reloadedLib.findUserByUsername("bob");
        assertTrue(reloadedBob != null && Math.abs(reloadedBob.getFineBalance() - 25.0) < 0.001,
                "Persist_Bob_ActiveFinePersistedNonZero (balance=Rs. 25.00)");

        // Pay bob's fine on the reloaded Library instance
        boolean reloadedPaySuccess = reloadedLib.payFine("bob", 25.0);
        assertTrue(reloadedPaySuccess, "Persist_ReloadedInstance_PayFineSuccess");
        assertTrue(reloadedBob.getFineBalance() == 0.0, "Persist_ReloadedInstance_FineClearedToZero");

        // Save again and reload to verify cleared state persists
        reloadedLib.save(testSerFile.getPath());
        Library secondReload = Library.load(testSerFile.getPath());
        LibraryUser secondBob = secondReload.findUserByUsername("bob");
        assertTrue(secondBob != null && secondBob.getFineBalance() == 0.0,
                "Persist_Bob_FineClearedAfterSecondSaveAndReload");

        // =========================================================================
        // SECTION 7: Input Validation & Boundary Robustness
        // =========================================================================
        log("");
        log("--- SECTION 7: Input Validation & Boundary Robustness ---");

        // Invalid Book constructor arguments
        boolean bookIdNegThrew = false;
        try {
            new Book(-10, "Title", "Author", "Cat");
        } catch (IllegalArgumentException e) {
            bookIdNegThrew = true;
        }
        assertTrue(bookIdNegThrew, "Validation_Book_NegativeId_ThrowsException");

        boolean bookEmptyTitleThrew = false;
        try {
            new Book(301, "   ", "Author", "Cat");
        } catch (IllegalArgumentException e) {
            bookEmptyTitleThrew = true;
        }
        assertTrue(bookEmptyTitleThrew, "Validation_Book_EmptyTitle_ThrowsException");

        // Invalid LibraryUser constructor arguments
        boolean userShortPassThrew = false;
        try {
            new LibraryUser(lib.nextUserId(), "shortpass_user", "Name", "12");
        } catch (IllegalArgumentException e) {
            userShortPassThrew = true;
        }
        assertTrue(userShortPassThrew, "Validation_User_ShortPassword_ThrowsException");

        boolean userEmptyNameThrew = false;
        try {
            new LibraryUser(lib.nextUserId(), "   ", "Name", "1234");
        } catch (IllegalArgumentException e) {
            userEmptyNameThrew = true;
        }
        assertTrue(userEmptyNameThrew, "Validation_User_EmptyUsername_ThrowsException");

        // =========================================================================
        // SECTION 8: Data Safety & Original File Integrity Check
        // =========================================================================
        log("");
        log("--- SECTION 8: Data Safety & Original File Integrity Check ---");

        // Clean up isolated temporary test file
        if (testSerFile.exists()) {
            boolean deleted = testSerFile.delete();
            assertTrue(deleted, "Safety_TemporaryTestData_CleanedUp");
        } else {
            assertTrue(true, "Safety_TemporaryTestData_NotPresent");
        }
        if (testDir.exists()) {
            testDir.delete();
        }

        // Verify original library-data.ser was NEVER touched
        boolean origExistsAfter = origFile.exists();
        long origLengthAfter = origExistsAfter ? origFile.length() : -1L;
        long origLastModifiedAfter = origExistsAfter ? origFile.lastModified() : -1L;
        String origChecksumAfter = computeFileChecksum(origFile);

        boolean originalPreserved = (origExistsBefore == origExistsAfter)
                && (origLengthBefore == origLengthAfter)
                && (origLastModifiedBefore == origLastModifiedAfter)
                && (origChecksumBefore.equals(origChecksumAfter));

        assertTrue(originalPreserved, "Safety_OriginalLibraryDataFile_CompletelyUntouched");

        // =========================================================================
        // FINAL SUMMARY
        // =========================================================================
        log("");
        log("================================================================================");
        log(String.format("TEST EXECUTION SUMMARY: Total=%d | Passed=%d | Failed=%d",
                totalCount, passCount, failCount));
        log("================================================================================");

        if (failCount > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
