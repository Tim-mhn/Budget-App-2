package hku.cs.sqlitedemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category {

    // A transaction category will have a name attribute and an image path (src) attribute
    // With such a class, we will be able to store new categories implemented by the user in a DB

    private String name = "";
    private String imageName = "default_image";
    private static List<Category> categories = new ArrayList<>(Arrays.asList(
            new Category("Dinner", "burger"),
            new Category("Nightlife", "confetti"),
            new Category("Health", "hospital"),
            new Category("Housing", "house")
    ));

    public Category(String name, String imageName) {
        this.name = name;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public static List<Category> getCategories() {
        return categories;
    }

    public static void setCategories(List<Category> categories) {
        Category.categories = categories;
    }

    public static void addCategory(Category category){
        Category.categories.add(category);
    }

    // Based on the transaction's category, we return the name of the category image

    public static String getImageName(String categoryTarget){
        int i = 0;
        int size = Category.getCategories().size();
        String category = Category.getCategories().get(i).getName();
        while(!category.equalsIgnoreCase(categoryTarget) && i<size-1){
            i++;
            category = Category.getCategories().get(i).getName();
        }

        // If we get out of the loop and the category still doesn't match the target category
        // The transaction will have a question mark image
        if(!category.equalsIgnoreCase(categoryTarget)){
            return("question");
        }
        // In usual cases, we return the found category's image name
        else {
            return categories.get(i).getImageName();
        }

    }
}
