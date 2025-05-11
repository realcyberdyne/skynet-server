package com.cyberdyne.skynet.connection.manager.Services.ResourceMgmt;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class ResourceMgmt
{

    public static String GetCpuUsage()
    {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        // Get the CPU usage of the whole system
        double systemCpuLoad = osBean.getSystemCpuLoad();

        // Convert to percentage
        if (systemCpuLoad >= 0) {
            int cpuUsagePercent = (int) (systemCpuLoad * 100);
            //System.out.println("CPU Usage: " + cpuUsagePercent + "%");
            return cpuUsagePercent+"%";
        } else {
//            System.out.println("CPU Usage: Not available");
            return "CPU Usage: Not available";
        }

    }



    public static String GetRamUsage()
    {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();   // JVM total memory
        long freeMemory = runtime.freeMemory();     // JVM free memory
        long usedMemory = totalMemory - freeMemory;

//        System.out.printf("Used Memory: %.2f MB\n", usedMemory / (1024.0 * 1024));
//        System.out.printf("Free Memory: %.2f MB\n", freeMemory / (1024.0 * 1024));
//        System.out.printf("Total Memory: %.2f MB\n", totalMemory / (1024.0 * 1024));

        return Math.round(usedMemory / (1024.0 * 1024))+"MB /"+Math.round(totalMemory / (1024.0 * 1024))+"MB";
    }

}
