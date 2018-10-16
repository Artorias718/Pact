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
@PactTestFor(providerName = "FragmentsDetailsProvider", port = "8080")
public class FragmentsDetailsConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTFragmentPage dTFragmentPage;

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
        PactDslResponse getPageModelsResponse = buildGetPageModels(getGroupsResponse);
        PactDslResponse getLanguagesResponse = buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getLanguagesResponse);

        PactDslResponse getWidgetsResponse = buildGetWidgets(getProfileTypesResponse,1,0);
        PactDslResponse getFragmentsResponse = PactUtil.buildGetFragments(getWidgetsResponse,1,10);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getFragmentsResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("UX Patterns", "Fragments");
        });
    }

    @Pact(provider = "FragmentsDetailsProvider", consumer = "FragmentsDetailsConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getFragmentsResponse = buildGetFragment(builder);
        return getFragmentsResponse.toPact();
    }

    private PactDslResponse buildGetWidgets(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Widgets get OPTIONS Interaction")
                .path("/entando/api/widgets")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Widgets GET Interaction")
                .path("/entando/api/widgets")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"pct\",\"used\":0,\"titles\":{\"en\":\"pct\",\"it\":\"pct\"},\"typology\":\"user\",\"group\":\"gruppo_prova\",\"pluginCode\":null,\"pluginDesc\":null,\"guiFragments\":[],\"hasConfig\":false},\n{\"code\":\"ANN_Archive\",\"used\":0,\"titles\":{\"en\":\"Announcements - Archive\",\"it\":\"Bandi - Archivio\"},\"typology\":\"user\",\"group\":\"free\",\"pluginCode\":null,\"pluginDesc\":null,\"guiFragments\":[],\"hasConfig\":false}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":100,\"lastPage\":1,\"totalItems\":79,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildGetFragment(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Fragment OPTIONS Interaction")
                .path("/entando/api/fragments/PCT")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Fragment GET Interaction")
                .path("/entando/api/fragments/PCT")
                .method("GET");
        return standardResponse(request, " {\"payload\":{\"code\":\"PCT\",\"locked\":false,\"widgetType\":{\"code\":null,\"title\":null},\"pluginCode\":null},\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":12,\"totalItems\":116,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}\");\n");
    }

    @Test
    public void runTest() throws InterruptedException {

        Kebab kebab = dTFragmentPage.getTable().getKebabOnTable("PCT",
                "Code", "Actions");
        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Details for: PCT").click();
    }
}