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
        USER_PROFILES.add(new UserProfile(UUID.randomUUID(), "skushal746", null));
        USER_PROFILES.add(new UserProfile(UUID.randomUUID(), "ruchirmeena", null));
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }

}
