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

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserSearchProvider", port = "8080")
public class UserSearchConsumerTest extends UsersTestBase {

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
        PactDslResponse getSearchedUsersResponse = buildSearchUsers(getLanguagesResponse,"username","ASC", 1,10);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getSearchedUsersResponse);
        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getProfileTypesResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("User Management", "Users");
        });
    }

    @Pact(provider = "UserSearchProvider", consumer = "UserSearchConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse optionsSearchUsersResponse = buildSearchUsers(builder,"username", "ASC",1,10);
        PactDslResponse getSearchUsersResponse = buildSearchUsers(optionsSearchUsersResponse,"username", "ASC",1,10);
        return getSearchUsersResponse.toPact();
    }

    public static PactDslResponse buildGetUsers(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder.uponReceiving("The User Query GET Interaction")
                .path("/entando/api/users")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"username\":\"UNIMPORTANT\",\"registration\":\"2018-08-31 00:00:00\",\"lastLogin\":null,\"lastPasswordChange\":null,\"status\":\"active\",\"accountNotExpired\":true,\"credentialsNotExpired\":true,\"profileType\":null,\"profileAttributes\":{},\"maxMonthsSinceLastAccess\":-1,\"maxMonthsSinceLastPasswordChange\":-1}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":1,\"totalItems\":1,\"sort\":\"username\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildSearchUsers(PactDslWithProvider builder, String sort, String direction, int page, int pageSize  ) {
        PactDslRequestWithPath request = builder.uponReceiving("The User Query GET Interaction")
                .path("/entando/api/users")
                .method("OPTIONS")
                .matchQuery("filters[0].attribute", "\\w+", "username")
                .matchQuery("filters[0].operator", "\\w+", "like")
                .matchQuery("filters[0].value", "\\w+", "UNIMPORTANT")
                .matchQuery("sort", "\\w+", "username")
                .matchQuery("direction", "\\w+", "" + direction)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{}");
    }

    public static PactDslResponse buildSearchUsers(PactDslResponse builder, String sort, String direction, int page, int pageSize  ) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The User Add POST Interaction")
                .path("/entando/api/users/")
                .method("GET")
                .matchQuery("filters[0].attribute", "\\w+", "username")
                .matchQuery("filters[0].operator", "\\w+", "like")
                .matchQuery("filters[0].value", "\\w+", "UNIMPORTANT")
                .matchQuery("sort", "\\w+", "username")
                .matchQuery("direction", "\\w+", "" + direction)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        return standardResponse(request, "{}");
    }

    @Test
    public void runTest() throws InterruptedException {

        dTUsersPage.getSearchField().sendKeys("UNIMPORTANT");
        dTUsersPage.getSearchButton().click();
    }
}


