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
@PactTestFor(providerName = "DataTypesEditProvider", port = "8080")
public class DataTypesEditConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTDataTypesAddPage dTDataTypesAddPage;

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

    @Pact(provider = "DataTypesEditProvider", consumer = "DataTypesEditConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getDataTypesToPutResponse = buildGetDataTypesToPut(builder);
        PactDslResponse getDataTypesAttributesResponse = buildGetDataTypesAttributes(getDataTypesToPutResponse,1,0);
        PactDslResponse putDataTypesResponse = buildPutDataTypes(getDataTypesAttributesResponse);
        PactDslResponse getDataTypesResponse = buildGetDataTypes(putDataTypesResponse,1,10);
        PactDslResponse getDataTypesStatus = buildGetDataTypesStatus(getDataTypesResponse);
        return getDataTypesStatus.toPact();
    }

    private PactDslResponse buildGetDataTypesStatus(PactDslResponse builder) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The Data Types Status GET Interaction")
                .path("/entando/api/dataTypesStatus")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"ready\":[\"AAA\",\"SLN\",\"SLM\",\"SLL\"],\"toRefresh\":[],\"refreshing\":[]},\"errors\":[],\"metaData\":{}}");
    }

    private PactDslResponse buildGetDataTypesAttributes(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Types attribues get OPTIONS Interaction")
                .path("/entando/api/dataTypeAttributes")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize)
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Types attributes GET Interaction")
                .path("/entando/api/dataTypeAttributes")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[\"Boolean\",\"CheckBox\",\"Composite\",\"Date\",\"Enumerator\",\"EnumeratorMap\",\"Hypertext\",\"List\",\"Longtext\",\"Monolist\",\"Monotext\",\"Number\",\"Text\",\"ThreeState\",\"Timestamp\"],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":100,\"lastPage\":1,\"totalItems\":15,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildGetDataTypesToPut(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Type get OPTIONS Interaction")
                .path("/entando/api/dataTypes/PCT")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Type GET Interaction")
                .path("/entando/api/dataTypes/PCT")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"code\":\"PCT\",\"name\":\"pactDataType\",\"status\":\"0\",\"attributes\":[],\"viewPage\":null,\"listModel\":null,\"defaultModel\":null},\"errors\":[],\"metaData\":{}}");
    }

    private PactDslResponse buildPutDataTypes(PactDslResponse builder) {

        PactDslRequestWithPath request = builder.
                uponReceiving("The Data Type PUT Interaction")
                .path("/entando/api/dataTypes/PCT")
                .method("PUT");
        return standardResponse(request, "{\"code\":\"PCT\",\"name\":\"pactDataType\",\"status\":\"0\",\"attributes\":[],\"viewPage\":null,\"listModel\":null,\"defaultModel\":null},\"errors\":[],\"metaData\":{}}");
    }

    private PactDslResponse buildGetDataTypes(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The Data Types GET Interaction")
                .path("/entando/api/dataTypes")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"AAA\",\"name\":\"AAA\",\"status\":\"0\"},{\"code\":\"SLL\",\"name\":\"SeleniumTest_DontTouch2\",\"status\":\"0\"},{\"code\":\"SLM\",\"name\":\"SeleniumTest_DontTouch1\",\"status\":\"0\"},{\"code\":\"SLN\",\"name\":\"SeleniumTest_DontTouch\",\"status\":\"0\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":4,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        Kebab kebab = dTDataTypesPage.getTable().getKebabOnTable("PCT",
                "Code", "Actions");
        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Edit").click();
        Utils.waitUntilIsVisible(driver,dTDataTypesAddPage.getSaveButton());
        dTDataTypesAddPage.setName("pactDataType");
        dTDataTypesAddPage.getSaveButton().click();
    }
}
