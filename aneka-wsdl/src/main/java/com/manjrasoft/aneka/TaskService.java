package com.manjrasoft.aneka;

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

	public com.manjrasoft.aneka.SubmitJobsResponse submitJobs(com.manjrasoft.aneka.SubmitJobs parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/SubmitJobs", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.SubmitJobsResponse response = (com.manjrasoft.aneka.SubmitJobsResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.SubmitJobsResponse(), (AttributeContainer) envelope.bodyIn);
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

	public com.manjrasoft.aneka.AuthenticateUserResponse authenticateUser(com.manjrasoft.aneka.AuthenticateUser parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/AuthenticateUser", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.AuthenticateUserResponse response = (com.manjrasoft.aneka.AuthenticateUserResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.AuthenticateUserResponse(), (AttributeContainer) envelope.bodyIn);
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

	public com.manjrasoft.aneka.AbortJobResponse abortJob(com.manjrasoft.aneka.AbortJob parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/AbortJob", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.AbortJobResponse response = (com.manjrasoft.aneka.AbortJobResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.AbortJobResponse(), (AttributeContainer) envelope.bodyIn);
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

	public com.manjrasoft.aneka.AbortApplicationResponse abortApplication(com.manjrasoft.aneka.AbortApplication parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/AbortApplication", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.AbortApplicationResponse response = (com.manjrasoft.aneka.AbortApplicationResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.AbortApplicationResponse(), (AttributeContainer) envelope.bodyIn);
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

	public com.manjrasoft.aneka.QueryJobResponse queryJob(com.manjrasoft.aneka.QueryJob parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/QueryJob", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.QueryJobResponse response = (com.manjrasoft.aneka.QueryJobResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.QueryJobResponse(), (AttributeContainer) envelope.bodyIn);
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

	public com.manjrasoft.aneka.QueryApplicationResponse queryApplication(com.manjrasoft.aneka.QueryApplication parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/QueryApplication", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.QueryApplicationResponse response = (com.manjrasoft.aneka.QueryApplicationResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.QueryApplicationResponse(), (AttributeContainer) envelope.bodyIn);
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

	public com.manjrasoft.aneka.QueryJobStatusResponse queryJobStatus(com.manjrasoft.aneka.QueryJobStatus parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/QueryJobStatus", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.QueryJobStatusResponse response = (com.manjrasoft.aneka.QueryJobStatusResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.QueryJobStatusResponse(), (AttributeContainer) envelope.bodyIn);
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

	public com.manjrasoft.aneka.CreateApplicationResponse createApplication(com.manjrasoft.aneka.CreateApplication parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/CreateApplication", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.CreateApplicationResponse response = (com.manjrasoft.aneka.CreateApplicationResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.CreateApplicationResponse(), (AttributeContainer) envelope.bodyIn);
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

	public com.manjrasoft.aneka.QueryApplicationStatusResponse queryApplicationStatus(com.manjrasoft.aneka.QueryApplicationStatus parameters) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = createSoapSerializationEnvelope();
		envelope.bodyOut = parameters;
		HttpTransportSE httpTransport = new HttpTransportSE(url);
		httpTransport.debug = debug;
		try {
			httpTransport.call("http://www.manjrasoft.com/Aneka/v2.0/WebServices/QueryApplicationStatus", envelope);
			if (envelope.bodyIn instanceof SoapFault) {
				throw (SoapFault) envelope.bodyIn;
			}
			com.manjrasoft.aneka.QueryApplicationStatusResponse response = (com.manjrasoft.aneka.QueryApplicationStatusResponse) KSoap2Utils.getObject(new com.manjrasoft.aneka.QueryApplicationStatusResponse(), (AttributeContainer) envelope.bodyIn);
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
