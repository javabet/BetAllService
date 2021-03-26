//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wisp.core.random;

import java.util.ArrayList;
import java.util.List;

public class RandomParams<T> {
    private List<RandomParam<T>> list = new ArrayList();

    public RandomParams() {
    }

    public static <T> RandomParams<T> createInstance() {
        return new RandomParams();
    }

    List<RandomParam<T>> list() {
        return this.list;
    }

    public RandomParams<T> add(T data, int count) {
        RandomParam<T> param = new RandomParam();
        param.data = data;
        param.count = count;
        this.list.add(param);
        return this;
    }

    static class RandomParam<T> {
        int count;
        T data;

        RandomParam() {
        }
    }
}
