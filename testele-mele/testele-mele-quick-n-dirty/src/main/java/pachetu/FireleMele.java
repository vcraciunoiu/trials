package pachetu;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class FireleMele {

    public static void main(String[] args) {
        
        Random raza = new Random();
        ArrayList<String> lista = new ArrayList<String>();
        Lock mylock = new Lock() {
            
            @Override
            public void unlock() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean tryLock() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public Condition newCondition() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public void lockInterruptibly() throws InterruptedException {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void lock() {
                // TODO Auto-generated method stub
                
            }
        };
        
        Thread producer = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("sunt pe firu 1");
                while(true) {
                    int size = lista.size();
                    if (size < 5) {
                        synchronized (lista) {
                            lista.add("elementul " + raza.nextInt());
                            System.out.println("sunt in firu 1. lista este: " + lista);
                        }
                    } else if (size==5) {
                        System.out.println("size==5");
                    }
                }
            }
        });
        
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("sunt pe firu 2");
                while(true) {
                    int size = lista.size();
                    if (size > 0) {
                        synchronized (lista) {
                            lista.remove(0);
                            System.out.println("sunt in firu 2. lista este: " + lista);
                        }
                    } else if (size==0) {
                        System.out.println("size==0");
                    }
                }
            }
        });

        producer.start();
        consumer.start();
        
        System.out.println("sunt in main. lista este: " + lista);
    }
}
