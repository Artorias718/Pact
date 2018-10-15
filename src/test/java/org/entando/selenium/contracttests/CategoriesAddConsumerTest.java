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
import org.entando.selenium.utils.pageParts.ExpandableTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "CategoriesAddProvider", port = "8080")
public class CategoriesAddConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTCategoriesPage dTCategoriesPage;

    @Autowired
    public DTCategoriesAddPage dTCategoriesAddPage;

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

    @Pact(provider = "CategoriesAddProvider", consumer = "CategoriesAddConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getCategoriesResponse = buildGetCategories(builder);
        PactDslResponse getCategoriesParentCodeResponse = buildGetCategoriesParentCode(getCategoriesResponse,"home");
        PactDslResponse getLanguagesResponse = buildGetLanguages(getCategoriesParentCodeResponse);
        PactDslResponse postCategoriesResponse = buildPostCategories(getLanguagesResponse);
        return postCategoriesResponse.toPact();
    }

    public static PactDslResponse buildGetCategories(PactDslWithProvider builder) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The Categories GET Interaction")
                .path("/entando/api/categories/home")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"code\":\"home\",\"parentCode\":\"home\",\"titles\":{\"en\":\"All\",\"it\":\"Generale\"},\"fullTitles\":{\"en\":\"All\",\"it\":\"Generale\"},\"children\":[\"category3\",\"categorytest\",\"categorytest00\",\"categorytest2\",\"jpcollaboration_categoryRoot\",\"jptagcloud_categoryRoot\",\"seleniumtest_donttouch\"],\"references\":{\"jpcollaborationIdeaManager\":false,\"DataObjectManager\":false,\"jacmsResourceManager\":false,\"jacmsContentManager\":false}},\"errors\":[],\"metaData\":{}}");
    }

    public static PactDslResponse buildGetCategoriesParentCode(PactDslResponse builder, String parentCode) {
        PactDslRequestWithPath request = builder.
                uponReceiving("The Categories parentCode GET Interaction")
                .path("/entando/api/categories")
                .method("GET")
                .matchQuery("parentCode", "\\w+", "" + parentCode);
        return standardResponse(request, "{\"payload\":[{\"code\":\"category3\",\"parentCode\":\"home\",\"titles\":{\"en\":\"Category3\",\"it\":\"Category3\"},\"fullTitles\":{\"en\":\"All / Category3\",\"it\":\"Generale / Category3\"},\"children\":[\"testcategory31\"],\"references\":{}},{\"code\":\"categorytest\",\"parentCode\":\"home\",\"titles\":{\"en\":\"CategoryTest\",\"it\":\"CategoryTest\"},\"fullTitles\":{\"en\":\"All / CategoryTest\",\"it\":\"Generale / CategoryTest\"},\"children\":[\"category_se\",\"category_se1\"],\"references\":{}},{\"code\":\"categorytest00\",\"parentCode\":\"home\",\"titles\":{\"en\":\"CategoryTest00\",\"it\":\"CategoryTest00\"},\"fullTitles\":{\"en\":\"All / CategoryTest00\",\"it\":\"Generale / CategoryTest00\"},\"children\":[],\"references\":{}},{\"code\":\"categorytest2\",\"parentCode\":\"home\",\"titles\":{\"en\":\"CategoryTest2\",\"it\":\"CategoryTest2\"},\"fullTitles\":{\"en\":\"All / CategoryTest2\",\"it\":\"Generale / CategoryTest2\"},\"children\":[],\"references\":{}},{\"code\":\"jpcollaboration_categoryRoot\",\"parentCode\":\"home\",\"titles\":{\"en\":\"Crowd Sourcing Root\",\"it\":\"Crowd Sourcing Root\"},\"fullTitles\":{\"en\":\"All / Crowd Sourcing Root\",\"it\":\"Generale / Crowd Sourcing Root\"},\"children\":[],\"references\":{}},{\"code\":\"jptagcloud_categoryRoot\",\"parentCode\":\"home\",\"titles\":{\"en\":\"Tag Cloud Root\",\"it\":\"Tag Cloud Root\"},\"fullTitles\":{\"en\":\"All / Tag Cloud Root\",\"it\":\"Generale / Tag Cloud Root\"},\"children\":[],\"references\":{}},{\"code\":\"seleniumtest_donttouch\",\"parentCode\":\"home\",\"titles\":{\"en\":\"SeleniumTest_DontTouch\",\"it\":\"SeleniumTest_DontTouch\"},\"fullTitles\":{\"en\":\"All / SeleniumTest_DontTouch\",\"it\":\"Generale / SeleniumTest_DontTouch\"},\"children\":[],\"references\":{}}],\"errors\":[],\"metaData\":{\"parentCode\":\"home\"}}");
    }

    public static PactDslResponse buildGetLanguages(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The get Languages OPTIONS Interaction")
                .path("/entando/api/languages")
                .method("OPTIONS")
                .matchQuery("filters[0].attribute", "isActive")
                .matchQuery("filters[0].operator", "eq")
                .matchQuery("filters[0].value", "true")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "0")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Languages GET Interaction")
                .path("/entando/api/languages")
                .method("GET")
                .matchQuery("filters[0].attribute", "isActive")
                .matchQuery("filters[0].operator", "eq")
                .matchQuery("filters[0].value", "true")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "0");
        return standardResponse(request, "{}");
    }

    public static PactDslResponse buildPostCategories(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The post Categories OPTIONS Interaction")
                .path("/entando/api/categories")
                .method("OPTIONS")
                .headers("Access-control-request-method", "POST");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Categories POST Interaction")
                .path("/entando/api/categories")
                .method("POST");
        return standardResponse(request, "{\"metaData\":{\"parentCode\":\"home\"},\"payload\":[{\"code\":\"pactCat\",\"fullTitles\":{\"en\":\"All / Category3\",\"it\":\"Generale / Category3\"},\"references\":{},\"parentCode\":\"home\",\"children\":[\"testcategory31\"],\"titles\":{\"en\":\"Category3\",\"it\":\"Category3\"}},{\"code\":\"categorytest\",\"fullTitles\":{\"en\":\"All / CategoryTest\",\"it\":\"Generale / CategoryTest\"},\"references\":{},\"parentCode\":\"home\",\"children\":[\"category_se\",\"category_se1\"],\"titles\":{\"en\":\"CategoryTest\",\"it\":\"CategoryTest\"}},{\"code\":\"categorytest00\",\"fullTitles\":{\"en\":\"All / CategoryTest00\",\"it\":\"Generale / CategoryTest00\"},\"references\":{},\"parentCode\":\"home\",\"children\":[],\"titles\":{\"en\":\"CategoryTest00\",\"it\":\"CategoryTest00\"}},{\"code\":\"categorytest2\",\"fullTitles\":{\"en\":\"All / CategoryTest2\",\"it\":\"Generale / CategoryTest2\"},\"references\":{},\"parentCode\":\"home\",\"children\":[],\"titles\":{\"en\":\"CategoryTest2\",\"it\":\"CategoryTest2\"}},{\"code\":\"jpcollaboration_categoryRoot\",\"fullTitles\":{\"en\":\"All / Crowd Sourcing Root\",\"it\":\"Generale / Crowd Sourcing Root\"},\"references\":{},\"parentCode\":\"home\",\"children\":[],\"titles\":{\"en\":\"Crowd Sourcing Root\",\"it\":\"Crowd Sourcing Root\"}},{\"code\":\"jptagcloud_categoryRoot\",\"fullTitles\":{\"en\":\"All / Tag Cloud Root\",\"it\":\"Generale / Tag Cloud Root\"},\"references\":{},\"parentCode\":\"home\",\"children\":[],\"titles\":{\"en\":\"Tag Cloud Root\",\"it\":\"Tag Cloud Root\"}},{\"code\":\"seleniumtest_donttouch\",\"fullTitles\":{\"en\":\"All / SeleniumTest_DontTouch\",\"it\":\"Generale / SeleniumTest_DontTouch\"},\"references\":{},\"parentCode\":\"home\",\"children\":[],\"titles\":{\"en\":\"SeleniumTest_DontTouch\",\"it\":\"SeleniumTest_DontTouch\"}}],\"errors\":[]}");
}

    @Test
    public void runTest() throws InterruptedException {

        String defaultCategoryName = "pactCat";
        dTCategoriesPage.getAddButton().click();
        Utils.waitUntilIsVisible(driver, dTCategoriesAddPage.getSaveButton());
        dTCategoriesAddPage.setEnNameField(defaultCategoryName);
        dTCategoriesAddPage.setItNameField(defaultCategoryName);
        dTCategoriesAddPage.setCodeField(defaultCategoryName);
        ExpandableTable table = dTCategoriesAddPage.getTable();
        WebElement row = table.findRowList("All", "Categories tree").get(0);
        row.click();
        dTCategoriesAddPage.getSaveButton().click();
        }
}
