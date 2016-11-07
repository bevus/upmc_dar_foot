package models;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Rencontre {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String description;
    private int nbJoueurs;
    private Date dateDebut;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.EXCEPTION)
    private Stade stade;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.EXCEPTION)
    private User organizer;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rencontre")
    private List<RencontreUser> players;
    @OneToOne(cascade = CascadeType.ALL)
    private Meteo meteo;
    private boolean cancled = false;

    public boolean isCancled() {
        return cancled;
    }

    public Rencontre setCancled(boolean cancled) {
        this.cancled = cancled;
        return this;
    }

    public Meteo getMeteo() {
        return meteo;
    }

    public Rencontre setMeteo(Meteo meteo) {
        this.meteo = meteo;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Rencontre setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getId() {
        return id;
    }

    public Rencontre setId(int id) {
        this.id = id;
        return this;
    }

    public int getNbJoueurs() {
        return nbJoueurs;
    }

    public Rencontre setNbJoueurs(int nbJoueurs) {
        this.nbJoueurs = nbJoueurs;
        return this;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public Rencontre setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
        return this;
    }

    public Stade getStade() {
        return stade;
    }

    public Rencontre setStade(Stade stade) {
        this.stade = stade;
        return this;
    }

    public User getOrganizer() {
        return organizer;
    }

    public Rencontre setOrganizer(User organizer) {
        this.organizer = organizer;
        return this;
    }

    public List<RencontreUser> getPlayers() {
        return players;
    }

    public Rencontre setPlayers(List<RencontreUser> players) {
        this.players = players;
        return this;
    }
}
