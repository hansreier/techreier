package com.sigmondsmart.edrops;

import com.sigmondsmart.edrops.domain.Dta;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestA2 {

    private static final Logger log = LoggerFactory.getLogger(TestA2.class);

    @Test
    public void testReachKotlin() {
        Dta dta = new Dta("Reier");
        String text = dta.getName();
        log.info("Reier was here {}", text);
    }
}
