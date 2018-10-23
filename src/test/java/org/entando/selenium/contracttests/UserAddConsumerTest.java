/*
Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
This library is free software; you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation; either version 2.1 of the License, or (at your option)
any later version.
This library is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details.
 */
package org.entando.selenium.contracttests;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.PactDslRequestWithPath;
import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import org.entando.selenium.pages.*;
import org.entando.selenium.utils.UsersTestBase;
import org.entando.selenium.utils.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserAddProvider", port = "8080")
public class UserAddConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTUsersPage dTUsersPage;

    @Autowired
    public DTUserAddPage dTUserAddPage;

    @Autowired
    public DTUserProfileTypePage dtUserProfileTypePage;

    @Autowired
    public DTUserProfileTypeAddPage dtUserProfileTypeAddPage;

    @BeforeAll
    public void setupSessionAndNavigateToUserManagement (){
        PactDslWithProvider builder = ConsumerPactBuilder.consumer("LoginConsumer").hasPactWith("LoginProvider");
        PactDslResponse accessTokenResponse = buildGetAccessToken(builder);
        PactDslResponse getUsersResponse = PactUtil.buildGetUsers(accessTokenResponse,1,1);
        getUsersResponse = PactUtil.buildGetUsers(getUsersResponse,1,10);
        PactDslResponse getPagesResponse = buildGetPages(getUsersResponse);
        PactDslResponse getPageStatusResponse = buildGetPageStatus(getPagesResponse);
        PactDslResponse getWidgetsResponse = buildGetWidgets(getPageStatusResponse);
        PactDslResponse getGroupsResponse = buildGetGroups(getWidgetsResponse);
        PactDslResponse getPageModelsResponse = buildGetPageModels(getGroupsResponse);
        PactDslResponse getLanguagesResponse = buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = PactUtil.buildGetProfileTypes(getLanguagesResponse);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(addStandardHeaders(getProfileTypesResponse).toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("User Management", "Users");
            Utils.waitUntilIsVisible(driver, dTUsersPage.getAddButton());
        });
    }

    @Pact(provider = "UserAddProvider", consumer = "UserAddConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse postUserResponse = buildPostUser(builder);
        PactDslResponse getUsersResponse = buildGetUsers(postUserResponse,1,1);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getUsersResponse);
        return getProfileTypesResponse.toPact();
    }

    @Pact(provider = "UserAddProvider", consumer = "UserAddConsumer")
    public RequestResponsePact createPact2(PactDslWithProvider builder) {
        PactDslResponse postUserResponse = buildPostUser2(builder);
        PactDslResponse getProfileTypesResponse = buildOnlyGetProfileTypes(postUserResponse);
        return getProfileTypesResponse.toPact();
    }

    private PactDslResponse buildGetUsers(PactDslResponse builder, int page, int pageSize) {
        String cane = "bau"; //\"username\":\""+cane+"\",

        PactDslRequestWithPath request = builder.uponReceiving("The User Query GET Interaction")
                .path("/entando/api/users")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        return standardResponse(request, "{\"payload\":[{\"username\":\"UNIMPORTANT\",\"registration\":\""+LocalDate+"\",\"lastLogin\":null,\"lastPasswordChange\":null,\"status\":\"inactive\",\"accountNotExpired\":true,\"credentialsNotExpired\":true,\"profileType\":null,\"profileAttributes\":{},\"maxMonthsSinceLastAccess\":-1,\"maxMonthsSinceLastPasswordChange\":-1}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":2,\"totalItems\":2,\"sort\":\"username\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildPostUser(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder.given("there is no user exists with the username UNIMPORTANT")
                .uponReceiving("The User Add OPTIONS Interaction")
                .path("/entando/api/users/")
                .method("OPTIONS");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The User Add POST Interaction")
                .path("/entando/api/users/")
                .method("POST")
                .body("{\"username\": \"UNIMPORTANT\", \"password\": \"adminadmin\", \"passwordConfirm\": \"adminadmin\", \"profileType\": \"PFL\"}");
        return standardResponse(request, "{\"payload\":{\"username\":\"UNIMPORTANT\",\"registration\":\""+LocalDate+"\",\"lastLogin\":null,\"lastPasswordChange\":null,\"status\":\"inactive\",\"accountNotExpired\":true,\"credentialsNotExpired\":true,\"profileType\":null,\"profileAttributes\":{},\"maxMonthsSinceLastAccess\":-1,\"maxMonthsSinceLastPasswordChange\":-1},\"errors\":[],\"metaData\":{}}");
    }

    private PactDslResponse buildPostUser2(PactDslWithProvider builder) {
        PactDslRequestWithPath  request = builder.given("a user exists with the username UNIMPORTANT")
                .uponReceiving("The User Add POST Interaction")
                //TODO add the expectation for the incoming data
                .path("/entando/api/users/")
                .method("POST")
                .body("{\"username\": \"UNIMPORTANT\", \"password\": \"adminadmin\", \"passwordConfirm\": \"adminadmin\", \"profileType\": \"PFL\"}");
            return conflictResponse(request, "{payload: [], errors: [{code: \"1\", message: \"The user 'UNIMPORTANT' already exists\"}], metaData: {}}");
    }

    private PactDslResponse buildGetProfileTypes(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The ProfileTypes OPTIONS Interaction")
                .path("/entando/api/profileTypes")
                .method("OPTIONS")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "\\d+", "" + 10);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The ProfileTypes GET Interaction")
                .path("/entando/api/profileTypes")
                .method("GET")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "\\d+","" + 10);
        return standardResponse(request, "{\"payload\":[{\"code\":\"PFL\",\"name\":\"Default user profile\",\"status\":\"0\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildOnlyGetProfileTypes(PactDslResponse builder) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The ProfileTypes GET Interaction")
                .path("/entando/api/profileTypes")
                .method("GET")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "\\d+","" + 10);
        return standardResponse(request, "{\"payload\":[{\"code\":\"PFL\",\"name\":\"Default user profile\",\"status\":\"0\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private void test(){
        dTUsersPage.getAddButton().click();
        Utils.waitUntilIsVisible(driver, dTUserAddPage.getPageTitle());
        dTUserAddPage.setUsernameField("UNIMPORTANT");
        dTUserAddPage.setPasswordField("adminadmin");
        dTUserAddPage.setPasswordConfirmField("adminadmin");
        dTUserAddPage.getProfileTypeSelect().selectByVisibleText("Default user profile");
        Assert.assertTrue(dTUserAddPage.getSaveButton().isEnabled());
        dTUserAddPage.getSaveButton().click();
        Utils.waitUntilIsDisappears(driver, dTUsersPage.spinnerTag);
    }

    @Test
    @PactTestFor(pactMethod = "createPact2")
    public void runTest() throws InterruptedException {
        test();
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    public void runTest2() throws InterruptedException {
        test();
    }
}


