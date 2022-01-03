package com.react.spring.ReactSpringS3Application.bucket;

public enum BucketName {

    PROFILE_IMAGE("react-spring-s3-application");

    private final String bucketName;


    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
