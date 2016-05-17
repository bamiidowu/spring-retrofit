package com.boostrdev.spring.retrofit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/config/spring-retrofit-config.xml")
public class AcronymWebServiceProxyTest {

    private static final Logger LOG = LoggerFactory.getLogger(AcronymWebServiceProxyTest.class);

    @Autowired
    private AcronymWebServiceProxy acronymWebServiceProxy;

    @Test
    public void testGetAcronymResultsSynchronously() {
        List<AcronymData> acronymDatas = acronymWebServiceProxy.getAcronymResultsSynchronously("HMM");
        verifyResponse(acronymDatas);
    }

    @Test
    public void testGetAcronymResultsAsynchronously() throws Exception {

        final CountDownLatch callbackLatch = new CountDownLatch(1);

        acronymWebServiceProxy.getAcronymResultsAsynchronously("HMM", new Callback<List<AcronymData>>() {
            public void success(List<AcronymData> acronymDataList, Response response) {
                // Request is successful if response code begins with 2XX
                if (String.valueOf(response.getStatus()).startsWith("2")) {
                    verifyResponse(acronymDataList);
                } else {
                    fail("Received a bad response");
                }
                callbackLatch.countDown();
            }

            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });

        callbackLatch.await(2000, TimeUnit.MILLISECONDS);
    }

    private void verifyResponse(List<AcronymData> acronymDataList) {

        // Check the body isn't empty
        assertNotNull(acronymDataList);

        // The response should have only one item
        assertEquals(acronymDataList.size(), 1);

        for (AcronymData acronymData : acronymDataList) {
            LOG.debug("Acronym short form: " + acronymData.toString());
            List<AcronymData.AcronymExpansion> acronymExpansionList = acronymData.getLfs();

            // The acronym data should have 8 acronym expansions
            assertEquals(acronymExpansionList.size(), 8);

            for (AcronymData.AcronymExpansion acronymExpansion : acronymExpansionList) {
                LOG.debug("Acronym long form: " + acronymExpansion.toString());
            }
        }
    }

}