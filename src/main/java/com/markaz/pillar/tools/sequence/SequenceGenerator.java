package com.markaz.pillar.tools.sequence;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

/**
 * Source: https://www.callicoder.com/distributed-unique-id-sequence-number-generator/
 *
 * Distributed Sequence Generator.
 * Inspired by Twitter snowflake: https://github.com/twitter/snowflake/tree/snowflake-2010
 *
 * This class should be used as a Singleton.
 * Make sure that you create and reuse a Single instance of SequenceGenerator per node in your distributed system cluster.
 */
@Service
public class SequenceGenerator {
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final int MAX_NODE_ID = (int)(Math.pow(2, NODE_ID_BITS) - 1);
    private static final int MAX_SEQUENCE = (int)(Math.pow(2, SEQUENCE_BITS) - 1);

    // Custom Epoch (Date and time (GMT): Thursday, January 9, 2020 12:00:00 AM)
    private static final long CUSTOM_EPOCH = 157852800000L;

    private final int nodeId;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence;

    // Create SequenceGenerator with a nodeId
    public SequenceGenerator(int nodeId) {
        if(nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(String.format("NodeId must be between %d and %d", 0, MAX_NODE_ID));
        }
        this.nodeId = nodeId;
    }

    // Let SequenceGenerator generate a nodeId
    public SequenceGenerator() {
        this.nodeId = createNodeId();
    }

    //    The resulting String is 64 bits, or 16 length
    public String nextId() {
        long currentTimestamp = timestamp();

        if(currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if(sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        long id = currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS);
        id |= (long) nodeId << SEQUENCE_BITS;
        id |= sequence;

        char[] str = BigInteger.valueOf(id).toString(26).toCharArray();
        for (int i = 0; i < str.length; i++) {
            str[i] += str[i] > '9' ? 10 : 49;
        }
        String result = new String(str);

        return String.format("%s-%s-%s", result.substring(0, 4), result.substring(4, 9), result.substring(9, 13));
    }


    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private static long timestamp() {
        return Instant.now().toEpochMilli() - CUSTOM_EPOCH;
    }

    // Wrapper and wait till next millisecond
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private int createNodeId() {
        int id;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for(int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                    }
                }
            }
            id = sb.toString().hashCode();
        } catch (Exception ex) {
            id = new SecureRandom().nextInt();
        }
        id = id & MAX_NODE_ID;
        return id;
    }
}
