package hku.cs.sqlitedemo;

import java.util.ArrayList;
import java.util.List;

public class TransactionsSorter {

    // Class to sort transactions from newest to oldest
    // In the MainTransactions Vue, we use this sorter to display the newest transactions first
    // To do as such, we create an instance of TransactionsSorter and use the mergeSort method implemented in this call


    public TransactionsSorter() {
    }

    // We sort transactions by using a merge sort

    public List<Transaction> mergeSort(List<Transaction> transactions){
        int n = transactions.size();

        if(n<=1){
            return transactions;
        } else {
            List <Transaction> left = mergeSort(transactions.subList(0,n/2));
            List <Transaction> right = mergeSort(transactions.subList(n/2,n));

            return merge(left, right);
        }
    }

    public List<Transaction> merge(List<Transaction> left, List<Transaction> right){
        if(left.size()==0){
            return right;
        }
        else if (right.size() == 0){
            return left;
        }
        else if (left.get(0).isOlder(right.get(0))){ // sorted list will have the newest transactions first
            List<Transaction> newList = new ArrayList<>();
            newList.add(right.get(0));
            newList.addAll(merge(left, right.subList(1,right.size())));
            return newList;
        } else {
            List<Transaction> newList = new ArrayList<>();
            newList.add(left.get(0));
            newList.addAll(merge(right, left.subList(1, left.size())));
            return newList;
        }
    }


}
