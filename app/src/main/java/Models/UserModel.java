package Models;

public class UserModel {

    String userid,name,phone,address;
    private int totalPrice;

    public UserModel() {
    }

    public UserModel(String userid, String name, String phone, String address) {
        this.userid = userid;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public UserModel(String userid, String name, String phone, String address, int totalPrice) {
        this.userid = userid;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.totalPrice = totalPrice;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setName(String name) {
        this.name = name;
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
}
