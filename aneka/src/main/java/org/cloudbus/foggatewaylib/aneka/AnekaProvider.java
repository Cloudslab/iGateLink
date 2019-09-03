package org.cloudbus.foggatewaylib.aneka;

import android.os.AsyncTask;

import androidx.annotation.CallSuper;

import com.manjrasoft.aneka.ArrayOfFile;
import com.manjrasoft.aneka.ArrayOfTaskItem;
import com.manjrasoft.aneka.Job;
import com.manjrasoft.aneka.JobStatus;

import org.apache.commons.net.ftp.FTP;
import org.cloudbus.foggatewaylib.aneka.ftp.SimpleFTPClient;
import org.cloudbus.foggatewaylib.core.Data;
import org.cloudbus.foggatewaylib.core.ThreadPoolProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AnekaProvider<T extends Data, S extends Data> extends ThreadPoolProvider<T, S> {

    protected String url;
    protected String username;
    protected String password;
    protected String appName;

    protected ArrayOfFile sharedFiles;

    protected String inputVirtualPath;
    protected String outputVirtualPath;

    private AnekaWebServices anekaWebServices;

    private int fileType;

    private int requestTimeout = 5000;
    private int jobTimeout = 60000;
    private int pollingPeriod = 500;

    protected Map<String, StorageBucket> storageBuckets = new HashMap<>();

    public AnekaProvider(int fileType, int nThreads, Class<T> inputType, Class<S> outputType) {
        super(nThreads, inputType, outputType);
        this.fileType = fileType;
    }

    @Override
    public S[] getData(ProgressPublisher progressPublisher, long requestID, T... input) throws Exception {
        long start = new Date().getTime();
        if (anekaWebServices == null)
            throw new Exception("Login to Aneka in progress...");
        if (!anekaWebServices.isLoggedIn() || !anekaWebServices.hasValidDefaultApplication()){
            if (anekaWebServices.getError() != null)
                throw new Exception(anekaWebServices.getError());
            else if (!anekaWebServices.isLoggedIn())
                throw new Exception("Login to Aneka failed");
            else // application creation failed
                throw new Exception("Unknown error while creating the Aneka application");
        }

        String anekaRequestId = UUID.randomUUID().toString();
        Job job = buildJob(anekaRequestId, input);

        progressPublisher.publish(0, "Uploading input to FTP server");

        SimpleFTPClient inputFTPClient
                = new SimpleFTPClient(getFTPInputBucket());
        if (!inputFTPClient.connect())
            throw new Exception("Input FTP login failed.");
        if (fileType == FTP.ASCII_FILE_TYPE){
            if (!inputFTPClient.putString(
                    job.getInputFiles().getFile()[0].getPath(),
                    inputToString(input))) {
                throw new Exception("Input FTP upload failed.");
            }
        } else {
            if (!inputFTPClient.putBytes(
                    job.getInputFiles().getFile()[0].getPath(),
                    inputToBytes(input))) {
                throw new Exception("Input FTP upload failed.");
            }
        }

        long startExe = new Date().getTime();

        progressPublisher.publish(0, "Submitting Job to Aneka");

        String jobId = anekaWebServices.submitJob(job);
        if (jobId == null){
            if (anekaWebServices.getError() != null)
                throw new Exception(anekaWebServices.getError());
            else // application creation failed
                throw new Exception("Unknown error while submitting Aneka job.");
        }

        progressPublisher.publish(0, "Executing Job in Aneka");

        String status = anekaWebServices.waitJobTermination(jobId);
        if (status == null || !status.equals(JobStatus.STATUS_COMPLETED)){
            throw new Exception("Job exited with status: " + status);
        }

        long endExe = new Date().getTime();

        progressPublisher.publish(0, "Downloading output from FTP server");

        SimpleFTPClient outputFTPClient
                = new SimpleFTPClient(getFTPOutputBucket());
        if (!outputFTPClient.connect()) {
            throw new Exception("Output FTP login failed.");
        }

        S[] out;

        if (fileType == FTP.ASCII_FILE_TYPE) {
            String outputString
                    = outputFTPClient.getString(job.getOutputFiles().getFile()[0].getPath());

            if (outputString == null) {
                throw new Exception("Output FTP download failed.");
            }

            out = stringToOutput(outputString);
        } else {
            byte[] outputBytes
                    = outputFTPClient.getBytes(job.getOutputFiles().getFile()[0].getPath());

            if (outputBytes == null) {
                throw new Exception("Output FTP download failed.");
            }

            out = bytesToOutput(outputBytes);
        }

        long end = new Date().getTime();
        progressPublisher.publish(100,
                String.format("Done in %.2fs (execution: %.2fs)",
                        (end-start)/1000f,
                        (endExe-startExe)/1000f)
        );

        return out;

    }

    protected Job buildJob(String anekaRequestId, T... input){
        Job job = new Job();
        job.setReservationId(anekaRequestId);
        job.setTasks(buildTasks());
        job.setInputFiles(WSDLBuilder.buildArrayOfFile(
                getInputBucket().getName(),
                buildInputPath(anekaRequestId),
                inputVirtualPath));
        job.setOutputFiles(WSDLBuilder.buildArrayOfFile(
                getOutputBucket().getName(),
                buildOutputPath(anekaRequestId),
                outputVirtualPath));
        return job;

    }

    protected abstract ArrayOfTaskItem buildTasks();

    protected abstract void initCredentials();

    protected abstract ArrayOfFile initSharedFiles();

    protected abstract StorageBucket[] initStorageBuckets();

    protected abstract byte[] inputToBytes(T... input);
    protected abstract String inputToString(T... input);

    protected abstract S[] bytesToOutput(byte[] bytes);
    protected abstract S[] stringToOutput(String string);

    protected void onLogin(AnekaWebServices anekaWebServices, boolean error){}

    private void login(){
        new LoginTask().execute();
    }

    protected FTPStorageBucket getFTPInputBucket(){
        return (FTPStorageBucket) storageBuckets.values().iterator().next();
    }

    protected FTPStorageBucket getFTPOutputBucket(){
        return (FTPStorageBucket) storageBuckets.values().iterator().next();
    }

    protected StorageBucket getInputBucket(){
        return getFTPInputBucket();
    }

    protected StorageBucket getOutputBucket(){
        return getFTPOutputBucket();
    }

    protected String buildInputPath(String anekaRequestId){
        return String.format("/%s/%s", anekaRequestId, inputVirtualPath);
    }

    protected String buildOutputPath(String anekaRequestId){
        return String.format("/%s/%s", anekaRequestId, outputVirtualPath);
    }

    @Override
    @CallSuper
    public void onAttach() {
        super.onAttach();
        initCredentials();
        sharedFiles = initSharedFiles();
        for (StorageBucket storageBucket: initStorageBuckets()){
            storageBuckets.put(storageBucket.getName(), storageBucket);
        }
        login();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        anekaWebServices = null;
    }

    private class LoginTask extends AsyncTask<Void, Void, AnekaWebServices>{
        @Override
        protected AnekaWebServices doInBackground(Void... voids) {
            AnekaWebServices webServices = new AnekaWebServices(url);
            if (!webServices.authenticateUser(username, password))
                return webServices;

            String appId = webServices.createApplication(
                    appName,
                    sharedFiles,
                    storageBuckets.values().toArray(new StorageBucket[]{}));

            if (appId == null)
                return webServices;

            return webServices;

        }

        @Override
        protected void onPostExecute(AnekaWebServices webServices) {
            super.onPostExecute(webServices);
            anekaWebServices = webServices;
            anekaWebServices.setJobTimeout(jobTimeout);
            anekaWebServices.setRequestTimeout(requestTimeout);
            anekaWebServices.setPollingPeriod(pollingPeriod);
            onLogin(anekaWebServices, webServices.getError() != null);
        }
    }

    public boolean isConnected(){
        return anekaWebServices != null
                && anekaWebServices.isLoggedIn()
                && anekaWebServices.hasValidDefaultApplication();

    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getJobTimeout() {
        return jobTimeout;
    }

    public void setJobTimeout(int jobTimeout) {
        this.jobTimeout = jobTimeout;
    }

    public int getPollingPeriod() {
        return pollingPeriod;
    }

    public void setPollingPeriod(int pollingPeriod) {
        this.pollingPeriod = pollingPeriod;
    }
}
