package com.knasardinov.idea.api;

import com.knasardinov.idea.pojo.TestCase;
import com.knasardinov.idea.pojo.TestStatus;
import com.knasardinov.idea.pojo.User;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface TestRailClient {

    @POST("/index.php%3F/api/v2/update_case/{caseId}")
    TestCase updateTestCase(@Path("caseId") int caseId, @Body TestCase testCase);

    @GET("/index.php%3F/api/v2/get_statuses")
    TestStatus[] getStatuses();

    @GET("/index.php%3F/api/v2/get_user/{userId}")
    User getUser(@Path("userId") int userId);
}