package org.cloudbus.foggatewaylib.aneka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.manjrasoft.aneka.*;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;

/**
 * Helper class for accessing the Aneka Web Services.
 *
 * First action after creation must be {@link #authenticateUser(String, String)} that authenticates
 * the user and saves the provided token.
 * After that, a new application must be created using {@code createApplication*} methods.
 * The id of the latest created application will be stored and used when no application id is
 * provided.
 * Within the new application, jobs can be submitted to Aneka using {@code #submitJob*} methods.
 * Last, when the application is no longer needed, it may be aborted using
 * {@link #abortApplication(String)}.
 *
 * @author Riccardo Mancini
 */
public class AnekaWebServices {

    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    public static final int DEFAULT_JOB_TIMEOUT = 60000;
    public static final int DEFAULT_POLLING_PERIOD = 500;

    private TaskService service;
    private UserCredential mUserCredential;
    private String error;
    private String defaultApplicationId;

    private int requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    private int jobTimeout = DEFAULT_JOB_TIMEOUT;
    private int pollingPeriod = DEFAULT_POLLING_PERIOD;

    /**
     * Constructs a new helper that uses the specified url. Debug is disabled.
     *
     * @param url url to the server API (e.g. http://www.example.com/Aneka.2.0/TaskService.asmx).
     * @see #AnekaWebServices(String, boolean)
     */
    public AnekaWebServices(String url){
        this(url, false);
    }

    /**
     * Constructs a new helper that uses the specified url with the possibility to enable debug.
     *
     * @param url url to the server API (e.g. http://www.example.com/Aneka.2.0/TaskService.asmx).
     * @param debug {@code true} to enable debug, {@code false} otherwise.
     * @see TaskService#TaskService(String, int, boolean)
     */
    public AnekaWebServices(String url, boolean debug){
        service = new TaskService(url, 120, debug);
    }

