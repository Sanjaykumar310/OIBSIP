import java.io.Serializable;
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private String title, author, category, borrowedBy;
    private boolean available = true;
    private long dueDateMillis;

    public Book(int id, String title, String author, String category) {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive.");
        this.id = id; setTitle(title); setAuthor(author); setCategory(category);
    }
    public int getId(){ return id; }
    public String getTitle(){ return title; }
    public String getAuthor(){ return author; }
    public String getCategory(){ return category; }
    public boolean isAvailable(){ return available; }
    public String getBorrowedBy(){ return borrowedBy; }
    public void setTitle(String v){ if(v==null||v.trim().isEmpty()) throw new IllegalArgumentException("Title required."); title=v.trim(); }
    public void setAuthor(String v){ if(v==null||v.trim().isEmpty()) throw new IllegalArgumentException("Author required."); author=v.trim(); }
    public void setCategory(String v){ if(v==null||v.trim().isEmpty()) throw new IllegalArgumentException("Category required."); category=v.trim(); }
    public void borrow(String username,int days){
        if(!available) throw new IllegalStateException("Already borrowed.");
        available=false; borrowedBy=username;
        dueDateMillis=System.currentTimeMillis()+days*24L*60L*60L*1000L;
    }
    public long overdueDays(){
        if(available||dueDateMillis==0) return 0;
        long late=System.currentTimeMillis()-dueDateMillis, day=24L*60L*60L*1000L;
        return late<=0?0:(late+day-1)/day;
    }
    public double calculateFine(double perDay){ return overdueDays()*perDay; }
    public void returnBook(){ available=true; borrowedBy=null; dueDateMillis=0; }
    public String toString(){ return "ID: "+id+" | Title: "+title+" | Author: "+author+" | Category: "+category+" | Status: "+(available?"Available":"Borrowed by "+borrowedBy); }
}
