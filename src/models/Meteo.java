package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hacene on 24/10/2016.
 */
@Entity
public class Meteo implements Comparable<Meteo>, Cloneable {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String dayName;
    private Date dayDate;
    private int dayT;
    private int min;
    private int max;
    private int eve;
    private int morn;
    private int nightT;
    private int humidity;
    private int pressure;
    private double speed;
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

    public int getMin() {
        return min;
    }

    public Meteo setMin(int min) {
        this.min = min;
        return this;
    }

    public int getMax() {
        return max;
    }

    public Meteo setMax(int max) {
        this.max = max;
        return this;
    }

    public int getEve() {
        return eve;
    }

    public Meteo setEve(int eve) {
        this.eve = eve;
        return this;
    }

    public int getMorn() {
        return morn;
    }

    public Meteo setMorn(int morn) {
        this.morn = morn;
        return this;
    }

    public double getSpeed() {
        return speed;
    }

    public Meteo setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public int getPressure() {
        return pressure;
    }

    public Meteo setPressure(int pressure) {
        this.pressure = pressure;
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


    public Meteo clone() throws CloneNotSupportedException {
        return (Meteo) super.clone();
    }
}
