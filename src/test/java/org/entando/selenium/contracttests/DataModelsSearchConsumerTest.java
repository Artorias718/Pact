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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "DataModelsSearchProvider", port = "8080")
public class DataModelsSearchConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTDataModelsPage dTDataModelsPage;

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

        PactDslResponse getDataTypesResponse = buildGetDataTypes(getProfileTypesResponse,1,10);
        PactDslResponse getDataModelsResponse = PactUtil.buildGetDataModels(getDataTypesResponse,1,10);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getDataModelsResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLink("Data", "Data Models");
        });
    }

    @Pact(provider = "DataModelsSearchProvider", consumer = "DataModelsSearchConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse searchAllDataModelsResponse = buildSearchAllDataModels(builder,"code","ASC",1,10);
        PactDslResponse searchDataModelsByTypeResponse = buildSearchDataModelsByTypeResponse(searchAllDataModelsResponse,"code","ASC",1,10, "type", "eq", "AAA");
        return searchDataModelsByTypeResponse.toPact();
    }

    private PactDslResponse buildSearchAllDataModels(PactDslWithProvider builder, String sort, String direction, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Models OPTIONS Interaction")
                .path("/entando/api/dataModels")
                .method("OPTIONS")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("direction", "\\w+", "" + direction)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize)
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Models DELETE Interaction")
                .path("/entando/api/dataModels")
                .method("GET")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("direction", "\\w+", "" + direction)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{}");
    }

    private PactDslResponse buildSearchDataModelsByTypeResponse(PactDslResponse builder, String sort, String direction, int page, int pageSize, String attribute, String operator, String value) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Models OPTIONS Interaction")
                .path("/entando/api/dataModels")
                .method("OPTIONS")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("direction", "\\w+", "" + direction)
                .matchQuery("filters[0].attribute", "\\w+", "" + attribute)
                .matchQuery("filters[0].operator", "\\w+", "" + operator)
                .matchQuery("filters[0].value", "\\w+", "" + value)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize)
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Models DELETE Interaction")
                .path("/entando/api/dataModels")
                .method("GET")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("direction", "\\w+", "" + direction)
                .matchQuery("filters[0].attribute", "\\w+", "" + attribute)
                .matchQuery("filters[0].operator", "\\w+", "" + operator)
                .matchQuery("filters[0].value", "\\w+", "" + value)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{}");
    }

    @Test
    public void runTest() throws InterruptedException {

        dTDataModelsPage.getSearchButton().click();
        dTDataModelsPage.setTypeSearchSelect("AAA");
        dTDataModelsPage.getSearchButton().click();
    }
}
