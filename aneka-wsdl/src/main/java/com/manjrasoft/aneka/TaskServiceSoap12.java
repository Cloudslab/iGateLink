package com.manjrasoft.aneka;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public interface TaskServiceSoap12 {

	String NAMESPACE_URI = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";

	com.manjrasoft.aneka.SubmitJobsResponse submitJobs(com.manjrasoft.aneka.SubmitJobs parameters) throws IOException, XmlPullParserException;

	com.manjrasoft.aneka.AuthenticateUserResponse authenticateUser(com.manjrasoft.aneka.AuthenticateUser parameters) throws IOException, XmlPullParserException;

	com.manjrasoft.aneka.AbortJobResponse abortJob(com.manjrasoft.aneka.AbortJob parameters) throws IOException, XmlPullParserException;

	com.manjrasoft.aneka.AbortApplicationResponse abortApplication(com.manjrasoft.aneka.AbortApplication parameters) throws IOException, XmlPullParserException;

	com.manjrasoft.aneka.QueryJobResponse queryJob(com.manjrasoft.aneka.QueryJob parameters) throws IOException, XmlPullParserException;

	com.manjrasoft.aneka.QueryApplicationResponse queryApplication(com.manjrasoft.aneka.QueryApplication parameters) throws IOException, XmlPullParserException;

	com.manjrasoft.aneka.QueryJobStatusResponse queryJobStatus(com.manjrasoft.aneka.QueryJobStatus parameters) throws IOException, XmlPullParserException;

	com.manjrasoft.aneka.CreateApplicationResponse createApplication(com.manjrasoft.aneka.CreateApplication parameters) throws IOException, XmlPullParserException;

	com.manjrasoft.aneka.QueryApplicationStatusResponse queryApplicationStatus(com.manjrasoft.aneka.QueryApplicationStatus parameters) throws IOException, XmlPullParserException;
}
