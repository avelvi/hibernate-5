package com.avelvi.hibernate.util.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class SessionUtil {

    private static final SessionUtil instance = new SessionUtil();
    private final SessionFactory factory;
    private static final Logger logger = LoggerFactory.getLogger(SessionUtil.class);

    private SessionUtil() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static Session getSession() {
        return getInstance().factory.openSession();
    }

    private static SessionUtil getInstance() {
        return instance;
    }

    public static void doWithSession(Consumer<Session> command) {
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();

            command.accept(session);
            if (tx.isActive() &&
                    !tx.getRollbackOnly()) {
                tx.commit();
            } else {
                tx.rollback();
            }
        }
    }

    public static <T> T returnFromSession(Function<Session, T> command) {
        try (Session session = getSession()) {

            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                return command.apply(session);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            } finally {
                if (tx != null) {
                    if (tx.isActive() &&
                            !tx.getRollbackOnly()) {
                        tx.commit();
                    } else {
                        tx.rollback();
                    }
                }
            }
        }
    }
}
