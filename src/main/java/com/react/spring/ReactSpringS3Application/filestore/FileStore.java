package com.react.spring.ReactSpringS3Application.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {

    private final AmazonS3 amazonS3;

    @Autowired
    public FileStore(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void save(String path,
                     String fileName,
                     Optional<Map<String, String>> optionalMetadata,
                     InputStream inputStream) {

        ObjectMetadata metadata = new ObjectMetadata();
        optionalMetadata.ifPresent(map->{
            if(!map.isEmpty()){
                map.forEach(metadata::addUserMetadata);
            }
        });

        try{
            amazonS3.putObject(path, fileName, inputStream, metadata);
        } catch(AmazonServiceException exception) {
            throw new IllegalStateException("Failed to store file to Amazon S3", exception);
        }
    }

    public byte[] download(String path, String key) {

        try{
            S3Object imageObject = amazonS3.getObject(path, key);
            return IOUtils.toByteArray(imageObject.getObjectContent())
        }
        catch(AmazonServiceException | IOException e){
            throw new IllegalStateException("Failed to download the image file");
        }

    }
}
