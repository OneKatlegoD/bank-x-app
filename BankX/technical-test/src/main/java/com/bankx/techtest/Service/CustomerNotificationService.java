package com.bankx.techtest.Service;

import java.lang.Exception;
import java.util.concurrent.BlockingQueue;

public class CustomerNotificationService implements Runnable {

    private BlockingQueue<String> notificationsChannel;
    private Thread nThread;

    public CustomerNotificationService(BlockingQueue<String> notificationsChannel)
    {
        this.notificationsChannel = notificationsChannel;
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startRunning()
    {
        nThread = new Thread(this);
        nThread.start();
    }

    private void doWork() throws InterruptedException
    {
        while (!nThread.isInterrupted())
        {
            // route notification to appropriate route. Routing to console for now and this makes the UI clutter a bit.
            String customerNotification = notificationsChannel.take();
            System.out.println("-----------Customer notification---------");
            System.out.println(customerNotification);
            System.out.println("-----------Customer notification---------");
        }
    }
}
