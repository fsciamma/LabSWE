package model;

public class Customer {

    private int _customerID;
    private String _first_name;
    private String _last_name;
    private String _email;

    public Customer() {

    }
    public Customer(int _customerID, String _first_name, String _last_name, String _email) {
        this._customerID = _customerID;
        this._first_name = _first_name;
        this._last_name = _last_name;
        this._email = _email;
    }

    public int get_customerID() {
        return _customerID;
    }

    public void set_customerID(int _customerID) {
        this._customerID = _customerID;
    }

    public String get_first_name() {
        return _first_name;
    }

    public void set_first_name(String _first_name) {
        this._first_name = _first_name;
    }

    public String get_last_name() {
        return _last_name;
    }

    public void set_last_name(String _last_name) {
        this._last_name = _last_name;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    @Override
    public String toString(){
        return "Cliente #" + this._customerID + ": " + this._first_name + " " + this._last_name + ", e-mail: " + this._email;
    }
}
