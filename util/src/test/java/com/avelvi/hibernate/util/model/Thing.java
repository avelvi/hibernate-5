package com.avelvi.hibernate.util.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "Thing")
@Data
@ToString
public class Thing {

    @Id
    @GeneratedValue
    @Setter
    @Getter
    private Integer id;

    @Column
    @Setter
    @Getter
    private String name;
}
