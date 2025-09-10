package com.bikeshare.service.validation;

import com.bikeshare.service.validation.impl.BuggyAgeCheck;
import com.bikeshare.service.validation.impl.CorrectAgeCheck;

public final class AgeCheckFactory {
    private AgeCheckFactory() {}

    /**
     * Choose implementation based on system property or env:
     * -Ddemo.buggy=true or env DEMO_BUGGY=true will return BuggyAgeCheck
     */
    public static AgeCheck create() {
        String prop = System.getProperty("demo.buggy", "");
        String env = System.getenv("DEMO_BUGGY");
        boolean buggy = "true".equalsIgnoreCase(prop) || "true".equalsIgnoreCase(env);
        return buggy ? new BuggyAgeCheck() : new CorrectAgeCheck();
    }
}
