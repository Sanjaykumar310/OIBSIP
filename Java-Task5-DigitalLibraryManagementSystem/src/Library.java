import java.io.*;
import java.util.*;
public class Library implements Serializable {
    private static final long serialVersionUID=1L;
    private static final int LOAN_DAYS=14;
    private static final double FINE_PER_DAY=5.0;
    private final List<Book> books=new ArrayList<>();
    private final List<LibraryUser> users=new ArrayList<>();
    private final Map<Integer,List<String>> reservations=new LinkedHashMap<>();
    private final List<String> queries=new ArrayList<>();

    public Book findBookById(int id){for(Book b:books)if(b.getId()==id)return b;return null;}
    public LibraryUser findUserByUsername(String name){for(LibraryUser u:users)if(u.getUsername().equalsIgnoreCase(name))return u;return null;}
    public int nextUserId(){int max=0;for(LibraryUser u:users)max=Math.max(max,u.getId());return max+1;}
    public void seedIfEmpty(){
        if(books.isEmpty()){books.add(new Book(101,"Clean Code","Robert C. Martin","Programming"));books.add(new Book(102,"Effective Java","Joshua Bloch","Programming"));books.add(new Book(103,"The Alchemist","Paulo Coelho","Fiction"));}
        if(findUserByUsername("user1")==null)users.add(new LibraryUser(nextUserId(),"user1","Library User One","1234"));
    }
    public boolean addBook(Book b){if(findBookById(b.getId())!=null)return false;books.add(b);return true;}
    public boolean updateBook(int id,String t,String a,String c){Book b=findBookById(id);if(b==null)return false;b.setTitle(t);b.setAuthor(a);b.setCategory(c);return true;}
    public boolean deleteBook(int id){Book b=findBookById(id);if(b==null||!b.isAvailable())return false;books.remove(b);reservations.remove(id);return true;}
    public boolean registerUser(LibraryUser u){if(findUserByUsername(u.getUsername())!=null)return false;for(LibraryUser x:users)if(x.getId()==u.getId())return false;users.add(u);return true;}
    public void displayBooks(){if(books.isEmpty()){System.out.println("No books.");return;}for(Book b:books)System.out.println(b);}
    public void searchBooks(String q){boolean found=false;String k=q.toLowerCase();for(Book b:books)if(b.getTitle().toLowerCase().contains(k)||b.getAuthor().toLowerCase().contains(k)||b.getCategory().toLowerCase().contains(k)){System.out.println(b);found=true;}if(!found)System.out.println("No matches.");}
    public boolean borrowBook(int id,String user){
        Book b=findBookById(id);
        if(b==null||findUserByUsername(user)==null||!b.isAvailable())return false;
        List<String> queue=reservations.get(id);
        // A queued reservation must be honoured in first-in, first-out order.
        if(queue!=null&&!queue.isEmpty()&&!queue.get(0).equalsIgnoreCase(user))return false;
        b.borrow(user,LOAN_DAYS);
        if(queue!=null){
            queue.removeIf(x->x.equalsIgnoreCase(user));
            if(queue.isEmpty())reservations.remove(id);
        }
        return true;
    }

    public double returnBook(int id,String user){
        Book b=findBookById(id);
        if(b==null||b.isAvailable()||!b.getBorrowedBy().equalsIgnoreCase(user))return -1;
        double fine=b.calculateFine(FINE_PER_DAY);
        LibraryUser u=findUserByUsername(user);
        if(u!=null)u.addFine(fine);
        b.returnBook();

        // Automatically issue the returned book to the first user in the queue.
        List<String> queue=reservations.get(id);
        if(queue!=null&&!queue.isEmpty()){
            String nextUser=queue.get(0);
            if(findUserByUsername(nextUser)!=null){
                b.borrow(nextUser,LOAN_DAYS);
                queue.remove(0);
            }
            if(queue.isEmpty())reservations.remove(id);
        }
        return fine;
    }
    public boolean reserveBook(int id,String user){Book b=findBookById(id);if(b==null||findUserByUsername(user)==null||b.isAvailable())return false;List<String> q=reservations.computeIfAbsent(id,k->new ArrayList<>());for(String x:q)if(x.equalsIgnoreCase(user))return false;q.add(user);return true;}
    public void displayUsers(){if(users.isEmpty())System.out.println("No users.");for(LibraryUser u:users)System.out.println(u);}
    public void displayBorrowed(){boolean any=false;for(Book b:books)if(!b.isAvailable()){System.out.println(b);any=true;}if(!any)System.out.println("No borrowed books.");}
    public void displayReservations(){if(reservations.isEmpty()){System.out.println("No reservations.");return;}for(Map.Entry<Integer,List<String>> e:reservations.entrySet())System.out.println("Book "+e.getKey()+": "+String.join(" -> ",e.getValue()));}
    public void submitQuery(String user,String msg){queries.add("User: "+user+" | "+new Date()+" | "+msg);}
    public void displayQueries(){if(queries.isEmpty()){System.out.println("No queries.");return;}for(String q:queries)System.out.println(q);}
    public boolean payFine(String user,double amount){LibraryUser u=findUserByUsername(user);return u!=null&&u.payFine(amount);}
    public static Library load(String path){File f=new File(path);if(!f.exists())return new Library();try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(f))){return (Library)in.readObject();}catch(Exception e){System.out.println("Starting fresh; saved data could not be loaded: "+e.getMessage());return new Library();}}
    public void save(String path){try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(path))){out.writeObject(this);}catch(IOException e){System.out.println("Save failed: "+e.getMessage());}}
}
