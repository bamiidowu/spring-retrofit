package com.boostrdev.spring.retrofit;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

import java.util.List;


/**
 * Retrofit Service to send requests to Acronym web service and
 * convert the Json response to POJO class.
 */
public interface AcronymWebServiceProxy {

    /**
     * Query Parameter.
     */
    public static final String SHORT_FORM_QUERY_PARAMETER =
            "sf";

    /**
     * Get List of LongForm associated with acronym from Acronym Web
     * service.  Asynchronous execution requires the last parameter of
     * the method be a Callback.
     *
     * @param shortForm
     * @return List of JsonAcronym
     */
    @Headers("Cache-Control: public, max-stale=10")
    @GET("/software/acromine/dictionary.py")
    public void getAcronymResultsAsynchronously(@Query(SHORT_FORM_QUERY_PARAMETER) String shortForm, Callback<List<AcronymData>> callback);

    /**
     * Get List of LongForm associated with acronym from Acronym Web
     * service.  Asynchronous execution requires the last parameter of
     * the method be a Callback.
     *
     * @param shortForm
     * @return List of JsonAcronym
     */
    @Headers("Cache-Control: public, max-stale=10")
    @GET("/software/acromine/dictionary.py")
    public List<AcronymData> getAcronymResultsSynchronously(@Query(SHORT_FORM_QUERY_PARAMETER) String shortForm);


}
