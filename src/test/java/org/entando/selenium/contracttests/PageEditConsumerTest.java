
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
@PactTestFor(providerName = "PageEditProvider", port = "8080")
public class PageEditConsumerTest extends UsersTestBase {

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

    @Pact(provider = "PageEditProvider", consumer = "PageEditConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getPagesResponse = buildGetHomePage(builder, "draft");
        PactDslResponse getGroupsResponse = buildGetGroups(getPagesResponse,1,0);
        PactDslResponse getPageModelsResponse = buildGetPageModels(getGroupsResponse,1,0);
        PactDslResponse getParentCodeResponse = buildGetPagesParentCode(getPageModelsResponse, "homepage");
        PactDslResponse getLanguagesResponse = buildGetLanguages(getParentCodeResponse, 1, 0);
        PactDslResponse putPageResponse = buildPutPage(getLanguagesResponse);
        PactDslResponse getPageToPutResponse = buildGetPageToPut(putPageResponse, "draft");
        return getPageToPutResponse.toPact();
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

    private PactDslResponse buildGetGroups(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The groups OPTIONS Interaction")
                .path("/entando/api/groups")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The hgroups GET Interaction")
                .path("/entando/api/groups")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"1seleniumtest_dontto\",\"name\":\"1SeleniumTest_DontTouch\"},{\"code\":\"1slnm_test_1443\",\"name\":\"1SLNM_TEST_1443\"},{\"code\":\"1slnm_test_4290\",\"name\":\"1SLNM_TEST_4290\"},{\"code\":\"administrators\",\"name\":\"Administrators\"},{\"code\":\"bpm_admin\",\"name\":\"Bpm Admin\"},{\"code\":\"bpm_appraiser\",\"name\":\"Bpm Appraiser\"},{\"code\":\"bpm_broker\",\"name\":\"Bpm Broker\"},{\"code\":\"bpm_manager\",\"name\":\"Bpm Manager\"},{\"code\":\"cdp\",\"name\":\"cdp\"},{\"code\":\"free\",\"name\":\"Free Access\"},{\"code\":\"gruppo_prova\",\"name\":\"GG\"},{\"code\":\"PleaseDoNotUseThis\",\"name\":\"PleaseDoNotUseThis\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":0,\"lastPage\":1,\"totalItems\":12,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildGetPageModels(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The page models OPTIONS Interaction")
                .path("/entando/api/pageModels")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The page models GET Interaction")
                .path("/entando/api/pageModels")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"1SLNM_TEST_1467\",\"descr\":\"1SLNM_TEST_1467\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"SeleniumCell\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}},{\"code\":\"1SLNM_TEST_2056\",\"descr\":\"1SLNM_TEST_2056\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"SeleniumCell\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}},{\"code\":\"1SLNM_TEST_9080\",\"descr\":\"1SLNM_TEST_9080\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"SeleniumCell\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}},{\"code\":\"1SLNM_TEST_DONT_TOUCH\",\"descr\":\"1SLNM_TEST_DONT_TOUCH\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"Test frame\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}},{\"code\":\"entando-page-inspinia\",\"descr\":\"Inspinia - BPM layout\",\"mainFrame\":11,\"pluginCode\":null,\"template\":\"<#assign wp=JspTaglibs[\\\"/aps-core\\\"]>\\r\\n<#assign c=JspTaglibs[\\\"http://java.sun.com/jsp/jstl/core\\\"]>\\r\\n<!DOCTYPE html>\\r\\n<html lang=\\\"en\\\">\\r\\n    <head>\\r\\n        <meta charset=\\\"utf-8\\\" />\\r\\n        <title>\\r\\n            <@wp.currentPage param=\\\"title\\\" /> - <@wp.i18n key=\\\"PORTAL_TITLE\\\" />\\r\\n        </title>\\r\\n        <meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1.0\\\" />\\r\\n        <meta name=\\\"description\\\" content=\\\"\\\" />\\r\\n        <meta name=\\\"author\\\" content=\\\"\\\" />\\r\\n        <link rel=\\\"icon\\\" href=\\\"<@wp.info key=\\\"systemParam\\\" paramName=\\\"applicationBaseURL\\\" />\\r\\n              favicon.png\\\" type=\\\"image/png\\\" />\\r\\n              <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->\\r\\n              <!--[if lt IE 9]>\\r\\n              <script src=\\\"<@wp.resourceURL />static/js/entando-misc-html5-essentials/html5shiv.js\\\"></script>\\r\\n              <![endif]-->\\r\\n              <@c.import url=\\\"/WEB-INF/aps/jsp/models/inc/content_inline_editing.jsp\\\" />\\r\\n              <@c.import url=\\\"/WEB-INF/aps/jsp/models/inc/header-inclusions.jsp\\\" />\\r\\n    </head>\\r\\n    <body class=\\\"pace-done\\\">\\r\\n        <div class=\\\"pace  pace-inactive\\\">\\r\\n            <div class=\\\"pace-progress\\\" data-progress-text=\\\"100%\\\" data-progress=\\\"99\\\" style=\\\"transform: translate3d(100%, 0px, 0px);\\\">\\r\\n                <div class=\\\"pace-progress-inner\\\"></div>\\r\\n            </div>\\r\\n            <div class=\\\"pace-activity\\\"></div>\\r\\n        </div>\\r\\n        <div id=\\\"wrapper\\\">\\r\\n            <nav class=\\\"navbar-default navbar-static-side\\\" role=\\\"navigation\\\">\\r\\n                <div class=\\\"sidebar-collapse\\\">\\r\\n                    <ul class=\\\"nav metismenu\\\" id=\\\"side-menu\\\">\\r\\n                        <li class=\\\"nav-header\\\">\\r\\n                            <div class=\\\"dropdown profile-element\\\">\\r\\n                                <#if (accountExpired?? && accountExpired == true) || (wrongAccountCredential?? && wrongAccountCredential == true)>open</#if>\\r\\n                                <#if (Session.currentUser != \\\"guest\\\")>\\r\\n                                <span>\\r\\n                                    <img alt=\\\"image\\\" class=\\\"\\\" src=\\\"<@wp.imgURL />entando-logo.png\\\">\\r\\n                                </span>\\r\\n                                <#else>\\r\\n                                <span>\\r\\n                                    <img alt=\\\"image\\\" class=\\\"\\\" src=\\\"<@wp.imgURL />entando-logo-1.png\\\">\\r\\n                                </span>\\r\\n                                </#if>\\r\\n                                <br>\\r\\n                                <@wp.show frame=0 />\\r\\n                            </div>\\r\\n                            <div class=\\\"logo-element\\\">\\r\\n                                E\\r\\n                            </div>\\r\\n                        </li>\\r\\n                        <@wp.show frame=8 />\\r\\n                        <@wp.show frame=10 />\\r\\n                        <@wp.show frame=12 />\\r\\n                        <@wp.show frame=15 />\\r\\n                        <@wp.show frame=17 />\\r\\n                    </ul>\\r\\n                </div>\\r\\n            </nav>\\r\\n            <div id=\\\"page-wrapper\\\" class=\\\"gray-bg dashbard-1\\\">\\r\\n                <div class=\\\"row border-bottom\\\">\\r\\n                    <nav class=\\\"navbar navbar-static-top\\\" role=\\\"navigation\\\" style=\\\"margin-bottom: 0\\\">\\r\\n                        <div class=\\\"navbar-header\\\">\\r\\n                            <a class=\\\"navbar-minimalize minimalize-styl-2 btn btn-primary \\\" href=\\\"#\\\">\\r\\n                                <i class=\\\"fa fa-bars\\\"></i>\\r\\n                            </a>\\r\\n                        </div>\\r\\n                        <ul class=\\\"nav navbar-top-links navbar-right\\\">\\r\\n                            <li class=\\\"dropdown\\\">\\r\\n                                <@wp.show frame=1 />\\r\\n                                <@wp.show frame=2 />\\r\\n                                <@wp.show frame=3 />\\r\\n                                <@wp.show frame=4 />\\r\\n</li>\\r\\n                        </ul>\\r\\n                    </nav>\\r\\n                </div>\\r\\n                <div class=\\\"row white-bg\\\" style=\\\"padding-top:20px; padding-bottom:10px; border-bottom:2px solid #e7eaec;\\\">\\r\\n                    <div class=\\\"col-md-4\\\">\\r\\n                        <div class=\\\"white-bg\\\">\\r\\n                            <@wp.show frame=5 />\\r\\n                        </div>\\r\\n                    </div>\\r\\n                    <div class=\\\"col-md-4\\\">\\r\\n                        <div class=\\\"white-bg\\\">\\r\\n                            <@wp.show frame=6 />\\r\\n                        </div>\\r\\n                    </div>\\r\\n                    <div class=\\\"col-md-4\\\">\\r\\n                        <div class=\\\"white-bg\\\">\\r\\n                            <@wp.show frame=7 />\\r\\n                        </div>\\r\\n                    </div>\\r\\n                </div>\\r\\n                <div class=\\\"row white-bg\\\" style=\\\"padding-top:20px; padding-bottom:10px; border-bottom:2px solid #e7eaec;\\\">\\r\\n                    <div class=\\\"col-md-12\\\">\\r\\n                        <div class=\\\"white-bg\\\">\\r\\n                            <@wp.show frame=9 />\\r\\n                        </div>\\r\\n                    </div>\\r\\n                </div>\\r\\n                <div class=\\\"row white-bg\\\" style=\\\"padding-top:20px; padding-bottom:10px; border-bottom:2px solid #e7eaec;\\\">\\r\\n                    <div class=\\\"col-md-12\\\">\\r\\n                        <div class=\\\"white-bg\\\">\\r\\n                            <@wp.show frame=11 />\\r\\n                        </div>\\r\\n                    </div>\\r\\n\\r\\n                </div>\\r\\n                <div class=\\\"row white-bg\\\" style=\\\"padding-top:20px; padding-bottom:10px; border-bottom:2px solid #e7eaec;\\\">\\r\\n                    <div class=\\\"col-md-6\\\">\\r\\n                        <div class=\\\"white-bg\\\">\\r\\n                            <@wp.show frame=13 />\\r\\n                        </div>\\r\\n                    </div>\\r\\n                    <div class=\\\"col-md-6\\\">\\r\\n                        <div class=\\\"white-bg\\\">\\r\\n                            <@wp.show frame=14 />\\r\\n                        </div>\\r\\n                    </div>\\r\\n                </div>\\r\\n                <div class=\\\"row white-bg\\\" style=\\\"padding-top:20px; padding-bottom:10px; border-bottom:2px solid #e7eaec;\\\">\\r\\n                    <@wp.show frame=16 />\\r\\n                </div>\\r\\n                <div class=\\\"row\\\">\\r\\n                    <div class=\\\"wrapper wrapper-content\\\">\\r\\n                        <div class=\\\"row\\\">\\r\\n                            <div class=\\\"col-lg-4\\\">\\r\\n                                <@wp.show frame=18 />\\r\\n                            </div>\\r\\n                            <div class=\\\"col-lg-4\\\">\\r\\n                                <@wp.show  frame=19/>\\r\\n                            </div>\\r\\n                            <div class=\\\"col-lg-4\\\">\\r\\n                                <@wp.show  frame=20 />\\r\\n                            </div>\\r\\n                        </div>\\r\\n                        <div class=\\\"row\\\">\\r\\n                            <div class=\\\"col-lg-4\\\">\\r\\n                                <@wp.show frame=21 />\\r\\n                            </div>\\r\\n                            <div class=\\\"col-lg-4\\\">\\r\\n                                <@wp.show  frame=22/>\\r\\n                            </div>\\r\\n                            <div class=\\\"col-lg-4\\\">\\r\\n                                <@wp.show  frame=23 />\\r\\n                            </div>\\r\\n                        </div>\\r\\n                    </div>\\r\\n                </div>\\r\\n                <div class=\\\"row\\\">\\r\\n                    <div class=\\\"footer\\\">\\r\\n                        <@wp.show frame=24 />\\r\\n                    </div>\\r\\n                </div>\\r\\n            </div>\\r\\n        </div>\\r\\n    </div>\\r\\n</body>\\r\\n</html>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"Sidebar 1\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":0,\"x2\":1,\"y2\":0}},{\"pos\":1,\"descr\":\"Top Bar 1\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":2,\"y1\":0,\"x2\":4,\"y2\":0}},{\"pos\":2,\"descr\":\"Top Bar 2\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":5,\"y1\":0,\"x2\":7,\"y2\":0}},{\"pos\":3,\"descr\":\"Top Bar 3\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":8,\"y1\":0,\"x2\":9,\"y2\":0}},{\"pos\":4,\"descr\":\"Top Bar 4\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":10,\"y1\":0,\"x2\":11,\"y2\":0}},{\"pos\":5,\"descr\":\"Left\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":2,\"y1\":1,\"x2\":4,\"y2\":1}},{\"pos\":6,\"descr\":\"Center\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":5,\"y1\":1,\"x2\":7,\"y2\":1}},{\"pos\":7,\"descr\":\"Right\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":8,\"y1\":1,\"x2\":11,\"y2\":1}},{\"pos\":8,\"descr\":\"Sidebar 2\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":1,\"x2\":1,\"y2\":1}},{\"pos\":9,\"descr\":\"Full 1\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":2,\"y1\":2,\"x2\":11,\"y2\":2}},{\"pos\":10,\"descr\":\"Sidebar 3\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":2,\"x2\":1,\"y2\":2}},{\"pos\":11,\"descr\":\"full 2\",\"mainFrame\":true,\"defaultWidget\":null,\"sketch\":{\"x1\":2,\"y1\":3,\"x2\":11,\"y2\":3}},{\"pos\":12,\"descr\":\"Sidebar 4\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":3,\"x2\":1,\"y2\":3}},{\"pos\":13,\"descr\":\"Content left\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":2,\"y1\":4,\"x2\":6,\"y2\":4}},{\"pos\":14,\"descr\":\"Content right\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":7,\"y1\":4,\"x2\":11,\"y2\":4}},{\"pos\":15,\"descr\":\"Sidebar 5\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":4,\"x2\":1,\"y2\":4}},{\"pos\":16,\"descr\":\"Full 3\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":2,\"y1\":5,\"x2\":11,\"y2\":5}},{\"pos\":17,\"descr\":\"Sidebar 5\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":5,\"x2\":1,\"y2\":5}},{\"pos\":18,\"descr\":\"Left\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":6,\"x2\":3,\"y2\":6}},{\"pos\":19,\"descr\":\"Center\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":4,\"y1\":6,\"x2\":7,\"y2\":6}},{\"pos\":20,\"descr\":\"Right\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":8,\"y1\":6,\"x2\":11,\"y2\":6}},{\"pos\":21,\"descr\":\"Left\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":7,\"x2\":3,\"y2\":7}},{\"pos\":22,\"descr\":\"Center\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":4,\"y1\":7,\"x2\":7,\"y2\":7}},{\"pos\":23,\"descr\":\"Right\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":8,\"y1\":7,\"x2\":11,\"y2\":7}},{\"pos\":24,\"descr\":\"Footer\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":8,\"x2\":11,\"y2\":8}}]}},{\"code\":\"home\",\"descr\":\"Home Page\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":null,\"configuration\":{\"frames\":[]}},{\"code\":\"jpgeoref_home\",\"descr\":\"Home Page for test georef Content\",\"mainFrame\":-1,\"pluginCode\":\"jpgeoref\",\"template\":null,\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"Test frame\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":0,\"x2\":11,\"y2\":0}}]}},{\"code\":\"PleaseDoNotUse\",\"descr\":\"PleaseDoNotUse\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<html></html>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"Navbar\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":0,\"x2\":2,\"y2\":0}},{\"pos\":1,\"descr\":\"Navbar 2\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":3,\"y1\":0,\"x2\":5,\"y2\":0}}]}},{\"code\":\"PleaseDoNotUsse\",\"descr\":\"PleaseDoNotUsse\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<html></html>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"Navbar\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":0,\"y1\":0,\"x2\":2,\"y2\":0}},{\"pos\":1,\"descr\":\"Navbar 2\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":{\"x1\":3,\"y1\":0,\"x2\":5,\"y2\":0}}]}},{\"code\":\"service\",\"descr\":\"Service Page\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<#assign wp=JspTaglibs[\\\"/aps-core\\\"]>\\n<!DOCTYPE HTML PUBLIC \\\"-//W3C//DTD HTML 4.0 Transitional//EN\\\">\\n<html>\\n<head>\\n\\t<title><@wp.currentPage param=\\\"title\\\" /></title>\\n</head>\\n<body>\\n<h1><@wp.currentPage param=\\\"title\\\" /></h1>\\n<a href=\\\"<@wp.url page=\\\"homepage\\\" />\\\" >Home</a><br>\\n<div><@wp.show frame=0 /></div>\\n</body>\\n</html>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"Sample Frame\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":0,\"lastPage\":1,\"totalItems\":10,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
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

    private PactDslResponse buildPutPage(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The page OPTIONS Interaction")
                .path("/entando/api/pages/pagina4")
                .method("OPTIONS")
                .headers("Access-control-request-method", "PUT")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The page PUT Interaction")
                .path("/entando/api/pages/pagina4")
                .method("PUT");
        return standardResponse(request, "{}");
    }

    private PactDslResponse buildGetPageToPut(PactDslResponse builder, String status) {
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

    @Test
    public void runTest() throws InterruptedException {


        Kebab kebab = dTPageTreePage.getTable().getKebabOnTable("pagina4",
                "Page tree", "Actions");

        kebab.getClickable().click();
        kebab.getAction("Edit").click();
        dTPageAddPage.setEnTitleField("PCT");
        dTPageAddPage.setItTitleField("PCT");
        ExpandableTable table = dTPageAddPage.getTable();
        WebElement row = table.findRowList("Home", "Page tree").get(0);
        row.click();
        dTPageAddPage.getPageModel().selectByVisibleText("Home Page");
        dTPageAddPage.getSaveButton().click();
    }
}