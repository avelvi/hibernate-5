package com.avelvi.hibernate.chapter03.services;

import com.avelvi.hibernate.chapter03.entities.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static com.avelvi.hibernate.util.hibernate.DBCleanerUtil.clearData;
import static org.testng.Assert.*;

public class HibernateRankingServiceTest {

    private final Logger logger = LoggerFactory.getLogger(HibernateRankingServiceTest.class);

    private final static String FIRST_SUBJECT_NAME = "Subject Name 1";
    private final static String SECOND_SUBJECT_NAME = "Subject Name 2";
    private final static String THIRD_SUBJECT_NAME = "Subject Name 3";
    private final static String FIRST_OBSERVER_NAME = "Observer Name 1";
    private final static String SECOND_OBSERVER_NAME = "Observer Name 2";
    private final static String THIRD_OBSERVER_NAME = "Observer Name 3";
    private final static String SKILL_JAVA_NAME = "Java";
    private final static String SKILL_PYTHON_NAME = "Python";

    private RankingService service = new HibernateRankingService();

    @Test
    public void testAddRanking() {

        service.addRanking(FIRST_SUBJECT_NAME, FIRST_OBSERVER_NAME, SKILL_JAVA_NAME, 8);
        assertEquals(service.getRankingFor(FIRST_SUBJECT_NAME, SKILL_JAVA_NAME), 8);

    }

    @Test
    public void testRankingAverage() {

        service.addRanking(SECOND_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 4);
        service.addRanking(SECOND_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 5);
        service.addRanking(SECOND_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 6);

        assertEquals(service.getRankingFor(SECOND_SUBJECT_NAME, SKILL_JAVA_NAME), 5);

        service.addRanking(SECOND_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 7);
        service.addRanking(SECOND_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 8);

        assertEquals(service.getRankingFor(SECOND_SUBJECT_NAME, SKILL_JAVA_NAME), 6);
    }

    @Test
    public void testUpdateNonExistentRanking() {

        assertEquals(service.getRankingFor(FIRST_SUBJECT_NAME, SKILL_JAVA_NAME), 0);

        service.updateRanking(FIRST_SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_JAVA_NAME, 7);
        assertEquals(service.getRankingFor(FIRST_SUBJECT_NAME, SKILL_JAVA_NAME), 7);

    }

    @Test
    public void testUpdateRanking() {

        service.addRanking(FIRST_SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_JAVA_NAME, 6);
        assertEquals(service.getRankingFor(FIRST_SUBJECT_NAME, SKILL_JAVA_NAME), 6);

        service.updateRanking(FIRST_SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_JAVA_NAME, 7);
        assertEquals(service.getRankingFor(FIRST_SUBJECT_NAME, SKILL_JAVA_NAME), 7);

    }

    @Test
    public void testRemoveNonExistentRanking() {
        assertEquals(service.getRankingFor(THIRD_SUBJECT_NAME, SKILL_PYTHON_NAME), 0);

        service.removeRanking(THIRD_SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_PYTHON_NAME);
        assertEquals(service.getRankingFor(THIRD_SUBJECT_NAME, SKILL_PYTHON_NAME), 0);
    }

    @Test
    public void testRemoveRanking() {
        service.addRanking(FIRST_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 8);
        assertEquals(service.getRankingFor(FIRST_SUBJECT_NAME, SKILL_JAVA_NAME), 8);

        service.removeRanking(FIRST_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME);
        assertEquals(service.getRankingFor(FIRST_SUBJECT_NAME, SKILL_JAVA_NAME), 0);
    }

    @Test
    public void testFindRankingsForEmptySet() {

        assertEquals(service.getRankingFor(SECOND_SUBJECT_NAME, SKILL_JAVA_NAME), 0);
        assertEquals(service.getRankingFor(SECOND_SUBJECT_NAME, SKILL_PYTHON_NAME), 0);

        assertEquals(service.findRankingsFor(SECOND_SUBJECT_NAME).size(), 0);

    }

    @Test
    public void testFindRankingsFor() {

        assertEquals(service.getRankingFor(THIRD_SUBJECT_NAME, SKILL_JAVA_NAME), 0);
        assertEquals(service.getRankingFor(THIRD_SUBJECT_NAME, SKILL_PYTHON_NAME), 0);

        service.addRanking(THIRD_SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_JAVA_NAME, 9);
        service.addRanking(THIRD_SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_JAVA_NAME, 7);
        service.addRanking(THIRD_SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_PYTHON_NAME, 7);
        service.addRanking(THIRD_SUBJECT_NAME, THIRD_OBSERVER_NAME, SKILL_PYTHON_NAME, 5);

        Map<String, Integer> rankings = service.findRankingsFor(THIRD_SUBJECT_NAME);

        assertEquals(rankings.size(), 2);
        assertNotNull(rankings.get(SKILL_JAVA_NAME));
        assertEquals(rankings.get(SKILL_JAVA_NAME), new Integer(8));
        assertNotNull(rankings.get(SKILL_PYTHON_NAME));
        assertEquals(rankings.get(SKILL_PYTHON_NAME), new Integer(6));

    }

    @Test
    public void testFindBestPersonForNonExistentSkill() {
        Person p = service.findBestPersonFor(SKILL_JAVA_NAME);
        assertNull(p);
    }

    @Test
    public void testFindBestPersonFor() {

        service.addRanking(FIRST_SUBJECT_NAME, FIRST_OBSERVER_NAME, SKILL_JAVA_NAME, 6);
        service.addRanking(FIRST_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 8);
        service.addRanking(SECOND_SUBJECT_NAME, FIRST_OBSERVER_NAME, SKILL_JAVA_NAME, 5);
        service.addRanking(SECOND_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 7);
        service.addRanking(THIRD_SUBJECT_NAME, FIRST_OBSERVER_NAME, SKILL_JAVA_NAME, 7);
        service.addRanking(THIRD_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_JAVA_NAME, 9);

        service.addRanking(FIRST_SUBJECT_NAME, SECOND_OBSERVER_NAME, SKILL_PYTHON_NAME, 2);

        Person p = service.findBestPersonFor(SKILL_JAVA_NAME);
        assertEquals(p.getName(), THIRD_SUBJECT_NAME);

    }

    @AfterMethod
    public void tearDown() {
        try {
            clearData();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}