package org.finra.rmcs.service;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EbillModeledScheduleWrapperServiceLocator {
  private ApplicationContext applicationContext;

  public EbillModeledScheduleWrapperService locateService(String serviceName){
    return this.applicationContext.getBean(serviceName, EbillModeledScheduleWrapperService.class);
  }

}
