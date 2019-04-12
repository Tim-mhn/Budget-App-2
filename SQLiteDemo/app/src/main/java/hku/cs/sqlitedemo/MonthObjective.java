package hku.cs.sqlitedemo;

public class MonthObjective {

    // MonthObjective instances allow to easier store the data of a monthly objective

    // The attributes of the class will be the month and year number, the income objective and the expense objective

    private double income;
    private double expense;

    public MonthObjective(double income, double expense) {
        this.income = income;
        this.expense = expense;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

}
