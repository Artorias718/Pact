
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
import org.entando.selenium.utils.Utils;
import org.entando.selenium.utils.UsersTestBase;
import org.entando.selenium.utils.pageParts.Kebab;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "FileBrowserDeleteProvider", port = "8080")
public class FileBrowserDeleteConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTFileBrowserPage dTFileBrowserPage;

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

        PactDslResponse getFileBrowserListResponse = buildGetFileBrowserList(getProfileTypesResponse);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getFileBrowserListResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("Configuration", "File browser");
        });
    }

    @Pact(provider = "FileBrowserDeleteProvider", consumer = "FileBrowserDeleteConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getDirectoryResponse = buildGetDirectory(builder, "false");
        PactDslResponse deleteDirectoryResponse = buildDeleteDirectory(getDirectoryResponse, "false","pactfolder");
        return deleteDirectoryResponse.toPact();
    }

    private PactDslResponse buildGetFileBrowserList(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The filebrowser OPTIONS Interaction")
                .path("/entando/api/fileBrowser")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The filebrowser GET Interaction")
                .path("/entando/api/fileBrowser")
                .method("GET");
        return standardResponse(request, "{\"payload\":[{\"name\":\"public\",\"lastModifiedTime\":null,\"size\":null,\"directory\":true,\"path\":\"public\",\"protectedFolder\":false},{\"name\":\"protected\",\"lastModifiedTime\":null,\"size\":null,\"directory\":true,\"path\":\"protected\",\"protectedFolder\":true}],\"errors\":[],\"metaData\":{\"prevPath\":null,\"currentPath\":\"\"}}");
    }

    private PactDslResponse buildGetDirectory(PactDslWithProvider builder, String protectedFolder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The directory OPTIONS Interaction")
                .path("/entando/api/fileBrowser")
                .method("OPTIONS")
                .matchQuery("protectedFolder", "\\w+", "" + protectedFolder)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The directory GET Interaction")
                .path("/entando/api/fileBrowser")
                .method("GET")
                .matchQuery("protectedFolder", "\\w+", "" + protectedFolder);

        return standardResponse(request, "{\"payload\":[{\"name\":\"cms\",\"lastModifiedTime\":\"2018-10-09 09:43:48\",\"size\":4096,\"directory\":true,\"path\":\"cms\",\"protectedFolder\":false},{\"name\":\"pactfolder\",\"lastModifiedTime\":\"2018-10-10 16:03:42\",\"size\":4096,\"directory\":true,\"path\":\"pactfolder\",\"protectedFolder\":false},{\"name\":\"plugins\",\"lastModifiedTime\":\"2018-10-09 08:56:37\",\"size\":4096,\"directory\":true,\"path\":\"plugins\",\"protectedFolder\":false},{\"name\":\"static\",\"lastModifiedTime\":\"2018-10-08 17:03:44\",\"size\":4096,\"directory\":true,\"path\":\"static\",\"protectedFolder\":false}],\"errors\":[],\"metaData\":{\"protectedFolder\":false,\"prevPath\":null,\"currentPath\":\"\"}}");

    }

    private PactDslResponse buildDeleteDirectory(PactDslResponse builder, String protectedFolder, String currentPath) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The directory delete OPTIONS Interaction")
                .path("/entando/api/fileBrowser/directory")
                .method("OPTIONS")
                .matchQuery("protectedFolder", "\\w+", "" + protectedFolder)
                .matchQuery("currentPath", currentPath)
                .headers("Access-control-request-method", "DELETE")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The directory DELETE Interaction")
                .path("/entando/api/fileBrowser/directory")
                .method("DELETE")
                .matchQuery("protectedFolder", "\\w+", "" + protectedFolder)
                .matchQuery("currentPath",  currentPath);

        return standardResponse(request, "{}");
    }

    @Test
    public void runTest() throws InterruptedException {

        dTFileBrowserPage.getTable().getLinkOnTable( "public", 0, 0).click();
        Kebab kebab = dTFileBrowserPage.getTable().getKebabOnTable("pactfolder", 0, 3);
        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Delete").click();
        Utils.waitUntilIsVisible(driver, dTFileBrowserPage.getDeleteButton());
        dTFileBrowserPage.getDeleteButton().click();
        Utils.waitUntilIsVisible(driver, dTFileBrowserPage.getUploadButton());
        Utils.waitUntilIsVisible(driver, dTFileBrowserPage.getTableBody());
    }
}
