package com.example.unitips.Favourites;

public class FavouritesItemData {
    private String title;
    private String description;
    private String category;


    public FavouritesItemData() {
        //empty constructor needed
    }

    public FavouritesItemData(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
