package com.sigmondsmart.edrops;

import com.sigmondsmart.edrops.domain.BlogData;
import com.sigmondsmart.edrops.domain.BlogEntry;
import com.sigmondsmart.edrops.repository.BlogOwnerRepository;
import com.sigmondsmart.edrops.repository.LanguageRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TestProblem {

    private static final Logger logger = LoggerFactory.getLogger(TestProblem.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BlogOwnerRepository ownerRepo;


    @Autowired
    private LanguageRepository languageRepo;

    @Test
    @DirtiesContext
    public void read() {
        logger.info("starting transactional test");
        BlogData data = new BlogData();
        languageRepo.save(data.getNorwegian());
        languageRepo.save(data.getEnglish());
        ownerRepo.save(data.getBlogOwner());
        entityManager.clear();
        List<BlogEntry> blogEntries = data.getBlogEntries1();
        Query queryBlog = entityManager.createQuery(
                "SELECT DISTINCT b FROM Blog b"
                        + " LEFT JOIN FETCH b.blogEntries t "
                        + " WHERE t in :p "
        );
        queryBlog.setParameter("p", blogEntries);
        queryBlog.getResultList();
    }
}
