package models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by khelifa on 08/10/2016.
 */
@Entity
public class Rencontre {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int nbJoueurs;
    private Date dateDebut;
    @ManyToOne
    private Stade stade;
    @ManyToOne
    private User organizer;
    @ManyToMany
    private List<User> players;
    @OneToMany(
            mappedBy = "rencontre"
    )
    private List<Comment> comments;

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

    public List<User> getPlayers() {
        return players;
    }

    public Rencontre setPlayers(List<User> players) {
        this.players = players;
        return this;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Rencontre setComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }
}
