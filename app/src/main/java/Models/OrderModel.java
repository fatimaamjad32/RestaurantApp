package Models;

import java.util.ArrayList;

public class OrderModel {
    private String userId;
    private String username;
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;
    private ArrayList<FoodModel> orderItems;
    private int totalPrice;

    // Empty constructor (required for Firestore)
    public OrderModel() {
    }

    // Parameterized constructor
    public OrderModel(String userId, ArrayList<FoodModel> orderItems, int totalPrice) {
        this.userId = userId;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<FoodModel> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<FoodModel> orderItems) {
        this.orderItems = orderItems;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
