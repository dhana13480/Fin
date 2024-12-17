package org.finra.rmcs.service;


import java.time.ZonedDateTime;

public interface IcmReportService {

    public boolean sendICMReportAsEmail(ZonedDateTime reportDate);
}
