package org.finra.rmcs.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.constants.Constants;

@Slf4j
public class Util {

    @SneakyThrows
    public static String getPassword(String passwordKey, String component) {
        FideliusClient fideliusClient = new FideliusClient();
        String credPassword =
                fideliusClient.getCredential(
                        passwordKey,
                        Constants.AGS,
                        System.getenv(Constants.SPRING_PROFILES_ACTIVE),
                        component,
                        null);
        if (StringUtils.isBlank(credPassword)) {
            log.info("Failed to retrieve password of {} from Fidelius", passwordKey);
        } else {
            log.info("Successfully retrieved password of {} from Fidelius", passwordKey);
        }
        return credPassword;
    }

}
