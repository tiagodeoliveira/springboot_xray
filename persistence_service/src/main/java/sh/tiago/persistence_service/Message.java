package sh.tiago.persistence_service;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Message {
    public Message() {
    }

    public Message(String message) {
        this.message = message;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String message;

    public Integer getId() {
        return this.id;
    }
}
