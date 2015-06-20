package com.free.walker.service.itinerary.res;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.DependencyException;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

public class LixingResourceProvider implements ResourceProvider {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String USER = "user";
    public static final String SIGN = "sign";

    private static final String SYNC_METHOD = "method=getUpdateProductInfo";

    private static final int BATCH_SIZE = 100;

    private static final Calendar SINCE_2014_01_01 = Calendar.getInstance();

    private static final String PAYLOAD_TEMPLATE = "para={0}&user={1}&sign={2}";
    private static final String SIGN_TEMPLATE = "para{0}{1}";
    private static final String GET_UPDATE_PRODUCT_INFO_PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<product-code-search-request>"
        + "<update-date-start>{0}</update-date-start>"
        + "<start>{1}</start>"
        + "<end>{2}</end>"
        + "</product-code-search-request>";

    static {
        SINCE_2014_01_01.set(Calendar.YEAR, 2014);
        SINCE_2014_01_01.set(Calendar.MONTH, Calendar.JANUARY);
        SINCE_2014_01_01.set(Calendar.DAY_OF_MONTH, 1);
        SINCE_2014_01_01.set(Calendar.HOUR_OF_DAY, 0);
        SINCE_2014_01_01.set(Calendar.MINUTE, 0);
        SINCE_2014_01_01.set(Calendar.SECOND, 0);
        SINCE_2014_01_01.set(Calendar.MILLISECOND, 0);
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

    public boolean sync(boolean exhausted, Calendar since) throws DependencyException {
        int start = 1, end = BATCH_SIZE, count = 0;
        boolean completed = false;
        while (!completed) {
            Vector<ProductDetails> details = internalSync(start, end, since);

            for (int i = 0; i < details.size(); i++) {
                System.out.println(details.get(i).toString());
            }
            count += details.size();

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

        System.out.println("Total Count: " + count);

        return true;
    }

    public boolean ping() throws DependencyException {
        Vector<ProductDetails> details = internalSync(1, 3, SINCE_2014_01_01);
        return details.size() == 3;
    }

    public boolean sanitize() {
        lixingRestClient = null;
        lixingRestClient = HttpClientBuilder.create().build();
        return true;
    }

    private Vector<ProductDetails> internalSync(int start, int end, Calendar since) throws DependencyException {
        Vector<ProductDetails> result = new Vector<ProductDetails>();

        String url = new StringBuilder(context.getContextAsString(URL, "")).append('?').append(SYNC_METHOD).toString();
        String sinceDateStr = new SimpleDateFormat("yyyy-MM-dd").format(since.getTime());
        String user = context.getContextAsString(USER, "");
        String payload = MessageFormat.format(GET_UPDATE_PRODUCT_INFO_PAYLOAD, sinceDateStr, start, end);

        String sign;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(MessageFormat.format(SIGN_TEMPLATE, payload,
                context.getContextAsString(SIGN, "")).getBytes());
            sign = Hex.encodeToString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new DependencyException(e);
        }

        HttpPost post = new HttpPost();
        try {
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
                    ProductDetails detail = new ProductDetails();
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
                            detail.setAction(value);
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
        } finally {
            post.abort();
        }

        return result;
    }

    private class ProductDetails {
        private String code;
        private String name;
        private String updateTime;
        private String action;
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
        public String getAction() {
            return action;
        }
        public void setAction(String action) {
            this.action = action;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(action);
            sb.append("\t");
            sb.append(code);
            sb.append("\t");
            sb.append(updateTime);
            sb.append("\t");
            sb.append(name);
            return sb.toString();
        }
    }
}
