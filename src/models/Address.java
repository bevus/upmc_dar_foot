package models;

import javax.persistence.*;

/**
 * Created by Hacene on 07/10/2016.
 */
@Entity
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int number;
    private String street;
    private String posteCode;
    private String city;

    public int getId() {
        return id;
    }

    public Address setId(int id) {
        this.id = id;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public Address setNumber(int number) {
        this.number = number;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public Address setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getPosteCode() {
        return posteCode;
    }

    public Address setPosteCode(String posteCode) {
        this.posteCode = posteCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Address setCity(String city) {
        this.city = city;
        return this;
    }
}
