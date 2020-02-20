package com.roncoo.eshop.cache.service.keys;

public class ProductKey extends BasePrefix {

    public ProductKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static ProductKey productInfo = new ProductKey(60 * 60 * 24 * 90, "productInfo:");
    public static ProductKey shopInfo = new ProductKey(60 * 60 * 24 * 90, "shopInfo:");
}
