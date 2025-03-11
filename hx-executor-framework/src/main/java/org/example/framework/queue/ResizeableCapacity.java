package org.example.framework.queue;

/**
 * 支持调整容量
 */
public interface ResizeableCapacity {

    void setCapacity(int capacity);

    int getCapacity();
}
