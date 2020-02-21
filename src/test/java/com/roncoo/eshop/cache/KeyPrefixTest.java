package com.roncoo.eshop.cache;

import com.roncoo.eshop.cache.service.keys.ProductKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyPrefixTest {
    public static void main(String[] args) {
        ProductKey productInfo = ProductKey.productInfo;
        log.info(productInfo.generateKey("22"));
    }
}
