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
import org.entando.selenium.utils.pageParts.Kebab;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "DataModelsEditProvider", port = "8080")
public class DataModelsEditConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTDataModelsPage dTDataModelsPage;

    @Autowired
    public DTDataModelsAddPage dTDataModelsAddPage;

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

    @Pact(provider = "DataModelsEditProvider", consumer = "DataModelsEditConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getDataModelToPutResponse = buildGetDataModelToPutResponse(builder);
        PactDslResponse getDataModelsResponse = buildGetDataModels(getDataModelToPutResponse,1,10);
        PactDslResponse getDataTypesResponse = buildGetDataTypes(getDataModelsResponse,1,10);
        PactDslResponse putDataModelsResponse = buildPutDataModels(getDataTypesResponse);
        return putDataModelsResponse.toPact();
    }

    private PactDslResponse buildGetDataModels(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The Data Models GET Interaction")
                .path("/entando/api/dataModels")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"modelId\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildPutDataModels(PactDslResponse builder) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The Data Models PUT Interaction")
                .path("/entando/api/dataModels/100")
                .method("PUT");
        return standardResponse(request, "{}");
    }

    private PactDslResponse buildGetDataModelToPutResponse(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Models OPTIONS Interaction")
                .path("/entando/api/dataModels/100")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Models GET Interaction")
                .path("/entando/api/dataModels/100")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"modelId\":\"100\",\"descr\":\"unimportant\",\"type\":\"AAA\",\"model\":\"unimportant\",\"stylesheet\":\"unimportant\"},\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"modelId\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        Utils.waitUntilIsVisible(driver, dTDataModelsPage.getNewButton());
        Kebab kebab = dTDataModelsPage.getTable().getKebabOnTable("100",
                "Id", "Actions");

        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Edit").click();
        Utils.waitUntilIsVisible(driver,dTDataModelsAddPage.getSaveButton());
        dTDataModelsAddPage.getSaveButton().click();
    }
}
