package org.cloudbus.foggatewaylib.aneka;

import android.os.AsyncTask;

import androidx.annotation.CallSuper;

import com.manjrasoft.aneka.ArrayOfFile;
import com.manjrasoft.aneka.ArrayOfTaskItem;
import com.manjrasoft.aneka.Job;
import com.manjrasoft.aneka.JobStatus;

import org.apache.commons.net.ftp.FTP;
import org.cloudbus.foggatewaylib.core.Data;
import org.cloudbus.foggatewaylib.core.ThreadPoolProvider;
import org.cloudbus.foggatewaylib.utils.SimpleFTPClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple provider for Aneka.
 *
 * For sake of simplicity, only one input file and one output file are used.
 *
 * When attached to the {@link org.cloudbus.foggatewaylib.core.ExecutionManager}, authentication
 * and application creation will be performed.
 *
 * When started, these are the actions that this class will do:
 * <ol>
 *     <li>Serialize input data;</li>
 *     <li>Send serialized data as a file to the FTP server;</li>
 *     <li>Start execution of the defined task;</li>
 *     <li>Retrieve output file from the FTP server;</li>
 *     <li>De-serialize the output file to produce the output data.</li>
 * </ol>
 *
 * When extending this class, these methods must be overridden:
 * <ul>
 *     <li>{@link #initCredentials()} to initialize the parameters to be used in authentication and
 *         application creation, namely: {@link #url}, {@link #username}, {@link #password}
 *         {@link #appName}.</li>
 *     <li>{@link #initStorageBuckets()} to specify the {@link StorageBucket}s that will be used.
 *          In the most simple case, only one {@link StorageBucket} would be necessary and it
 *          will automatically be used for both the input and the output files.</li>
 *     <li>{@link #initSharedFiles()} to specify the shared files used by the application.</li>
 *     <li>{@link #buildTasks()} to specify which tasks to execute with the data.</li>
 *     <li>{@link #getInputVirtualPath()}} to specify the virtual path in which to place the
 *         input file.</li>
 *     <li>{@link #getOutputVirtualPath()}} to specify the virtual path in which to place the
 *         output file.</li>
 *     <li>{@link #inputToBytes(Data[])} and {@link #inputToString(Data[])} to specify how data
 *         should be serialized (only one of the two is usually needed, the other can be
 *         left empty).</li>
 *     <li>{@link #bytesToOutput(byte[])} and {@link #stringToOutput(String)} to specify how data
 *         should be de-serialized (only one of the two is usually needed, the other can be
 *         left empty).</li>
 * </ul>
 *
 * The file type used in FTP transfers can be set through the constructor. Depending on whether
 * ASCII mode is used or not, different functions for serialization and de-serialization will be
 * used.
 *
 * In order to support more advanced use-cases, these methods may be overridden:
 * <ul>
 *     <li>{@link #onLogin(AnekaWebServices, boolean)} to perform action immediately after login
 *         is completed.</li>
 *     <li>{@link #getFTPOutputBucket()} and {@link #getFTPInputBucket()} to specify which
 *         bucket to use for download/upload to the FTP server. By default the first bucket
 *         will be used for both.</li>
 *     <li>{@link #getOutputBucket()} and {@link #getInputBucket()} to specify which
 *         bucket the Aneka server will use to use for upload/download from the FTP server.
 *         By default the first bucket will be used for both.</li>
 *     <li>{@link #buildInputPath(String)} and {@link #buildOutputPath(String)} to specify the
 *         paths of input and output files in the FTP server. By default a random UUID is generated
 *         for each task and used as base directory in which the same directory structure as in the
 *         virtual execution environment will be recreated ("/$UUID/$virtualPath").</li>
 *     <li>{@link #buildJob(String, Data[])} to specify a different way to build the {@link Job}
 *         with regards to what has been described above. </li>
 * </ul>
 *
 * @param <T> type of the input data
 * @param <S> type of the output data
 *
 * @author Riccardo Mancini
 */
public abstract class AnekaProvider<T extends Data, S extends Data> extends ThreadPoolProvider<T, S> {

    /**
     * The url of the Aneka Task Service.
     * E.g. http://www.example.com/Aneka.2.0/TaskService.asmx
     */
    protected String url;

    /**
     * The username of the Aneka master.
     */
    protected String username;

    /**
     * The password of the Aneka master.
     */
    protected String password;

    /**
     * An arbitrary name to give to the Aneka application created by this class.
     */
    protected String appName;

    private ArrayOfFile sharedFiles;

    private AnekaWebServices anekaWebServices;

    private int fileType;

    private int requestTimeout = AnekaWebServices.DEFAULT_REQUEST_TIMEOUT;
    private int jobTimeout = AnekaWebServices.DEFAULT_JOB_TIMEOUT;
    private int pollingPeriod = AnekaWebServices.DEFAULT_POLLING_PERIOD;

    /**
     * A map mapping each storage bucket to its name/id.
     */
    protected Map<String, StorageBucket> storageBuckets = new HashMap<>();

    /**
     * Constructor.
     *
     * @param fileType the file type to be used in FTP transfers:
     *        {@link FTP#ASCII_FILE_TYPE} or {@link FTP#BINARY_FILE_TYPE}). In most cases
     *        {@link FTP#ASCII_FILE_TYPE} is the most appropriate.
     * @param nThreads the number of threads this provider can run on.
     * @param inputType the type of the input data.
     * @param outputType the type of the output data.
     */
    public AnekaProvider(int fileType, int nThreads, Class<T> inputType, Class<S> outputType) {
        super(nThreads, inputType, outputType);
        this.fileType = fileType;
    }

    @Override
    public S[] getData(ProgressPublisher progressPublisher, long requestID, T... input) throws Exception {
        long start = new Date().getTime();

        // LOGIN CHECK

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

        // FILE UPLOAD

        String anekaRequestId = UUID.randomUUID().toString();
        Job job = buildJob(anekaRequestId, input);

        progressPublisher.publish(0, "Uploading input to FTP server");

        SimpleFTPClient inputFTPClient = getFTPInputBucket().buildFTPClient();
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

        // JOB SUBMISSION

        long startExe = new Date().getTime();

        progressPublisher.publish(0, "Submitting Job to Aneka");

        String jobId = anekaWebServices.submitJobWait(job);
        if (jobId == null){
            if (anekaWebServices.getError() != null)
                throw new Exception(anekaWebServices.getError());
            else // application creation failed
                throw new Exception("Unknown error while submitting Aneka job.");
        }

        // JOB EXECUTION

        progressPublisher.publish(0, "Executing Job in Aneka");

        String status = anekaWebServices.waitJobTermination(jobId);
        if (status == null || !status.equals(JobStatus.STATUS_COMPLETED)){
            throw new Exception("Job exited with status: " + status);
        }

        // FILE DOWNLOAD

        long endExe = new Date().getTime();

        progressPublisher.publish(0, "Downloading output from FTP server");

        SimpleFTPClient outputFTPClient = getFTPOutputBucket().buildFTPClient();
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

        // COMPLETED

        long end = new Date().getTime();
        progressPublisher.publish(100,
                String.format("Done in %.2fs (execution: %.2fs)",
                        (end-start)/1000f,
                        (endExe-startExe)/1000f)
        );

        return out;

    }

    /**
     * Builds the {@link ArrayOfTaskItem} to be executed in the Aneka cloud.
     */
    protected abstract ArrayOfTaskItem buildTasks();

    /**
     * Sets the values of {@link #url}, {@link #username}, {@link #password}, {@link #appName}.
     * This method will be called when attached to the
     * {@link org.cloudbus.foggatewaylib.core.ExecutionManager} in order to provide a way to change
     * the credentials in response to a UI event.
     * After the new credentials are set, a login will be started.
     */
    protected abstract void initCredentials();

    /**
     * Builds the {@link ArrayOfFile} of the shared files.
     */
    protected abstract ArrayOfFile initSharedFiles();

    /**
     * Returns the {@link StorageBucket}s to be used by the application.
     */
    protected abstract StorageBucket[] initStorageBuckets();

    /**
     * "Serializes" input data into a byte array.
     *
     * @see #inputToString(Data[])
     */
    protected abstract byte[] inputToBytes(T... input);

    /**
     * Serializes input data into a string.
     *
     * @see #inputToBytes(Data[])
     */
    protected abstract String inputToString(T... input);

    /**
     * "De-serializes" output data from a byte array.
     *
     * @see #stringToOutput(String)
     */
    protected abstract S[] bytesToOutput(byte[] bytes);

    /**
     * De-serializes output data from a string.
     *
     * @see #bytesToOutput(byte[])
     */
    protected abstract S[] stringToOutput(String string);

    /**
     * Returns the virtual path of the input file.
     */
    protected abstract String getInputVirtualPath();

    /**
     * Returns the virtual path of the output file.
     */
    protected abstract String getOutputVirtualPath();

    /**
     * Callback for login completion.
     *
     * @param anekaWebServices the instance of {@link AnekaWebServices} in which login was
     *                         completed.
     * @param error {@code false} if login was successful, {@code true} otherwise.
     */
    protected void onLogin(AnekaWebServices anekaWebServices, boolean error){}

    /**
     * Returns the bucket to be used to upload the input file to the FTP server.
     */
    protected FTPStorageBucket getFTPInputBucket(){
        return (FTPStorageBucket) storageBuckets.values().iterator().next();
    }

    /**
     * Returns the bucket to be used to download the output file from the FTP server.
     */
    protected FTPStorageBucket getFTPOutputBucket(){
        return (FTPStorageBucket) storageBuckets.values().iterator().next();
    }

    /**
     * Returns the bucket to be used by the server to download the input file from the FTP server.
     *
     * @see #getFTPInputBucket()
     */
    protected StorageBucket getInputBucket(){
        return getFTPInputBucket();
    }

    /**
     * Returns the bucket to be used by the server to upload the output file from the FTP server.
     *
     * @see #getFTPOutputBucket()
     */
    protected StorageBucket getOutputBucket(){
        return getFTPOutputBucket();
    }

    /**
     * Returns the path to which the file should be uploaded in the FTP server and from which
     * the server will download it.
     *
     * @param anekaRequestId a random UUID used to identify this request.
     * @see #getInputVirtualPath()
     */
    protected String buildInputPath(String anekaRequestId){
        return String.format("/%s/%s", anekaRequestId, getInputVirtualPath());
    }

    /**
     * Returns the path from which the file should be downloaded in the FTP server and to which
     * the server will upload it.
     *
     * @param anekaRequestId a random UUID used to identify this request.
     * @see #getOutputVirtualPath()
     */
    protected String buildOutputPath(String anekaRequestId){
        return String.format("/%s/%s", anekaRequestId, getOutputVirtualPath());
    }

    /**
     * Returns the job to be submitted to Aneka.
     *
     * @param anekaRequestId a random UUID used to identify this request.
     * @param input the input data
     * @return the built job.
     */
    protected Job buildJob(String anekaRequestId, T... input){
        Job job = new Job();
        job.setReservationId(anekaRequestId);
        job.setTasks(buildTasks());
        job.setInputFiles(WSDLBuilder.buildArrayOfFile(
                getInputBucket().getName(),
                buildInputPath(anekaRequestId),
                getInputVirtualPath()));
        job.setOutputFiles(WSDLBuilder.buildArrayOfFile(
                getOutputBucket().getName(),
                buildOutputPath(anekaRequestId),
                getOutputVirtualPath()));
        return job;

    }

    private void login(){
        new LoginTask().execute();
    }

    /**
     * When attached to the {@link org.cloudbus.foggatewaylib.core.ExecutionManager},
     * initializes credentials, shared files and storage buckets before starting login.
     */
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

    /**
     * AsyncTask inner class used to log in the server.
     */
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

    /**
     * Returns {@code true} if the Aneka web services are connected, login has been successful and
     * the application has been created. {@code false} otherwise.
     */
    public boolean isConnected(){
        return anekaWebServices != null
                && anekaWebServices.isLoggedIn()
                && anekaWebServices.hasValidDefaultApplication();

    }

    /**
     * Returns the file type used by this provider.
     */
    public int getFileType() {
        return fileType;
    }

    /**
     * Sets the file type used by this provider.
     *
     * NB: make sure to implement the required {@code inputTo*} and {@code *ToOutput} methods.
     */
    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    /**
     * Returns the request timeout used in the {@link AnekaWebServices}.
     *
     * @see #setRequestTimeout(int)
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Sets the request timeout to be used in the {@link AnekaWebServices}. Default is
     * {@link AnekaWebServices#DEFAULT_REQUEST_TIMEOUT}
     *
     * @see AnekaWebServices#setRequestTimeout(int)
     */
    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
        if (anekaWebServices != null)
            anekaWebServices.setRequestTimeout(requestTimeout);
    }

    /**
     * Returns the job timeout used in the {@link AnekaWebServices}.
     *
     * @see #setJobTimeout(int)
     */
    public int getJobTimeout() {
        return jobTimeout;
    }

    /**
     * Sets the job timeout to be used in the {@link AnekaWebServices}. Default is
     * {@link AnekaWebServices#DEFAULT_JOB_TIMEOUT}
     *
     * @see AnekaWebServices#setJobTimeout(int)
     */
    public void setJobTimeout(int jobTimeout) {
        this.jobTimeout = jobTimeout;
    }

    /**
     * Returns the polling period used in the {@link AnekaWebServices}.
     *
     * @see #setPollingPeriod(int)
     */
    public int getPollingPeriod() {
        return pollingPeriod;
    }

    /**
     * Sets the polling period to be used in the {@link AnekaWebServices}. Default is
     * {@link AnekaWebServices#DEFAULT_POLLING_PERIOD}
     *
     * @see AnekaWebServices#setPollingPeriod(int)
     */
    public void setPollingPeriod(int pollingPeriod) {
        this.pollingPeriod = pollingPeriod;
    }
}
