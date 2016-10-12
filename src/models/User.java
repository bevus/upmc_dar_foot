package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hacene on 06/10/2016.
 */
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;
    protected String firstName;
    protected String lastName;
    @Column(unique = false, nullable = false)
    protected String email;
    @JsonIgnore
    protected String password;
    protected String img;
    protected Date creationDate;
    protected String phoneNumber;
    @OneToOne(cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.EXCEPTION)
    private Address address;

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getImg() {
        return img;
    }

    public User setImg(String img) {
        this.img = img;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public User setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
