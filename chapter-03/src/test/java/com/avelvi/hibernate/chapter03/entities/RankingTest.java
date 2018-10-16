package com.avelvi.hibernate.chapter03.entities;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RankingTest {

    private final Logger logger = LoggerFactory.getLogger(RankingTest.class);
    private final static String SUBJECT_NAME = "Subject Name";
    private final static String FIRST_OBSERVER_NAME = "Observer Name 1";
    private final static String SECOND_OBSERVER_NAME = "Observer Name 2";
    private final static String THIRD_OBSERVER_NAME = "Observer Name 3";
    private final static String SKILL_JAVA_NAME = "Java";

    private SessionFactory factory;

    @BeforeMethod
    public void setup(){

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();

        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();

    }

    @Test
    public void testSaveRanking() {
        try(Session session = factory.openSession()) {

            Transaction tx = session.beginTransaction();

            Person subject = savePerson(session, SUBJECT_NAME);
            Person observer = savePerson(session, FIRST_OBSERVER_NAME);
            Skill skill = saveSkill(session, SKILL_JAVA_NAME);

            Ranking ranking = new Ranking();
            ranking.setSubject(subject);
            ranking.setObserver(observer);
            ranking.setSkill(skill);
            ranking.setRanking(8);
            session.save(ranking);

            tx.commit();

            logger.info("Saved: " + ranking.toString());

        }
    }

    @Test
    public void testRankings() {

        populateRankingData();

        try (Session session = factory.openSession()) {

            Transaction tx = session.beginTransaction();

            Query<Ranking> query = session.createQuery("from Ranking r " +
                    "where r.subject.name = :name " +
                    "and r.skill.name = :skill", Ranking.class);
            query.setParameter("name", SUBJECT_NAME);
            query.setParameter("skill", "Java");

            IntSummaryStatistics summaryStatistics = query.list()
                    .stream()
                    .collect(Collectors.summarizingInt(Ranking::getRanking));

            long count = summaryStatistics.getCount();
            int average = (int) summaryStatistics.getAverage();

            tx.commit();

            assertEquals(count, 3);
            assertEquals(average, 7);

        }

    }

    @Test
    public void changeRanking() {

        populateRankingData();

        try(Session session = factory.openSession()) {

            Transaction tx = session.beginTransaction();
            Query<Ranking> query = session.createQuery("from Ranking r " +
                    "where r.subject.name = :subject and " +
                    "r.observer.name = :observer and " +
                    "r.skill.name = :skill", Ranking.class);
            query.setParameter("subject", SUBJECT_NAME);
            query.setParameter("observer", FIRST_OBSERVER_NAME);
            query.setParameter("skill", SKILL_JAVA_NAME);
            Ranking ranking = query.uniqueResult();
            assertNotNull(ranking, "Could not find matching ranking");
            ranking.setRanking(9);
            tx.commit();
        }
        assertEquals(getAverage(SUBJECT_NAME, SKILL_JAVA_NAME), 8);

    }

    @Test
    public void removeRanking() {
        populateRankingData();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Ranking ranking = findRanking(session, SUBJECT_NAME,
                    FIRST_OBSERVER_NAME, SKILL_JAVA_NAME);
            assertNotNull(ranking, "Ranking not found");
            session.delete(ranking);
            tx.commit();
        }
        assertEquals(getAverage(SUBJECT_NAME, SKILL_JAVA_NAME), 7);
    }

    private int getAverage(String subject, String skill) {
        try (Session session = factory.openSession()) {

            Transaction tx = session.beginTransaction();

            Query<Ranking> query = session.createQuery("from Ranking r "
                    + "where r.subject.name = :name "
                    + "and r.skill.name = :skill", Ranking.class);
            query.setParameter("name", subject);
            query.setParameter("skill", skill);

            IntSummaryStatistics stats = query.list()
                    .stream()
                    .collect(Collectors.summarizingInt(Ranking::getRanking));

            int average = (int) stats.getAverage();
            tx.commit();
            return average;
        }
    }

    private void populateRankingData() {

        try(Session session = factory.openSession()) {

            Transaction tx = session.beginTransaction();

            createData(session, SUBJECT_NAME, FIRST_OBSERVER_NAME, SKILL_JAVA_NAME, 6);
            createData(session, SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 7);
            createData(session, SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_JAVA_NAME, 8);

            tx.commit();
        }

    }

    private void createData(Session session, String subjectName,
                            String observerName, String skillName, int rank) {

        Person subject = savePerson(session, subjectName);
        Person observer = savePerson(session, observerName);
        Skill skill = saveSkill(session, skillName);

        Ranking ranking = new Ranking();
        ranking.setSubject(subject);
        ranking.setObserver(observer);
        ranking.setSkill(skill);
        ranking.setRanking(rank);
        session.save(ranking);
    }



    private Skill saveSkill(Session session, String name) {

        Skill skill = findSkill(session, name);
        if(isNull(skill)) {
            skill = new Skill();
            skill.setName(name);
            session.save(skill);

            logger.info("Saved: " + skill.toString());
        }

        return skill;
    }

    private Skill findSkill(Session session, String name) {

        Query<Skill> query = session.createQuery("from Skill s where s.name = :name", Skill.class);
        query.setParameter("name", name);

        return query.uniqueResult();

    }

    private Person savePerson(Session session, String name) {

        Person person = findPerson(session, name);

        if(isNull(person)) {
            person = new Person();
            person.setName(name);
            session.save(person);
            logger.info("Saved: " + person.toString());
        }

        return person;

    }

    private Person findPerson(Session session, String name) {

        Query<Person> query = session.createQuery("from Person p where p.name = :name", Person.class);
        query.setParameter("name", name);

        return query.uniqueResult();
    }

    private Ranking findRanking(Session session, String subject, String observer, String skill) {
        Query<Ranking> query = session.createQuery("from Ranking r "
                + "where r.subject.name = :subject and "
                + "r.observer.name = :observer and "
                + "r.skill.name = :skill", Ranking.class);
        query.setParameter("subject", subject);
        query.setParameter("observer", observer);
        query.setParameter("skill", skill);
        return query.uniqueResult();
    }

}