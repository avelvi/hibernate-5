package com.avelvi.hibernate.chapter03.pojo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class PojoTest {

    private final Logger logger = LoggerFactory.getLogger(PojoTest.class);

    @Test
    public void testModelCreation() {

        Person subject = new Person();
        subject.setName("Subject Name");

        Person observer = new Person();
        observer.setName("Observer Name");

        Skill skill = new Skill();
        skill.setName("Java");

        Ranking ranking = new Ranking();
        ranking.setSubject(subject);
        ranking.setObserver(observer);
        ranking.setSkill(skill);
        ranking.setRanking(8);

        logger.info(ranking.toString());

    }

}