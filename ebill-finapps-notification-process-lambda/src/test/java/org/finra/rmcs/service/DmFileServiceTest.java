package org.finra.rmcs.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.FileTypeEnum;
import org.finra.rmcs.dto.BusinessObjectData;
import org.finra.rmcs.dto.BusinessObjectDataKey;
import org.finra.rmcs.dto.DmNotification;
import org.finra.rmcs.dto.StorageFile;
import org.finra.rmcs.dto.StorageUnit;
import org.finra.rmcs.service.impl.DmFileServiceImpl;
import org.finra.rmcs.utils.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@SpringJUnitConfig
public class DmFileServiceTest {
  @InjectMocks
  DmFileServiceImpl dmFileService;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  OAuth2RestTemplate oAuth2RestTemplate;

  @Value("${dm.endpoint}")
  String dmEndPoint;
  @Value("${dm.file.namespace}")
  String dmFileNameSpace;

  DmNotification pdfNotificationMessage;
  BusinessObjectData businessObjectData;

  @BeforeEach
  public void setup(){
    dmFileService = new DmFileServiceImpl(this.dmEndPoint, this.dmFileNameSpace, this.oAuth2RestTemplate, this.restTemplate);
    ReflectionTestUtils.setField(dmFileService,"dmEndPoint",dmEndPoint);
    ReflectionTestUtils.setField(dmFileService,"dmFileNameSpace",dmFileNameSpace);

    pdfNotificationMessage = DmNotification.builder().eventDate(new Timestamp(System.currentTimeMillis()))
        .businessObjectDataKey(BusinessObjectDataKey.builder().businessObjectFormatFileType(
            FileTypeEnum.PDF.name()).partitionValue("RGF1234").businessObjectDataVersion(0).build()).newBusinessObjectDataStatus(
            Constants.VALID_BUSINESS_OBJECT_STATUS).build();
    DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
    Mockito.when(oAuth2RestTemplate.getAccessToken()).thenReturn(token);

    businessObjectData = BusinessObjectData.builder()
        .id(123).businessObjectDefinitionName("RGFEE").businessObjectFormatUsage("PRC").businessObjectFormatFileType("PDF")
        .storageUnits(Arrays.asList(StorageUnit.builder().storageFiles(Arrays.asList(
            StorageFile.builder().filePath(
                    "finapps-int/erp/prc/gz/rgfee/schm-v0/data-v0/invoice-id=RGF17803548/trade-date=2023-12-14/RGFEE-2023-12-14-RGF17803548.GZ")
                .build())).build())).build();
  }

  @Test
  public void getDmFileInformationTest(){
    Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(
        HttpMethod.class), ArgumentMatchers.any(), ArgumentMatchers.<Class<BusinessObjectData>>any())).thenReturn(new ResponseEntity<>(businessObjectData,
        HttpStatus.OK));
    BusinessObjectData response = dmFileService.getDmFileInformation("1234", pdfNotificationMessage);
    Map<String, String> fileInfoMap = Util.getFileNameFromDmFileInformation(response);
    String expected = "RGFEE-2023-12-14-RGF17803548.GZ";
    Assertions.assertEquals(expected, fileInfoMap.get(Constants.FILE_NAME));
  }

}
