package com.mycompany.app;

import com.microsoft.azure.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String connectionString = "HostName=adojeiothub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=83Gfbj8wfjsErg+2Fihv5gLuEFHz4YX8pT0wsoZNGn4=";
    private static final String deviceId = "myFirstJavaDevice";

    public static void main( String[] args ) throws IOException, URISyntaxException, Exception
    {
        System.out.println("Hello World!");

         RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);

        Device device = Device.createFromId(deviceId, null, null);
        try {
            device = registryManager.addDevice(device);
        } catch (IotHubException iote) {
            try {
                device = registryManager.getDevice(deviceId);
            } catch (IotHubException iotf) {
                iotf.printStackTrace();
            }
        }

        System.out.println("Device ID: " + device.getDeviceId());
        System.out.println("Device key: " + device.getPrimaryKey());
    }
}
