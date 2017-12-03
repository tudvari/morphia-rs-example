package com.tudvari.morphia.rs.example.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.mongodb.morphia.annotations.Id;

@Getter @Setter @EqualsAndHashCode public class Article
{
    private String body;
    @Id private String id;
    private String title;
}
