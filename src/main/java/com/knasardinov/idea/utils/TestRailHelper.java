package com.knasardinov.idea.utils;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.knasardinov.idea.api.TestRailClient;
import com.knasardinov.idea.api.TestRailClientBuilder;
import lombok.extern.log4j.Log4j;
import org.awaitility.Awaitility;
import org.fest.assertions.Assertions;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j
public class TestRailHelper {

    public static final String TEST_RAIL_CRED_NAME = "testRailCreds";
    public static final String TEST_RAIL_ENDPOINT_NAME = "testRailEndpoint";
    public static final int FIRST_USER_ID = 1;

    public boolean validateTestRailCredentials(String login, String password, String endPoint){
        try {
            TestRailClient testRail = new TestRailClientBuilder(endPoint, login, password).build();
            Awaitility.await().atMost(10, TimeUnit.SECONDS)
                    .pollInterval(1, TimeUnit.SECONDS)
                    .untilAsserted(() -> Assertions
                            .assertThat(testRail.getUser(FIRST_USER_ID).getId() == FIRST_USER_ID)
                            .isTrue());

            CredentialAttributes attributesPassword = new CredentialAttributes(TEST_RAIL_CRED_NAME, login,
                    this.getClass(), false);
            Credentials saveCredentialsPassword = new Credentials(attributesPassword.getUserName(), password);
            PasswordSafe.getInstance().set(attributesPassword, saveCredentialsPassword);

            CredentialAttributes attributesEndpoint = new CredentialAttributes(TEST_RAIL_ENDPOINT_NAME, login,
                    this.getClass(), false);
            Credentials saveCredentialsEndpoint = new Credentials(attributesEndpoint.getUserName(), endPoint);
            PasswordSafe.getInstance().set(attributesEndpoint, saveCredentialsEndpoint);
        } catch (Exception e){
            log.info(e.getMessage());
            return false;
        }
        return true;
    }

    public static Optional<String> getUsername(){
        CredentialAttributes attributes = new CredentialAttributes(TEST_RAIL_CRED_NAME);
        Credentials creds = PasswordSafe.getInstance().get(attributes);
        try {
            return Optional.ofNullable(creds.getUserName());
        } catch (NullPointerException e){
            log.info("There is no credentials in the system");
        }
        return null;
    }

    public static Optional<String> getPassword(){
        CredentialAttributes attributes = new CredentialAttributes(TEST_RAIL_CRED_NAME);
        Credentials creds = PasswordSafe.getInstance().get(attributes);
        try {
            return Optional.ofNullable(creds.getPasswordAsString());
        } catch (NullPointerException e){
            log.info("There is no credentials in the system");
        }
        return null;
    }

    public static Optional<String> getEndpoint(){
        CredentialAttributes attributes = new CredentialAttributes(TEST_RAIL_ENDPOINT_NAME);
        Credentials creds = PasswordSafe.getInstance().get(attributes);
        try {
            return Optional.ofNullable(creds.getPasswordAsString());
        } catch (NullPointerException e){
            log.info("There is no endpoint stored in the system");
        }
        return null;
    }
}
