package com.avelvi.hibernate.util.hibernate;

import com.avelvi.hibernate.util.model.Thing;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class SessionBuilderTest {

    private static final Logger logger = LoggerFactory.getLogger(SessionBuilderTest.class);

    @Test
    public void testSessionFactory() {
        try (Session session = SessionUtil.getSession()) {
            assertNotNull(session);
        }
    }
    @Test
    public void testDoWithSession() {
        SessionUtil.doWithSession(session -> {
            session.createQuery("delete from Thing").executeUpdate();

            Thing thing = new Thing();
            thing.setName("Thing Name");
            session.persist(thing);
        });
        Thing thing = SessionUtil.returnFromSession(session -> {
            Query<Thing> query = session.createQuery("from Thing t where t.name = :name", Thing.class);
            query.setParameter("name", "Thing Name");
            return query.getSingleResult();
        });
        assertNotNull(thing);
        logger.info(thing.toString());
        assertEquals(thing.getName(), "Thing Name");
    }


}
