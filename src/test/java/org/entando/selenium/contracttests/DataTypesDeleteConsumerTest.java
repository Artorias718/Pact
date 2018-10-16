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
import static java.lang.Thread.sleep;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "DataTypesDeleteProvider", port = "8080")
public class DataTypesDeleteConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTDataTypesPage dTDataTypesPage;

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

        PactDslResponse getDataTypesResponse = PactUtil.buildGetDataTypes(getProfileTypesResponse,1,10);
        PactDslResponse getDataTypesStatus = PactUtil.buildGetDataTypesStatus(getDataTypesResponse);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getDataTypesStatus.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLink("Data", "Data Types");
            sleep(300);
        });
    }

    @Pact(provider = "DataTypesDeleteProvider", consumer = "DataTypesDeleteConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse deleteDataTypesResponse = buildDeleteDataTypes(builder);
        return deleteDataTypesResponse.toPact();
    }

    private PactDslResponse buildDeleteDataTypes(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Types delete OPTIONS Interaction")
                .path("/entando/api/dataTypes/SLN")
                .method("OPTIONS")
                .headers("Access-control-request-method", "DELETE");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Types DELETE Interaction")
                .path("/entando/api/dataTypes/SLN")
                .method("DELETE");
        return standardResponse(request, "{}");
    }

    @Test
    public void runTest() throws InterruptedException {


        Kebab kebab = dTDataTypesPage.getTable().getKebabOnTable("SLN",
                "Code", "Actions");

        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Delete").click();
        Utils.waitUntilIsVisible(driver, dTDataTypesPage.getDeleteModalButton());
        dTDataTypesPage.getDeleteModalButton().click();
    }
}
