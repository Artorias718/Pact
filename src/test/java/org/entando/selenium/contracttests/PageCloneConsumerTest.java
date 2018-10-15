
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
import org.entando.selenium.utils.pageParts.ExpandableTable;
import org.entando.selenium.utils.pageParts.Kebab;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PageCloneProvider", port = "8080")
public class PageCloneConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTPageTreePage dTPageTreePage;

    @Autowired
    public DTPageAddPage dTPageAddPage;

    @BeforeAll
    public void setupSessionAndNavigateToUserManagement (){
        PactDslWithProvider builder = ConsumerPactBuilder.consumer("LoginConsumer").hasPactWith("LoginProvider");
        PactDslResponse accessTokenResponse = buildGetAccessToken(builder);
        PactDslResponse getUsersResponse = PactUtil.buildGetUsers(accessTokenResponse,1,1);
        getUsersResponse = PactUtil.buildGetUsers(getUsersResponse,1,10);
        PactDslResponse getPagesResponse = buildGetPages(getUsersResponse);
        PactDslResponse getPageStatusResponse = buildGetPageStatus(getPagesResponse);
        PactDslResponse getWidgetsDashResponse = PactUtil.buildGetWidgets(getPageStatusResponse);
        PactDslResponse getGroupsResponse = PactUtil.buildGetGroups(getWidgetsDashResponse);
        PactDslResponse getPageModelsResponse = PactUtil.buildGetPageModels(getGroupsResponse);
        PactDslResponse getLanguagesResponse = PactUtil.buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getLanguagesResponse);

        PactDslResponse getHomePage = buildGetHomePage(getProfileTypesResponse, "draft");
        PactDslResponse getParentCodeResponse = PactUtil.buildGetPagesParentCode(getHomePage, "homepage");

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getParentCodeResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("Page Designer", "Page Tree");
        });
    }

    @Pact(provider = "PageCloneProvider", consumer = "PageCloneConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getPagesResponse = buildGetHomePage(builder, "draft");
        PactDslResponse postPageClonedResponse = buildPostPage(getPagesResponse);
        PactDslResponse getParentCodeResponse = buildGetPagesParentCode(postPageClonedResponse, "homepage");
        PactDslResponse getLanguagesResponse = buildGetLanguages(getParentCodeResponse, 1, 0);
        PactDslResponse getPageToCloneResponse = buildGetPageToClone(getLanguagesResponse, "draft");
        return getPageToCloneResponse.toPact();
    }

    private PactDslResponse buildGetHomePage(PactDslWithProvider builder,String status) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The home page GET Interaction")
                .path("/entando/api/pages/homepage")
                .method("GET")
                .matchQuery("status", "\\w+", "" + status);
        return standardResponse(request, "{\"payload\":{\"code\":\"homepage\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Home\",\"it\":\"Home\"},\"fullTitles\":{\"en\":\"Home\",\"it\":\"Home\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"service\",\"page_se\",\"pagina4\",\"node1\"],\"position\":-1,\"numWidget\":4,\"lastModified\":\"2018-10-02 11:28:11\",\"fullPath\":\"homepage\",\"token\":\"FskPvpj07Z++YLIYLHlofw==\",\"references\":{\"jacmsContentManager\":false}},\"errors\":[],\"metaData\":{\"status\":\"draft\"}}");
    }

    private PactDslResponse buildGetHomePage(PactDslResponse builder,String status) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The home page OPTIONS Interaction")
                .path("/entando/api/pages/homepage")
                .method("OPTIONS")
                .matchQuery("status", "\\w+", "" + status)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The home page GET Interaction")
                .path("/entando/api/pages/homepage")
                .method("GET")
                .matchQuery("status", "\\w+", "" + status);
        return standardResponse(request, "{\"payload\":{\"code\":\"homepage\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Home\",\"it\":\"Home\"},\"fullTitles\":{\"en\":\"Home\",\"it\":\"Home\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"service\",\"page_se\",\"pagina4\",\"node1\"],\"position\":-1,\"numWidget\":4,\"lastModified\":\"2018-10-02 11:28:11\",\"fullPath\":\"homepage\",\"token\":\"FskPvpj07Z++YLIYLHlofw==\",\"references\":{\"jacmsContentManager\":false}},\"errors\":[],\"metaData\":{\"status\":\"draft\"}}");
    }

    private PactDslResponse buildGetPagesParentCode(PactDslResponse builder,String parentCode) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The home page parentCode GET Interaction")
                .path("/entando/api/pages")
                .method("GET")
                .matchQuery("parentCode", "\\w+", "" + parentCode);
        return standardResponse(request, "{\"payload\":[{\"code\":\"service\",\"status\":\"published\",\"displayedInMenu\":false,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Service\",\"it\":\"Pagine di Servizio\"},\"fullTitles\":{\"en\":\"Home / Service\",\"it\":\"Home / Pagine di Servizio\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"notfound\",\"errorpage\",\"login\"],\"position\":1,\"numWidget\":1,\"lastModified\":\"2018-09-13 13:36:35\",\"fullPath\":\"homepage/service\",\"token\":null},{\"code\":\"page_se\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":true,\"titles\":{\"en\":\"Page_se\",\"it\":\"Page_se\"},\"fullTitles\":{\"en\":\"Home / Page_se\",\"it\":\"Home / Page_se\"},\"ownerGroup\":\"administrators\",\"joinGroups\":[\"free\",\"administrators\"],\"children\":[\"page_se_cl\"],\"position\":2,\"numWidget\":2,\"lastModified\":\"2018-10-02 15:29:56\",\"fullPath\":\"homepage/page_se\",\"token\":null},{\"code\":\"pagina4\",\"status\":\"unpublished\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Pagina4\",\"it\":\"Pagina4\"},\"fullTitles\":{\"en\":\"Home / Pagina4\",\"it\":\"Home / Pagina4\"},\"ownerGroup\":\"administrators\",\"joinGroups\":[],\"children\":[],\"position\":3,\"numWidget\":0,\"lastModified\":\"2018-10-02 11:09:30\",\"fullPath\":\"homepage/pagina4\",\"token\":null},{\"code\":\"node1\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Node1\",\"it\":\"Nodo1\"},\"fullTitles\":{\"en\":\"Home / Node1\",\"it\":\"Home / Nodo1\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"node11\"],\"position\":4,\"numWidget\":5,\"lastModified\":\"2018-10-02 15:39:08\",\"fullPath\":\"homepage/node1\",\"token\":null}],\"errors\":[],\"metaData\":{\"parentCode\":\"homepage\"}}");
    }

    private PactDslResponse buildGetLanguages(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The languages GET Interaction")
                .path("/entando/api/languages")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{}");
    }

    private PactDslResponse buildGetPageToClone(PactDslResponse builder, String status) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The page OPTIONS Interaction")
                .path("/entando/api/pages/pagina4")
                .method("OPTIONS")
                .matchQuery("status", "\\w+", "" + status)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The page GET Interaction")
                .path("/entando/api/pages/pagina4")
                .method("GET")
                .matchQuery("status", "\\w+", "" + status);

        return standardResponse(request, "{\"payload\":{\"code\":\"pagina4\",\"status\":\"unpublished\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Pagina4\",\"it\":\"Pagina4\"},\"fullTitles\":{\"en\":\"Home / Pagina4\",\"it\":\"Home / Pagina4\"},\"ownerGroup\":\"administrators\",\"joinGroups\":[],\"children\":[],\"position\":3,\"numWidget\":0,\"lastModified\":\"2018-10-02 11:09:30\",\"fullPath\":\"homepage/pagina4\",\"token\":\"Uc9NyLgq7CM=\",\"references\":{\"jacmsContentManager\":false}},\"errors\":[],\"metaData\":{\"status\":\"draft\"}}");
    }

    private PactDslResponse buildPostPage(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The page OPTIONS Interaction")
                .path("/entando/api/pages")
                .method("OPTIONS")
                .headers("Access-control-request-method", "POST")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The page POST Interaction")
                .path("/entando/api/pages")
                .method("POST");
        return standardResponse(request, "{}");
    }

    @Test
    public void runTest() throws InterruptedException {

         Kebab kebab = dTPageTreePage.getTable().getKebabOnTable("pagina4",
                "Page tree", "Actions");
        kebab.getClickable().click();
        kebab.getAction("Clone").click();
        dTPageAddPage.setEnTitleField("PCT");
        dTPageAddPage.setItTitleField("PCT");
        ExpandableTable table = dTPageAddPage.getTable();
        WebElement row = table.findRowList("Home", "Page tree").get(0);
        row.click();
        dTPageAddPage.getSaveButton().click();
    }
}