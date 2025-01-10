package com.reliaquest.api.utility;

import java.util.UUID;

public class ValidationUtils {

    // This is to check UUID validity
    public static boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid); // Throws an exception if not a valid UUID
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
