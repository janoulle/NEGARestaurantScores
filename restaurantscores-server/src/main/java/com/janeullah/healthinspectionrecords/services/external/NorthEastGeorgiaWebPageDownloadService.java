package com.janeullah.healthinspectionrecords.services.external;

import com.janeullah.healthinspectionrecords.config.feign.NorthEastGeorgiaFeignConfiguration;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "northEastGeorgiaWebPageDownloadService", url = WebPageConstants.BASE_URL, configuration = NorthEastGeorgiaFeignConfiguration.class)
public interface NorthEastGeorgiaWebPageDownloadService {

    @GetMapping(value = "{county}" + WebPageConstants.PAGE_URL)
    String getWebPage(@PathVariable(value = "county") String county);

}
