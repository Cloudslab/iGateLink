package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.SoapFault;
import org.ksoap2.binding.BindingStubBase;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class TaskService extends BindingStubBase implements TaskServiceSoap12 {

	public TaskService(String url, int soapVersion, boolean debug) {
		super(url, NAMESPACE_URI, soapVersion, debug);
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobsResponse submitJobs(org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobs parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/SubmitJobs", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobsResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobsResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobsResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("SubmitJobs", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUserResponse authenticateUser(org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUser parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/AuthenticateUser", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUserResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUserResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUserResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("AuthenticateUser", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.AbortJobResponse abortJob(org.cloudbus.foggatewaylib.aneka.wsdl.AbortJob parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/AbortJob", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.AbortJobResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.AbortJobResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.AbortJobResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("AbortJob", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplicationResponse abortApplication(org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplication parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/AbortApplication", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplicationResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplicationResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplicationResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("AbortApplication", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobResponse queryJob(org.cloudbus.foggatewaylib.aneka.wsdl.QueryJob parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/QueryJob", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("QueryJob", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationResponse queryApplication(org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplication parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/QueryApplication", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("QueryApplication", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatusResponse queryJobStatus(org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatus parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/QueryJobStatus", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatusResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatusResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatusResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("QueryJobStatus", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplicationResponse createApplication(org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplication parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/CreateApplication", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplicationResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplicationResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplicationResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("CreateApplication", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}

	public org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatusResponse queryApplicationStatus(org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatus parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/QueryApplicationStatus", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatusResponse response = (org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatusResponse) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatusResponse(), (AttributeContainer) envelope.bodyIn);
			logInfo("QueryApplicationStatus", response);
			return response;
		} catch (IOException e) {
			logError(httpTransport);
			SoapFault soapFault = parseSoapFault(httpTransport, envelope);
			throw soapFault != null ? soapFault : e;
		} catch (XmlPullParserException e) {
			logError(httpTransport);
			throw e;
		}
	}
}
