package com.example.common.exception;

/**
 * 商品のキャッシュに矛盾があることを表す例外
 */
public class GoodsCacheContradictionException extends RuntimeException {

    public GoodsCacheContradictionException(String message) {
        super(message);
    }

    public GoodsCacheContradictionException(String message, Throwable cause) {
        super(message, cause);
    }
}
