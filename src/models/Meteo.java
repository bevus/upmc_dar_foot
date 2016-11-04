package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hacene on 24/10/2016.
 */
@Entity
public class Meteo {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String dayName;
    private Date dayDate;
    private int dayT;
    private int nightT;
    private int humidity;
    private String description;
    private String icon;

    @ManyToOne
    @JsonIgnore
    private Stade stade;

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
}
