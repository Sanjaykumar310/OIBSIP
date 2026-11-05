import java.util.Scanner;
public class Main {
    private static final Scanner sc=new Scanner(System.in);
    private static final String DATA="library-data.ser";
    private static final String ADMIN="admin", ADMIN_PASS="admin123";
    private static Library lib;
    public static void main(String[] args){
        // Launch Swing GUI instead of console interaction
        LibraryGUI.launch();
    }
    private static void adminLogin(){
        System.out.print("Admin username: ");String u=sc.nextLine().trim();
        System.out.print("Admin password: ");String p=sc.nextLine();
        if(!ADMIN.equals(u)||!ADMIN_PASS.equals(p)){System.out.println("Invalid admin credentials.");return;}
        boolean run=true;while(run){
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1 View books\n2 Search books\n3 Add book\n4 Update book\n5 Delete book\n6 View users\n7 Borrowed books\n8 Reservations\n9 User queries\n10 Logout");
            int c=number("Choose: ",1,10);
            switch(c){
                case 1:lib.displayBooks();break;case 2:search();break;case 3:addBook();break;
                case 4:updateBook();break;case 5:deleteBook();break;case 6:lib.displayUsers();break;
                case 7:lib.displayBorrowed();break;case 8:lib.displayReservations();break;
                case 9:lib.displayQueries();break;case 10:run=false;break;
            }save();
        }
    }
    private static void userLogin(){
        System.out.print("Username: ");String name=sc.nextLine().trim();
        System.out.print("Password: ");String pass=sc.nextLine();
        LibraryUser user=lib.findUserByUsername(name);
        if(user==null||!user.verifyPassword(pass)){System.out.println("Invalid username or password.");return;}
        boolean run=true;while(run){
            System.out.println("\n--- USER MENU ("+user.getFullName()+") ---");
            System.out.println("1 View books\n2 Search books\n3 Borrow book\n4 Return book\n5 Reserve unavailable book\n6 View/pay fine\n7 Submit query\n8 Logout");
            int c=number("Choose: ",1,8);
            switch(c){
                case 1:lib.displayBooks();break;case 2:search();break;
                case 3:{int id=number("Book ID: ",1,Integer.MAX_VALUE);System.out.println(lib.borrowBook(id,name)?"Book issued for 14 days.":"Borrow failed: check ID, account and availability.");break;}
                case 4:{
                    int id=number("Book ID: ",1,Integer.MAX_VALUE);
                    double fine=lib.returnBook(id,name);
                    if(fine<0){
                        System.out.println("Return failed: this book is not borrowed by you.");
                    }else{
                        System.out.printf("Book returned. Fine added: Rs. %.2f%n",fine);
                        Book returnedBook=lib.findBookById(id);
                        if(returnedBook!=null&&!returnedBook.isAvailable()){
                            System.out.println("Reservation fulfilled. Book issued to: "+returnedBook.getBorrowedBy());
                        }
                    }
                    break;
                }
                case 5:{int id=number("Book ID: ",1,Integer.MAX_VALUE);System.out.println(lib.reserveBook(id,name)?"Reservation added.":"Reservation failed: book must be borrowed and not already reserved by you.");break;}
                case 6:{System.out.printf("Outstanding fine: Rs. %.2f%n",user.getFineBalance());if(user.getFineBalance()>0){System.out.print("Pay full amount? (y/n): ");if(sc.nextLine().trim().equalsIgnoreCase("y"))System.out.println(lib.payFine(name,user.getFineBalance())?"Fine paid.":"Payment failed.");}break;}
                case 7:System.out.print("Your query: ");String q=sc.nextLine().trim();if(q.isEmpty())System.out.println("Query cannot be empty.");else{lib.submitQuery(name,q);System.out.println("Query recorded.");}break;
                case 8:run=false;break;
            }save();
        }
    }
    private static void register(){
        System.out.print("Username: ");String u=sc.nextLine().trim();
        System.out.print("Full name: ");String n=sc.nextLine().trim();
        System.out.print("Password (min 4 chars): ");String p=sc.nextLine();
        try{LibraryUser x=new LibraryUser(lib.nextUserId(),u,n,p);System.out.println(lib.registerUser(x)?"Registration successful.":"Username already exists.");save();}
        catch(IllegalArgumentException e){System.out.println(e.getMessage());}
    }
    private static void addBook(){
        int id=number("Book ID: ",1,Integer.MAX_VALUE);
        System.out.print("Title: ");String t=sc.nextLine().trim();
        System.out.print("Author: ");String a=sc.nextLine().trim();
        System.out.print("Category: ");String c=sc.nextLine().trim();
        try{System.out.println(lib.addBook(new Book(id,t,a,c))?"Book added.":"Book ID already exists.");}
        catch(IllegalArgumentException e){System.out.println(e.getMessage());}
    }
    private static void updateBook(){
        int id=number("Book ID: ",1,Integer.MAX_VALUE);Book b=lib.findBookById(id);
        if(b==null){System.out.println("Book not found.");return;}
        System.out.print("New title: ");String t=sc.nextLine().trim();
        System.out.print("New author: ");String a=sc.nextLine().trim();
        System.out.print("New category: ");String c=sc.nextLine().trim();
        try{System.out.println(lib.updateBook(id,t,a,c)?"Book updated.":"Update failed.");}
        catch(IllegalArgumentException e){System.out.println(e.getMessage());}
    }
    private static void deleteBook(){int id=number("Book ID: ",1,Integer.MAX_VALUE);System.out.println(lib.deleteBook(id)?"Book deleted.":"Cannot delete: book not found or currently borrowed.");}
    private static void search(){System.out.print("Search title, author or category: ");String q=sc.nextLine().trim();if(q.isEmpty())System.out.println("Enter a search term.");else lib.searchBooks(q);}
    private static int number(String prompt,int min,int max){
        while(true){System.out.print(prompt);try{int v=Integer.parseInt(sc.nextLine().trim());if(v>=min&&v<=max)return v;}catch(NumberFormatException ignored){}System.out.println("Enter a whole number between "+min+" and "+max+".");}
    }
    private static void save(){if(lib!=null)lib.save(DATA);}
}
