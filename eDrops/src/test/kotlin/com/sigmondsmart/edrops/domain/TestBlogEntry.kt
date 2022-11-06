package com.sigmondsmart.edrops.domain

import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase
import org.hibernate.testing.transaction.TransactionUtil.doInHibernate
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

// https://www.baeldung.com/kotlin/jpa
// Note must use JUNIT 4 due to inheritance from BaseCoreFunctionalTestCase (Hibernate 5).
// Config file must be called hibernate.cfg
// Hibernate 6 connot be used in Spring yet (wait for Spring 3.0).
// The code in the base class is too complex, should be avoided
// Actually want to get rid of the code, due to these weaknesses.
class TestBlogEntry : BaseCoreFunctionalTestCase() {
    override fun getAnnotatedClasses(): Array<Class<*>> {
        return arrayOf(BlogEntry::class.java)
    }

    @Test
    fun givenBlogEntry_whenSaved_thenFound() {
        doInHibernate({ sessionFactory() }) { session ->
            val blogEntry = BlogEntry(
                LocalDateTime.now(), null, "Dette er Reiers første blog"
            )
            log.info("testing Hibernate persistence 1")
            session.persist(blogEntry)
            val blogEntryFound = session.find(BlogEntry::class.java, blogEntry.id)
            session.refresh(blogEntryFound)

            assertTrue(blogEntry.text == blogEntryFound.text)
        }
    }

    @Test
    fun givenAddressWithDefaultEquals_whenAddedToSet_thenNotFound() {
        doInHibernate({ sessionFactory() }) { session ->
            val blogEntry = BlogEntry(
                LocalDateTime.now(), null, "Dette er Reiers første blog"
            )
            log.info("testing Hibernate persistence 2")
            session.persist(blogEntry)
            val blogEntryFound = session.find(BlogEntry::class.java, blogEntry.id)
            session.refresh(blogEntryFound)
            assertTrue(blogEntry.text == blogEntryFound.text)
        }
    }
}