class Mythread1 implements Runnable {
    public void run() {
    System.out.println("Thread 1 is running");
    System.out.println("Current Thread is:"+Thread.currentThread().getName());
    } }
    class Mythread2 extends Thread {
    public void run() {
    System.out.println("Thread 2 is running");
    System.out.println("Current Thread is:"+Thread.currentThread().getName());
     } }
    class Runthread3 {
     public static void main(String arg[]){
     Mythread1 r1=new Mythread1();
    Thread t1=new Thread(r1,"thread1");
     t1.start();
     Mythread2 r2=new Mythread2();
    Thread t2=new Thread(r2,"thread2");
     t2.start();
    System.out.println("Name of t1:"+t1.getName());
     System.out.println("Name of t2:"+t2.getName());
     System.out.println("id of t1:"+t1.getId());
     System.out.println("id of t2:"+t2.getId());
     t1.setName("IFM");
     System.out.println("After changing name of t1:"+t1.getName());
     System.out.println("Current Thread is:"+Thread.currentThread().getName());
     }}