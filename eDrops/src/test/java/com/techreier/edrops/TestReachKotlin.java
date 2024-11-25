package com.techreier.edrops;

import com.techreier.edrops.domain.Blog;
import com.techreier.edrops.domain.BlogOwner;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public class TestReachKotlin {

    private static final Logger logger = LoggerFactory.getLogger(TestReachKotlin.class);

    @Test
    public void testReachKotlin() {

        Set<Blog> blogList = new HashSet<>();

        BlogOwner blogOwner = new BlogOwner(ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS),
                null, "Reier", "Passord",
                "Reier", "Sigmond",
                "reier.sigmond@gmail.com", "91668863", "Sløttvegen 17",
                "2390", "Moelv", "NO", blogList, 1L);
        String address = blogOwner.getAddress();
        logger.info("Reier was here {}", address);
    }

}
