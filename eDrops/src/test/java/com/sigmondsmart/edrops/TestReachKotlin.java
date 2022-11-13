package com.sigmondsmart.edrops;

import com.sigmondsmart.edrops.domain.BlogOwner;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class TestReachKotlin {

    private static final Logger logger = LoggerFactory.getLogger(TestReachKotlin.class);

    @Test
    public void testReachKotlin() {
        BlogOwner blogOwner = new BlogOwner(LocalDateTime.now(), null, "Reier", "Sigmond",
                "reier.sigmond@gmail.com", "91668863", "Sløttvegen 17",
                "2390", "Moelv", 1L, null);
        String address = blogOwner.getAddress();
        logger.info("Reier was here {}", address);
    }
}
