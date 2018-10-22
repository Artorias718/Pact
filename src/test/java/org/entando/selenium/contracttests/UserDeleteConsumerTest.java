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
import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserDeleteProvider", port = "8080")
public class UserDeleteConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTUsersPage dTUsersPage;

    @BeforeAll
    public void setupSessionAndNavigateToUserManagement (){
        PactDslWithProvider builder = ConsumerPactBuilder.consumer("LoginConsumer").hasPactWith("LoginProvider");
        PactDslResponse accessTokenResponse = buildGetAccessToken(builder);
        PactDslResponse getUsersResponse = PactUtil.buildGetUsers(accessTokenResponse,1,1);
        PactDslResponse getPagesResponse = buildGetPages(getUsersResponse);
        PactDslResponse getPageStatusResponse = buildGetPageStatus(getPagesResponse);
        PactDslResponse getWidgetsResponse = buildGetWidgets(getPageStatusResponse);
        PactDslResponse getGroupsResponse = buildGetGroups(getWidgetsResponse);
        PactDslResponse getPageModelsResponse = buildGetPageModels(getGroupsResponse);
        PactDslResponse getLanguagesResponse = buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(getLanguagesResponse);
        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getProfileTypesResponse.toPact(), config, mockServer -> {
            login();

            dTDashboardPage.SelectSecondOrderLinkWithSleep("User Management", "Users");
            Utils.waitUntilIsVisible(driver, dTUsersPage.getAddButton());
        });
    }

    @Pact(provider = "UserDeleteProvider", consumer = "UserDeleteConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslResponse getUsersResponse = buildGetUsers(builder,1,10);
        PactDslResponse deleteUserResponse = buildDeleteUser(getUsersResponse);
        return deleteUserResponse.toPact();
    }

    private PactDslResponse buildDeleteUser(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The User Delete OPTIONS Interaction")
                .path("/entando/api/users/UNIMPORTANT")
                .method("OPTIONS");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The User Query DELETE Interaction")
                .path("/entando/api/users/UNIMPORTANT")
                .method("DELETE");
        return standardResponse(request, "{\"payload\":{\"code\":\"UNIMPORTANT\"},\"errors\":[],\"metaData\":{}}");
    }

    private PactDslResponse buildGetUsers(PactDslWithProvider builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder.uponReceiving("The User Query GET Interaction")
                .path("/entando/api/users")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"username\":\"admin\",\"registration\":\"2008-10-10 00:00:00\",\"lastLogin\":\""+LocalDate+"\",\"lastPasswordChange\":\"2018-09-18 00:00:00\",\"status\":\"active\",\"accountNotExpired\":true,\"credentialsNotExpired\":true,\"profileType\":null,\"profileAttributes\":{\"fullname\":\"\",\"email\":\"\"},\"maxMonthsSinceLastAccess\":-1,\"maxMonthsSinceLastPasswordChange\":-1}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"username\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");

    }

    @Test
    public void runTest() throws InterruptedException {


        Kebab kebab = dTUsersPage.getTable().getKebabOnTable("UNIMPORTANT", usersTableHeaderTitles.get(0), usersTableHeaderTitles.get(4));
        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Delete").click();
        dTUsersPage.getDeleteModalButton().click();
    }
}


