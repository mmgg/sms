package com.clwu.sms.service.impl;

import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.service.ShowApiDrugService;
import com.clwu.sms.utils.StringUtil;
import com.clwu.sms.vo.PhysicBarcodeInfoVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Service
public class ShowApiDrugServiceImpl implements ShowApiDrugService {

    private static final Logger log = LoggerFactory.getLogger(ShowApiDrugServiceImpl.class);
    private static final String API_CODE = "66-24";

    @Value("${showapi.base-url:https://route.showapi.com}")
    private String baseUrl;

    @Value("${showapi.app-key:}")
    private String appKey;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public ShowApiDrugServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(60).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(60).toMillis());
        this.restTemplate = new RestTemplate(requestFactory);
    }

    @Override
    public PhysicBarcodeInfoVo queryByBarcode(String barcode) {
        if (StringUtil.isBlank(barcode)) {
            throw new BusinessException(400, "条码不能为空");
        }
        if (StringUtil.isBlank(appKey)) {
            throw new BusinessException(500, "未配置 ShowAPI AppKey");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", barcode.trim());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/66-24")
                .queryParam("appKey", appKey)
                .toUriString();

        try {
            log.info("调用药品条码接口: api={}, barcode={}", API_CODE, barcode);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(form, headers), String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            if (root.path("showapi_res_code").asInt(-1) != 0) {
                throw new BusinessException(500,
                        "药品条码接口失败: " + root.path("showapi_res_error").asText(""));
            }
            JsonNode body = root.path("showapi_res_body");
            if (body.path("ret_code").asInt(-1) != 0) {
                throw new BusinessException(404,
                        body.path("remark").asText("未查询到药品条码信息"));
            }

            PhysicBarcodeInfoVo result = new PhysicBarcodeInfoVo();
            result.setBarcode(text(body, "code", barcode));
            result.setName(text(body, "name", null));
            result.setSpec(text(body, "spec", null));
            result.setTrademark(text(body, "trademark", null));
            result.setManufacturer(text(body, "manuName", null));
            result.setManufacturerAddress(text(body, "manuAddress", null));
            result.setApprovalNumber(text(body, "approval", null));
            result.setDosage(text(body, "dosage", null));
            result.setIndications(text(body, "purpose", null));
            result.setMainIngredients(text(body, "basis", null));
            result.setContraindications(text(body, "taboo", null));
            result.setPrecautions(text(body, "consideration", null));
            result.setStorageCondition(text(body, "storage", null));
            result.setValidityPeriod(text(body, "validity", null));
            result.setCharacteristics(text(body, "character", null));
            result.setOtherNotes(text(body, "other", null));
            result.setNote(text(body, "note", null));
            result.setImageUrl(text(body, "img", null));
            result.setOtcType(body.path("type").isMissingNode() || body.path("type").isNull()
                    ? null : body.path("type").asInt());
            result.setSourceApi(API_CODE);
            result.setSourcePayload(body.toString());
            return result;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("药品条码接口调用异常: barcode={}", barcode, ex);
            throw new BusinessException(500, "药品条码接口调用失败");
        }
    }

    private String text(JsonNode node, String field, String defaultValue) {
        String value = node.path(field).asText(null);
        return StringUtil.isBlank(value) ? defaultValue : value.trim();
    }
}
