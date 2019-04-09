package hku.cs.sqlitedemo;

public class Transaction {
    private int id;
    private String amount;
    private String description;
    private String type;
    private String category;
    private String date;

    private int year;
    private int month;
    private int day;


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

    public Transaction(String amount, String description, String type, String category, String date, int year, int month, int day) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.category = category;
        this.date = date;
        this.year = year;
        this.month = month;
        this.day = day;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
