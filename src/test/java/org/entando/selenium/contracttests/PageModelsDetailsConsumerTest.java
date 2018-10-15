
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
import org.entando.selenium.utils.pageParts.Kebab;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;
import static java.lang.Thread.sleep;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PageModelsDetailsProvider", port = "8080")
public class PageModelsDetailsConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTPageModelsPage dTPageModelsPage;

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

    @Pact(provider = "PageModelsDetailsProvider", consumer = "PageModelsDetailsConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse detailsPageModelsResponse = buildGetDetailsResponse(builder);
        PactDslResponse getPageModelsReferencesResponse = buildGetPageModelsReferencesResponse(detailsPageModelsResponse,1,10);
        return getPageModelsReferencesResponse.toPact();
    }

    private PactDslResponse buildGetDetailsResponse(PactDslWithProvider builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The page models details OPTIONS Interaction")
                .path("/entando/api/pageModels/PCT")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The page models details GET Interaction")
                .path("/entando/api/pageModels/PCT")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"code\":\"PCT\",\"descr\":\"PCT\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"PactCell\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}},\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":10,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildGetPageModelsReferencesResponse(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The page models details OPTIONS Interaction")
                .path("/entando/api/pageModels/PCT/references/PageManager")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The page models details GET Interaction")
                .path("/entando/api/pageModels/PCT/references/PageManager")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"service\",\"status\":\"published\",\"displayedInMenu\":false,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Service\",\"it\":\"Pagine di Servizio\"},\"fullTitles\":{\"en\":\"Home / Service\",\"it\":\"Home / Pagine di Servizio\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"notfound\",\"errorpage\",\"login\"],\"position\":1,\"numWidget\":1,\"lastModified\":\"2018-09-13 13:36:35\",\"fullPath\":\"homepage/service\",\"token\":null},{\"code\":\"notfound\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Page not found\",\"it\":\"Pagina non trovata\"},\"fullTitles\":{\"en\":\"Home / Service / Page not found\",\"it\":\"Home / Pagine di Servizio / Pagina non trovata\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":1,\"numWidget\":0,\"lastModified\":\"2017-02-17 16:37:10\",\"fullPath\":\"homepage/service/notfound\",\"token\":null},{\"code\":\"errorpage\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Error page\",\"it\":\"Pagina di errore\"},\"fullTitles\":{\"en\":\"Home / Service / Error page\",\"it\":\"Home / Pagine di Servizio / Pagina di errore\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":2,\"numWidget\":0,\"lastModified\":\"2017-02-17 21:11:54\",\"fullPath\":\"homepage/service/errorpage\",\"token\":null},{\"code\":\"login\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Login\",\"it\":\"Pagina di login\"},\"fullTitles\":{\"en\":\"Home / Service / Login\",\"it\":\"Home / Pagine di Servizio / Pagina di login\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":3,\"numWidget\":0,\"lastModified\":\"2017-02-17 15:32:34\",\"fullPath\":\"homepage/service/login\",\"token\":null},{\"code\":\"service\",\"status\":\"published\",\"displayedInMenu\":false,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Service\",\"it\":\"Pagine di Servizio\"},\"fullTitles\":{\"en\":\"Home / Service\",\"it\":\"Home / Pagine di Servizio\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"notfound\",\"errorpage\",\"login\"],\"position\":1,\"numWidget\":1,\"lastModified\":\"2018-09-13 13:36:35\",\"fullPath\":\"homepage/service\",\"token\":null},{\"code\":\"notfound\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Page not found\",\"it\":\"Pagina non trovata\"},\"fullTitles\":{\"en\":\"Home / Service / Page not found\",\"it\":\"Home / Pagine di Servizio / Pagina non trovata\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":1,\"numWidget\":0,\"lastModified\":\"2017-02-17 16:37:10\",\"fullPath\":\"homepage/service/notfound\",\"token\":null},{\"code\":\"errorpage\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Error page\",\"it\":\"Pagina di errore\"},\"fullTitles\":{\"en\":\"Home / Service / Error page\",\"it\":\"Home / Pagine di Servizio / Pagina di errore\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":2,\"numWidget\":0,\"lastModified\":\"2017-02-17 21:11:54\",\"fullPath\":\"homepage/service/errorpage\",\"token\":null},{\"code\":\"login\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Login\",\"it\":\"Pagina di login\"},\"fullTitles\":{\"en\":\"Home / Service / Login\",\"it\":\"Home / Pagine di Servizio / Pagina di login\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":3,\"numWidget\":0,\"lastModified\":\"2017-02-17 15:32:34\",\"fullPath\":\"homepage/service/login\",\"token\":null}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":8,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        Kebab kebab = dTPageModelsPage.getTable().getKebabOnTable("PCT",
                "Code", "Actions");
        kebab.getClickable().click();
        kebab.getAction("Details").click();
    }
}
