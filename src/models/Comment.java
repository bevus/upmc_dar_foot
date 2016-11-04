package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Hacene on 08/10/2016.
 */
@Entity
public class Comment implements Comparable<Comment> {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;
    protected Date dateComment;
    protected String textComment;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.EXCEPTION)
    protected User author;
    @NotFound(action = NotFoundAction.EXCEPTION)
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Stade stade;
    @ManyToMany
    private List<User> upvoters;

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

    public Stade getStade() {
        return stade;
    }

    public void setStade(Stade stade) {
        this.stade = stade;
    }

    public List<User> getUpvoters() {
        return upvoters;
    }

    public void setUpvoters(List<User> upvoters) {
        this.upvoters = upvoters;
    }

    @Override
    public int compareTo(Comment o) {
        return this.getUpvoters().size() - o.getUpvoters().size();
    }
}
