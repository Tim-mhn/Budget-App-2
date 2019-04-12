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

    public static String getImageName(String categoryTarget){
        int i = 0;
        String category = Category.getCategories().get(i).getName();
        while(!category.equalsIgnoreCase(categoryTarget) && i<Category.getCategories().size()){
            i++;
            category = Category.getCategories().get(i).getName();
        }
        return Category.getCategories().get(i).getImageName();
    }
}
