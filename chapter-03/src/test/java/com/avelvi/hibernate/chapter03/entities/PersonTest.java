package com.avelvi.hibernate.chapter03.entities;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PersonTest {

    private final Logger logger = LoggerFactory.getLogger(PersonTest.class);

    private SessionFactory sessionFactory;

    @BeforeClass
    public void setup(){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Test
    public void testSavePerson(){

        try(Session session = sessionFactory.openSession()){
            Transaction tx = session.beginTransaction();
            Person person = new Person();
            person.setName("Person Name");

            Long personId = (Long) session.save(person);

            tx.commit();

            logger.info("Person Id = " + personId);

        }

    }

}