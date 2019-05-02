package com.jkramr.demowalletserver.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date created;

    public User(Integer id) {
        this.id = id;
    }
}
