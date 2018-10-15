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

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserProfileTypeAddProvider", port = "8080")
public class UserProfileTypeAddConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTUsersPage dTUsersPage;

    @Autowired
    public DTUserAddPage dTUserAddPage;

    @Autowired
    public DTUserProfileTypePage dtUserProfileTypePage;

    @Autowired
    public DTUserRolesPage dtUserRolesPage;

    @Autowired
    public DTUserProfileTypeAddPage dtUserProfileTypeAddPage;

    @Autowired
    public DTUserRolesPage dTUserRolesPage;

    @Autowired
    public DTUserProfileTypePage dTUserProfileTypePage;

    @Autowired
    public DTUserProfileTypeAddPage dTUserProfileTypeAddPage;

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

        PactDslResponse getProfileTypesStatusResponse = buildGetProfileTypesStatus(getLanguagesResponse);
        PactDslResponse getProfileTypesListResponse = buildGetProfileTypesList(getProfileTypesStatusResponse, "name",1,10);
        PactDslResponse getProfileTypesAttributesResponse = buildGetProfileTypesAttributes(getProfileTypesListResponse,1,10);
        PactDslResponse getProfileTypesResponse = PactUtil.buildGetProfileTypes(getProfileTypesAttributesResponse);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getProfileTypesResponse.toPact(), config, mockServer -> {
            login();
            //Navigation to the page
            dTDashboardPage.SelectSecondOrderLinkWithSleep("User Management", "Profile types");
            Utils.waitUntilIsVisible(driver, dtUserProfileTypePage.getAddButton());
        });
    }

    @Pact(provider = "UserProfileTypeAddProvider", consumer = "UserProfileTypeAddConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse postProfileTypesResponse = buildPostProfileTypes(builder);
        PactDslResponse getProfileTypesAttributesResponse = buildGetProfileTypesAttributes(postProfileTypesResponse,1,0);
        PactDslResponse getProfileTypeForAttributesResponse = buildGetProfileTypeForAttributes(getProfileTypesAttributesResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getProfileTypeForAttributesResponse, "name",1,10);
        PactDslResponse getProfileTypesStatusResponse = buildGetProfileTypesStatus(getProfileTypesResponse);
        PactDslResponse putProfileTypesForAttributes = buildPutProfileTypeForAttributes(getProfileTypesStatusResponse);
        return putProfileTypesForAttributes.toPact();
    }

    public static PactDslResponse buildGetProfileTypes(PactDslResponse builder, String sort, int page, int pageSize) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/profileTypes")
                .method("GET")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"PCT\",\"name\":\"pact test pt\",\"status\":\"0\",\"attributes\":[]}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":2,\"totalItems\":11,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetProfileTypesStatus(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/profileTypesStatus")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/profileTypesStatus")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"ready\":\"UNP\",\"toRefresh\":[],\"refreshing\":[]},\"errors\":[],\"metaData\":{}}");
    }

    public static PactDslResponse buildGetProfileTypesAttributes(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/profileTypeAttributes")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize)
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/profileTypeAttributes")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[\"Boolean\",\"CheckBox\",\"Composite\",\"Date\",\"Enumerator\",\"EnumeratorMap\",\"Hypertext\",\"List\",\"Longtext\",\"Number\",\"Text\",\"ThreeState\",\"Timestamp\"],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":100,\"lastPage\":1,\"totalItems\":15,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildPostProfileTypes(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The User Add OPTIONS Interaction")
                .path("/entando/api/profileTypes")
                .method("OPTIONS")
                .headers("Access-control-request-method", "POST")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The User Add POST Interaction")
                //TODO add the expectation for the incoming data
                .path("/entando/api/profileTypes")
                .method("POST");
        return standardResponse(request, "{\"payload\":{\"code\":\"PCT\",\"name\":\"pact test pt\",\"status\":\"0\",\"attributes\":[]},\"errors\":[],\"metaData\":{}}");
    }

    public static PactDslResponse buildGetProfileTypeForAttributes(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/profileTypes/PCT")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/profileTypes/PCT")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"code\":\"PCT\",\"name\":\"pact test pt\",\"status\":\"0\",\"attributes\":[]},\"errors\":[],\"metaData\":{}}");
    }

    public static PactDslResponse buildPutProfileTypeForAttributes(PactDslResponse builder) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/profileTypes/PCT")
                .method("PUT");
        return standardResponse(request, "{\"payload\":{\"code\":\"PCT\",\"name\":\"pact test pt\",\"status\":\"0\",\"attributes\":[]},\"errors\":[],\"metaData\":{}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        String profileTypeName = "UNIMPORTANT";
        dTUserProfileTypePage.getAddButton().click();
        dTUserProfileTypeAddPage.setNameField(profileTypeName);
        dTUserProfileTypeAddPage.setCodeField(profileTypeCode);
        dTUserProfileTypeAddPage.getSaveButton().click();
        sleep(400);
        dTUserProfileTypeAddPage.getSaveButton().click();
    }
}
