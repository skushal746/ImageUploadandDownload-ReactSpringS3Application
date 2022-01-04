package com.react.spring.ReactSpringS3Application.datastore;

import com.react.spring.ReactSpringS3Application.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<UserProfile>();

    static{
        USER_PROFILES.add(new UserProfile(UUID.fromString("061baf4e-2569-412d-9062-17b25e21047a"), "skushal746", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("84cc9768-3e39-4d6d-aa3f-9d9e6e6427a2"), "ruchirmeena", null));
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }

}
