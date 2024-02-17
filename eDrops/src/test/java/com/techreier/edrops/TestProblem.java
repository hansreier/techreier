package com.techreier.edrops;

import com.techreier.edrops.domain.BlogData;
import com.techreier.edrops.domain.BlogEntry;
import com.techreier.edrops.repository.BlogOwnerRepository;
import com.techreier.edrops.repository.LanguageRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.techreier.edrops.domain.BlogDataKt.English;
import static com.techreier.edrops.domain.BlogDataKt.Norwegian;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class TestProblem {

    private static final Logger logger = LoggerFactory.getLogger(TestProblem.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private BlogOwnerRepository ownerRepo;
    @Autowired
    private BlogData blogData;
    @Autowired
    private LanguageRepository languageRepo;

    @Test
    @DirtiesContext
    public void read() {
        logger.info("starting transactional test");
        languageRepo.save(Norwegian);
        languageRepo.save(English);
        ownerRepo.save(blogData.getBlogOwner());
        entityManager.clear();
        List<BlogEntry> blogEntries = blogData.getBlogEntries1();
        Query queryBlog = entityManager.createQuery(
                "SELECT DISTINCT b FROM Blog b"
                        + " LEFT JOIN FETCH b.blogEntries t "
                        + " WHERE t in :p "
        );
        queryBlog.setParameter("p", blogEntries);
        queryBlog.getResultList();
    }
}
