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
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static java.lang.Thread.sleep;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserGroupDeleteProvider", port = "8080")
public class UserGroupDeleteConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTUserGroupsPage dTUserGroupsPage;

    @BeforeAll
    public void setupSessionAndNavigateToUserManagement (){
        PactDslWithProvider builder = ConsumerPactBuilder.consumer("LoginConsumer").hasPactWith("LoginProvider");
        PactDslResponse accessTokenResponse = buildGetAccessToken(builder);
        PactDslResponse getUsersResponse = PactUtil.buildGetUsers(accessTokenResponse,1,1);
        getUsersResponse = PactUtil.buildGetUsers(getUsersResponse,1,10);
        PactDslResponse getPagesResponse = buildGetPages(getUsersResponse);
        PactDslResponse getPageStatusResponse = buildGetPageStatus(getPagesResponse);
        PactDslResponse getWidgetsResponse = buildGetWidgets(getPageStatusResponse);
        PactDslResponse getGroupsResponse = PactUtil.buildGetGroups(getWidgetsResponse);
        PactDslResponse getGroupsListResponse = buildGetGroups(getGroupsResponse,"name",1,10);
        PactDslResponse getPageModelsResponse = buildGetPageModels(getGroupsListResponse);
        PactDslResponse getLanguagesResponse = buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getLanguagesResponse);
        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getProfileTypesResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("User Management", "Groups");
        });
    }

    @Pact(provider = "UserGroupDeleteProvider", consumer = "UserGroupDeleteConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse deleteGroupResponse = buildDeleteGroup(builder);
        return deleteGroupResponse.toPact();
    }

    private PactDslResponse buildGetGroups(PactDslResponse builder, String sort, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/groups")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .matchQuery("sort", "name", "" + sort)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The Groups GET Interaction")
                .path("/entando/api/groups")
                .method("GET")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        String json = "{\"payload\":[{\"code\":\"unimportant\",\"name\":\"UNIMPORTANT\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":10,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}";
        return standardResponse(request, json);
    }

    private PactDslResponse buildDeleteGroup(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/groups/unimportant")
                .method("OPTIONS").headers("Access-control-request-method", "DELETE")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The Groups POST Interaction")
                .path("/entando/api/groups/unimportant")
                .method("DELETE");
        String json = "{\"payload\":[{\"code\":\"unimportant\",\"name\":\"UNIMPORTANT\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":10,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}";
        return standardResponse(request, json);
    }

    @Test
    public void runTest() throws InterruptedException {

        Kebab kebab = dTUserGroupsPage.getTable().getKebabOnTable("UNIMPORTANT", groupsTableHeaderTitles.get(0), groupsTableHeaderTitles.get(2));
        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Delete").click();
        dTUserGroupsPage.getDeleteModalButton().click();
    }
}
