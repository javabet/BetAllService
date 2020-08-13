package com.wisp.game.core.random;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomHandler {

    public static RandomHandler Instance;

    private final Random random = new Random();

    public RandomHandler() {
        Instance = this;
        random.setSeed(System.currentTimeMillis());
    }


    /**
     * 包头不包尾
     * @param maxValue
     * @return
     */
    public int getRandomValue(int maxValue) {
        return this.random.nextInt(maxValue);
    }

    /**
     * 包头不包尾
     * @param minValu
     * @param maxValue
     * @return
     */
    public int getRandomValue(int minValu, int maxValue) {
        return this.random.nextInt(maxValue - minValu + 1) + minValu;
    }

    public int getRandomValue(double minValue, double maxValue) {

        return getRandomValue((int)maxValue - (int)minValue) + (int)minValue;
    }


}
