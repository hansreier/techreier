package com.sigmondsmart.edrops.domain

import edrops.domain.BlogEntry
import org.hibernate.cfg.Configuration
import org.hibernate.testing.transaction.TransactionUtil.doInHibernate
import org.junit.Assert.assertTrue
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase
import org.junit.Test
import java.io.IOException

import java.time.LocalDateTime
import java.util.*

// TODO Why not possible, pom problem
//https://www.baeldung.com/kotlin/jpa
//Note must use JUNIT 4 due to inheritance from BaseCoreFunctionalTestCase (Hibernate 5).
// Hibernate 6 connot be used in Spring yet (wait for Spring 3.0).
class TestBlogEntry : BaseCoreFunctionalTestCase() {

    private val properties: Properties
        @Throws(IOException::class)
        get() {
            val properties = Properties()
            properties.load(javaClass.classLoader.getResourceAsStream("hibernate.properties"))
            return properties
        }

    override fun getAnnotatedClasses(): Array<Class<*>> {
        return arrayOf(BlogEntry::class.java)
    }

    override fun configure(configuration: Configuration) {
        super.configure(configuration)
        configuration.properties = properties
    }

    @Test
    fun givenBlogEntry_whenSaved_thenFound() {
        doInHibernate({ sessionFactory() }) { session ->
            val blogEntry = BlogEntry(
                LocalDateTime.now(), null, "Dette er Reiers første blog"
            )
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
                LocalDateTime.now(), null,"Dette er Reiers første blog")
            session.persist(blogEntry)
            val blogEntryFound = session.find(BlogEntry::class.java, blogEntry.id)
            session.refresh(blogEntryFound)
            assertTrue(blogEntry.text == blogEntryFound.text)
        }


    }

    //private fun sessionFactory() {
    //    TODO("Not yet implemented")
    //}
}