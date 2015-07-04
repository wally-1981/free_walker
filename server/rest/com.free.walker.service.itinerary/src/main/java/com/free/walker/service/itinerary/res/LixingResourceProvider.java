package com.free.walker.service.itinerary.res;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.shiro.codec.Hex;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.DependencyException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

public class LixingResourceProvider implements ResourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(LixingResourceProvider.class);

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String USER = "user";
    public static final String SIGN = "sign";

    private static final String METHOD_SYNC = "?method=getUpdateProductInfo";
    private static final String METHOD_DETAIL = "?method=getProductDetail";

    private static final int BATCH_SIZE = 100;
    private static final String DEFAULT_CURRENCY = "CNY";

    private static final Calendar SINCE_2014_01_01 = Calendar.getInstance();

    private static final String PAYLOAD_TEMPLATE = "para={0}&user={1}&sign={2}";
    private static final String SIGN_TEMPLATE = "para{0}{1}";
    private static final String GET_UPDATE_PRODUCT_INFO_PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<product-code-search-request>"
        + "<update-date-start>{0}</update-date-start>"
        + "<start>{1}</start>"
        + "<end>{2}</end>"
        + "</product-code-search-request>";
    private static final String GET_PRODUCT_DETAIL_PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<product-detail-request>"
        + "<product-code>{0}</product-code>"
        + "<currency>{1}</currency>"
        + "</product-detail-request>";

    private static final XPathExpression PROD_CODE;
    private static final XPathExpression PROD_NAME;
    private static final XPathExpression LOCATION;
    private static final XPathExpression LATITUDE;
    private static final XPathExpression LONGITUDE;
    private static final XPathExpression DEST_NAME;
    private static final XPathExpression DEPT_NAME;
    private static final XPathExpression SUMMARY;
    private static final XPathExpression DESCRIPTION;
    private static final XPathExpression PHOTOS;
    private static final XPathExpression TAGS;
    private static final XPathExpression DURATION_TYPE;
    private static final XPathExpression DURATION_PRIMARY_PART;
    private static final XPathExpression DURATION_SECONDARY_PART;
    private static final XPathExpression PICK_UP_REMARK;
    private static final XPathExpression DROP_OFF_REMARK;
    private static final XPathExpression HIGHLIGHTS;
    private static final XPathExpression NET_PRICE;
    private static final XPathExpression RETAIL_PRICE;
    private static final XPathExpression INCLUSION;
    private static final XPathExpression EXCLUSION;
    private static final XPathExpression ATTENTION;
    private static final XPathExpression SPECIFICATIONS;

    static {
        SINCE_2014_01_01.set(Calendar.YEAR, 2014);
        SINCE_2014_01_01.set(Calendar.MONTH, Calendar.JANUARY);
        SINCE_2014_01_01.set(Calendar.DAY_OF_MONTH, 1);
        SINCE_2014_01_01.set(Calendar.HOUR_OF_DAY, 0);
        SINCE_2014_01_01.set(Calendar.MINUTE, 0);
        SINCE_2014_01_01.set(Calendar.SECOND, 0);
        SINCE_2014_01_01.set(Calendar.MILLISECOND, 0);

        XPathFactory factory = XPathFactory.newInstance();
        try {
            PROD_NAME = factory.newXPath().compile("/response/payload/product-detail/product-name");
            PROD_CODE = factory.newXPath().compile("/response/payload/product-detail/product-code");
            LOCATION = factory.newXPath().compile("/response/payload/product-detail/location");
            LATITUDE = factory.newXPath().compile("/response/payload/product-detail/lat");
            LONGITUDE = factory.newXPath().compile("/response/payload/product-detail/lon");
            DEST_NAME = factory.newXPath().compile("/response/payload/product-detail/dest-name");
            DEPT_NAME = factory.newXPath().compile("/response/payload/product-detail/depart-name");
            SUMMARY = factory.newXPath().compile("/response/payload/product-detail/short-description");
            DESCRIPTION = factory.newXPath().compile("/response/payload/product-detail/descriptions/description/content");
            PHOTOS = factory.newXPath().compile("/response/payload/product-detail/photos/product-photo/url");
            TAGS = factory.newXPath().compile("/response/payload/product-detail/tags/tag");
            DURATION_TYPE = factory.newXPath().compile("/response/payload/product-detail/duration-type");
            DURATION_PRIMARY_PART = factory.newXPath().compile("/response/payload/product-detail/duration-part1");
            DURATION_SECONDARY_PART = factory.newXPath().compile("/response/payload/product-detail/duration-part2");
            PICK_UP_REMARK = factory.newXPath().compile("/response/payload/product-detail/departure-remark");
            DROP_OFF_REMARK = factory.newXPath().compile("/response/payload/product-detail/drop-off-remark");
            HIGHLIGHTS = factory.newXPath().compile("/response/payload/product-detail/sale-points");
            NET_PRICE = factory.newXPath().compile("/response/payload/product-detail/net-price-from");
            RETAIL_PRICE = factory.newXPath().compile("/response/payload/product-detail/retail-price-from");
            INCLUSION = factory.newXPath().compile("/response/payload/product-detail/inclusions");
            EXCLUSION = factory.newXPath().compile("/response/payload/product-detail/exclusions");
            ATTENTION = factory.newXPath().compile("/response/payload/product-detail/attentions");
            SPECIFICATIONS = factory.newXPath().compile("/response/payload/product-detail/product-specifications/product-specification");
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }

    private HttpClient lixingRestClient;
    private ResourceProviderContext context;

    public ResourceProvider setup(ResourceProviderContext context) {
        String providerId = context.getContextAsString(ID, "");
        if (StringUtils.isBlank(providerId)) {
            throw new IllegalArgumentException();
        }

        if (StringUtils.isBlank(context.getContextAsString(NAME, ""))) {
            throw new IllegalArgumentException(LocalMessages.getMessage(
                LocalMessages.missing_resource_provider_settings, providerId, Constants.providers_provider_name));
        }

        if (StringUtils.isBlank(context.getContextAsString(URL, ""))) {
            throw new IllegalArgumentException(LocalMessages.getMessage(
                LocalMessages.missing_resource_provider_settings, providerId, Constants.providers_provider_url));
        }

        if (StringUtils.isBlank(context.getContextAsString(USER, ""))) {
            throw new IllegalArgumentException(LocalMessages.getMessage(
                LocalMessages.missing_resource_provider_settings, providerId, Constants.providers_provider_user));
        }

        if (StringUtils.isBlank(context.getContextAsString(SIGN, ""))) {
            throw new IllegalArgumentException(LocalMessages.getMessage(
                LocalMessages.missing_resource_provider_settings, providerId, Constants.providers_provider_sign));
        }

        this.lixingRestClient = HttpClientBuilder.create().build();
        this.context = context;

        return this;
    }

    public String getProviderId() {
        return context.getContextAsString(ID, "");
    }

    public String getProviderName() {
        return context.getContextAsString(NAME, "");
    }

    public Calendar getProviderSince() {
        return SINCE_2014_01_01;
    }

    public Vector<JsonArray> sync(boolean exhausted, Calendar since) throws DependencyException {
        Vector<JsonArray> syncResult = new Vector<JsonArray>(3);
        int start = 1, end = BATCH_SIZE;
        JsonArrayBuilder addedResBuilder = Json.createArrayBuilder();
        JsonArrayBuilder updatedResBuilder = Json.createArrayBuilder();
        JsonArrayBuilder deletedResBuilder = Json.createArrayBuilder();
        boolean completed = false;
        while (!completed) {
            Vector<LixingProductSyncMeta> details = internalSync(start, end, since);

            fullSync(details, addedResBuilder, updatedResBuilder, deletedResBuilder);

            if (exhausted) {
                if (details.size() == BATCH_SIZE) {
                    start = end + 1;
                    end = start + BATCH_SIZE - 1;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        syncResult.add(addedResBuilder.build());
        syncResult.add(updatedResBuilder.build());
        syncResult.add(deletedResBuilder.build());

        return syncResult;
    }

    public boolean ping() throws DependencyException {
        Vector<LixingProductSyncMeta> details = internalSync(1, 3, SINCE_2014_01_01);
        return details.size() == 3;
    }

    public boolean sanitize() {
        lixingRestClient = null;
        lixingRestClient = HttpClientBuilder.create().build();
        return true;
    }

    private String simpleFind(Document doc, String name) {
        NodeList elements = doc.getElementsByTagName(name);
        if (elements.getLength() == 0) {
            return "";
        } else {
            return elements.item(0).getTextContent().trim();
        }
    }

    private void fullSync(Vector<LixingProductSyncMeta> products, JsonArrayBuilder addedRes,
        JsonArrayBuilder updatedRes, JsonArrayBuilder deletedRes) throws DependencyException {
        String url = new StringBuilder(context.getContextAsString(URL, "")).append(METHOD_DETAIL).toString();
        String user = context.getContextAsString(USER, "");

        for (int i = 0; i < products.size(); i++) {
            LixingProductSyncMeta productSyncMeta = products.get(i);

            LOG.info(LocalMessages.getMessage(LocalMessages.full_sync_resource, this.getProviderId(),
                this.getProviderName(), productSyncMeta.getUpdateTime(), productSyncMeta.getCode(),
                productSyncMeta.getName(), productSyncMeta.getAction()));

            if (UpdateAction.OFF_SHELF.equals(productSyncMeta.getAction())) {
                deletedRes.add(productSyncMeta.toJson());
                continue;
            }

            String payload = MessageFormat.format(GET_PRODUCT_DETAIL_PAYLOAD, productSyncMeta.getCode(),
                DEFAULT_CURRENCY);

            JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
            HttpPost post = new HttpPost();
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(MessageFormat.format(SIGN_TEMPLATE, payload,
                    context.getContextAsString(SIGN, "")).getBytes());
                String sign = Hex.encodeToString(messageDigest.digest());

                String signedPayload = MessageFormat.format(PAYLOAD_TEMPLATE, payload, user, sign);
                post.setEntity(new StringEntity(signedPayload));
                post.setURI(new URI(url));
                post.addHeader(HttpHeaders.CONTENT_TYPE, "UTF-8");
                HttpResponse response = lixingRestClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(response.getEntity().getContent());
                    Document doc = docBuilder.parse(inputSource);

                    LOG.info("************************************************************************************");

                    LOG.info(Introspection.JSONKeys.Resounce.PROVIDER_ID + ":" + getProviderId());
                    LOG.info(Introspection.JSONKeys.Resounce.PROVIDER_NAME + ":" + getProviderName());
                    LOG.info(Introspection.JSONKeys.Resounce.LEVEL_1_CATEGORY + ":" + "");
                    LOG.info(Introspection.JSONKeys.Resounce.LEVEL_2_CATEGORY + ":" + "");
                    LOG.info(Introspection.JSONKeys.Resounce.CODE + ":" + PROD_CODE.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.NAME + ":" + PROD_NAME.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.LOCATION + ":" + LOCATION.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.LATITUDE + ":" + LATITUDE.evaluate(doc, XPathConstants.NUMBER));
                    LOG.info(Introspection.JSONKeys.Resounce.LONGITUDE + ":" + LONGITUDE.evaluate(doc, XPathConstants.NUMBER));
                    LOG.info(Introspection.JSONKeys.Resounce.DEST_NAME + ":" + DEST_NAME.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.DEPT_NAME + ":" + DEPT_NAME.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.SUMMARY + ":" + SUMMARY.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.DESCRIPTION + ":" + DESCRIPTION.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.DURATION_TYPE + ":" + DURATION_TYPE.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.DURATION_PRIMARY_PART + ":" + DURATION_PRIMARY_PART.evaluate(doc, XPathConstants.NUMBER));
                    LOG.info(Introspection.JSONKeys.Resounce.DURATION_SECONDARY_PART + ":" + DURATION_SECONDARY_PART.evaluate(doc, XPathConstants.NUMBER));
                    LOG.info(Introspection.JSONKeys.Resounce.PICK_UP_REMARK + ":" + PICK_UP_REMARK.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.DROP_OFF_REMARK + ":" + DROP_OFF_REMARK.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.NET_PRICE + ":" + NET_PRICE.evaluate(doc, XPathConstants.NUMBER));
                    LOG.info(Introspection.JSONKeys.Resounce.RETAIL_PRICE + ":" + RETAIL_PRICE.evaluate(doc, XPathConstants.NUMBER));
                    LOG.info(Introspection.JSONKeys.Resounce.INCLUSION + ":" + INCLUSION.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.EXCLUSION + ":" + EXCLUSION.evaluate(doc, XPathConstants.STRING));
                    LOG.info(Introspection.JSONKeys.Resounce.ATTENTION + ":" + ATTENTION.evaluate(doc, XPathConstants.STRING));

                    NodeList photoUrls = (NodeList) PHOTOS.evaluate(doc, XPathConstants.NODESET);
                    for (int j = 0; j < photoUrls.getLength(); j++) {
                        LOG.info(Introspection.JSONKeys.Resounce.PHOTOS + (j + 1) + ":" + photoUrls.item(j).getTextContent());
                    }

                    NodeList tags = (NodeList) TAGS.evaluate(doc, XPathConstants.NODESET);
                    for (int j = 0; j < tags.getLength(); j++) {
                        LOG.info(Introspection.JSONKeys.Resounce.TAGS + (j + 1) + ":" + tags.item(j).getTextContent());
                    }

                    NodeList highlights = (NodeList) HIGHLIGHTS.evaluate(doc, XPathConstants.NODESET);
                    for (int j = 0; j < highlights.getLength(); j++) {
                        LOG.info(Introspection.JSONKeys.Resounce.HIGHLIGHTS + (j + 1) + ":" + highlights.item(j).getTextContent());
                    }

                    NodeList specifications = (NodeList) SPECIFICATIONS.evaluate(doc, XPathConstants.NODESET);
                    for (int j = 0; j < specifications.getLength(); j++) {
                        Node spec = specifications.item(j);
                        Node specType = spec.getFirstChild().getNextSibling();
                        Node crowdType = specType.getNextSibling();
                        Node specName = crowdType.getNextSibling().getNextSibling();
                        Node minAge = specName.getNextSibling().getNextSibling();
                        Node maxAge = minAge.getNextSibling();
                        Node unit = maxAge.getNextSibling().getNextSibling();
                        LOG.info(Introspection.JSONKeys.Resounce.SPECIFICATIONS + (j + 1) + ":" + specType.getTextContent());
                        LOG.info(Introspection.JSONKeys.Resounce.SPECIFICATIONS + (j + 1) + ":" + crowdType.getTextContent());
                        LOG.info(Introspection.JSONKeys.Resounce.SPECIFICATIONS + (j + 1) + ":" + specName.getTextContent());
                        LOG.info(Introspection.JSONKeys.Resounce.SPECIFICATIONS + (j + 1) + ":" + minAge.getTextContent());
                        LOG.info(Introspection.JSONKeys.Resounce.SPECIFICATIONS + (j + 1) + ":" + maxAge.getTextContent());
                        LOG.info(Introspection.JSONKeys.Resounce.SPECIFICATIONS + (j + 1) + ":" + unit.getTextContent());
                    }

                    LOG.info("************************************************************************************");

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);

                    File dir = new File(MessageFormat.format("/tmp/{0}/{1}/{2}", getProviderName(),
                        simpleFind(doc, "category"), simpleFind(doc, "sub-category")));
                    dir.mkdirs();
                    StreamResult result = new StreamResult(new File(dir, MessageFormat.format("{1}.xml",
                        getProviderName(), productSyncMeta.getCode())));
                    transformer.transform(source, result);
                } else {
                    throw new DependencyException(IOUtils.toString(response.getEntity().getContent()));
                }
            } catch (IOException e) {
                throw new DependencyException(e);
            } catch (URISyntaxException e) {
                throw new DependencyException(e);
            } catch (IllegalStateException e) {
                throw new DependencyException(e);
            } catch (ParserConfigurationException e) {
                throw new DependencyException(e);
            } catch (SAXException e) {
                throw new DependencyException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new DependencyException(e);
            } catch (TransformerConfigurationException e) {
                throw new DependencyException(e);
            } catch (TransformerException e) {
                throw new DependencyException(e);
            } catch (XPathExpressionException e) {
                throw new DependencyException(e);
            } finally {
                post.abort();
            }

            if (UpdateAction.RELEASE.equals(productSyncMeta.getAction())) {
                addedRes.add(resourceBuilder.build());
                continue;
            } else if (UpdateAction.UPDATE.equals(productSyncMeta.getAction())) {
                updatedRes.add(resourceBuilder.build());
                continue;
            } else {
                continue;
            }
        }

        return;
    }

    private Vector<LixingProductSyncMeta> internalSync(int start, int end, Calendar since) throws DependencyException {
        Vector<LixingProductSyncMeta> result = new Vector<LixingProductSyncMeta>();

        String url = new StringBuilder(context.getContextAsString(URL, "")).append(METHOD_SYNC).toString();
        String sinceDateStr = new SimpleDateFormat("yyyy-MM-dd").format(since.getTime());
        String payload = MessageFormat.format(GET_UPDATE_PRODUCT_INFO_PAYLOAD, sinceDateStr, start, end);
        String user = context.getContextAsString(USER, "");

        HttpPost post = new HttpPost();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(MessageFormat.format(SIGN_TEMPLATE, payload,
                context.getContextAsString(SIGN, "")).getBytes());
            String sign = Hex.encodeToString(messageDigest.digest());

            String signedPayload = MessageFormat.format(PAYLOAD_TEMPLATE, payload, user, sign);
            post.setEntity(new StringEntity(signedPayload));
            post.setURI(new URI(url));
            post.addHeader(HttpHeaders.CONTENT_TYPE, "UTF-8");
            HttpResponse response = lixingRestClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.OK_200) {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                InputSource inputSource = new InputSource(response.getEntity().getContent());
                Document doc = docBuilder.parse(inputSource);
                NodeList productsInfo = doc.getElementsByTagName("product-code-info");
                for (int i = 0; i < productsInfo.getLength(); i++) {
                    LixingProductSyncMeta detail = new LixingProductSyncMeta();
                    Node productInfo = productsInfo.item(i);
                    NodeList productInfoItems = productInfo.getChildNodes();
                    for (int j = 0; j < productInfoItems.getLength(); j++) {
                        Node productInfoItem = productInfoItems.item(j);
                        String key = productInfoItem.getNodeName();
                        String value = productInfoItem.getTextContent();
                        if ("product-code".equals(key)) {
                            detail.setCode(value);
                        } else if ("product-name".equals(key)) {
                            detail.setName(value);
                        } else if ("last-update-time".equals(key)) {
                            detail.setUpdateTime(value);
                        } else if ("last-update-action".equals(key)) {
                            detail.setAction(UpdateAction.valueOf(value));
                        } else {
                            ;
                        }
                    }
                    result.add(detail);
                }
            } else {
                throw new DependencyException(IOUtils.toString(response.getEntity().getContent()));
            }
        } catch (IOException e) {
            throw new DependencyException(e);
        } catch (URISyntaxException e) {
            throw new DependencyException(e);
        } catch (IllegalStateException e) {
            throw new DependencyException(e);
        } catch (ParserConfigurationException e) {
            throw new DependencyException(e);
        } catch (SAXException e) {
            throw new DependencyException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new DependencyException(e);
        } finally {
            post.abort();
        }

        return result;
    }

    private class LixingProductSyncMeta {
        private String code;
        private String name;
        private String updateTime;
        private UpdateAction action;
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getUpdateTime() {
            return updateTime;
        }
        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
        public UpdateAction getAction() {
            return action;
        }
        public void setAction(UpdateAction action) {
            this.action = action;
        }
        public JsonObject toJson() {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add(Introspection.JSONKeys.Resounce.CODE, getCode());
            builder.add(Introspection.JSONKeys.Resounce.NAME, getName());
            builder.add(Introspection.JSONKeys.Resounce.SYNC_ACTION, getAction().name());
            return builder.build();
        }
    }

    private enum UpdateAction {
        RELEASE, UPDATE, OFF_SHELF
    }
}
