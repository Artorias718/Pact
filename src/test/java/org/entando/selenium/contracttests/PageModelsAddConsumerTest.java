
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
@PactTestFor(providerName = "PageModelsAddProvider", port = "8080")
public class PageModelsAddConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTPageModelsPage dTPageModelsPage;

    @Autowired
    public DTPageModelsAddPage dTPageModelsAddPage;

    @BeforeAll
    public void setupSessionAndNavigateToUserManagement (){
        PactDslWithProvider builder = ConsumerPactBuilder.consumer("LoginConsumer").hasPactWith("LoginProvider");
        PactDslResponse accessTokenResponse = buildGetAccessToken(builder);
        PactDslResponse getUsersResponse = PactUtil.buildGetUsers(accessTokenResponse,1,1);
        getUsersResponse = PactUtil.buildGetUsers(getUsersResponse,1,10);
        PactDslResponse getPagesResponse = buildGetPages(getUsersResponse);
        PactDslResponse getPageStatusResponse = buildGetPageStatus(getPagesResponse);
        PactDslResponse getWidgetsDashResponse = PactUtil.buildGetWidgets(getPageStatusResponse);
        PactDslResponse getGroupsResponse = buildGetGroups(getWidgetsDashResponse);
        PactDslResponse getPageModelsResponse = PactUtil.buildGetPageModels(getGroupsResponse);
        PactDslResponse getLanguagesResponse = buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getLanguagesResponse);

        PactDslResponse getPageModelsListResponse = PactUtil.buildGetPageModelsList(getProfileTypesResponse, 1,10);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getPageModelsListResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("UX Patterns", "Page Models");
        });
    }

    @Pact(provider = "PageModelsAddProvider", consumer = "PageModelsAddConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {

        PactDslResponse postPageModelResponse = buildPostPageModel(builder);
        PactDslResponse getPageModelsList = buildGetPageModelsList(postPageModelResponse,1,10);

        return getPageModelsList.toPact();
    }

    private PactDslResponse buildGetPageModelsList(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The page models list GET Interaction")
                .path("/entando/api/pageModels")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize);
        return standardResponse(request, "{\"payload\":{\"code\":\"1SLNM_TEST_1467\",\"descr\":\"1SLNM_TEST_1467\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"SeleniumCell\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}},\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":10,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildPostPageModel(PactDslWithProvider builder){
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The page models add OPTIONS Interaction")
                .path("/entando/api/pageModels")
                .method("OPTIONS")
                .headers("Access-control-request-method", "POST")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The page models add POST Interaction")
                .path("/entando/api/pageModels")
                .method("POST");
        return standardResponse(request, "{\"payload\":{\"code\":\"PCT\",\"descr\":\"PCT\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"PactCell\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}},\"errors\":[],\"metaData\":{}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        dTPageModelsPage.getAddButton().click();
        Utils.waitUntilIsVisible(driver, dTPageModelsAddPage.getSaveButton());
        dTPageModelsAddPage.setCodeField("PCT");
        dTPageModelsAddPage.setNameField("PCT");
        dTPageModelsAddPage.clearJsonConfigurationField();
        dTPageModelsAddPage.setJsonConfigurationField("{\n" +
                "  \"frames\": [\n" +
                "    {\n" +
                "      \"pos\": 0,\n" +
                "      \"descr\": \"PactCell\",\n" +
                "      \"mainFrame\": false,\n" +
                "      \"defaultWidget\": null,\n" +
                "      \"sketch\": null\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        dTPageModelsAddPage.setTemplateField("<>");
        dTPageModelsAddPage.getSaveButton().click();
    }
}
