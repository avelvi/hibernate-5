package com.avelvi.hibernate.util.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashSet;
import java.util.Set;

public class DBCleanerUtil {

    public static void clearData() {

        try(Session session = SessionUtil.getSession()) {

            Transaction tx = session.beginTransaction();

            Query query = session.createSQLQuery("SET REFERENTIAL_INTEGRITY FALSE");
            query.executeUpdate();

            query = session.createSQLQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
            @SuppressWarnings("unchecked")
            Set<String> tables = new HashSet<>(query.getResultList());
            tables.forEach(table -> {
                Query q = session.createSQLQuery("TRUNCATE TABLE " + table);
                q.executeUpdate();
            });

            query = session.createSQLQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
            @SuppressWarnings("unchecked")
            Set<String> sequences = new HashSet<>(query.getResultList());
            sequences.forEach(sequence -> {
                Query q = session.createSQLQuery("ALTER SEQUENCE " + sequence + " RESTART WITH 1");
                q.executeUpdate();
            });

            query = session.createSQLQuery("SET REFERENTIAL_INTEGRITY TRUE");
            query.executeUpdate();

            tx.commit();

        }

    }

}
