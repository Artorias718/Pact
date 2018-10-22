package org.entando.selenium.contracttests;

import au.com.dius.pact.consumer.dsl.*;
import au.com.dius.pact.provider.junit.State;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class PactUtil {

    public static final String MATCH_ANY_HTTP_VERB = "(GET|HEAD|POST|DELETE|TRACE|OPTIONS|PATCH|PUT\\,\\s){1,8}";

    public static String LocalDate = java.time.LocalDate.now() + " 00:00:00";

//    public static PactDslJsonBody toDslJsonBody(String json) {
//        JSONObject jsonObject = new JSONObject(json);
//        PactDslJsonBody dslJsonBody = new PactDslJsonBody();
//        copyStructureInto(jsonObject, dslJsonBody);
//        JSONObject targetJsonObject = (JSONObject) dslJsonBody.getBody();
//        for (String name : jsonObject.keySet()) {
//            targetJsonObject.put(name,jsonObject.get(name));
//        }
//        System.out.println(dslJsonBody.toString());
//
//        return dslJsonBody;
//    }

    private static void copyStructureInto(JSONObject jsonObject, PactDslJsonBody dslJsonBody) {
        for (String string : jsonObject.keySet()) {
            copyPropertyTypeNamed(jsonObject, dslJsonBody, string);
        }
        dslJsonBody.closeObject();
    }

    private static void copyPropertyTypeNamed(JSONObject jsonObject, PactDslJsonBody dslJsonBody, String string) {
        Object o = jsonObject.get(string);
        if (o instanceof Number) {
            dslJsonBody.numberType(string, (Number) o);
        } else if (o instanceof Boolean) {
            dslJsonBody.booleanType(string, (Boolean) o);
        } else if (o instanceof String) {
            //TODO check for dates/ip addresses and what not
            dslJsonBody.stringType(string, (String) o);
        } else if (o instanceof JSONObject) {
            copyStructureInto((JSONObject) o, dslJsonBody.object(string));
        } else if (o instanceof JSONArray) {
            JSONArray arr = (JSONArray) o;
            if (arr.length() > 0) {
                if (arr.get(0) instanceof JSONObject) {
                    PactDslJsonBody jsonBodyToMatch = dslJsonBody.eachLike(string, arr.length());
                    copyStructureInto(arr.getJSONObject(0), jsonBodyToMatch);
                    PactDslJsonArray dslJsonArray = (PactDslJsonArray) jsonBodyToMatch.closeObject();
                    dslJsonArray.closeArray();
                }else if(arr.get(0) instanceof Number){
                    dslJsonBody.array(string).numberType((Number) arr.get(0));
                }else if(arr.get(0) instanceof String){
                    dslJsonBody.array(string).stringType((String) arr.get(0));
                }else if(arr.get(0) instanceof Boolean){
                    dslJsonBody.array(string).booleanType((Boolean) arr.get(0));
                }
            }
        }
    }

    public static JSONObject toDslJsonBody(String json) {
        return new JSONObject(json);
    }
    public static PactDslResponse buildGetProfileTypes(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The ProfileTypes OPTIONS Interaction")
                .path("/entando/api/profileTypes")
                .method("OPTIONS")
                .matchQuery("page", "\\d+")
                .matchQuery("pageSize", "\\d+");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The ProfileTypes GET Interaction")
                .path("/entando/api/profileTypes")
                .method("GET")
                .matchQuery("page", "\\d+")
                .matchQuery("pageSize", "\\d+");
        return standardResponse(request, "{\"payload\":[{\"code\":\"PFL\",\"name\":\"Default user profile\",\"status\":\"0\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"name\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetUsers(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The User Query OPTIONS Interaction")
                .path("/entando/api/users")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", ""+pageSize);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The User Query GET Interaction")
                .path("/entando/api/users")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"username\":\"admin\",\"registration\":\"2008-10-10 00:00:00\",\"lastLogin\":\"2018-10-21 00:00:00\",\"lastPasswordChange\":\"2018-09-18 00:00:00\",\"status\":\"active\",\"accountNotExpired\":true,\"credentialsNotExpired\":true,\"profileType\":null,\"profileAttributes\":{\"fullname\":\"\",\"email\":\"\"},\"maxMonthsSinceLastAccess\":-1,\"maxMonthsSinceLastPasswordChange\":-1},{\"username\":\"UNIMPORTANT\",\"registration\":\""+LocalDate+"\",\"lastLogin\":null,\"lastPasswordChange\":null,\"status\":\"inactive\",\"accountNotExpired\":true,\"credentialsNotExpired\":true,\"profileType\":null,\"profileAttributes\":{},\"maxMonthsSinceLastAccess\":-1,\"maxMonthsSinceLastPasswordChange\":-1}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":2,\"sort\":\"username\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetLanguages(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Languages OPTIONS Interaction")
                .path("/entando/api/languages")
                .method("OPTIONS")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "0");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The Languages GET Interaction")
                .path("/entando/api/languages")
                .method("GET")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "0");
        return standardResponse(request, "{\"payload\":[{\"code\":\"aa\",\"description\":\"Afar\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ab\",\"description\":\"Abkhazian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"af\",\"description\":\"Afrikaans\",\"isActive\":false,\"isDefault\":false},{\"code\":\"am\",\"description\":\"Amharic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ar\",\"description\":\"Arabic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"as\",\"description\":\"Assamese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ay\",\"description\":\"Aymara\",\"isActive\":false,\"isDefault\":false},{\"code\":\"az\",\"description\":\"Azerbaijani\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ba\",\"description\":\"Bashkir\",\"isActive\":false,\"isDefault\":false},{\"code\":\"be\",\"description\":\"Byelorussian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bg\",\"description\":\"Bulgarian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bh\",\"description\":\"Bihari\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bi\",\"description\":\"Bislama\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bn\",\"description\":\"Bengali; Bangla\",\"isActive\":false,\"isDefault\":false},{\"code\":\"bo\",\"description\":\"Tibetan\",\"isActive\":false,\"isDefault\":false},{\"code\":\"br\",\"description\":\"Breton\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ca\",\"description\":\"Catalan\",\"isActive\":false,\"isDefault\":false},{\"code\":\"co\",\"description\":\"Corsican\",\"isActive\":false,\"isDefault\":false},{\"code\":\"cs\",\"description\":\"Czech\",\"isActive\":false,\"isDefault\":false},{\"code\":\"cy\",\"description\":\"Welsh\",\"isActive\":false,\"isDefault\":false},{\"code\":\"da\",\"description\":\"Danish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"de\",\"description\":\"German\",\"isActive\":false,\"isDefault\":false},{\"code\":\"dz\",\"description\":\"Bhutani\",\"isActive\":false,\"isDefault\":false},{\"code\":\"el\",\"description\":\"Greek\",\"isActive\":false,\"isDefault\":false},{\"code\":\"en\",\"description\":\"English\",\"isActive\":true,\"isDefault\":true},{\"code\":\"eo\",\"description\":\"Esperanto\",\"isActive\":false,\"isDefault\":false},{\"code\":\"es\",\"description\":\"Spanish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"et\",\"description\":\"Estonian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"eu\",\"description\":\"Basque\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fa\",\"description\":\"Persian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fi\",\"description\":\"Finnish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fj\",\"description\":\"Fiji\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fo\",\"description\":\"Faroese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fr\",\"description\":\"French\",\"isActive\":false,\"isDefault\":false},{\"code\":\"fy\",\"description\":\"Frisian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ga\",\"description\":\"Irish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"gd\",\"description\":\"Scots Gaelic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"gl\",\"description\":\"Galician\",\"isActive\":false,\"isDefault\":false},{\"code\":\"gn\",\"description\":\"Guarani\",\"isActive\":false,\"isDefault\":false},{\"code\":\"gu\",\"description\":\"Gujarati\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ha\",\"description\":\"Hausa\",\"isActive\":false,\"isDefault\":false},{\"code\":\"he\",\"description\":\"Hebrew (formerly iw)\",\"isActive\":false,\"isDefault\":false},{\"code\":\"hi\",\"description\":\"Hindi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"hr\",\"description\":\"Croatian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"hu\",\"description\":\"Hungarian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"hy\",\"description\":\"Armenian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ia\",\"description\":\"Interlingua\",\"isActive\":false,\"isDefault\":false},{\"code\":\"id\",\"description\":\"Indonesian (formerly in)\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ie\",\"description\":\"Interlingue\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ik\",\"description\":\"Inupiak\",\"isActive\":false,\"isDefault\":false},{\"code\":\"is\",\"description\":\"Icelandic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"it\",\"description\":\"Italian\",\"isActive\":true,\"isDefault\":false},{\"code\":\"iu\",\"description\":\"Inuktitut\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ja\",\"description\":\"Japanese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"jw\",\"description\":\"Javanese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ka\",\"description\":\"Georgian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"kk\",\"description\":\"Kazakh\",\"isActive\":false,\"isDefault\":false},{\"code\":\"kl\",\"description\":\"Greenlandic\",\"isActive\":false,\"isDefault\":false},{\"code\":\"km\",\"description\":\"Cambodian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"kn\",\"description\":\"Kannada\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ko\",\"description\":\"Korean\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ks\",\"description\":\"Kashmiri\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ku\",\"description\":\"Kurdish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ky\",\"description\":\"Kirghiz\",\"isActive\":false,\"isDefault\":false},{\"code\":\"la\",\"description\":\"Latin\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ln\",\"description\":\"Lingala\",\"isActive\":false,\"isDefault\":false},{\"code\":\"lo\",\"description\":\"Laothian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"lt\",\"description\":\"Lithuanian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"lv\",\"description\":\"Latvian, Lettish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mg\",\"description\":\"Malagasy\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mi\",\"description\":\"Maori\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mk\",\"description\":\"Macedonian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ml\",\"description\":\"Malayalam\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mn\",\"description\":\"Mongolian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mo\",\"description\":\"Moldavian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mr\",\"description\":\"Marathi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ms\",\"description\":\"Malay\",\"isActive\":false,\"isDefault\":false},{\"code\":\"mt\",\"description\":\"Maltese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"my\",\"description\":\"Burmese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"na\",\"description\":\"Nauru\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ne\",\"description\":\"Nepali\",\"isActive\":false,\"isDefault\":false},{\"code\":\"nl\",\"description\":\"Dutch\",\"isActive\":false,\"isDefault\":false},{\"code\":\"no\",\"description\":\"Norwegian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"oc\",\"description\":\"Occitan\",\"isActive\":false,\"isDefault\":false},{\"code\":\"om\",\"description\":\"(Afan) Oromo\",\"isActive\":false,\"isDefault\":false},{\"code\":\"or\",\"description\":\"Oriya\",\"isActive\":false,\"isDefault\":false},{\"code\":\"pa\",\"description\":\"Punjabi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"pl\",\"description\":\"Polish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ps\",\"description\":\"Pashto, Pushto\",\"isActive\":false,\"isDefault\":false},{\"code\":\"pt\",\"description\":\"Portuguese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"qu\",\"description\":\"Quechua\",\"isActive\":false,\"isDefault\":false},{\"code\":\"rm\",\"description\":\"Rhaeto-Romance\",\"isActive\":false,\"isDefault\":false},{\"code\":\"rn\",\"description\":\"Kirundi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ro\",\"description\":\"Romanian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ru\",\"description\":\"Russian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"rw\",\"description\":\"Kinyarwanda\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sa\",\"description\":\"Sanskrit\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sd\",\"description\":\"Sindhi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sg\",\"description\":\"Sangho\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sh\",\"description\":\"Serbo-Croatian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"si\",\"description\":\"Sinhalese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sk\",\"description\":\"Slovak\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sl\",\"description\":\"Slovenian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sm\",\"description\":\"Samoan\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sn\",\"description\":\"Shona\",\"isActive\":false,\"isDefault\":false},{\"code\":\"so\",\"description\":\"Somali\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sq\",\"description\":\"Albanian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sr\",\"description\":\"Serbian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ss\",\"description\":\"Siswati\",\"isActive\":false,\"isDefault\":false},{\"code\":\"st\",\"description\":\"Sesotho\",\"isActive\":false,\"isDefault\":false},{\"code\":\"su\",\"description\":\"Sundanese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sv\",\"description\":\"Swedish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"sw\",\"description\":\"Swahili\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ta\",\"description\":\"Tamil\",\"isActive\":false,\"isDefault\":false},{\"code\":\"te\",\"description\":\"Telugu\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tg\",\"description\":\"Tajik\",\"isActive\":false,\"isDefault\":false},{\"code\":\"th\",\"description\":\"Thai\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ti\",\"description\":\"Tigrinya\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tk\",\"description\":\"Turkmen\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tl\",\"description\":\"Tagalog\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tn\",\"description\":\"Setswana\",\"isActive\":false,\"isDefault\":false},{\"code\":\"to\",\"description\":\"Tonga\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tr\",\"description\":\"Turkish\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ts\",\"description\":\"Tsonga\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tt\",\"description\":\"Tatar\",\"isActive\":false,\"isDefault\":false},{\"code\":\"tw\",\"description\":\"Twi\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ug\",\"description\":\"Uighur\",\"isActive\":false,\"isDefault\":false},{\"code\":\"uk\",\"description\":\"Ukrainian\",\"isActive\":false,\"isDefault\":false},{\"code\":\"ur\",\"description\":\"Urdu\",\"isActive\":false,\"isDefault\":false},{\"code\":\"uz\",\"description\":\"Uzbek\",\"isActive\":false,\"isDefault\":false},{\"code\":\"vi\",\"description\":\"Vietnamese\",\"isActive\":false,\"isDefault\":false},{\"code\":\"vo\",\"description\":\"Volapuk\",\"isActive\":false,\"isDefault\":false},{\"code\":\"wo\",\"description\":\"Wolof\",\"isActive\":false,\"isDefault\":false},{\"code\":\"xh\",\"description\":\"Xhosa\",\"isActive\":false,\"isDefault\":false},{\"code\":\"yi\",\"description\":\"Yiddish (formerly ji)\",\"isActive\":false,\"isDefault\":false},{\"code\":\"yo\",\"description\":\"Yoruba\",\"isActive\":false,\"isDefault\":false},{\"code\":\"za\",\"description\":\"Zhuang\",\"isActive\":false,\"isDefault\":false},{\"code\":\"zh\",\"description\":\"Chinese - Traditional\",\"isActive\":false,\"isDefault\":false},{\"code\":\"zhs\",\"description\":\"Chinese - Simplified\",\"isActive\":false,\"isDefault\":false},{\"code\":\"zu\",\"description\":\"Zulu\",\"isActive\":false,\"isDefault\":false}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":0,\"lastPage\":1,\"totalItems\":140,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetPageModels(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Page Models OPTIONS Interaction")
                .path("/entando/api/pageModels")
                .method("OPTIONS")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "1");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The Page Models GET Interaction")
                .path("/entando/api/pageModels")
                .method("GET")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "1");
        return standardResponse(request, "{\"payload\":[{\"code\":\"1SLNM_TEST_4883\",\"descr\":\"1SLNM_TEST_4883\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"SeleniumCell\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":4,\"totalItems\":4,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetGroups(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/groups")
                .method("OPTIONS")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "1");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The Groups GET Interaction")
                .path("/entando/api/groups")
                .method("GET")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "1");
        String json = "{\"payload\":[{\"code\":\"1slnm_test_8645\",\"name\":\"1SLNM_TEST_8645\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":7,\"totalItems\":7,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}";
        return standardResponse(request, json);
    }

    public static PactDslResponse standardResponse(PactDslRequestWithPath request, String json) {
        return addStandardHeaders(request
                .willRespondWith()
                .status(200)
                .body(toDslJsonBody(json)));
    }

    public static PactDslResponse buildGetWidgets(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Widgets OPTIONS Interaction")
                .path("/entando/api/widgets")
                .method("OPTIONS")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "1");
        PactDslResponse optionsrResponse = optionsResponse(optionsRequest);

        return standardResponse(optionsrResponse.uponReceiving("The Widgets GET Interaction")
                .path("/entando/api/widgets")
                .method("GET")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "1"), "{\"payload\":[{\"code\":\"bpm-case-actions\",\"used\":0,\"titles\":{\"en\":\"PAM-Case actions\",\"it\":\"Azioni caso PAM\"},\"typology\":\"jpkiebpm\",\"group\":null,\"pluginCode\":\"jpkiebpm\",\"pluginDesc\":\"Entando Red Hat PAM Connector\",\"guiFragments\":[],\"hasConfig\":true}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":22,\"totalItems\":22,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetPageStatus(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Page Status OPTIONS Interaction")
                .path("/entando/api/dashboard/pageStatus")
                .method("OPTIONS");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse

                .uponReceiving("The Page Status GET Interaction")
                .path("/entando/api/dashboard/pageStatus")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"published\":5,\"unpublished\":0,\"draft\":0,\"lastUpdate\":\"2017-02-18 00:12:24\"},\"errors\":[],\"metaData\":{}}");
    }

    public static PactDslResponse buildGetPages(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Page Query Interaction")
                .path("/entando/api/pages/search")
                .method("OPTIONS")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "5")
                .matchQuery("sort", "lastModified")
                .matchQuery("direction", "DESC");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The Page Query Interaction")
                .path("/entando/api/pages/search")
                .method("GET")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "5")
                .matchQuery("sort", "lastModified")
                .matchQuery("direction", "DESC")
                .matchHeader("accept", "\\*\\/\\*");
        return standardResponse(request, "{\"payload\":[{\"code\":\"homepage\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"home\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Home\",\"it\":\"Home\"},\"fullTitles\":{\"en\":\"Home\",\"it\":\"Home\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"service\"],\"position\":-1,\"numWidget\":0,\"lastModified\":\"2017-02-18 00:12:24\",\"fullPath\":\"homepage\",\"token\":null},{\"code\":\"errorpage\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Error page\",\"it\":\"Pagina di errore\"},\"fullTitles\":{\"en\":\"Home / Service / Error page\",\"it\":\"Home / Pagine di Servizio / Pagina di errore\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":2,\"numWidget\":0,\"lastModified\":\"2017-02-17 21:11:54\",\"fullPath\":\"homepage/service/errorpage\",\"token\":null},{\"code\":\"notfound\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Page not found\",\"it\":\"Pagina non trovata\"},\"fullTitles\":{\"en\":\"Home / Service / Page not found\",\"it\":\"Home / Pagine di Servizio / Pagina non trovata\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":1,\"numWidget\":0,\"lastModified\":\"2017-02-17 16:37:10\",\"fullPath\":\"homepage/service/notfound\",\"token\":null},{\"code\":\"login\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"service\",\"seo\":false,\"titles\":{\"en\":\"Login\",\"it\":\"Pagina di login\"},\"fullTitles\":{\"en\":\"Home / Service / Login\",\"it\":\"Home / Pagine di Servizio / Pagina di login\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":3,\"numWidget\":0,\"lastModified\":\"2017-02-17 15:32:34\",\"fullPath\":\"homepage/service/login\",\"token\":null},{\"code\":\"service\",\"status\":\"published\",\"displayedInMenu\":false,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Service\",\"it\":\"Pagine di Servizio\"},\"fullTitles\":{\"en\":\"Home / Service\",\"it\":\"Home / Pagine di Servizio\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"notfound\",\"errorpage\",\"login\"],\"position\":1,\"numWidget\":0,\"lastModified\":\"2017-02-17 13:06:24\",\"fullPath\":\"homepage/service\",\"token\":null}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":5,\"lastPage\":1,\"totalItems\":5,\"sort\":\"lastModified\",\"direction\":\"DESC\",\"additionalParams\":{},\"pageCodeToken\":null}}");
    }

    public static PactDslResponse buildGetAccessToken(PactDslWithProvider builder) {
        return builder
                .uponReceiving("The Login  interaction")
                .path("/entando/OAuth2/access_token")
                .method("POST")
                .matchHeader("Content-Type", "application\\/x-www-form-urlencoded")
                .matchHeader("Accept", "\\*\\/\\*")
                .body("username=admin&password=adminadmin&grant_type=password&client_id=true&client_secret=true")
                .willRespondWith()
                .status(200)
                .matchHeader("Access-Control-Allow-Methods",MATCH_ANY_HTTP_VERB, "GET, POST, PUT, DELETE, OPTIONS")
                .matchHeader("Access-Control-Allow-Origin","\\*. ", "*")
                .matchHeader("Set-Cookie", "JESSIONID.+", "JSESSIONID=082F7mzH9gxErT4EnT9L7PL6PLwgarDsQmK5Pov9.a006c24d3c59; path=/entando")
                .matchHeader("Access-Control-Allow-Headers",".+", "Content-Type, Authorization")
                .body(toDslJsonBody("{\"access_token\":\"564e2bd3f55363e6d1fc0f53b0580bb3\",\"refresh_token\":\"c007499217281691532aa71b530e3fad\",\"expires_in\":3600}"));
    }

    public static PactDslResponse buildGetProfileTypesList(PactDslResponse builder, String sort, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/profileTypes")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/profileTypes")
                .method("GET")
                .matchQuery("sort", "\\w+", "" + sort)
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":2,\"totalItems\":11,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static void main(String[] args) {
        System.out.println(Pattern.compile("((GET|HEAD|POST|DELETE|TRACE|OPTIONS|PATCH|PUT),){1,8}").matcher("GET, POST, PUT, DELETE, OPTIONS").find());
    }

    public static PactDslResponse addStandardHeaders(PactDslResponse response) {
        response
                //.matchHeader("Allow", MATCH_ANY_HTTP_VERB, "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH")
                .matchHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS", "GET, POST, PUT, DELETE, OPTIONS")
                .matchHeader("Access-Control-Allow-Origin", "\\*","*")
                .matchHeader("Access-Control-Max-Age", "3600", "3600")
                .matchHeader("Connection", "keep-alive", "keep-alive")
                .matchHeader("Access-Control-Allow-Headers", ".+", "Content-Type, Authorization");
        return response;
    }

    public static PactDslResponse optionsResponse(PactDslRequestWithPath optionsRequest) {
        return addStandardHeaders(optionsRequest
                .willRespondWith()
                .status(200)
                .body(""));
    }

    public static PactDslResponse buildGetCategories(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Categories OPTIONS Interaction")
                .path("/entando/api/categories/home")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Categories GET Interaction")
                .path("/entando/api/categories/home")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"code\":\"home\",\"parentCode\":\"home\",\"titles\":{\"en\":\"All\",\"it\":\"Generale\"},\"fullTitles\":{\"en\":\"All\",\"it\":\"Generale\"},\"children\":[\"category3\",\"categorytest\",\"categorytest00\",\"categorytest2\",\"jpcollaboration_categoryRoot\",\"jptagcloud_categoryRoot\",\"seleniumtest_donttouch\"],\"references\":{\"jpcollaborationIdeaManager\":false,\"DataObjectManager\":false,\"jacmsResourceManager\":false,\"jacmsContentManager\":false}},\"errors\":[],\"metaData\":{}}");
    }

    public static PactDslResponse buildGetCategoriesParentCode(PactDslResponse builder, String parentCode) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Categories parentCode OPTIONS Interaction")
                .path("/entando/api/categories")
                .method("OPTIONS")
                .matchQuery("parentCode", "\\w+", "" + parentCode)
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Categories parentCode GET Interaction")
                .path("/entando/api/categories")
                .method("GET")
                .matchQuery("parentCode", "\\w+", "" + parentCode);
        return standardResponse(request, "{\"payload\":[{\"code\":\"category3\",\"parentCode\":\"home\",\"titles\":{\"en\":\"Category3\",\"it\":\"Category3\"},\"fullTitles\":{\"en\":\"All / Category3\",\"it\":\"Generale / Category3\"},\"children\":[\"testcategory31\"],\"references\":{}},{\"code\":\"categorytest\",\"parentCode\":\"home\",\"titles\":{\"en\":\"CategoryTest\",\"it\":\"CategoryTest\"},\"fullTitles\":{\"en\":\"All / CategoryTest\",\"it\":\"Generale / CategoryTest\"},\"children\":[\"category_se\",\"category_se1\"],\"references\":{}},{\"code\":\"categorytest00\",\"parentCode\":\"home\",\"titles\":{\"en\":\"CategoryTest00\",\"it\":\"CategoryTest00\"},\"fullTitles\":{\"en\":\"All / CategoryTest00\",\"it\":\"Generale / CategoryTest00\"},\"children\":[],\"references\":{}},{\"code\":\"categorytest2\",\"parentCode\":\"home\",\"titles\":{\"en\":\"CategoryTest2\",\"it\":\"CategoryTest2\"},\"fullTitles\":{\"en\":\"All / CategoryTest2\",\"it\":\"Generale / CategoryTest2\"},\"children\":[],\"references\":{}},{\"code\":\"jpcollaboration_categoryRoot\",\"parentCode\":\"home\",\"titles\":{\"en\":\"Crowd Sourcing Root\",\"it\":\"Crowd Sourcing Root\"},\"fullTitles\":{\"en\":\"All / Crowd Sourcing Root\",\"it\":\"Generale / Crowd Sourcing Root\"},\"children\":[],\"references\":{}},{\"code\":\"jptagcloud_categoryRoot\",\"parentCode\":\"home\",\"titles\":{\"en\":\"Tag Cloud Root\",\"it\":\"Tag Cloud Root\"},\"fullTitles\":{\"en\":\"All / Tag Cloud Root\",\"it\":\"Generale / Tag Cloud Root\"},\"children\":[],\"references\":{}},{\"code\":\"seleniumtest_donttouch\",\"parentCode\":\"home\",\"titles\":{\"en\":\"SeleniumTest_DontTouch\",\"it\":\"SeleniumTest_DontTouch\"},\"fullTitles\":{\"en\":\"All / SeleniumTest_DontTouch\",\"it\":\"Generale / SeleniumTest_DontTouch\"},\"children\":[],\"references\":{}}],\"errors\":[],\"metaData\":{\"parentCode\":\"home\"}}");
    }

    public static PactDslResponse buildGetRoles(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/roles")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/roles")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"unimportant\",\"name\":\"UNIMPORTANT\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":2,\"totalItems\":11,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetDataModels(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Models OPTIONS Interaction")
                .path("/entando/api/dataModels")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize)
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Models GET Interaction")
                .path("/entando/api/dataModels")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"modelId\":\"100\",\"descr\":\"unimportant\",\"type\":\"AAA\",\"model\":\"unimportant\",\"stylesheet\":\"unimportant\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"modelId\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetDataTypes(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Types OPTIONS Interaction")
                .path("/entando/api/dataTypes")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize)
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Types GET Interaction")
                .path("/entando/api/dataTypes")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"PCT\",\"name\":\"pactDataType\",\"status\":\"0\"},{\"code\":\"AAA\",\"name\":\"AAA\",\"status\":\"0\"},{\"code\":\"SLL\",\"name\":\"SeleniumTest_DontTouch2\",\"status\":\"0\"},{\"code\":\"SLM\",\"name\":\"SeleniumTest_DontTouch1\",\"status\":\"0\"},{\"code\":\"SLN\",\"name\":\"SeleniumTest_DontTouch\",\"status\":\"0\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":4,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetDataTypesStatus(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Data Models OPTIONS Interaction")
                .path("/entando/api/dataTypesStatus")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The Data Models GET Interaction")
                .path("/entando/api/dataTypesStatus")
                .method("GET");
        return standardResponse(request, "{\"payload\":{\"ready\":[\"AAA\",\"SLN\",\"SLM\",\"SLL\"],\"toRefresh\":[],\"refreshing\":[]},\"errors\":[],\"metaData\":{}}");
    }

    public static PactDslResponse buildGetFragments(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/fragments")
                .method("OPTIONS")
                .headers("Access-control-request-method", "GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", "" + pageSize);
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.
                uponReceiving("The User Query GET Interaction")
                .path("/entando/api/fragments")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"PCT\",\"locked\":false,\"widgetType\":{\"code\":null,\"title\":null},\"pluginCode\":null},{\"code\":\"AAA\",\"locked\":false,\"widgetType\":{\"code\":null,\"title\":null},\"pluginCode\":null},{\"code\":\"ANN_Archive\",\"locked\":false,\"widgetType\":{\"code\":\"ANN_Archive\",\"title\":null},\"pluginCode\":null}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":12,\"totalItems\":116,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetPagesParentCode(PactDslResponse builder, String parentCode) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The home page parentCode OPTIONS Interaction")
                .path("/entando/api/pages")
                .method("OPTIONS")
                .matchQuery("parentCode", "\\w+", "" + parentCode)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The home page parentCode GET Interaction")
                .path("/entando/api/pages")
                .method("GET")
                .matchQuery("parentCode", "\\w+", "" + parentCode);
        return standardResponse(request, "{\"payload\":[{\"code\":\"service\",\"status\":\"published\",\"displayedInMenu\":false,\"pageModel\":\"service\",\"charset\":\"utf8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Service\",\"it\":\"Pagine di Servizio\"},\"fullTitles\":{\"en\":\"Home / Service\",\"it\":\"Home / Pagine di Servizio\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"notfound\",\"errorpage\",\"login\"],\"position\":1,\"numWidget\":1,\"lastModified\":\"2018-09-13 13:36:35\",\"fullPath\":\"homepage/service\",\"token\":null},{\"code\":\"page_se\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":true,\"titles\":{\"en\":\"Page_se\",\"it\":\"Page_se\"},\"fullTitles\":{\"en\":\"Home / Page_se\",\"it\":\"Home / Page_se\"},\"ownerGroup\":\"administrators\",\"joinGroups\":[\"free\",\"administrators\"],\"children\":[\"page_se_cl\"],\"position\":2,\"numWidget\":2,\"lastModified\":\"2018-10-02 15:29:56\",\"fullPath\":\"homepage/page_se\",\"token\":null},{\"code\":\"pagina4\",\"status\":\"unpublished\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Pagina4\",\"it\":\"Pagina4\"},\"fullTitles\":{\"en\":\"Home / Pagina4\",\"it\":\"Home / Pagina4\"},\"ownerGroup\":\"administrators\",\"joinGroups\":[],\"children\":[],\"position\":3,\"numWidget\":0,\"lastModified\":\"2018-10-12 09:47:56\",\"fullPath\":\"homepage/pagina4\",\"token\":null},{\"code\":\"node1\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"Node1\",\"it\":\"Nodo1\"},\"fullTitles\":{\"en\":\"Home / Node1\",\"it\":\"Home / Nodo1\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[\"node11\"],\"position\":4,\"numWidget\":5,\"lastModified\":\"2018-10-02 15:39:08\",\"fullPath\":\"homepage/node1\",\"token\":null},{\"code\":\"pamtesting1\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"PAMTesting1\",\"it\":\"PAMTesting1\"},\"fullTitles\":{\"en\":\"Home / PAMTesting1\",\"it\":\"Home / PAMTesting1\"},\"ownerGroup\":\"administrators\",\"joinGroups\":[],\"children\":[],\"position\":5,\"numWidget\":4,\"lastModified\":\"2018-10-08 16:03:49\",\"fullPath\":\"homepage/pamtesting1\",\"token\":null},{\"code\":\"pagina5\",\"status\":\"published\",\"displayedInMenu\":true,\"pageModel\":\"entando-page-inspinia\",\"charset\":\"utf-8\",\"contentType\":\"text/html\",\"parentCode\":\"homepage\",\"seo\":false,\"titles\":{\"en\":\"pagina5\",\"it\":\"pagina5\",\"zh\":\"PaginaDelta\"},\"fullTitles\":{\"en\":\"Home / pagina5\",\"it\":\"Home / pagina5\",\"zh\":\"homepage / PaginaDelta\"},\"ownerGroup\":\"free\",\"joinGroups\":[],\"children\":[],\"position\":6,\"numWidget\":3,\"lastModified\":\"2018-10-11 13:43:04\",\"fullPath\":\"homepage/pagina5\",\"token\":null}],\"errors\":[],\"metaData\":{\"parentCode\":\"homepage\"}}");
    }

    public static PactDslResponse buildGetPageModelsList(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The page models OPTIONS Interaction")
                .path("/entando/api/pageModels")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The page models GET Interaction")
                .path("/entando/api/pageModels")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize);
        return standardResponse(request, "{\"payload\":[{\"code\":\"PCT\",\"descr\":\"PCT\",\"mainFrame\":-1,\"pluginCode\":null,\"template\":\"<>\",\"configuration\":{\"frames\":[{\"pos\":0,\"descr\":\"PactCell\",\"mainFrame\":false,\"defaultWidget\":null,\"sketch\":null}]}}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":4,\"totalItems\":4,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");

    }

    public static PactDslResponse buildGetFileBrowserList(PactDslResponse builder) {
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

    public static PactDslResponse buildGetDatabase(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The database OPTIONS Interaction")
                .path("/entando/api/database")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The database GET Interaction")
                .path("/entando/api/database")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize);

        return standardResponse(request, "{\"payload\":[],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":100,\"lastPage\":0,\"totalItems\":0,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    public static PactDslResponse buildGetLabels(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath optionsRequest = builder
                .uponReceiving("The labels OPTIONS Interaction")
                .path("/entando/api/labels")
                .method("OPTIONS")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize)
                .headers("Access-control-request-method", "GET")
                .body("");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse
                .uponReceiving("The labels  GET Interaction")
                .path("/entando/api/labels")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize", "\\d+", "" + pageSize);
        return standardResponse(request, "{\"payload\":[{\"key\":\"AAA\",\"titles\":{\"en\":\"My Title\",\"it\":\"Mio Titolo\"}},{\"key\":\"ADMINISTRATION_BASIC\",\"titles\":{\"en\":\"Normal\",\"it\":\"Normale\"}},{\"key\":\"ADMINISTRATION_BASIC_GOTO\",\"titles\":{\"en\":\"Go to the administration with normal client\",\"it\":\"Accedi con client normale\"}},{\"key\":\"ADMINISTRATION_MINT\",\"titles\":{\"en\":\"Advanced\",\"it\":\"Avanzata\"}},{\"key\":\"ADMINISTRATION_MINT_GOTO\",\"titles\":{\"en\":\"Go to the administration with advanced client\",\"it\":\"Accedi con client avanzato\"}},{\"key\":\"ALL\",\"titles\":{\"en\":\"All\",\"it\":\"Tutte\"}},{\"key\":\"ANN_DOCUMENTS\",\"titles\":{\"en\":\"Documents\",\"it\":\"Documenti\"}},{\"key\":\"ANN_FROM\",\"titles\":{\"en\":\"from\",\"it\":\"da\"}},{\"key\":\"ANN_READ_MORE\",\"titles\":{\"en\":\"View details\",\"it\":\"Dettagli\"}},{\"key\":\"ANN_TO\",\"titles\":{\"en\":\"to\",\"it\":\"a\"}}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":38,\"totalItems\":377,\"sort\":\"key\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

}
