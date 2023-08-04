package Models;

import java.io.Serializable;
import java.util.Objects;

public class FoodModel implements Serializable {
    private String title,imageUri,description;
    private String price;
    private int numberInCart;
    private String documentid;

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public FoodModel(String title, String imageUri, String description, String price, int numberInCart, String documentid) {
        this.title = title;
        this.imageUri = imageUri;
        this.description = description;
        this.price = price;
        this.numberInCart = numberInCart;
        this.documentid = documentid;
    }

    public FoodModel() {
    }

    public FoodModel(String title, String imageUri, String description, String price) {
        this.title = title;
        this.imageUri = imageUri;
        this.description = description;
        this.price = price;
    }

    public FoodModel(String title, String imageUri, String description, String price, int numberInCart) {
        this.title = title;
        this.imageUri = imageUri;
        this.description = description;
        this.price = price;
        this.numberInCart = numberInCart;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }


}
