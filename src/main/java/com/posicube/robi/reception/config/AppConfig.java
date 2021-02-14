package com.posicube.robi.reception.config;

import com.posicube.robi.reception.util.CsvReaderUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CsvReaderUtil csvReaderUtil() {
        return new CsvReaderUtil();
    }
}
