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
import org.entando.selenium.utils.pageParts.ExpandableTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserRestrictionsProvider", port = "8080")
public class UserRestrictionsConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTUserRestrictionsPage dTUserRestrictionsPage;

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
        PactDslResponse getLanguagesResponse = PactUtil.buildGetLanguages(getPageModelsResponse);
        PactDslResponse getRoles = buildGetRoles(getLanguagesResponse, 1, 10);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getRoles);

        PactDslResponse getUserRestrictionsResponse = buildGetUserRestrictions(getProfileTypesResponse);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getUserRestrictionsResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLink("User Management", "User Restrictions");
        });
    }

    @Pact(provider = "UserRestrictionsProvider", consumer = "UserRestrictionsConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {

        PactDslResponse putUserRestrictionsResponse = buildPutUserRestrictions(builder);

        return putUserRestrictionsResponse.toPact();
    }

    public static PactDslResponse buildPutUserRestrictions(PactDslWithProvider builder) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The user restrictions PUT Interaction")
                .path("/entando/api/userSettings")
                .method("PUT");
        return standardResponse(request, "{\"payload\":{\"passwordAlwaysActive\":true,\"restrictionsActive\":false,\"enableGravatarIntegration\":false,\"lastAccessPasswordExpirationMonths\":0,\"maxMonthsPasswordValid\":0},\"errors\":[],\"metaData\":{}}");
    }

    public static PactDslResponse buildGetUserRestrictions(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The user restrictions OPTIONS Interaction")
                .path("/entando/api/userSettings")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The user restrictions GET Interaction")
                .path("/entando/api/userSettings")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"passwordAlwaysActive\":true,\"restrictionsActive\":false,\"enableGravatarIntegration\":false,\"lastAccessPasswordExpirationMonths\":0,\"maxMonthsPasswordValid\":0},\"errors\":[],\"metaData\":{}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        dTUserRestrictionsPage.getSaveButton().click();
    }
}
