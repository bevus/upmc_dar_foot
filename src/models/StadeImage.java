package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * Created by Hacene on 10/10/2016.
 */
@Entity
public class StadeImage {
    @Id
    private String url;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JsonIgnore
    private Stade stade;

    public Stade getStade() {
        return stade;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStade(Stade stade) {
        this.stade = stade;
    }
}
