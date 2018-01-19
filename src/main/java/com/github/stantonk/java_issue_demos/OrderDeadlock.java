package com.github.stantonk.java_issue_demos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*

Goetz, Brian. “10.1.1 Lock-Ordering Deadlocks.” Java Concurrency in Practice: Brian Goetz ..., Addison-Wesley, 2013.

 */
public class OrderDeadlock {
    private static final Object left = new Object();
    private static final Object right = new Object();

    private static void log(String l) {
        System.out.println(Thread.currentThread().getName() + ": " + l);
    }


    public static class Thing1 extends Thread {

        private final Object left;
        private final Object right;

        public Thing1(Object left, Object right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void run() {
            while (true) {
                log("wait on left...");
                synchronized (left) {
                    log("locked left!");
                    log("wait on right...");
                    synchronized (right) {
                        log("locked right!");
                        doSomething();
                    }
                    log("released lock for right!");
                }
                log("released lock for left!");
            }
        }

        private void doSomething() {
            log("doSomething()");
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Thing2 extends Thread {
        private final Object left;
        private final Object right;

        public Thing2(Object left, Object right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void run() {
            while (true) {
                log("wait on right...");
                synchronized (right) {
                    log("locked right!");
                    log("wait on left...");
                    synchronized (left) {
                        log("locked left!");
                        doSomethingElse();
                    }
                    log("released lock for left!");
                }
                log("released lock for right!");
            }
        }

        private void doSomethingElse() {
            log("doSomethingElse()");
            try {
                Thread.sleep(100l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        log("Starting...");
        new Thing1(left, right).start();
        new Thing2(left, right).start();
    }
}
