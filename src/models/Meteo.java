package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hacene on 24/10/2016.
 */
@Entity
public class Meteo implements Comparable<Meteo> {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String dayName;
    private Date dayDate;
    private int dayT;
    private int nightT;
    private int humidity;
    private int code;
    private String description;
    private String icon;

    @ManyToOne
    @JsonIgnore
    private Stade stade;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "meteo")
    @JsonIgnore
    private Rencontre rencontre;

    public Rencontre getRencontre() {
        return rencontre;
    }

    public Meteo setRencontre(Rencontre rencontre) {
        this.rencontre = rencontre;
        return this;
    }

    public Stade getStade() {
        return stade;
    }

    public void setStade(Stade stade) {
        this.stade = stade;
    }

    public int getId() {
        return id;
    }

    public Meteo setId(int id) {
        this.id = id;
        return this;
    }

    public String getDayName() {
        return dayName;
    }

    public Meteo setDayName(String dayName) {
        this.dayName = dayName;
        return this;
    }

    public Date getDayDate() {
        return dayDate;
    }

    public Meteo setDayDate(Date dayDate) {
        this.dayDate = dayDate;
        return this;
    }

    public int getDayT() {
        return dayT;
    }

    public Meteo setDayT(int dayT) {
        this.dayT = dayT;
        return this;
    }

    public int getNightT() {
        return nightT;
    }

    public Meteo setNightT(int nightT) {
        this.nightT = nightT;
        return this;
    }

    public int getHumidity() {
        return humidity;
    }

    public Meteo setHumidity(int humidity) {
        this.humidity = humidity;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Meteo setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public Meteo setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Meteo setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public int compareTo(Meteo o) {
        return  this.getDayDate().compareTo(o.getDayDate());
    }

    @Override
    public String toString() {
        return "Meteo{" +
                "id=" + id +
                ", dayName='" + dayName + '\'' +
                ", dayDate=" + dayDate +
                ", dayT=" + dayT +
                ", nightT=" + nightT +
                ", humidity=" + humidity +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", stade=" + stade +
                ", rencontre=" + rencontre +
                '}';
    }


    public Meteo clone()  {
        Meteo m = new Meteo();
        m.setCode(getCode());
        m.setIcon(getIcon());
        m.setStade(getStade());
        m.setHumidity(getHumidity());
        m.setDayT(getDayT());
        m.setId(getId());
        m.setRencontre(getRencontre());
        m.setNightT(getNightT());
        m.setDescription(getDescription());
        m.setDayName(getDayName());
        m.setDayDate(getDayDate());
        return m;
    }
}
