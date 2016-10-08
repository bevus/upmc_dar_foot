package models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hacene on 08/10/2016.
 */
@Entity
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;
    protected Date dateComment;
    protected String textComment;
    @ManyToOne
    protected User author;
    @ManyToOne
    protected Rencontre rencontre;

    public Rencontre getRencontre() {
        return rencontre;
    }

    public Comment setRencontre(Rencontre rencontre) {
        this.rencontre = rencontre;
        return this;
    }

    public int getId() {
        return id;
    }

    public Comment setId(int id) {
        this.id = id;
        return this;
    }

    public Date getDateComment() {
        return dateComment;
    }

    public Comment setDateComment(Date dateComment) {
        this.dateComment = dateComment;
        return this;
    }

    public String getTextComment() {
        return textComment;
    }

    public Comment setTextComment(String textComment) {
        this.textComment = textComment;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public Comment setAuthor(User author) {
        this.author = author;
        return this;
    }
}
