package com.github.stantonk.java_issue_demos;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*

 Goetz, Brian. “10.1.5 Resource Deadlocks.” Java Concurrency in Practice: Brian Goetz ..., Addison-Wesley, 2013.

 */
public class ResourceExhaustionDeadlock {

    public static class Task1 implements Runnable {

        private final DataSource dsA;
        private final DataSource dsB;

        public Task1(DataSource dsA, DataSource dsB) {
            this.dsA = dsA;
            this.dsB = dsB;
        }

        @Override
        public void run() {

            while (true) {
                System.out.println("Task 1 begin.");
                Connection cA = null, cB = null;
                PreparedStatement psA = null, psB = null;
                try {
                    cA = dsA.getConnection();
                    psA = cA.prepareStatement("SELECT SLEEP(2);");
                    psA.executeQuery();

                    cB = dsB.getConnection();
                    psB = cB.prepareStatement("SELECT SLEEP(2);");
                    psB.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        psA.close();
                        psB.close();
                        cA.close();
                        cB.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Task 1 completed.");
            }

        }
    }

    public static class Task2 implements Runnable {

        private final DataSource dsA;
        private final DataSource dsB;

        public Task2(DataSource dsA, DataSource dsB) {
            this.dsA = dsA;
            this.dsB = dsB;
        }

        @Override
        public void run() {

            while (true) {
                System.out.println("Task 2 begin.");
                Connection cA = null, cB = null;
                PreparedStatement psA = null, psB = null;
                try {
                    cB = dsB.getConnection();
                    psB = cB.prepareStatement("SELECT SLEEP(2);");
                    psB.executeQuery();

                    cA = dsA.getConnection();
                    psA = cA.prepareStatement("SELECT SLEEP(2);");
                    psA.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        psB.close();
                        psA.close();
                        cB.close();
                        cA.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Task 2 completed.");
            }

        }
    }


    public static void main(String[] args) {

        HikariDataSource ds1 = new HikariDataSource();
        ds1.setJdbcUrl("jdbc:mysql://localhost:13306/bizeebe");
        ds1.setUsername("root");
        ds1.setPassword("");
        ds1.setDriverClassName("com.mysql.jdbc.Driver");
        ds1.setMaximumPoolSize(4);

        HikariDataSource ds2 = new HikariDataSource();
        ds2.setJdbcUrl("jdbc:mysql://localhost:23306/bizeebe");
        ds2.setUsername("root");
        ds2.setPassword("");
        ds2.setDriverClassName("com.mysql.jdbc.Driver");
        ds2.setMaximumPoolSize(4);

        ExecutorService pool1 = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("Task1-%d").build());
        pool1.execute(new Task1(ds1, ds2));
        pool1.execute(new Task1(ds1, ds2));
        pool1.execute(new Task1(ds1, ds2));
        pool1.execute(new Task1(ds1, ds2));

        ExecutorService pool2 = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("Task2-%d").build());
        pool2.execute(new Task2(ds1, ds2));
        pool2.execute(new Task2(ds1, ds2));
        pool2.execute(new Task2(ds1, ds2));
        pool2.execute(new Task2(ds1, ds2));

    }
}
