package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by khelifa on 08/10/2016.
 */
@Entity
public class Stade {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nom;
    private String commune;
    private int codePostal;
    private double latitude;
    private double longitude;
    private int note;
    @OneToMany(mappedBy = "stade", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @OrderBy("dateComment desc ")
    @JsonIgnore
    private List<Comment> comments;
    @OneToMany(mappedBy = "stade", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<StadeImage> images;
    @ManyToMany
    private List<User> stadeUpvoters;
    @OneToMany(mappedBy = "stade", cascade = CascadeType.ALL)
    private List<Meteo> meteos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(int codePostal) {
        this.codePostal = codePostal;
    }

    public List<Comment> getComments() {
        Collections.sort(comments);
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public List<StadeImage> getImages() {
        return images;
    }

    public void setImages(List<StadeImage> images) {
        this.images = images;
    }


    public List<Meteo> getMeteos() {
        return meteos;
    }

    public void setMeteos(List<Meteo> meteos) {
        this.meteos = meteos;
    }

    public List<User> getStadeUpvoters() {
        return stadeUpvoters;
    }

    public void setStadeUpvoters(List<User> upvoters) {
        this.stadeUpvoters = upvoters;
    }
}