    /**
     * Authenticates in the API using the given credentials. Access token is stored in
     * {@link #mUserCredential}.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param username the Aneka username.
     * @param password the Aneka password.
     * @return {@code true} if authentication was successful, {@code false} otherwise.
     * @see #getError()
     */
    public boolean authenticateUser(String username, String password){
        AuthenticateUserResponse response;

        AuthenticateUser authenticateUserParams = new AuthenticateUser();
        authenticateUserParams.setUsername(username);
        authenticateUserParams.setPassword(password);

        try {
             response = service.authenticateUser(authenticateUserParams);
             mUserCredential = response.getAuthenticateUserResult().getUserCredential();
             dumpError(response.getAuthenticateUserResult());
             return response.getAuthenticateUserResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    /**
     * Creates a new application with the given name, shared files and storage buckets.
     * The latest created application id will be saved in {@link #defaultApplicationId},
     * accessible through {@link #getDefaultApplicationId()}.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * NB: immediately after calling this method, the application will not already be ready to be
     *     used. You need to check its status before using it.
     *
     * @param name an arbitrary name for the new application.
     * @param sharedFiles shared files, if any, {@code null} otherwise.
     * @param storageBuckets storage buckets, if any.
     * @return the application id if the application was submitted successfully,
     *         {@code null} otherwise.
     * @see #getError()
     * @see #createApplicationWait(String, ArrayOfFile, StorageBucket...)
     */
    @Nullable
    public String createApplication(String name, ArrayOfFile sharedFiles,
                                    StorageBucket... storageBuckets){
        CreateApplicationResponse response;

        CreateApplication createApplicationParams = new CreateApplication();
        ApplicationSubmissionRequest applicationSubmissionRequest = new ApplicationSubmissionRequest();
        applicationSubmissionRequest.setDisplayName(name);
        applicationSubmissionRequest.setSharedFiles(sharedFiles);
        applicationSubmissionRequest.setUserCredential(mUserCredential);
        createApplicationParams.setRequest(applicationSubmissionRequest);

        if (storageBuckets.length > 0){
            PropertyGroup metadata = new PropertyGroup();
            metadata.setNameProperty("Metadata");
            ArrayOfPropertyGroup metadataArray = new ArrayOfPropertyGroup();

            PropertyGroup buckets = new PropertyGroup();
            buckets.setNameProperty("StorageBuckets");
            PropertyGroup[] bucketGroups = new PropertyGroup[storageBuckets.length];

            for (int i = 0; i< storageBuckets.length; i++){
                bucketGroups[i] = storageBuckets[i].asPropertyGroup();
            }
            ArrayOfPropertyGroup arrayOfPropertyGroup = new ArrayOfPropertyGroup();
            arrayOfPropertyGroup.setPropertyGroup(bucketGroups);
            buckets.setGroups(arrayOfPropertyGroup);

            metadataArray.setPropertyGroup(new PropertyGroup[]{buckets});
            metadata.setGroups(metadataArray);

            applicationSubmissionRequest.setMetadata(metadata);
        }

        try {
             response = service.createApplication(createApplicationParams);
             dumpError(response.getCreateApplicationResult());
             if (response.getCreateApplicationResult().isSuccess()){
                 String applicationId = response.getCreateApplicationResult().getApplicationId();
                 defaultApplicationId = applicationId;
                 return applicationId;
             } else
                 return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    /**
     * Queries application information for the given application id.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application.
     * @return the retrieved {@link ApplicationResult} class if successful, {@code null} otherwise.
     * @see #getError()
     */
    @Nullable
    public ApplicationResult queryApplication(String applicationId){
        QueryApplicationResponse response;

        QueryApplication queryApplicationParams = new QueryApplication();
        ApplicationQueryRequest applicationQueryRequest = new ApplicationQueryRequest();
        applicationQueryRequest.setApplicationId(applicationId);
        applicationQueryRequest.setUserCredential(mUserCredential);
        queryApplicationParams.setRequest(applicationQueryRequest);

        try {
             response = service.queryApplication(queryApplicationParams);
            dumpError(response.getQueryApplicationResult());
             return response.getQueryApplicationResult();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    /**
     * Queries the default application.
     *
     * @see #queryApplication(String)
     */
    @Nullable
    public ApplicationResult queryApplication(){
        return queryApplication(defaultApplicationId);
    }

    /**
     * Queries application status for the given application id.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application.
     * @return one of the constants in {@link ApplicationStatus}, representing the application
     *         status if API call was successful, {@code null} otherwise.
     * @see #getError()
     */
    @Nullable
    public String queryApplicationStatus(String applicationId){
        QueryApplicationStatusResponse response;

        QueryApplicationStatus queryApplicationStatusParams = new QueryApplicationStatus();
        ApplicationQueryRequest applicationQueryRequest = new ApplicationQueryRequest();
        applicationQueryRequest.setApplicationId(applicationId);
        applicationQueryRequest.setUserCredential(mUserCredential);
        queryApplicationStatusParams.setRequest(applicationQueryRequest);

        try {
            response = service.queryApplicationStatus(queryApplicationStatusParams);
            dumpError(response.getQueryApplicationStatusResult());
            if(response.getQueryApplicationStatusResult().getStatus() != null)
                return response.getQueryApplicationStatusResult().getStatus().getValue();
             else
                 return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    /**
     * Queries the status of the default application.
     *
     * @see #queryApplicationStatus(String)
     */
    @Nullable
    public String queryApplicationStatus(){
        return queryApplicationStatus(defaultApplicationId);
    }

    /**
     * Aborts the application identified by the given application id.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application.
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see #getError()
     */
    public boolean abortApplication(String applicationId){
        AbortApplicationResponse response;

        AbortApplication abortApplicationParams = new AbortApplication();
        ApplicationAbortRequest applicationAbortRequest = new ApplicationAbortRequest();
        applicationAbortRequest.setApplicationId(applicationId);
        applicationAbortRequest.setUserCredential(mUserCredential);
        abortApplicationParams.setRequest(applicationAbortRequest);

        try {
            response = service.abortApplication(abortApplicationParams);
            dumpError(response.getAbortApplicationResult());
            return response.getAbortApplicationResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    /**
     * Aborts the default application.
     *
     * @see #abortApplication(String)
     */
    public boolean abortApplication(){
        return abortApplication(defaultApplicationId);
    }

    /**
     * Waits for the application identified by the given {@code applicationId} to be ready to be
     * used.
     * This method calls {@link #queryApplicationStatus(String)} every {@link #pollingPeriod}
     * milliseconds until the application is ready or after {@link #requestTimeout} milliseconds
     * have elapsed.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application.
     * @return {@code true} in case of success, {@code false} otherwise.
     * @see #getError()
     */
    public boolean waitApplicationCreation(String applicationId){
        long stopTime = new Date().getTime() + requestTimeout;
        while(true){
            String status = queryApplicationStatus(applicationId);
            if (status == null)
                status = "NULL";

            switch (status){
                case ApplicationStatus.STATUS_SUBMITTED:
                case ApplicationStatus.STATUS_RUNNING:
                    return true;

                case "NULL":
                case ApplicationStatus.STATUS_UNSUBMITTED:
                    if (requestTimeout > 0 && new Date(stopTime).before(new Date())){
                        error = "Timeout";
                        return false;
                    } else
                        break;

                case ApplicationStatus.STATUS_FINISHED:
                case ApplicationStatus.STATUS_PAUSED:
                case ApplicationStatus.STATUS_ERROR:
                case ApplicationStatus.STATUS_UNKNOWN:
                default:
                    return false;
            }

            if (pollingPeriod > 0){
                try {
                    Thread.sleep(pollingPeriod);
                } catch (InterruptedException e){
                    e.printStackTrace();
                    dumpError(e);
                    return false;
                }
            }
        }
    }

    /**
     * Waits for the default application to be ready to be used.
     *
     * @see #waitApplicationCreation(String)
     */
    public boolean waitApplicationCreation(){
        return waitApplicationCreation(defaultApplicationId);
    }

    /**
     * Creates a new application and waits for it to be ready.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * NB: this method will block the thread it is executing in.
     *
     * @see #createApplication(String, ArrayOfFile, StorageBucket...)
     * @see #waitApplicationCreation(String)
     */
    @Nullable
    public String createApplicationWait(String name, ArrayOfFile sharedFiles,
                                        StorageBucket... storageBuckets){
        String id = createApplication(name, sharedFiles, storageBuckets);

        if (id == null)
            return null;

        if (waitApplicationCreation(id)){
            return id;
        } else {
            return null;
        }
    }

    /**
     * Creates a new application and waits for it to be ready.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * NB: this method will block the thread it is executing in.
     *
     * @see #createApplicationWait(String, ArrayOfFile, StorageBucket...)
     */
    @Nullable
    public String createApplicationWait(String name, StorageBucket... storageBuckets){
        return createApplicationWait(name, null, storageBuckets);
    }

    @NonNull
    private String[] __submitJobs(String applicationId, Job... jobs)
            throws IOException, XmlPullParserException{
        if (jobs.length == 0)
            return new String[jobs.length];

        SubmitJobsResponse response;

        SubmitJobs submitJobsParams = new SubmitJobs();
        JobSubmissionRequest jobSubmissionRequest = new JobSubmissionRequest();
        jobSubmissionRequest.setApplicationId(applicationId);
        jobSubmissionRequest.setUserCredential(mUserCredential);
        ArrayOfJob arrayOfJob = new ArrayOfJob();
        arrayOfJob.setJob(jobs);
        jobSubmissionRequest.setJobs(arrayOfJob);
        submitJobsParams.setRequest(jobSubmissionRequest);

        response = service.submitJobs(submitJobsParams);
        dumpError(response.getSubmitJobsResult());
        if(response.getSubmitJobsResult().isSuccess())
            return response.getSubmitJobsResult().getIds().getString();
        else
            return new String[jobs.length];
    }

    /**
     * Submits the given {@code jobs} to the application identified by the given
     * {@code applicationId}.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application.
     * @param jobs the jobs to be submitted.
     * @return an array of the same length as {@code jobs} containing the ids of the created jobs
     *         in the order they were given. If a job creation fails, its id will be {@code null}.
     */
    @NonNull
    public String[] submitJobs(String applicationId, Job... jobs) {

        try{
            return __submitJobs(applicationId, jobs);
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return new String[jobs.length];
        }
    }

    /**
     * Submits the given {@code jobs} to the default application.
     *
     * @see #submitJob(String, Job)
     */
    @NonNull
    public String[] submitJobs(Job... jobs) {
        return submitJobs(defaultApplicationId, jobs);
    }

    private int countNulls(Object... objs){
        int n = 0;
        for (Object o:objs)
            if (o == null)
                n++;
        return n;
    }

    /**
     * Submits the given {@code jobs} to the application identified by the given
     * {@code applicationId}, retrying any failing jobs every {@link #pollingPeriod} milliseconds
     * up to a maximum time of {@link #requestTimeout} milliseconds.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application.
     * @param jobs the jobs to be submitted.
     * @return an array of the same length as {@code jobs} containing the ids of the created jobs
     *         in the order they were given. If a job creation fails, its id will be {@code null}.
     * @see #submitJobs(String, Job...)
     */
    @NonNull
    public String[] submitJobsWait(String applicationId, Job... jobs) {
        long stopTime = new Date().getTime() + requestTimeout;
        try{
            String[] result = new String[jobs.length];
            int nNulls;

            while ((nNulls = countNulls(result)) != 0) {
                Job[] remainingJobs = new Job[nNulls];

                for (int i = 0, j = 0; i < jobs.length; i++){
                    if (result[i] == null)
                        remainingJobs[j++] = jobs[i];
                }

                String[] retryResult = __submitJobs(applicationId, remainingJobs);

                for (int i = 0, j = 0; i < jobs.length; i++){
                    if (result[i] == null)
                        result[i] = retryResult[j++];
                }

                if (requestTimeout > 0 && new Date(stopTime).before(new Date())){
                    error = "Timeout";
                    return new String[jobs.length];
                }
                if (pollingPeriod > 0){
                    try {
                        Thread.sleep(pollingPeriod);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                        dumpError(e);
                        return new String[jobs.length];
                    }
                }
            }
            return result;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return new String[jobs.length];
        }
    }

    /**
     * Submits the given {@code jobs} to the default, retrying any failing jobs every
     * {@link #pollingPeriod} milliseconds for a maximum time {@link #requestTimeout} milliseconds.
     *
     * @see #submitJobsWait(String, Job...)
     */
    @NonNull
    public String[] submitJobsWait(Job... jobs) {
        return submitJobsWait(defaultApplicationId, jobs);
    }

    /**
     * Queries all information about the given job.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application the job runs in.
     * @param jobId the id of the job to query.
     * @return the state of the job or {@code null} in case of error.
     */
    @Nullable
    public JobResult queryJob(String applicationId, String jobId){
        QueryJobResponse response;

        QueryJob queryJobParams = new QueryJob();
        JobQueryRequest jobQueryRequest = new JobQueryRequest();
        jobQueryRequest.setJobId(jobId);
        jobQueryRequest.setApplicationId(applicationId);
        jobQueryRequest.setUserCredential(mUserCredential);
        queryJobParams.setRequest(jobQueryRequest);

        try {
            response = service.queryJob(queryJobParams);
            dumpError(response.getQueryJobResult());
            if(response.getQueryJobResult().isSuccess())
                return response.getQueryJobResult();
            else
                return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    /**
     * Queries all information about the given job in the default application.
     *
     * @see #queryJob(String, String)
     */
    @Nullable
    public JobResult queryJob(String jobId) {
        return queryJob(defaultApplicationId, jobId);
    }

    /**
     * Queries the status of a job.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application the job runs in.
     * @param jobId the id of the job to query.
     * @return one of the constants in {@link JobStatus}, representing the job status,
     *         if API call was successful. {@code null} otherwise.
     * @see JobStatus
     */
    @Nullable
    public String queryJobStatus(String applicationId, String jobId){
        QueryJobStatusResponse response;

        QueryJobStatus queryJobStatusParams = new QueryJobStatus();
        JobQueryRequest jobQueryRequest = new JobQueryRequest();
        jobQueryRequest.setJobId(jobId);
        jobQueryRequest.setApplicationId(applicationId);
        jobQueryRequest.setUserCredential(mUserCredential);
        queryJobStatusParams.setRequest(jobQueryRequest);

        try {
            response = service.queryJobStatus(queryJobStatusParams);
            dumpError(response.getQueryJobStatusResult());
            if(response.getQueryJobStatusResult().getStatus() != null)
                return response.getQueryJobStatusResult().getStatus().getValue();
            else
                return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    /**
     * Quesries the status of a job in the default application.
     *
     * @see #queryJobStatus(String, String)
     */
    @Nullable
    public String queryJobStatus(String jobId) {
        return queryJobStatus(defaultApplicationId, jobId);
    }

    /**
     * Aborts the given job.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application the job runs in.
     * @param jobId the id of the job to abort.
     * @return {@code true} in case of success, {@code false} otherwise.
     */
    public boolean abortJob(String applicationId, String jobId){
        AbortJobResponse response;

        AbortJob abortJobParams = new AbortJob();
        JobAbortRequest abortRequest = new JobAbortRequest();
        abortRequest.setJobId(jobId);
        abortRequest.setApplicationId(applicationId);
        abortRequest.setUserCredential(mUserCredential);

        try {
            response = service.abortJob(abortJobParams);
            dumpError(response.getAbortJobResult());
            return response.getAbortJobResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    /**
     * Aborts the given job in the default application.
     *
     * @see #abortJob(String, String)
     */
    @Nullable
    public boolean abortJob(String jobId) {
        return abortJob(defaultApplicationId, jobId);
    }

    /**
     * Submits the given job.
     *
     * @param applicationId the id of the application the job runs in.
     * @param job the job to be submitted.
     * @return the id of the created job or {@code null} in case of error.
     * @see #submitJobs(String, Job...)
     */
    @Nullable
    public String submitJob(String applicationId, Job job){
        return submitJobs(applicationId, job)[0];
    }

    /**
     * Submits the given job in the default application.
     *
     * @see #submitJob(Job...)
     */
    @Nullable
    public String submitJob(Job job){
        return submitJob(defaultApplicationId, job);
    }

    /**
     * Submits the given {@code job} to the application identified by the given
     * {@code applicationId}, retrying every {@link #pollingPeriod} milliseconds in case of failure
     * up to a maximum time of {@link #requestTimeout} milliseconds.
     *
     * @param applicationId the id of the application the job runs in.
     * @param job the job to be submitted.
     * @return the id of the created job or {@code null} in case of error.
     * @see #submitJobsWait(String, Job...)
     */
    @Nullable
    public String submitJobWait(String applicationId, Job job){
        return submitJobsWait(applicationId, job)[0];
    }

    /**
     * Submits the given {@code job} to the default application, retrying every
     * {@link #pollingPeriod} milliseconds in case of failure, up to a maximum time of
     * {@link #requestTimeout} milliseconds.
     *
     * @see #submitJobWait(String, Job)
     */
    @Nullable
    public String submitJobWait(Job job){
        return submitJobWait(defaultApplicationId, job);
    }

    /**
     * Waits for the given job termination for up to {@link #jobTimeout} milliseconds, polling
     * the Aneka server every {@link #pollingPeriod} milliseconds.
     *
     * NB: this method will block the thread.
     *
     * @param applicationId the id of the application the job runs in.
     * @param jobId the id of the job to wait.
     * @return one of the constants in {@link JobStatus} representing the job status,
     *      if API call was successful. {@code null} otherwise.
     */
    @Nullable
    public String waitJobTermination(String applicationId, String jobId){
        long stopTime = new Date().getTime() + jobTimeout;
        while(true){
            String status = queryJobStatus(applicationId, jobId);
            if (status == null)
                return null;

            switch (status){
                case JobStatus.STATUS_QUEUED:
                case JobStatus.STATUS_RUNNING:
                case JobStatus.STATUS_STAGINGIN:
                case JobStatus.STATUS_STAGINGOUT:
                    if (jobTimeout > 0 && new Date(stopTime).before(new Date())){
                        error = "Timeout";
                        return null;
                    } else
                        break;

                case JobStatus.STATUS_COMPLETED:
                case JobStatus.STATUS_ABORTED:
                case JobStatus.STATUS_FAILED:
                case JobStatus.STATUS_REJECTED:
                case JobStatus.STATUS_UNKNOWN:
                default:
                    return status;

            }

            if (pollingPeriod > 0){
                try {
                    Thread.sleep(pollingPeriod);
                } catch (InterruptedException e){
                    e.printStackTrace();
                    dumpError(e);
                    return null;
                }
            }
        }
    }

    /**
     * Waits for the given job termination for up to {@link #jobTimeout} milliseconds, polling
     * the Aneka server every {@link #pollingPeriod} milliseconds. The job must be in the default
     * application.
     *
     * NB: this method will block the thread.
     *
     * @see #waitJobTermination(String, String)
     */
    @Nullable
    public String waitJobTermination(String jobId){
        return waitJobTermination(defaultApplicationId, jobId);
    }

    private void dumpError(Result result){
        if (result != null && result.getError() != null){
            error = result.getError().getMessage();
        } else{
            error = null;
        }
    }

    private void dumpError(Exception e){
        error = e.getMessage();
    }

    /**
     * Returns the latest error message or {@code null} if there was none.
     */
    public String getError() {
        return error;
    }

    /**
     * Returns the user credentials used by this instance or {@code null} in case of authentication
     * error (or no authentication at all).
     */
    public UserCredential getUserCredential() {
        return mUserCredential;
    }

    /**
     * Sets the user credential to an arbitrary value.
     */
    public void setUserCredential(UserCredential mUserCredential) {
        this.mUserCredential = mUserCredential;
    }

    /**
     * Returns the id of the current default application, which is the latest created application.
     */
    public String getDefaultApplicationId() {
        return defaultApplicationId;
    }

    /**
     * Sets the default application id to an arbitrary value.
     */
    public void setDefaultApplicationId(String defaultApplicationId) {
        this.defaultApplicationId = defaultApplicationId;
    }

    /**
     * Returns the currently set request timeout.
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Sets a new request timeout in milliseconds. A value lower than or equal to {@code 0}
     * means no timeout at all. Default value is 5 seconds ({@code 5000}).
     *
     * @see #DEFAULT_REQUEST_TIMEOUT
     * @see #waitApplicationCreation(String)
     * @see #submitJobsWait(String, Job...)
     */
    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    /**
     * Returns the currently set job timeout.
     */
    public int getJobTimeout() {
        return jobTimeout;
    }

    /**
     * Sets a new job timeout in milliseconds. A value lower than or equal to {@code 0}
     * means no timeout at all. Default value is 1 minute ({@code 60000}).
     *
     * @see #DEFAULT_JOB_TIMEOUT
     * @see #waitJobTermination(String, String)
     */
    public void setJobTimeout(int jobTimeout) {
        this.jobTimeout = jobTimeout;
    }

    /**
     * Returns the currently set polling period.
     */
    public int getPollingPeriod() {
        return pollingPeriod;
    }

    /**
     * Sets a new polling period in milliseconds. A value lower than or equal to {@code 0}
     * will result in back-to-back requests. Default value is half second ({@code 500}).
     *
     * @see #DEFAULT_POLLING_PERIOD
     * @see #waitApplicationCreation(String)
     * @see #submitJobsWait(String, Job...)
     * @see #waitJobTermination(String, String)
     */
    public void setPollingPeriod(int pollingPeriod) {
        this.pollingPeriod = pollingPeriod;
    }

    /**
     * Returns {@code true} if this instance is authenticated, {@code false} otherwise.
     */
    public boolean isLoggedIn(){
        return mUserCredential != null;
    }

    /**
     * Returns {@code true} if the default application is set, {@code false} otherwise.
     */
    public boolean hasValidDefaultApplication(){
        return defaultApplicationId != null;
    }
}
