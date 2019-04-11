package hku.cs.sqlitedemo;

public class Transaction {
    private int id;
    private String amount;
    private String description;
    private String type;
    private String category;
    private String date;



    public Transaction(String amount, String description, String type, String category, String date) {
        this(0, amount, description, type, category, date);
    }

    public Transaction(int id, String amount, String description, String type, String category, String date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.category = category;
        this.date = date;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String[] getDateElements(){
        return this.date.split("[, ]+");
    }

    public String getDay(){
        return this.getDateElements()[1];
    }

    public String getMonth(){
        return this.getDateElements()[0];
    }

    public int getMonthInt(){
        String month = this.getMonth();
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        int i = 0; // Month number
        while(!months[i].equals(month)){
            i++;
        }
        return i;

    }

    public String getYear(){
        return this.getDateElements()[2];
    }

    // Method used by the TransactionsSorter to sort transactions
    // It defines which transactions is oldest comparing first the year, then month and day if necessary
    
    public boolean isOlder(Transaction t2){
        int y1 = Integer.parseInt(this.getYear());
        int y2 = Integer.parseInt(t2.getYear());
        if(y1<y2){
            return true;
        }
        else if (y1>y2){
            return false;
        } else {
            int m1 = this.getMonthInt();
            int m2 = t2.getMonthInt();

            if(m1<m2){
                return true;
            }
            else if (m1>m2){
                return false;
            } else {
                int d1 = Integer.parseInt(this.getDay());
                int d2 = Integer.parseInt(t2.getDay());

                if(d1<d2){
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
