package com.roncoo.eshop.cache;

import com.roncoo.eshop.cache.service.keys.ProductKey;

public class KeyPrefixTest {
    public static void main(String[] args) {
        ProductKey productInfo = ProductKey.productInfo;
        System.out.println(productInfo.generateKey("22"));
    }
}
