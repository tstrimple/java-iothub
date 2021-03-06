package com.mycompany.app;

import java.io.IOException;
import com.microsoft.azure.eventhubs.*;
import com.microsoft.azure.servicebus.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.*;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.function.*;
import java.util.logging.*;

/**
 * Hello world!
 *

Device ID: myFirstJavaDevice
Device key: YXceGcXGCEXjPaTMD6Wxxg==

 */
public class App 
{
    private static String connStr = "Endpoint=sb://ihsuprodblres007dednamespace.servicebus.windows.net/;EntityPath=iothub-ehub-adojeiothu-102512-30eae7898b;SharedAccessKeyName=iothubowner;SharedAccessKey=83Gfbj8wfjsErg+2Fihv5gLuEFHz4YX8pT0wsoZNGn4=";
    
    public static void main( String[] args ) throws IOException
    {
        EventHubClient client0 = receiveMessages("0");
        EventHubClient client1 = receiveMessages("1");
        System.out.println("Press ENTER to exit.");
        System.in.read();
        try
        {
            client0.closeSync();
            client1.closeSync();
            System.exit(0);
        }
        catch (ServiceBusException sbe)
        {
            System.exit(1);
        }
    }

     private static EventHubClient receiveMessages(final String partitionId)
    {
    EventHubClient client = null;
    try {
        client = EventHubClient.createFromConnectionStringSync(connStr);
    }
    catch(Exception e) {
        System.out.println("Failed to create client: " + e.getMessage());
        System.exit(1);
    }
    try {
        client.createReceiver( 
        EventHubClient.DEFAULT_CONSUMER_GROUP_NAME,  
        partitionId,  
        Instant.now()).thenAccept(new Consumer<PartitionReceiver>()
        {
        public void accept(PartitionReceiver receiver)
        {
            System.out.println("** Created receiver on partition " + partitionId);
            try {
            while (true) {
                Iterable<EventData> receivedEvents = receiver.receive(100).get();
                int batchSize = 0;
                if (receivedEvents != null)
                {
                for(EventData receivedEvent: receivedEvents)
                {
                    System.out.println(String.format("Offset: %s, SeqNo: %s, EnqueueTime: %s", 
                    receivedEvent.getSystemProperties().getOffset(), 
                    receivedEvent.getSystemProperties().getSequenceNumber(), 
                    receivedEvent.getSystemProperties().getEnqueuedTime()));
                    System.out.println(String.format("| Device ID: %s", receivedEvent.getSystemProperties()));
                    System.out.println(String.format("| Message Payload: %s", new String(receivedEvent.getBody(),
                    Charset.defaultCharset())));
                    batchSize++;
                }
                }
                System.out.println(String.format("Partition: %s, ReceivedBatch Size: %s", partitionId,batchSize));
            }
            }
            catch (Exception e)
            {
            System.out.println("Failed to receive messages: " + e.getMessage());
            }
        }
        });
    }
    catch (Exception e)
    {
        System.out.println("Failed to create receiver: " + e.getMessage());
    }
    return client;
    }


}
