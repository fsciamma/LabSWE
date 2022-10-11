package model;

import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Customer {

    private static final String EMAIL_REGEX_PATTERN = "^(.+)@(.+).(.+)$";

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

    public void set_first_name(){
        Scanner mySc = new Scanner(System.in);
        System.out.println("Nuovo nome: ");

        String fn = mySc.nextLine();
        if(!Objects.equals(fn, ""))
            this._first_name = fn;
        System.out.println("Nome: " + this._first_name);
    }

    public String get_last_name() {
        return _last_name;
    }

    public void set_last_name(String _last_name) {
        this._last_name = _last_name;
    }

    public void set_last_name(){
        Scanner mySc = new Scanner(System.in);
        System.out.println("Nuovo cognome: ");

        String ln = mySc.nextLine();
        if(!Objects.equals(ln, ""))
            this._last_name = ln;
        System.out.println("Cognome: " + this._last_name);
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public void set_email(){
        Scanner mySc = new Scanner(System.in);
        System.out.println("Nuova e-mail: ");

        String em = mySc.nextLine();
        this.isValidEmail(em);
        if(!Objects.equals(em, ""))
            this._email = em;
        System.out.println("E-mail: " + this._email);
    }

    private void isValidEmail(final String em){
        Pattern pattern = Pattern.compile(EMAIL_REGEX_PATTERN);

        if(!pattern.matcher(em).matches()){
            throw new IllegalArgumentException("Indirizzo e-mail non valido...");
        }
    }

    @Override
    public String toString(){
        return "Cliente #" + this._customerID + ": " + this._first_name + " " + this._last_name + ", e-mail: " + this._email;
    }
}
