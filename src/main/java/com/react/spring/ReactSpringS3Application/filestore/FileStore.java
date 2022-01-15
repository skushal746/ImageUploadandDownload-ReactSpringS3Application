package com.react.spring.ReactSpringS3Application.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.react.spring.ReactSpringS3Application.bucket.BucketName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
            PutObjectResult imageUploadResult =  amazonS3.putObject(path, fileName, inputStream, metadata);
            System.out.println(imageUploadResult);
        } catch(AmazonServiceException exception) {
            throw new IllegalStateException("Failed to store file to Amazon S3", exception);
        }
    }

    public byte[] download(String path, String key) {
        try{
            S3Object imageObject = amazonS3.getObject(path, key);
            return IOUtils.toByteArray(imageObject.getObjectContent());
        }
        catch(AmazonServiceException | IOException e){
            throw new IllegalStateException("Failed to download the image file");
        }
    }
    
    public String getFirstImageLinkForUser(String bucketName, UUID userProfileId)
    {
    	try
    	{
    		ListObjectsV2Request req = new ListObjectsV2Request()
    				.withBucketName(BucketName.PROFILE_IMAGE.getBucketName())
    				.withPrefix( String.format("%s",userProfileId))
    				.withMaxKeys(2);
    		ListObjectsV2Result result;
    		result = amazonS3.listObjectsV2(req);
    		
    		for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
    			return objectSummary.getKey().substring(objectSummary.getKey().lastIndexOf("/") + 1);
            }

    		/*
    		ObjectListing listing = amazonS3.listObjects( path, "" );
        	List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        	if(!summaries.isEmpty())
        		return summaries.get(0).getKey();
        	*/
    	}
    	catch(SdkClientException amazonServiceException)
    	{
    		System.out.println("Exception occured while fetching the first image link " + amazonServiceException);
    		throw amazonServiceException;
    	}
    	return null;    	
    }

}
