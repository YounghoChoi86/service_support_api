package com.tmoncorp.institute.exception;

public class InstituteNotFoundException extends Exception {
    public InstituteNotFoundException(String key) {
        super(key + "에 해당하는 기관을 찾을 수 없습니다.");
    }
}
