package com.react.spring.ReactSpringS3Application.datastore;

import com.react.spring.ReactSpringS3Application.bucket.BucketName;
import com.react.spring.ReactSpringS3Application.filestore.FileStore;
import com.react.spring.ReactSpringS3Application.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<UserProfile>();

    private FileStore fileStore;
    
    public FakeUserProfileDataStore(FileStore fileStore) {
    	this.fileStore = fileStore;
	}
    
    static{
        USER_PROFILES.add(new UserProfile(UUID.fromString("061baf4e-2569-412d-9062-17b25e21047a"), "skushal746", null, "This user is simply awesome. He loves watching movies. Also in his free time he likes to play various sports"));
        USER_PROFILES.add(new UserProfile(UUID.fromString("84cc9768-3e39-4d6d-aa3f-9d9e6e6427a2"), "ruchirmeena", null, "This user is also an amazing person. He likes driving car."));
    }
    
    @PostConstruct
    private void init() {
    	System.out.println("This is the code execute on server start up");
    	
    	for(UserProfile userProfile: USER_PROFILES)
    	{
        	String userProfileImageLink = fileStore.getFirstImageLinkForUser(BucketName.PROFILE_IMAGE.getBucketName(), userProfile.getUserProfileId());
        	userProfile.setUserProfileImageLink(userProfileImageLink);
    	}    	
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }

}
