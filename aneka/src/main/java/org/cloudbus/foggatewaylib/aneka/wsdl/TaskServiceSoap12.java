package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public interface TaskServiceSoap12 {

	String NAMESPACE_URI = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";

	org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobsResponse submitJobs(org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobs parameters) throws IOException, XmlPullParserException;

	org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUserResponse authenticateUser(org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUser parameters) throws IOException, XmlPullParserException;

	org.cloudbus.foggatewaylib.aneka.wsdl.AbortJobResponse abortJob(org.cloudbus.foggatewaylib.aneka.wsdl.AbortJob parameters) throws IOException, XmlPullParserException;

	org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplicationResponse abortApplication(org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplication parameters) throws IOException, XmlPullParserException;

	org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobResponse queryJob(org.cloudbus.foggatewaylib.aneka.wsdl.QueryJob parameters) throws IOException, XmlPullParserException;

	org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationResponse queryApplication(org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplication parameters) throws IOException, XmlPullParserException;

	org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatusResponse queryJobStatus(org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatus parameters) throws IOException, XmlPullParserException;

	org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplicationResponse createApplication(org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplication parameters) throws IOException, XmlPullParserException;

	org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatusResponse queryApplicationStatus(org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatus parameters) throws IOException, XmlPullParserException;
}
