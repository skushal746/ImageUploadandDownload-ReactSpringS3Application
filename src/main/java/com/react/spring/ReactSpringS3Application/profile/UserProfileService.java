package com.react.spring.ReactSpringS3Application.profile;

import com.react.spring.ReactSpringS3Application.bucket.BucketName;
import com.react.spring.ReactSpringS3Application.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;
    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfile() {
        return userProfileDataAccessService.getUserProfiles();
    }

    void uploadUserProfileImage(UUID userProfileId, MultipartFile file) throws IllegalStateException {

        /*
        1. Check if the file is empty
         */
        if(file == null || file.isEmpty())
        {
            throw new IllegalStateException("File is empty or not found");
        }

        /*
        2. If file is an image
         */
        if(!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_PNG.getMimeType(), ContentType.IMAGE_GIF.getMimeType()).contains(file.getContentType()))
        {
            throw new IllegalStateException("File uploaded needs to be of image type");
        }

        /*
        3. the user exists in our database
        Could have used stream apis
         */
        List<UserProfile> userProfiles = userProfileDataAccessService.getUserProfiles();
        UserProfile userProfileOfTheUser = null;
        for(UserProfile userProfile : userProfiles)
        {
            if(userProfile.getUserProfileId().equals(userProfileId))
                userProfileOfTheUser = userProfile;
        }

        if(userProfileOfTheUser==null)
        {
            throw new IllegalStateException("UUID does not belong to any user currently in the system");
        }

        /*
        4. grab metadata from file if any
         */
        Map<String, String> metaData = new HashMap<String, String>();
        metaData.put("Content-Type", file.getContentType());
        metaData.put("Content-Length", String.valueOf(file.getSize()));


        /*
        5. Store the image in s3 and update database with s3 image link
         */

        System.out.println(BucketName.PROFILE_IMAGE);
        System.out.println(BucketName.PROFILE_IMAGE.getBucketName());

        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), userProfileOfTheUser.getUserProfileId());
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
        try {
            fileStore.save(path, fileName, Optional.of(metaData), file.getInputStream());
            userProfileOfTheUser.setUserProfileImageLink(fileName);
        } catch (IOException e) {
            throw new IllegalStateException("Exception while saving the file in the S3");
        }
    }

    public byte[] downloadUserProfilemage(UUID userProfileId) {

        List<UserProfile> userProfiles = userProfileDataAccessService.getUserProfiles();
        UserProfile userProfileOfTheUser = null;
        for(UserProfile userProfile : userProfiles)
        {
            if(userProfile.getUserProfileId().equals(userProfileId))
                userProfileOfTheUser = userProfile;
        }

        if(userProfileOfTheUser==null)
        {
            throw new IllegalStateException("UUID does not belong to any user currently in the system");
        }

        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                userProfileOfTheUser.getUserProfileId());

        return userProfileOfTheUser.getUserProfileImageLink()
        		.map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }
}
