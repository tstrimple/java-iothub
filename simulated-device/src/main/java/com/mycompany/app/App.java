package com.mycompany.app;

import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.Message;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubMessageResult;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Hello world!
 *
 */
public class App 
{
    private static String connString = "HostName=adojeiothub.azure-devices.net;DeviceId=myFirstJavaDevice;SharedAccessKey=83Gfbj8wfjsErg+2Fihv5gLuEFHz4YX8pT0wsoZNGn4=";
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private static String deviceId = "myFirstJavaDevice";
    private static DeviceClient client;

    public static void main( String[] args ) throws IOException, URISyntaxException {
    client = new DeviceClient(connString, protocol);
    client.open();

    MessageSender sender = new MessageSender();

    ExecutorService executor = Executors.newFixedThreadPool(1);
    executor.execute(sender);

    System.out.println("Press ENTER to exit.");
    System.in.read();
    executor.shutdownNow();
    client.close();
    }
    
    private static class MessageSender implements Runnable {
        public volatile boolean stopThread = false;

        public void run()  {
            try {
            double avgWindSpeed = 10; // m/s
            Random rand = new Random();

            while (!stopThread) {
                double currentWindSpeed = avgWindSpeed + rand.nextDouble() * 4 - 2;
                TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
                telemetryDataPoint.deviceId = deviceId;
                telemetryDataPoint.windSpeed = currentWindSpeed;

                String msgStr = telemetryDataPoint.serialize();
                Message msg = new Message(msgStr);
                System.out.println("Sending: " + msgStr);

                Object lockobj = new Object();
                EventCallback callback = new EventCallback();
                client.sendEventAsync(msg, callback, lockobj);

                synchronized (lockobj) {
                lockobj.wait();
                }
                Thread.sleep(1000);
            }
            } catch (InterruptedException e) {
            System.out.println("Finished.");
            }
        }
        }

    private static class EventCallback implements IotHubEventCallback
    {
        public void execute(IotHubStatusCode status, Object context) {
            System.out.println("IoT Hub responded to message with status: " + status.name());

            if (context != null) {
            synchronized (context) {
                context.notify();
            }
            }
        }
    }

    private static class TelemetryDataPoint {
        public String deviceId;
        public double windSpeed;

        public String serialize() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}
