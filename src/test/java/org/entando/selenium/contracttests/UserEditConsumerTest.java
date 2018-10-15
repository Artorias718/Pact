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
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.support.ui.Sleeper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static java.lang.Thread.sleep;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserEditProvider", port = "8080")
public class UserEditConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTUsersPage dTUsersPage;

    @Autowired
    public DTUserEditPage dTUserEditPage;

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
        PactDslResponse getProfileTypesResponse = PactUtil.buildGetProfileTypes(getLanguagesResponse);
        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getProfileTypesResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("User Management", "Users");
        });
    }

    @Pact(provider = "UserEditProvider", consumer = "UserEditConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getUsersResponse = buildGetUsers(builder,1,10);
        PactDslResponse getUserToPutResponse = buildGetUserToPut(getUsersResponse);
        PactDslResponse putUserResponse = buildPutUser(getUserToPutResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(putUserResponse);
        return getProfileTypesResponse.toPact();
    }

    public static PactDslResponse buildPutUser(PactDslResponse builder ){
        PactDslRequestWithPath request = builder.uponReceiving("The User put PUT Interaction")
                .path("/entando/api/users/UNIMPORTANT")
                .method("PUT");
        return standardResponse(request, "{\"payload\":[{\"password\":\"1.000755E7\",\"passwordConfirm\":\"1.000755E7\",\"reset\":\"true\"}]}");
    }

    public static PactDslResponse buildGetUsers(PactDslWithProvider builder, int page, int pageSize) {

        PactDslRequestWithPath request = builder.uponReceiving("The User Query GET Interaction")
                .path("/entando/api/users")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"username\":\"UNIMPORTANT\",\"registration\":\"2018-08-31 00:00:00\",\"lastLogin\":null,\"lastPasswordChange\":null,\"status\":\"active\",\"accountNotExpired\":true,\"credentialsNotExpired\":true,\"profileType\":null,\"profileAttributes\":{},\"maxMonthsSinceLastAccess\":-1,\"maxMonthsSinceLastPasswordChange\":-1}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":1,\"totalItems\":1,\"sort\":\"username\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetUserToPut(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The User Query OPTIONS Interaction")
                .path("/entando/api/users/UNIMPORTANT")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The user to put request")
                .path("/entando/api/users/UNIMPORTANT")
                .method("GET");
        return standardResponse(request, "{\"payload\": {\"username\": \"UNIMPORTANT\", \"registration\": \"2018-09-18 00:00:00\", \"lastLogin\": null, \"lastPasswordChange\": null}}");
    }

    public static PactDslResponse buildGetProfileTypes(PactDslResponse builder) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The ProfileTypes GET Interaction")
                .path("/entando/api/profileTypes")
                .method("GET")
                .matchQuery("page", "\\d+")
                .matchQuery("pageSize", "\\d+");
        return standardResponse(request, "{\"payload\":[{\"code\":\"PFL\",\"name\":\"Default user profile\",\"status\":\"0\"},{\"code\":\"1DF\",\"name\":\"1SeleniumTest_DontTouch\",\"status\":\"0\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"name\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        Kebab kebab = dTUsersPage.getTable().getKebabOnTable("UNIMPORTANT", usersTableHeaderTitles.get(0), usersTableHeaderTitles.get(4));
        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        sleep(200);
        kebab.getAction("Edit").click();
        dTUserEditPage.setPassword("password");
        dTUserEditPage.setPasswordConfirm("password");
        dTUserEditPage.getResetSwitch().setOn();
        dTUserEditPage.getSaveButton().click();
    }
}
