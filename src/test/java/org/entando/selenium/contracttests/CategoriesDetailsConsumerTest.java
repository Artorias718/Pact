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
import java.util.List;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "CategoriesDetailsProvider", port = "8080")
public class CategoriesDetailsConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTCategoriesPage dTCategoriesPage;

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
        PactDslResponse getCategoriesResponse = PactUtil.buildGetCategories(getProfileTypesResponse);
        PactDslResponse getCategoriesParentCodeResponse = PactUtil.buildGetCategoriesParentCode(getCategoriesResponse, "home");

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getCategoriesParentCodeResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLink("Configuration", "Categories");
        });
    }

    @Pact(provider = "CategoriesDetailsProvider", consumer = "CategoriesDetailsConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getCategoriesDetailsResponse = buildGetCategoriesDetails(builder);
        return getCategoriesDetailsResponse.toPact();
    }

    public static PactDslResponse buildGetCategoriesDetails(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Categories get details OPTIONS Interaction")
                .path("/entando/api/categories/category3")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Categories GET details Interaction")
                .path("/entando/api/categories/category3")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"code\":\"category3\",\"parentCode\":\"home\",\"titles\":{\"en\":\"Category3\",\"it\":\"Category3\"},\"fullTitles\":{\"en\":\"All / Category3\",\"it\":\"Generale / Category3\"},\"children\":[\"testcategory31\"],\"references\":{}},\"errors\":[],\"metaData\":{\"parentCode\":\"home\"}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        List<Kebab> foundedKebabs = dTCategoriesPage.getTable().getKebabsOnTable
                ("Category3", "Categories tree", "Actions");

        for(Kebab kebab : foundedKebabs){
            kebab.getClickable().click();
            Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
            kebab.getAction("Details").click();
        }
    }
}
