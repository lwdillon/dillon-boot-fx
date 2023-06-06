package org.dillon.fx.domain.server;

import lombok.Data;

import java.lang.management.ManagementFactory;

/**
 * JVM相关信息
 * 
 * @author ruoyi
 */
@Data
public class Jvm
{
    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;


}
