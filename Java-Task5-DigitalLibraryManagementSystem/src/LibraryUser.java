import java.io.Serializable;
public class LibraryUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String username, fullName, password;
    private double fineBalance;
    public LibraryUser(int id,String username,String fullName,String password){
        if(id<=0) throw new IllegalArgumentException("User ID must be positive.");
        if(username==null||username.trim().isEmpty()) throw new IllegalArgumentException("Username required.");
        if(fullName==null||fullName.trim().isEmpty()) throw new IllegalArgumentException("Full name required.");
        if(password==null||password.length()<4) throw new IllegalArgumentException("Password must be at least 4 characters.");
        this.id=id; this.username=username.trim(); this.fullName=fullName.trim(); this.password=password;
    }
    public int getId(){return id;}
    public String getUsername(){return username;}
    public String getFullName(){return fullName;}
    public double getFineBalance(){return fineBalance;}
    public boolean verifyPassword(String p){return password.equals(p);}
    public void addFine(double v){if(v>0) fineBalance+=v;}
    public boolean payFine(double v){if(v<=0||v>fineBalance)return false;fineBalance-=v;return true;}
    public String toString(){return String.format("User ID: %d | Username: %s | Name: %s | Fine: Rs. %.2f",id,username,fullName,fineBalance);}
}
