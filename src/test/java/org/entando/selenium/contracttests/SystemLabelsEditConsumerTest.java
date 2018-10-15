
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
@PactTestFor(providerName = "SystemLabelsEditProvider", port = "8080")
public class SystemLabelsEditConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTLabelsAndLanguagesPage dTLabelsAndLanguagesPage;

    @Autowired
    public DTSystemLabelsPage dTSystemLabelsPage;

    @Autowired
    public DTSystemLabelsAddPage dTSystemLabelsAddPage;

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
        PactDslResponse getLanguagesResponse = PactUtil.buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getLanguagesResponse);

        PactDslResponse getLabelsResponse = PactUtil.buildGetLabels(getProfileTypesResponse,1,10);

        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getLabelsResponse.toPact(), config, mockServer -> {
            login();
            dTDashboardPage.SelectSecondOrderLinkWithSleep("Configuration", "Labels and Languages");
        });
    }

    @Pact(provider = "SystemLabelsEditProvider", consumer = "SystemLabelsEditConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getLanguagesResponse = buildGetLanguages(builder, 1,0);
        PactDslResponse getLabelToPutResponse = buildGetLabelToPut(getLanguagesResponse);
        PactDslResponse putLabelResponse = buildPutLabel(getLabelToPutResponse);
        PactDslResponse getOnlyLabelsResponse = buildGetLabels(putLabelResponse,1,10);
        return getOnlyLabelsResponse.toPact();
    }

    private PactDslResponse buildGetLabelToPut(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The label GET Interaction")
                .path("/entando/api/labels/AAA")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The label GET Interaction")
                .path("/entando/api/labels/AAA")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"key\":\"AAA\",\"titles\":{\"en\":\"My Title\",\"it\":\"Mio Titolo\"}},\"errors\":[],\"metaData\":{}}");

    }

    private PactDslResponse buildGetLanguages(PactDslWithProvider builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The languages GET Interaction")
                .path("/entando/api/languages")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"aa\",\"description\":\"Afar\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ab\",\"description\":\"Abkhazian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"af\",\"description\":\"Afrikaans\",\"isActive\":false,\"isDefault\":false},{\"code\":\"am\",\"description\":\"Amharic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ar\",\"description\":\"Arabic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"as\",\"description\":\"Assamese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ay\",\"description\":\"Aymara\",\"isActive\":false,\"isDefault\":false},{\"code\":\"az\",\"description\":\"Azerbaijani\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ba\",\"description\":\"Bashkir\",\"isActive\":false,\"isDefault\":false},{\"code\":\"be\",\"description\":\"Byelorussian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bg\",\"description\":\"Bulgarian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bh\",\"description\":\"Bihari\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bi\",\"description\":\"Bislama\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bn\",\"description\":\"Bengali; Bangla\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bo\",\"description\":\"Tibetan\",\"isActive\":false,\"isDefault\":false},{\"code\":\"br\",\"description\":\"Breton\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ca\",\"description\":\"Catalan\",\"isActive\":false,\"isDefault\":false},{\"code\":\"co\",\"description\":\"Corsican\",\"isActive\":false,\"isDefault\":false},{\"code\":\"cs\",\"description\":\"Czech\",\"isActive\":false,\"isDefault\":false},{\"code\":\"cy\",\"description\":\"Welsh\",\"isActive\":false,\"isDefault\":false},{\"code\":\"da\",\"description\":\"Danish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"de\",\"description\":\"German\",\"isActive\":false,\"isDefault\":false},{\"code\":\"dz\",\"description\":\"Bhutani\",\"isActive\":false,\"isDefault\":false},{\"code\":\"el\",\"description\":\"Greek\",\"isActive\":false,\"isDefault\":false},{\"code\":\"en\",\"description\":\"English\",\"isActive\":true,\"isDefault\":true},{\"code\":\"eo\",\"description\":\"Esperanto\",\"isActive\":false,\"isDefault\":false},{\"code\":\"es\",\"description\":\"Spanish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"et\",\"description\":\"Estonian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"eu\",\"description\":\"Basque\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fa\",\"description\":\"Persian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fi\",\"description\":\"Finnish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fj\",\"description\":\"Fiji\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fo\",\"description\":\"Faroese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fr\",\"description\":\"French\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fy\",\"description\":\"Frisian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ga\",\"description\":\"Irish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"gd\",\"description\":\"Scots Gaelic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"gl\",\"description\":\"Galician\",\"isActive\":false,\"isDefault\":false},{\"code\":\"gn\",\"description\":\"Guarani\",\"isActive\":false,\"isDefault\":false},{\"code\":\"gu\",\"description\":\"Gujarati\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ha\",\"description\":\"Hausa\",\"isActive\":false,\"isDefault\":false},{\"code\":\"he\",\"description\":\"Hebrew (formerly iw)\",\"isActive\":false,\"isDefault\":false},{\"code\":\"hi\",\"description\":\"Hindi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"hr\",\"description\":\"Croatian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"hu\",\"description\":\"Hungarian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"hy\",\"description\":\"Armenian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ia\",\"description\":\"Interlingua\",\"isActive\":false,\"isDefault\":false},{\"code\":\"id\",\"description\":\"Indonesian (formerly in)\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ie\",\"description\":\"Interlingue\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ik\",\"description\":\"Inupiak\",\"isActive\":false,\"isDefault\":false},{\"code\":\"is\",\"description\":\"Icelandic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"it\",\"description\":\"Italian\",\"isActive\":true,\"isDefault\":false},{\"code\":\"iu\",\"description\":\"Inuktitut\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ja\",\"description\":\"Japanese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"jw\",\"description\":\"Javanese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ka\",\"description\":\"Georgian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"kk\",\"description\":\"Kazakh\",\"isActive\":false,\"isDefault\":false},{\"code\":\"kl\",\"description\":\"Greenlandic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"km\",\"description\":\"Cambodian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"kn\",\"description\":\"Kannada\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ko\",\"description\":\"Korean\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ks\",\"description\":\"Kashmiri\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ku\",\"description\":\"Kurdish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ky\",\"description\":\"Kirghiz\",\"isActive\":false,\"isDefault\":false},{\"code\":\"la\",\"description\":\"Latin\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ln\",\"description\":\"Lingala\",\"isActive\":false,\"isDefault\":false},{\"code\":\"lo\",\"description\":\"Laothian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"lt\",\"description\":\"Lithuanian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"lv\",\"description\":\"Latvian, Lettish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mg\",\"description\":\"Malagasy\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mi\",\"description\":\"Maori\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mk\",\"description\":\"Macedonian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ml\",\"description\":\"Malayalam\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mn\",\"description\":\"Mongolian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mo\",\"description\":\"Moldavian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mr\",\"description\":\"Marathi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ms\",\"description\":\"Malay\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mt\",\"description\":\"Maltese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"my\",\"description\":\"Burmese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"na\",\"description\":\"Nauru\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ne\",\"description\":\"Nepali\",\"isActive\":false,\"isDefault\":false},{\"code\":\"nl\",\"description\":\"Dutch\",\"isActive\":false,\"isDefault\":false},{\"code\":\"no\",\"description\":\"Norwegian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"oc\",\"description\":\"Occitan\",\"isActive\":false,\"isDefault\":false},{\"code\":\"om\",\"description\":\"(Afan) Oromo\",\"isActive\":false,\"isDefault\":false},{\"code\":\"or\",\"description\":\"Oriya\",\"isActive\":false,\"isDefault\":false},{\"code\":\"pa\",\"description\":\"Punjabi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"pl\",\"description\":\"Polish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ps\",\"description\":\"Pashto, Pushto\",\"isActive\":false,\"isDefault\":false},{\"code\":\"pt\",\"description\":\"Portuguese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"qu\",\"description\":\"Quechua\",\"isActive\":false,\"isDefault\":false},{\"code\":\"rm\",\"description\":\"Rhaeto-Romance\",\"isActive\":false,\"isDefault\":false},{\"code\":\"rn\",\"description\":\"Kirundi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ro\",\"description\":\"Romanian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ru\",\"description\":\"Russian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"rw\",\"description\":\"Kinyarwanda\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sa\",\"description\":\"Sanskrit\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sd\",\"description\":\"Sindhi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sg\",\"description\":\"Sangho\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sh\",\"description\":\"Serbo-Croatian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"si\",\"description\":\"Sinhalese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sk\",\"description\":\"Slovak\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sl\",\"description\":\"Slovenian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sm\",\"description\":\"Samoan\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sn\",\"description\":\"Shona\",\"isActive\":false,\"isDefault\":false},{\"code\":\"so\",\"description\":\"Somali\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sq\",\"description\":\"Albanian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sr\",\"description\":\"Serbian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ss\",\"description\":\"Siswati\",\"isActive\":false,\"isDefault\":false},{\"code\":\"st\",\"description\":\"Sesotho\",\"isActive\":false,\"isDefault\":false},{\"code\":\"su\",\"description\":\"Sundanese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sv\",\"description\":\"Swedish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sw\",\"description\":\"Swahili\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ta\",\"description\":\"Tamil\",\"isActive\":false,\"isDefault\":false},{\"code\":\"te\",\"description\":\"Telugu\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tg\",\"description\":\"Tajik\",\"isActive\":false,\"isDefault\":false},{\"code\":\"th\",\"description\":\"Thai\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ti\",\"description\":\"Tigrinya\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tk\",\"description\":\"Turkmen\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tl\",\"description\":\"Tagalog\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tn\",\"description\":\"Setswana\",\"isActive\":false,\"isDefault\":false},{\"code\":\"to\",\"description\":\"Tonga\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tr\",\"description\":\"Turkish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ts\",\"description\":\"Tsonga\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tt\",\"description\":\"Tatar\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tw\",\"description\":\"Twi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ug\",\"description\":\"Uighur\",\"isActive\":false,\"isDefault\":false},{\"code\":\"uk\",\"description\":\"Ukrainian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ur\",\"description\":\"Urdu\",\"isActive\":false,\"isDefault\":false},{\"code\":\"uz\",\"description\":\"Uzbek\",\"isActive\":false,\"isDefault\":false},{\"code\":\"vi\",\"description\":\"Vietnamese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"vo\",\"description\":\"Volapuk\",\"isActive\":false,\"isDefault\":false},{\"code\":\"wo\",\"description\":\"Wolof\",\"isActive\":false,\"isDefault\":false},{\"code\":\"xh\",\"description\":\"Xhosa\",\"isActive\":false,\"isDefault\":false},{\"code\":\"yi\",\"description\":\"Yiddish (formerly ji)\",\"isActive\":false,\"isDefault\":false},{\"code\":\"yo\",\"description\":\"Yoruba\",\"isActive\":false,\"isDefault\":false},{\"code\":\"za\",\"description\":\"Zhuang\",\"isActive\":false,\"isDefault\":false},{\"code\":\"zh\",\"description\":\"Chinese - Traditional\",\"isActive\":false,\"isDefault\":false},{\"code\":\"zhs\",\"description\":\"Chinese - Simplified\",\"isActive\":false,\"isDefault\":false},{\"code\":\"zu\",\"description\":\"Zulu\",\"isActive\":false,\"isDefault\":false}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":0,\"lastPage\":1,\"totalItems\":140,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildPutLabel(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The label PUT Interaction")
                .path("/entando/api/labels/AAA")
                .method("OPTIONS")
                .headers("Access-control-request-method", "PUT")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The label PUT Interaction")
                .path("/entando/api/labels/AAA")
                .method("PUT");
        return standardResponse(request, "{\"payload\":{\"key\":\"AAA\",\"titles\":{\"en\":\"My Title\",\"it\":\"Mio Titolo\"}},\"errors\":[],\"metaData\":{}}");
    }

    private PactDslResponse buildGetLabels(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder
                .uponReceiving("The labels GET Interaction")
                .path("/entando/api/labels")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize);
        return standardResponse(request, "{\"payload\":[{\"key\":\"AAA\",\"titles\":{\"en\":\"My Title\",\"it\":\"Mio Titolo\"}},{\"key\":\"ADMINISTRATION_BASIC\",\"titles\":{\"en\":\"Normal\",\"it\":\"Normale\"}},{\"key\":\"ADMINISTRATION_BASIC_GOTO\",\"titles\":{\"en\":\"Go to the administration with normal client\",\"it\":\"Accedi con client normale\"}},{\"key\":\"ADMINISTRATION_MINT\",\"titles\":{\"en\":\"Advanced\",\"it\":\"Avanzata\"}},{\"key\":\"ADMINISTRATION_MINT_GOTO\",\"titles\":{\"en\":\"Go to the administration with advanced client\",\"it\":\"Accedi con client avanzato\"}},{\"key\":\"ALL\",\"titles\":{\"en\":\"All\",\"it\":\"Tutte\"}},{\"key\":\"ANN_DOCUMENTS\",\"titles\":{\"en\":\"Documents\",\"it\":\"Documenti\"}},{\"key\":\"ANN_FROM\",\"titles\":{\"en\":\"from\",\"it\":\"da\"}},{\"key\":\"ANN_READ_MORE\",\"titles\":{\"en\":\"View details\",\"it\":\"Dettagli\"}},{\"key\":\"ANN_TO\",\"titles\":{\"en\":\"to\",\"it\":\"a\"}}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":38,\"totalItems\":378,\"sort\":\"key\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        dTLabelsAndLanguagesPage.getSystemLabelsButton().click();
        Kebab kebab = dTSystemLabelsPage.getTable().getKebabOnTable("AAA",
                "Code", "Actions");
        kebab.getClickable().click();
        kebab.getAction("Edit").click();
        dTSystemLabelsAddPage.getSaveButton().click();
    }
}
