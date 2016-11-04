package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hacene on 15/10/2016.
 */
@Entity
public class RencontreUser {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String team;
    @ManyToOne(cascade = CascadeType.MERGE)
    private User player;
    private Date dateCreation;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonIgnore
    private Rencontre rencontre;

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public Rencontre getRencontre() {
        return rencontre;
    }

    public void setRencontre(Rencontre rencontre) {
        this.rencontre = rencontre;
    }
}
